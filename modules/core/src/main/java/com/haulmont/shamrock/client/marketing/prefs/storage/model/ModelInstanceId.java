/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.client.marketing.prefs.storage.model;

import java.util.Objects;
import java.util.UUID;

public class ModelInstanceId {
    private UUID id;

    private String code;

    public ModelInstanceId() {
    }

    public ModelInstanceId(UUID id) {
        this.id = id;
    }

    public ModelInstanceId(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return id == null ? code : id.toString();
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ModelInstanceId)) return false;
        ModelInstanceId modelInstanceId = (ModelInstanceId) object;
        return getId() == null ?
                Objects.equals(getCode(), modelInstanceId.getCode()) :
                Objects.equals(getId(), modelInstanceId.getId());
    }

    @Override
    public int hashCode() {
        return getId() == null ?
                Objects.hash(getCode()) :
                Objects.hash(getId());
    }
}
