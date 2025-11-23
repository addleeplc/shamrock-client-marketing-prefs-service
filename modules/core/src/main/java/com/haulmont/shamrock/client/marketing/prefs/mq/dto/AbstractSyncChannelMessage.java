/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.client.marketing.prefs.mq.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.haulmont.shamrock.client.marketing.prefs.dto.Channel;

import java.util.function.Supplier;

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class AbstractSyncChannelMessage extends AbstractSyncModelMessage<AbstractSyncChannelMessage.Data> {
    public static <R extends AbstractSyncChannelMessage> R create(Channel channel, Supplier<R> constructor) {
        R res = constructor.get();

        Data data = new Data();
        data.setChannel(channel);

        res.setData(data);

        return res;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Data extends AbstractSyncModelMessage.Data {
        @JsonProperty("channel")
        private Channel channel;

        public Channel getChannel() {
            return channel;
        }

        public void setChannel(Channel channel) {
            this.channel = channel;
        }
    }
}
