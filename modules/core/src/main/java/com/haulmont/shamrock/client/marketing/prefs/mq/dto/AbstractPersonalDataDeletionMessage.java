/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.client.marketing.prefs.mq.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class AbstractPersonalDataDeletionMessage extends AbstractMessage<AbstractPersonalDataDeletionMessage.Data> {
    public static class Data {

        @JsonProperty("identity_deletion_request_id")
        private UUID identityDeletionRequestId;

        @JsonProperty("transaction")
        private IdentityDataDeletionTransaction transaction;

        public UUID getIdentityDeletionRequestId() {
            return identityDeletionRequestId;
        }

        public void setIdentityDeletionRequestId(UUID identityDeletionRequestId) {
            this.identityDeletionRequestId = identityDeletionRequestId;
        }

        public IdentityDataDeletionTransaction getTransaction() {
            return transaction;
        }

        public void setTransaction(IdentityDataDeletionTransaction transaction) {
            this.transaction = transaction;
        }
    }
}
