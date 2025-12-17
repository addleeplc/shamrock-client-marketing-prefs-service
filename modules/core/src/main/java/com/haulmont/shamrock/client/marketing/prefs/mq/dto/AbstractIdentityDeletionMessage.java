/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.client.marketing.prefs.mq.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;
import java.util.UUID;

public class AbstractIdentityDeletionMessage extends AbstractMessage<AbstractIdentityDeletionMessage.Data> {
    public static class Data {

        @JsonProperty("identity")
        private Identity identity;

        @JsonProperty("identity_deletion_request_id")
        private UUID identityDeletionRequestId;

        @JsonProperty("ignore_personal_data_deletion_tasks")
        private Collection<String> ignorePersonalDataDeletionTasks;

        public Identity getIdentity() {
            return identity;
        }

        public void setIdentity(Identity identity) {
            this.identity = identity;
        }

        public UUID getIdentityDeletionRequestId() {
            return identityDeletionRequestId;
        }

        public void setIdentityDeletionRequestId(UUID identityDeletionRequestId) {
            this.identityDeletionRequestId = identityDeletionRequestId;
        }

        public Collection<String> getIgnorePersonalDataDeletionTasks() {
            return ignorePersonalDataDeletionTasks;
        }

        public void setIgnorePersonalDataDeletionTasks(Collection<String> ignorePersonalDataDeletionTasks) {
            this.ignorePersonalDataDeletionTasks = ignorePersonalDataDeletionTasks;
        }

        public static class Identity {

            @JsonProperty("id")
            public String id;

            @JsonProperty("pid")
            public String pid;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getPid() {
                return pid;
            }

            public void setPid(String pid) {
                this.pid = pid;
            }

        }
    }
}
