/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.client.marketing.prefs.mq.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.haulmont.bali.jackson.joda.DateTimeAdapter;
import org.joda.time.DateTime;

import java.util.UUID;

public class IdentityDataDeletionTransaction {
    @JsonProperty("id")
    private UUID id;

    @JsonProperty("create_ts")
    @JsonSerialize(using = DateTimeAdapter.Serializer.class)
    @JsonDeserialize(using = DateTimeAdapter.Deserializer.class)
    private DateTime createTs;

    @JsonProperty("app")
    private String app;

    @JsonProperty("task")
    private String task;

    @JsonProperty("status")
    private Status status;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public DateTime getCreateTs() {
        return createTs;
    }

    public void setCreateTs(DateTime createTs) {
        this.createTs = createTs;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public static class Status {

        @JsonProperty("code")
        private Code code;

        @JsonProperty("date")
        @JsonSerialize(using = DateTimeAdapter.Serializer.class)
        @JsonDeserialize(using = DateTimeAdapter.Deserializer.class)
        private DateTime date;

        public Code getCode() {
            return code;
        }

        public void setCode(Code code) {
            this.code = code;
        }

        public DateTime getDate() {
            return date;
        }

        public void setDate(DateTime date) {
            this.date = date;
        }

        public enum Code {
            ENQUEUED,
            PROCESSING,
            COMPLETED,
            FAILED
        }
    }
}
