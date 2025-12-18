package com.haulmont.shamrock.client.marketing.prefs.resources.v1;

import com.haulmont.monaco.ServiceException;
import com.haulmont.monaco.response.ErrorCode;
import com.haulmont.monaco.response.Response;
import com.haulmont.shamrock.client.marketing.prefs.ClientPrefsService;
import com.haulmont.shamrock.client.marketing.prefs.dto.*;
import com.haulmont.shamrock.client.marketing.prefs.model.ClientId;
import com.haulmont.shamrock.client.marketing.prefs.model.ClientPrefs;
import com.haulmont.shamrock.client.marketing.prefs.utils.ParamUtils;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

@Path("/v1")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public class ClientPrefsResource {
    @Inject
    private ClientPrefsService clientPrefsService;

    @GET
    @Path("/clients/prefs")
    public PrefsResponse getPrefs() {
        return new PrefsResponse(clientPrefsService.get());
    }

    @GET
    @Path("/clients/{client}/prefs")
    public PrefsResponse getClientPrefs(@PathParam("client") String client, @QueryParam("channel") String[] channels) {
        ClientId clientId = getClientId(client);
        if (!clientId.isDefined()) {
            throw new ServiceException(ErrorCode.BAD_REQUEST, "No client identifier specified in the request");
        }

        ClientPrefs res = clientPrefsService.get(clientId, channels == null || channels.length == 0 ? null : Arrays.stream(channels).collect(Collectors.toSet()));

        return new PrefsResponse(res == null ? null : res.getPreferences());
    }

    @POST
    @Path("/clients/prefs")
    public ClientPrefsResponse addClientPrefs(ClientPrefsRequest clientPrefsRequest) {
        if (clientPrefsRequest == null) {
            throw new ServiceException(ErrorCode.BAD_REQUEST, "Empty request not allowed");
        }
        if (clientPrefsRequest.getClientId() == null || !clientPrefsRequest.getClientId().isDefined()) {
            throw new ServiceException(ErrorCode.BAD_REQUEST, "There is no any client data in the request");
        }
        if (clientPrefsRequest.getPreferences() == null) {
            throw new ServiceException(ErrorCode.BAD_REQUEST, "There is no any preferences in the request");
        }

        ClientPrefs clientPrefs = clientPrefsService.add(
                clientPrefsRequest.getClientId(), clientPrefsRequest.getPreferences()
        );
        return clientPrefs == null ? new ClientPrefsResponse() : new ClientPrefsResponse(clientPrefs);
    }

    @POST
    @Path("/clients/{client}/prefs/reset")
    public PrefsResponse resetClientPrefs(@PathParam("client") String client) {
        ClientId clientId = getClientId(client);
        if (!clientId.isDefined()) {
            throw new ServiceException(ErrorCode.BAD_REQUEST, "No client identifier specified in the request");
        }

        ClientPrefs clientPrefs = clientPrefsService.reset(clientId);

        return new PrefsResponse(clientPrefs.getPreferences());
    }

    @PATCH
    @Path("/clients/{client}/prefs")
    public PrefsResponse updateClientPrefs(@PathParam("client") String client, ClientPrefsRequest request) {
        ClientId clientId = getClientId(client);
        if (!clientId.isDefined()) {
            throw new ServiceException(ErrorCode.BAD_REQUEST, "No client identifier specified in the request");
        }

        if (request == null || request.getPreferences() == null && request.getClientId() == null) {
            throw new ServiceException(ErrorCode.BAD_REQUEST, "No effective update found in the request");
        }

        ClientPrefs clientPrefs = clientPrefsService.update(clientId, request.getPreferences());

        return new PrefsResponse(clientPrefs.getPreferences());
    }

    @DELETE
    @Path("/clients/{client}/prefs")
    public Response deleteClientPrefs(@PathParam("client") String client) {
        ClientId clientId = getClientId(client);
        if (!clientId.isDefined()) {
            throw new ServiceException(ErrorCode.BAD_REQUEST, "No client identifier specified in the request");
        }

        clientPrefsService.delete(clientId);

        return new Response(ErrorCode.OK);
    }

    protected ClientId getClientId(String input) {
        ClientId clientId = new ClientId();

        if (ParamUtils.isUUID(input)) {
            clientId.setId(UUID.fromString(input));
        } else if (ParamUtils.isEmail(input)) {
            clientId.setEmail(input);
        } else {
            clientId.setUid(input);
        }

        return clientId;
    }
}
