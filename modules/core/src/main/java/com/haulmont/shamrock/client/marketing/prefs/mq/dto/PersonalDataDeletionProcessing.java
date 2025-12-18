package com.haulmont.shamrock.client.marketing.prefs.mq.dto;

import org.joda.time.DateTime;

import java.util.UUID;

public class PersonalDataDeletionProcessing extends AbstractPersonalDataDeletionMessage {

    public static PersonalDataDeletionProcessing build(UUID taskId, String task, UUID requestId) {
        IdentityDataDeletionTransaction transaction = new IdentityDataDeletionTransaction();
        transaction.setId(taskId);
        transaction.setTask(task);

        Data data = new Data();
        data.setIdentityDeletionRequestId(requestId);
        data.setTransaction(transaction);

        PersonalDataDeletionProcessing processing = new PersonalDataDeletionProcessing();
        processing.setDate(DateTime.now());
        processing.setData(data);

        return processing;
    }

}
