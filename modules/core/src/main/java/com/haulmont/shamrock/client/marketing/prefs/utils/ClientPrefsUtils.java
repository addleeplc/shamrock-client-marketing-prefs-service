/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.client.marketing.prefs.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.haulmont.monaco.ServiceException;
import com.haulmont.monaco.jackson.ObjectMapperFactory;
import com.haulmont.monaco.response.ErrorCode;
import com.haulmont.shamrock.client.marketing.prefs.model.ClientId;
import com.haulmont.shamrock.client.marketing.prefs.model.ClientPrefs;
import com.haulmont.shamrock.client.marketing.prefs.model.Preferences;

public final class ClientPrefsUtils {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapperFactory().mapper();

    private ClientPrefsUtils() {
    }

    public static com.haulmont.shamrock.client.marketing.prefs.storage.model.ClientId convert(ClientId id) {
        if (id == null) {
            return null;
        }

        com.haulmont.shamrock.client.marketing.prefs.storage.model.ClientId res = new com.haulmont.shamrock.client.marketing.prefs.storage.model.ClientId();

        res.setId(id.getId());
        res.setUid(id.getUid());
        res.setEmail(id.getEmail());

        return res;
    }

    public static com.haulmont.shamrock.client.marketing.prefs.storage.model.ClientPrefs convert(ClientId id, Preferences preferences) {
        return convert(id, preferences, null);
    }

    public static com.haulmont.shamrock.client.marketing.prefs.storage.model.ClientPrefs convert(ClientId id, Preferences preferences, Class<?> view) {
        if (id == null) {
            return null;
        }

        com.haulmont.shamrock.client.marketing.prefs.storage.model.ClientPrefs res = new com.haulmont.shamrock.client.marketing.prefs.storage.model.ClientPrefs();

        res.setClientId(id.getId());
        res.setClientUid(id.getUid());
        res.setClientEmail(id.getEmail());

        try {
            String prefs = view == null ? OBJECT_MAPPER.writeValueAsString(preferences) : OBJECT_MAPPER.writerWithView(view).writeValueAsString(preferences);
            res.setPrefs(prefs);
        } catch (JsonProcessingException e) {
            throw new ServiceException(ErrorCode.BAD_REQUEST, "Can't render preferences from the request (client: " + id + ")", e);
        }

        return res;
    }

    public static ClientPrefs convert(com.haulmont.shamrock.client.marketing.prefs.storage.model.ClientPrefs prefs) {
        if (prefs == null) {
            return null;
        }

        ClientId clientId = new ClientId();

        clientId.setId(prefs.getClientId());
        clientId.setUid(prefs.getClientUid());
        clientId.setEmail(prefs.getClientEmail());

        Preferences preferences;

        try {
            preferences = OBJECT_MAPPER.readValue(prefs.getPrefs(), Preferences.class);
        } catch (JsonProcessingException e) {
            throw new ServiceException(ErrorCode.CONFLICT, "Can't read client preferences (id: " + clientId + ")", e);
        }

        return new ClientPrefs(clientId, preferences);
    }
}
