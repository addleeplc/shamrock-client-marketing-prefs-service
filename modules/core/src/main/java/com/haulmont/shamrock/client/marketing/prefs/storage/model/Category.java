/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.client.marketing.prefs.storage.model;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class Category extends ModelInstanceId {
    private DateTime createTs;
    private String createdBy;
    private DateTime updateTs;
    private String updatedBy;
    private String name;
    private String description;
    private UUID parentCategoryId;

    private Collection<Category> children = new ArrayList<>();
    private Collection<CategoryChannel> channels = new ArrayList<>();

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

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public DateTime getUpdateTs() {
        return updateTs;
    }

    public void setUpdateTs(DateTime updateTs) {
        this.updateTs = updateTs;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getParentCategoryId() {
        return parentCategoryId;
    }

    public void setParentCategoryId(UUID parentCategoryId) {
        this.parentCategoryId = parentCategoryId;
    }

    public Collection<Category> getChildren() {
        return children;
    }

    public void setChildren(Collection<Category> children) {
        this.children = children;
    }

    public Collection<CategoryChannel> getChannels() {
        return channels;
    }

    public void setChannels(Collection<CategoryChannel> channels) {
        this.channels = channels;
    }
}
