/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.client.marketing.prefs.model;

import java.util.Objects;
import java.util.UUID;

public class ClientId {
    private UUID id;
    private String uid;
    private String email;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        if (id != null) {
            return id.toString();
        }

        return uid == null ? email : uid;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ClientId)) return false;

        ClientId clientId = (ClientId) object;
        if (getId() != null || clientId.getId() != null) {
            return Objects.equals(getId(), clientId.getId());
        }

        if (getUid() != null || clientId.getUid() != null) {
            return Objects.equals(getUid(), clientId.getUid());
        }

        return Objects.equals(getEmail(), clientId.getEmail());
    }

    @Override
    public int hashCode() {
        if (getId() != null) {
            return getId().hashCode();
        }

        return getUid() != null ? getUid().hashCode() : Objects.hash(getEmail());
    }
}
