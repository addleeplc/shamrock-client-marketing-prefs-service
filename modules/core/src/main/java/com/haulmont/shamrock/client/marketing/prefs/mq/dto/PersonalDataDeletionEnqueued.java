/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.client.marketing.prefs.mq.dto;


import org.joda.time.DateTime;

import java.util.UUID;

public class PersonalDataDeletionEnqueued extends AbstractPersonalDataDeletionMessage {

    public static PersonalDataDeletionEnqueued build(UUID taskId, String task, UUID requestId) {
        IdentityDataDeletionTransaction transaction = new IdentityDataDeletionTransaction();
        transaction.setId(taskId);
        transaction.setTask(task);

        Data data = new Data();
        data.setIdentityDeletionRequestId(requestId);
        data.setTransaction(transaction);

        PersonalDataDeletionEnqueued enqueued = new PersonalDataDeletionEnqueued();
        enqueued.setDate(DateTime.now());
        enqueued.setData(data);

        return enqueued;
    }

}
