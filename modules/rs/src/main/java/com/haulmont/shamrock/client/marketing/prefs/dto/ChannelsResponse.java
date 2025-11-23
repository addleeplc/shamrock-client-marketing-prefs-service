package com.haulmont.shamrock.client.marketing.prefs.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.haulmont.monaco.response.ErrorCode;
import com.haulmont.monaco.response.Response;

import java.util.Collection;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;

@JsonAutoDetect(fieldVisibility = ANY, getterVisibility = NONE, setterVisibility = NONE)
@JsonPropertyOrder({"code", "message", "channels"})
public class ChannelsResponse extends Response {
    @JsonProperty("channels")
    private Collection<Channel> channels;

    public ChannelsResponse(Collection<Channel> channels) {
        super(ErrorCode.OK);

        this.channels = channels;
    }

    public Collection<Channel> getChannels() {
        return channels;
    }

    public void setChannels(Collection<Channel> channels) {
        this.channels = channels;
    }
}
