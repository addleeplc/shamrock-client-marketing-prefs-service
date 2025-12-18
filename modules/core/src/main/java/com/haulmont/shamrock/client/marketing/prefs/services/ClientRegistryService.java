/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.client.marketing.prefs.services;

import com.haulmont.bali.lang.StringUtils;
import com.haulmont.monaco.response.ErrorCode;
import com.haulmont.monaco.unirest.ServiceCallUtils;
import com.haulmont.shamrock.client.marketing.prefs.model.Client;
import com.haulmont.shamrock.client.marketing.prefs.services.dto.client_registry.ClientResponse;
import com.haulmont.shamrock.client.marketing.prefs.services.dto.client_registry.ClientsResponse;
import kong.unirest.HttpRequest;
import org.picocontainer.annotations.Component;

import java.util.*;

@Component
public class ClientRegistryService extends AbstractService {
    public ClientRegistryService() {
        super("shamrock-client-registry-service");
    }

    public Client getById(UUID clientId) {
        if (clientId == null) {
            return null;
        }

        return new GetClientByIdCommand(clientId).getOne(
                Set.of(ErrorCode.NOT_FOUND.getCode(), 100),
                response -> ServiceCallUtils.extract(response, ClientResponse::getClient)
        );
    }

    public List<Client> searchByEmail(String email) {
        if (StringUtils.isBlank(email)) {
            return Collections.emptyList();
        }

        return new SearchClientsByEmailCommand(email).getList(
                r -> ServiceCallUtils.extract(r, ClientsResponse::getClients)
        );
    }

    public class GetClientByIdCommand extends Command<ClientResponse> {
        private final UUID clientId;

        public GetClientByIdCommand(UUID clientId) {
            super(ClientResponse.class);

            this.clientId = clientId;
        }

        @Override
        protected HttpRequest<?> prepareRequest(String url, Path path) {
            return get(url, path);
        }

        @Override
        protected Path getPath() {
            return new Path("/clients/{client_id}", Map.of("client_id", clientId));
        }
    }

    public class SearchClientsByEmailCommand extends Command<ClientsResponse> {
        private final String email;

        public SearchClientsByEmailCommand(String email) {
            super(ClientsResponse.class);

            this.email = email;
        }

        @Override
        protected HttpRequest<?> prepareRequest(String url, Path path) {
            return get(url, path)
                    .queryString("email", email.toLowerCase())
                    .queryString("status", "ACTIVE");
        }

        @Override
        protected Path getPath() {
            return new Path("/clients/search");
        }
    }
}
