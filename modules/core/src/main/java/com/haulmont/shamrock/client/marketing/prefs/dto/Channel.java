package com.haulmont.shamrock.client.marketing.prefs.dto;

import com.fasterxml.jackson.annotation.*;
import com.haulmont.shamrock.client.marketing.prefs.jackson.Views;

@JsonPropertyOrder({"id", "code", "name"})
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Channel extends Identifier {
    @JsonProperty("name")
    @JsonView(Views.Store.class)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
