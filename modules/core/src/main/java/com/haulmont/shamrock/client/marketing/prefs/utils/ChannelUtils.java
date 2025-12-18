package com.haulmont.shamrock.client.marketing.prefs.utils;

import com.haulmont.shamrock.client.marketing.prefs.model.Channel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class ChannelUtils {
    private ChannelUtils() {
    }

    public static Channel convert(com.haulmont.shamrock.client.marketing.prefs.storage.model.Channel channel) {
        if (channel == null) {
            return null;
        }

        Channel res = new Channel();

        res.setId(channel.getId());
        res.setCode(channel.getCode());
        res.setName(channel.getName());

        return res;
    }

    public static Collection<Channel> convert(Collection<com.haulmont.shamrock.client.marketing.prefs.storage.model.Channel> channels) {
        if (channels == null) {
            return null;
        }

        List<Channel> res = new ArrayList<>(channels.size());

        for (com.haulmont.shamrock.client.marketing.prefs.storage.model.Channel channel : channels) {
            res.add(convert(channel));
        }

        return res;
    }

    public static com.haulmont.shamrock.client.marketing.prefs.storage.model.Channel convert(Channel channel) {
        if (channel == null) {
            return null;
        }

        com.haulmont.shamrock.client.marketing.prefs.storage.model.Channel res = new com.haulmont.shamrock.client.marketing.prefs.storage.model.Channel();

        res.setId(channel.getId());
        res.setCode(channel.getCode());
        res.setName(channel.getName());

        return res;
    }
}
