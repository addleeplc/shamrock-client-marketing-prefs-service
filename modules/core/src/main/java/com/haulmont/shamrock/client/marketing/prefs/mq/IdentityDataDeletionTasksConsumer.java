/*
 * Copyright 2008 - 2022 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.client.marketing.prefs.mq;

import com.haulmont.monaco.mq.annotations.Subscribe;
import com.haulmont.monaco.rabbit.mq.annotations.Consumer;
import com.haulmont.shamrock.client.marketing.prefs.ClientMarketingPrefsModule;
import com.haulmont.shamrock.client.marketing.prefs.eventbus.IdentityDataDeletionEventBus;
import com.haulmont.shamrock.client.marketing.prefs.mq.dto.IdentityDeletionEnqueued;
import org.picocontainer.annotations.Component;
import org.picocontainer.annotations.Inject;

@Consumer(server = ClientMarketingPrefsModule.IDENTITY_DELETION_MQ + ".serverName", queue = ClientMarketingPrefsModule.IDENTITY_DELETION_MQ + ".consumer")
@Component
public class IdentityDataDeletionTasksConsumer {

    @Inject
    private IdentityDataDeletionEventBus eventBus;

    //

    @Subscribe
    public void handleIdentityDeletionEnqueued(IdentityDeletionEnqueued message) {
        eventBus.post(message);
    }
}
