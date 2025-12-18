/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.client.marketing.prefs.services.dto.client_registry;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.haulmont.monaco.response.Response;
import com.haulmont.shamrock.client.marketing.prefs.model.Client;

import java.util.List;

public class ClientsResponse extends Response {
    @JsonProperty("clients")
    List<Client> clients;

    public List<Client> getClients() {
        return clients;
    }

    public void setClients(List<Client> clients) {
        this.clients = clients;
    }
}