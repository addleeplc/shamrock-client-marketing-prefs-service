/*
 * Copyright 2008 - 2022 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.client.marketing.prefs.mq;

import com.haulmont.monaco.mq.annotations.Subscribe;
import com.haulmont.monaco.rabbit.mq.annotations.Consumer;
import com.haulmont.shamrock.client.marketing.prefs.eventbus.IdentityDeletionEventBus;
import com.haulmont.shamrock.client.marketing.prefs.mq.dto.IdentityDeletionEnqueued;
import org.picocontainer.annotations.Component;
import org.picocontainer.annotations.Inject;

import static com.haulmont.shamrock.client.marketing.prefs.ServiceConfiguration.MQ_CLIENT_IDENTITY_DELETION_CONSUMER;
import static com.haulmont.shamrock.client.marketing.prefs.ServiceConfiguration.MQ_CLIENT_IDENTITY_DELETION_SERVER_NAME;

@Consumer(server = MQ_CLIENT_IDENTITY_DELETION_SERVER_NAME, queue = MQ_CLIENT_IDENTITY_DELETION_CONSUMER)
@Component
public class IdentityDeletionConsumer {
    @Inject
    private IdentityDeletionEventBus eventBus;

    @Subscribe
    public void handleIdentityDeletionEnqueued(IdentityDeletionEnqueued message) {
        eventBus.post(message);
    }
}
