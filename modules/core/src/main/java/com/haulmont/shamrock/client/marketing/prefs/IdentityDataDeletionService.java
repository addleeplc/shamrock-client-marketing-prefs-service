/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.client.marketing.prefs;

import com.google.common.eventbus.Subscribe;
import com.haulmont.shamrock.client.marketing.prefs.eventbus.IdentityDataDeletionEventBus;
import com.haulmont.shamrock.client.marketing.prefs.model.ClientId;
import com.haulmont.shamrock.client.marketing.prefs.mq.IdentityDataDeletionMessagingService;
import com.haulmont.shamrock.client.marketing.prefs.mq.dto.*;
import org.picocontainer.annotations.Component;
import org.picocontainer.annotations.Inject;
import org.slf4j.Logger;

import java.util.UUID;

@Component
public class IdentityDataDeletionService {

    private static final String TASK_ID = "delete.marketingPreferences";

    @Inject
    private Logger logger;

    @Inject
    private IdentityDataDeletionEventBus identityDeletionEventBus;

    @Inject
    private IdentityDataDeletionMessagingService messagingService;

    @Inject
    private ClientPrefsService clientPrefsService;

    //

    @SuppressWarnings("unused")
    public void start() {
        identityDeletionEventBus.register(this);
    }

    @SuppressWarnings("unused")
    public void stop() {
        identityDeletionEventBus.unregister(this);
    }

    //

    @Subscribe
    protected void handleIdentityDeletionEnqueued(IdentityDeletionEnqueued message) {
        UUID taskId = UUID.randomUUID();

        AbstractIdentityDeletionMessage.Data data = message.getData();
        UUID requestId = data.getIdentityDeletionRequestId();

        messagingService.publish(PersonalDataDeletionEnqueued.build(taskId, TASK_ID, requestId));

        try {
            processIdentityDataDeletion(taskId, data);
        } catch (Throwable e) {
            logger.error(String.format("Deletion task '%s' failed for request '%s'", TASK_ID, requestId), e);

            messagingService.asyncPublish(PersonalDataDeletionFailed.build(taskId, TASK_ID, requestId));
        }
    }

    public void processIdentityDataDeletion(UUID taskId, AbstractIdentityDeletionMessage.Data data) {
        UUID requestId = data.getIdentityDeletionRequestId();

        messagingService.publish(PersonalDataDeletionProcessing.build(taskId, TASK_ID, requestId));
        deleteIdentityData(data);

        messagingService.asyncPublish(PersonalDataDeletionCompleted.build(taskId, TASK_ID, requestId));
    }

    private void deleteIdentityData(AbstractIdentityDeletionMessage.Data data) {
        ClientId clientId = new ClientId();
        clientId.setId(UUID.fromString(data.getIdentity().getId()));

        clientPrefsService.delete(clientId);
        logger.debug("Client marketing preferences were deleted (clientId: {})", clientId);
    }

}
