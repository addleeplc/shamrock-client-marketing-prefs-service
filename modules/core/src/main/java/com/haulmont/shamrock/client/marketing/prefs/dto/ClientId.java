package com.haulmont.shamrock.client.marketing.prefs.dto;

import com.fasterxml.jackson.annotation.*;
import com.haulmont.bali.lang.StringUtils;
import com.haulmont.shamrock.client.marketing.prefs.jackson.Views;

import java.util.Objects;
import java.util.UUID;

@JsonPropertyOrder({"id", "uid", "email"})
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClientId {
    @JsonProperty("id")
    @JsonView(Views.Store.class)
    protected UUID id;

    @JsonProperty("uid")
    @JsonView(Views.Store.class)
    protected String uid;

    @JsonProperty("email")
    @JsonView(Views.Store.class)
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
        if (getId() != null) {
            return getId().toString();
        }

        if (getUid() != null) {
            return getUid();
        }

        return email;
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

    @JsonIgnore
    public boolean isDefined() {
        return id != null || StringUtils.isNotBlank(uid) || StringUtils.isNotBlank(email);
    }
}
