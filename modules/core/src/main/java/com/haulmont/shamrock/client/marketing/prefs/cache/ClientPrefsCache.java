/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.client.marketing.prefs.cache;

import com.haulmont.shamrock.client.marketing.prefs.ServiceConfiguration;
import com.haulmont.shamrock.client.marketing.prefs.storage.ClientPrefsRepository;
import com.haulmont.shamrock.client.marketing.prefs.model.ClientPrefs;
import com.haulmont.shamrock.client.marketing.prefs.storage.model.ClientId;
import com.haulmont.shamrock.client.marketing.prefs.utils.ClientPrefsUtils;
import org.picocontainer.annotations.Component;
import org.picocontainer.annotations.Inject;

import java.util.Optional;

@Component
public class ClientPrefsCache extends AbstractCache.ByClientId<ClientPrefs> {
    private static final long CLIENTS_PREFS_CACHE_EXPIRY_SECONDS = 60L;
    private static final long CLIENTS_PREFS_CACHE_MAX_SIZE = 20000L;

    @Inject
    private ServiceConfiguration configuration;

    public ClientPrefsCache(ClientPrefsRepository clientPrefsRepository) {
        super(id -> {
            if (id == null) {
                return null;
            }

            ClientId clientId = ClientPrefsUtils.convert(id);
            com.haulmont.shamrock.client.marketing.prefs.storage.model.ClientPrefs prefs = clientPrefsRepository.get(clientId);

            return ClientPrefsUtils.convert(prefs);
        });
    }

    @Override
    protected long getExpiryInSeconds() {
        return Optional.ofNullable(configuration.getClientsPrefsCacheExpirySeconds()).orElse(CLIENTS_PREFS_CACHE_EXPIRY_SECONDS);
    }

    @Override
    protected long getMaxSize() {
        return Optional.ofNullable(configuration.getClientsPrefsCacheMaxSize()).orElse(CLIENTS_PREFS_CACHE_MAX_SIZE);
    }
}
