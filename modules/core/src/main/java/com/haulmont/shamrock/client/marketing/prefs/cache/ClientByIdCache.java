/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.client.marketing.prefs.cache;

import com.haulmont.shamrock.client.marketing.prefs.ServiceConfiguration;
import com.haulmont.shamrock.client.marketing.prefs.dto.Client;
import com.haulmont.shamrock.client.marketing.prefs.services.ClientRegistryService;
import org.picocontainer.annotations.Component;
import org.picocontainer.annotations.Inject;

import java.util.Optional;

@Component
public class ClientByIdCache extends AbstractCache.ByClientId<Client> {
    private static final long CLIENT_CACHE_BY_ID_EXPIRY_SECONDS = 60L;
    private static final long CLIENT_BY_ID_CACHE_MAX_SIZE = 20000L;

    @Inject
    private ServiceConfiguration configuration;

    public ClientByIdCache(ClientRegistryService clientRegistryService) {
        super(id -> id.getId() == null ? null : clientRegistryService.getById(id.getId()));
    }

    @Override
    protected long getExpiryInSeconds() {
        return Optional.ofNullable(configuration.getClientsCacheByIdExpirySeconds()).orElse(CLIENT_CACHE_BY_ID_EXPIRY_SECONDS);
    }

    @Override
    protected long getMaxSize() {
        return Optional.ofNullable(configuration.getClientsCacheByIdMaxSize()).orElse(CLIENT_BY_ID_CACHE_MAX_SIZE);
    }
}
