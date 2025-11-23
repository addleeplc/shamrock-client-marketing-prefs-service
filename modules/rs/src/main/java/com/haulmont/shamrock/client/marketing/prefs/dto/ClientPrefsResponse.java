package com.haulmont.shamrock.client.marketing.prefs.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;

@JsonAutoDetect(fieldVisibility = ANY, getterVisibility = NONE, setterVisibility = NONE)
@JsonPropertyOrder({"code", "message", "client", "prefs"})
public class ClientPrefsResponse extends PrefsResponse {
    @JsonProperty("client")
    private ClientId clientId;

    public ClientPrefsResponse() {
        super(null);
    }

    public ClientPrefsResponse(ClientPrefs clientPrefs) {
        super(clientPrefs.getPreferences());

        this.clientId = new ClientId();

        this.clientId.setId(clientPrefs.getId());
        this.clientId.setUid(clientPrefs.getUid());
        this.clientId.setEmail(clientPrefs.getEmail());
    }

    public ClientId getClientId() {
        return clientId;
    }

    public void setClientId(ClientId clientId) {
        this.clientId = clientId;
    }
}
