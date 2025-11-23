package com.haulmont.shamrock.client.marketing.prefs.resources.v1;

import com.haulmont.monaco.ServiceException;
import com.haulmont.monaco.response.ErrorCode;
import com.haulmont.monaco.response.Response;
import com.haulmont.shamrock.client.marketing.prefs.ChannelsService;
import com.haulmont.shamrock.client.marketing.prefs.dto.Channel;
import com.haulmont.shamrock.client.marketing.prefs.dto.ChannelResponse;
import com.haulmont.shamrock.client.marketing.prefs.dto.ChannelsResponse;
import com.haulmont.shamrock.client.marketing.prefs.dto.Identifier;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collection;

@Path("/v1")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public class ChannelsResource extends AbstractResource {
    @Inject
    private ChannelsService channelsService;

    @GET
    @Path("/channels")
    public ChannelsResponse getChannels() {
        Collection<Channel> channels = channelsService.getAll();

        return new ChannelsResponse(channels);
    }

    @GET
    @Path("/channels/{channel}")
    public ChannelResponse getChannel(@PathParam("channel") String channel) {
        Identifier identifier = getIdentifier(channel);
        if (!identifier.isDefined()) {
            throw new ServiceException(ErrorCode.BAD_REQUEST, "No category identifier specified in the request");
        }

        return new ChannelResponse(channelsService.get(identifier));
    }

    @POST
    @Path("/channels")
    public ChannelResponse addChannel(Channel channel) {
        if (channel == null) {
            throw new ServiceException(ErrorCode.BAD_REQUEST, "There is no any channel in the request");
        }

        if (!channel.isDefined()) {
            throw new ServiceException(ErrorCode.BAD_REQUEST, "No category identifier specified in the request");
        }

        Channel newChannel = channelsService.addChannel(channel);

        return new ChannelResponse(newChannel);
    }

    @PATCH
    @Path("/channels/{channel}")
    public ChannelResponse updateChannel(@PathParam("channel") String channelId, Channel channel) {
        if (channel == null) {
            throw new ServiceException(ErrorCode.BAD_REQUEST, "There is no any data to update the channel in the request");
        }

        Identifier identifier = getIdentifier(channelId);
        Channel newChannel = channelsService.updateChannel(identifier, channel);

        return new ChannelResponse(newChannel);
    }

    @DELETE
    @Path("/channels/{channel}")
    public Response deleteChannels(@PathParam("channel") String channel) {
        Identifier identifier = getIdentifier(channel);
        if (!identifier.isDefined()) {
            throw new ServiceException(ErrorCode.BAD_REQUEST, "No category identifier specified in the request");
        }

        channelsService.delete(identifier);

        return new Response(ErrorCode.OK);
    }

    private void validateChannel(Channel channel) {
        if (!channel.isDefined()) {
            throw new ServiceException(ErrorCode.BAD_REQUEST, "Channel should have an identifier: " + channel);
        }
    }
}
