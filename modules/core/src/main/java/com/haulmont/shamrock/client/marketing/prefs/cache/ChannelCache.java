/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.client.marketing.prefs.cache;

import com.haulmont.shamrock.client.marketing.prefs.ServiceConfiguration;
import com.haulmont.shamrock.client.marketing.prefs.db.ChannelsRepository;
import com.haulmont.shamrock.client.marketing.prefs.model.Channel;
import org.picocontainer.annotations.Component;
import org.picocontainer.annotations.Inject;

import java.util.Optional;

@Component
public class ChannelCache extends AbstractCache.ById<Channel> {
    private static final long CHANNEL_CACHE_EXPIRY_SECONDS = 180;

    @Inject
    private ServiceConfiguration configuration;

    public ChannelCache(ChannelsRepository channelsRepository) {
        super(channelsRepository::get);
    }

    @Override
    protected long getExpiryInSeconds() {
        return Optional.ofNullable(configuration.getChannelsCacheExpirySeconds()).orElse(CHANNEL_CACHE_EXPIRY_SECONDS);
    }
}
