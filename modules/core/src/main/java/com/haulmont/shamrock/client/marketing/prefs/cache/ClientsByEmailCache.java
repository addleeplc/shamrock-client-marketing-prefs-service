/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.client.marketing.prefs.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.haulmont.bali.lang.StringUtils;
import com.haulmont.shamrock.client.marketing.prefs.ServiceConfiguration;
import com.haulmont.shamrock.client.marketing.prefs.dto.Client;
import com.haulmont.shamrock.client.marketing.prefs.services.ClientRegistryService;
import org.picocontainer.annotations.Component;
import org.picocontainer.annotations.Inject;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Component
public class ClientsByEmailCache implements CacheManagement<String, List<Client>> {
    private static final long CLIENT_BY_EMAIL_CACHE_EXPIRY_SECONDS = 60L;
    private static final long CLIENT_BY_EMAIL_CACHE_MAX_SIZE = 10000L;

    @Inject
    private ServiceConfiguration configuration;

    @Inject
    private ClientRegistryService clientRegistryService;

    private volatile LoadingCache<String, List<Client>> byEmail;

    public void start() {
        build();
    }

    private void build() {
        LoadingCache<String, List<Client>> byEmail =
                CacheBuilder.newBuilder()
                        .expireAfterWrite(getExpiryInSeconds(), TimeUnit.SECONDS)
                        .maximumSize(getByEmailMaxSize())
                        .build(new CacheLoader<>() {
                            @Override
                            public List<Client> load(String email) throws Exception {
                                if (StringUtils.isBlank(email)) {
                                    return Collections.emptyList();
                                }

                                return clientRegistryService.searchByEmail(email);
                            }
                        });

        synchronized (this) {
            this.byEmail = byEmail;
        }
    }

    @Override
    public List<Client> get(String email) {
        try {
            return byEmail.get(email);
        } catch (ExecutionException e) {
            throw new RuntimeException("Can't retrieve clients from cache (key: " + email + ")", e);
        }
    }

    private long getExpiryInSeconds() {
        return Optional.ofNullable(configuration.getClientsCacheByEmailExpirySeconds()).orElse(CLIENT_BY_EMAIL_CACHE_EXPIRY_SECONDS);
    }

    private long getByEmailMaxSize() {
        return Optional.ofNullable(configuration.getClientsCacheByEmailMaxSize()).orElse(CLIENT_BY_EMAIL_CACHE_MAX_SIZE);
    }

    @Override
    public void invalidateAll() {
        build();
    }

    @Override
    public void invalidate(String email) {
        byEmail.invalidate(email);
    }

    @Override
    public long getSize() {
        return byEmail.size();
    }
}
