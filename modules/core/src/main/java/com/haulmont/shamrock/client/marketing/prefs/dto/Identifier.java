package com.haulmont.shamrock.client.marketing.prefs.dto;

import com.fasterxml.jackson.annotation.*;
import com.haulmont.bali.lang.StringUtils;
import com.haulmont.shamrock.client.marketing.prefs.jackson.Views;

import java.util.Objects;
import java.util.UUID;

@JsonPropertyOrder({"id", "code"})
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Identifier {
    @JsonProperty("id")
    @JsonView(Views.Store.class)
    protected UUID id;

    @JsonProperty("code")
    @JsonView(Views.Store.class)
    protected String code;

    public Identifier() {
    }

    public Identifier(String code) {
        this.code = code;
    }

    public Identifier(UUID id) {
        this.id = id;
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

    @JsonIgnore
    public boolean isDefined() {
        return id != null || StringUtils.isNotBlank(code);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Identifier)) return false;
        Identifier identifier = (Identifier) other;
        return getId() == null ?
                Objects.equals(getCode(), identifier.getCode()) :
                Objects.equals(getId(), identifier.getId());
    }

    @Override
    public int hashCode() {
        return getId() == null ?
                Objects.hash(getCode()) :
                Objects.hash(getId());
    }
}
