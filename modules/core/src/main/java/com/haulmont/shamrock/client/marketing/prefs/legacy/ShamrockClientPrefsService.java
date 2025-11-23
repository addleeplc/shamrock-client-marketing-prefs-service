/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.client.marketing.prefs.legacy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.haulmont.bali.lang.BooleanUtils;
import com.haulmont.bali.lang.StringUtils;
import com.haulmont.monaco.jackson.ObjectMapperFactory;
import com.haulmont.shamrock.client.marketing.prefs.ServiceConfiguration;
import com.haulmont.shamrock.client.marketing.prefs.cache.ChannelCache;
import com.haulmont.shamrock.client.marketing.prefs.dto.Preferences.ChannelOptIn;
import com.haulmont.shamrock.client.marketing.prefs.legacy.db.ShamrockClientPrefsRepository;
import com.haulmont.shamrock.client.marketing.prefs.legacy.model.Preferences;
import com.haulmont.shamrock.client.marketing.prefs.model.ClientId;
import com.haulmont.shamrock.client.marketing.prefs.model.ModelInstanceId;
import com.haulmont.shamrock.client.marketing.prefs.utils.ClientPrefsUtils;
import org.picocontainer.annotations.Component;
import org.picocontainer.annotations.Inject;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Component
@Deprecated // get rid of the bean when there is no client using preferences in sybase and all the clients are imported
public class ShamrockClientPrefsService {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapperFactory().mapper();
    private static final XmlMapper XML_MAPPER = new ObjectMapperFactory().xmlMapper();

    private static final Map<String, Function<Preferences, Boolean>> CHANNELS = Map.of(
            "email", p -> Optional.ofNullable(p.getEmailOptOut()).map(oo -> !oo).orElse(null),
            "sms", p -> Optional.ofNullable(p.getSmsOptOut()).map(oo -> !oo).orElse(null),
            "push", p -> Optional.ofNullable(p.getPushOptOut()).map(oo -> !oo).orElse(null),
            "call", p -> Optional.ofNullable(p.getCallOptOut()).map(oo -> !oo).orElse(null)
    );

    public static final String[] NOTIFICATION_SETTINGS_OFFER_PATH = new String[]{
            "data", "notificationSettings", "com.haulmont.shamrock.live.dto.NotificationSettings", "offer"
    };

    @Inject
    private Logger logger;

    @Inject
    private ServiceConfiguration serviceConfiguration;

    @Inject
    private ShamrockClientPrefsRepository clientPrefsRepository;

    @Inject
    private ChannelCache channelCache;


    public List<ChannelOptIn> getPreferences(com.haulmont.shamrock.client.marketing.prefs.dto.ClientId id) {
        com.haulmont.shamrock.client.marketing.prefs.model.ClientId clientId = ClientPrefsUtils.convert(id);

        Preferences preferences = clientPrefsRepository.getPreferencesAsXml(clientId);
        if (preferences == null && clientId.getId() != null) {
            preferences = clientPrefsRepository.getPreferences(clientId);
        } else {
            preferences = spreadXmlPreferences(id, preferences);
        }

        if (preferences == null) {
            logger.warn("Legacy preferences not found for the client (id: {})", id);
            return null;
        }

        return createChannelOptIns(preferences);
    }

    private List<ChannelOptIn> createChannelOptIns(Preferences preferences) {
        List<ChannelOptIn> res = new ArrayList<>();

        if (preferences.getSmsOptOut() == null && preferences.getEmailOptOut() == null && preferences.getCallOptOut() == null && preferences.getPushOptOut() == null) {
            return null;
        }

        CHANNELS.forEach((code, fn) -> {
            if (channelCache.get(new ModelInstanceId(code)) != null) {
                ChannelOptIn optIn = new ChannelOptIn();

                optIn.setCode(code);
                optIn.setOptIn(Optional.ofNullable(fn.apply(preferences)).orElseGet(this::getPrefsChannelsOptInByDefault));

                res.add(optIn);
            }
        });

        return res;
    }

    private boolean getPrefsChannelsOptInByDefault() {
        return Optional.ofNullable(serviceConfiguration.getPrefsChannelsOptInByDefault()).orElse(true);
    }

    private Preferences spreadXmlPreferences(com.haulmont.shamrock.client.marketing.prefs.dto.ClientId id, Preferences preferences) {
        if (id == null) {
            return null;
        }

        com.haulmont.shamrock.client.marketing.prefs.model.ClientId clientId = ClientPrefsUtils.convert(id);

        if (preferences == null) {
            preferences = new Preferences();
        }

        if (StringUtils.isNotBlank(preferences.getPrefs())) {
            try {
                String prefs = decodeHex(preferences.getPrefs());
                JsonNode node = XML_MAPPER.readTree(new StringReader(prefs));
                JsonNode offerNode = findChildNode(node, NOTIFICATION_SETTINGS_OFFER_PATH, 1);

                NotificationSettings offerSettings = OBJECT_MAPPER.readValue(offerNode.toString(), NotificationSettings.class);
                if (offerSettings == null) {
                    throw new RuntimeException("Couldn't parse NotificationSettings");
                }

                preferences.setEmailOptOut(BooleanUtils.isNotTrue(offerSettings.getUseEmail()));
                preferences.setSmsOptOut(BooleanUtils.isNotTrue(offerSettings.getUseSms()));
                preferences.setPushOptOut(BooleanUtils.isNotTrue(offerSettings.getUsePush()));
                preferences.setCallOptOut(BooleanUtils.isNotTrue(offerSettings.getUseCall()));
            } catch (IOException e) {
                logger.warn("Wrong preferences are written for the client (id: {})", id);
                return null;
            }
        }

        copyClientId(clientId, preferences, true);

        return preferences;
    }

    private JsonNode findChildNode(JsonNode node, String[] path, int step) {
        if (node == null || step >= path.length) {
            return null;
        }

        JsonNode child = node.get(path[step]);

        if (step == path.length - 1) {
            return child;
        }

        return findChildNode(child, path, step + 1);
    }

    private String decodeHex(String hexString) {
        if (hexString == null) {
            return null;
        }

        byte[] bytes = new byte[hexString.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(hexString.substring(2 * i, 2 * i + 2), 16);
        }

        return new String(bytes);
    }

    private <T extends ClientId, R extends ClientId> void copyClientId(T from, R to, boolean override) {
        if (from == null || to == null) {
            return;
        }

        if (to.getId() == null || override) {
            to.setId(from.getId());
        }

        if (to.getUid() == null || override) {
            to.setUid(from.getUid());
        }

        if (to.getEmail() == null || override) {
            to.setEmail(from.getEmail());
        }
    }

    private static class NotificationSettings {
        private Boolean useEmail;
        private Boolean useSms;
        private Boolean usePush;
        private Boolean useCall;

        public Boolean getUseCall() {
            return useCall;
        }

        public void setUseCall(Boolean useCall) {
            this.useCall = useCall;
        }

        public Boolean getUseEmail() {
            return useEmail;
        }

        public void setUseEmail(Boolean useEmail) {
            this.useEmail = useEmail;
        }

        public Boolean getUsePush() {
            return usePush;
        }

        public void setUsePush(Boolean usePush) {
            this.usePush = usePush;
        }

        public Boolean getUseSms() {
            return useSms;
        }

        public void setUseSms(Boolean useSms) {
            this.useSms = useSms;
        }
    }
}
