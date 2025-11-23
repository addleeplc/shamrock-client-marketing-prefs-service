/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.client.marketing.prefs.mq;

import com.haulmont.monaco.model.ModelUpdatesCoordinator;
import com.haulmont.monaco.mq.annotations.Subscribe;
import com.haulmont.monaco.rabbit.mq.annotations.Consumer;
import com.haulmont.shamrock.client.marketing.prefs.eventbus.ModelSyncEventBus;
import com.haulmont.shamrock.client.marketing.prefs.mq.dto.*;
import org.picocontainer.annotations.Component;
import org.picocontainer.annotations.Inject;

@Component
@Consumer(server = "mq.clientMarketingPreferences.serverName", queue = "mq.clientMarketingPreferences.consumer", coordinator = ModelUpdatesCoordinator.class)
public class PreferencesModelUpdateMessagesConsumer {
    @Inject
    private ModelSyncEventBus modelSyncEventBus;

    @Subscribe
    public void handleChannelCreated(ChannelCreated message) {
        handleModelUpdateEvent(message);
    }

    @Subscribe
    public void handleChannelUpdated(ChannelUpdated message) {
        handleModelUpdateEvent(message);
    }

    @Subscribe
    public void handleChannelDeleted(ChannelDeleted message) {
        handleModelUpdateEvent(message);
    }

    @Subscribe
    public void handleIntegratorCreated(CategoryCreated message) {
        handleModelUpdateEvent(message);
    }

    @Subscribe
    public void handleIntegratorUpdated(CategoryUpdated message) {
        handleModelUpdateEvent(message);
    }

    @Subscribe
    public void handleIntegratorDeleted(CategoryDeleted message) {
        handleModelUpdateEvent(message);
    }

    private void handleModelUpdateEvent(AbstractSyncModelMessage<?> message) {
        modelSyncEventBus.post(message);
    }
}
