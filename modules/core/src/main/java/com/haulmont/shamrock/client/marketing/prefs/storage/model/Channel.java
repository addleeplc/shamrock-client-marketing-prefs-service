/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.client.marketing.prefs.storage.model;

import org.joda.time.DateTime;

public class Channel extends ModelInstanceId {
    private DateTime createTs;
    private String createdBy;
    private String name;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
