/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.client.marketing.prefs.storage.model;

import org.joda.time.DateTime;

import java.util.UUID;

public class CategoryChannel extends ModelInstanceId {
    private DateTime createTs;
    private String createdBy;
    private UUID categoryId;
    private Boolean optIn;
    private Boolean editable;

    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public DateTime getCreateTs() {
        return createTs;
    }

    public void setCreateTs(DateTime createTs) {
        this.createTs = createTs;
    }

    public Boolean getOptIn() {
        return optIn;
    }

    public void setOptIn(Boolean optIn) {
        this.optIn = optIn;
    }

    public Boolean getEditable() {
        return editable;
    }

    public void setEditable(Boolean editable) {
        this.editable = editable;
    }
}
