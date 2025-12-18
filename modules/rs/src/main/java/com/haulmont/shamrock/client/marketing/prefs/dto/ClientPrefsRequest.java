package com.haulmont.shamrock.client.marketing.prefs.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.haulmont.shamrock.client.marketing.prefs.model.ClientId;
import com.haulmont.shamrock.client.marketing.prefs.model.Preferences;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;

@JsonAutoDetect(fieldVisibility = ANY, getterVisibility = NONE, setterVisibility = NONE)
@JsonPropertyOrder({"client", "prefs"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClientPrefsRequest {
    @JsonProperty("client")
    private ClientId clientId;

    @JsonProperty("prefs")
    private Preferences preferences;

    public ClientId getClientId() {
        return clientId;
    }

    public void setClientId(ClientId clientId) {
        this.clientId = clientId;
    }

    public Preferences getPreferences() {
        return preferences;
    }

    public void setPreferences(Preferences preferences) {
        this.preferences = preferences;
    }
}
