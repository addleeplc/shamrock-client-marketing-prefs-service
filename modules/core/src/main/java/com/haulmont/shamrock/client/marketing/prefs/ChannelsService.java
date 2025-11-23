package com.haulmont.shamrock.client.marketing.prefs;

import com.haulmont.monaco.ServiceException;
import com.haulmont.monaco.response.ErrorCode;
import com.haulmont.shamrock.client.marketing.prefs.cache.ChannelCache;
import com.haulmont.shamrock.client.marketing.prefs.db.ChannelsRepository;
import com.haulmont.shamrock.client.marketing.prefs.dto.Channel;
import com.haulmont.shamrock.client.marketing.prefs.dto.Identifier;
import com.haulmont.shamrock.client.marketing.prefs.model.ModelInstanceId;
import com.haulmont.shamrock.client.marketing.prefs.mq.ModelEventsMessagingService;
import com.haulmont.shamrock.client.marketing.prefs.mq.dto.ChannelCreated;
import com.haulmont.shamrock.client.marketing.prefs.mq.dto.ChannelDeleted;
import com.haulmont.shamrock.client.marketing.prefs.mq.dto.ChannelUpdated;
import com.haulmont.shamrock.client.marketing.prefs.utils.ChannelUtils;
import com.haulmont.shamrock.client.marketing.prefs.utils.IdUtils;
import org.picocontainer.annotations.Component;
import org.picocontainer.annotations.Inject;

import java.util.Collection;
import java.util.Optional;

@Component
public class ChannelsService extends AbstractCachedService<ModelInstanceId, com.haulmont.shamrock.client.marketing.prefs.model.Channel, ChannelCache> {
    @Inject
    private ChannelsRepository channelsRepository;

    @Inject
    private ModelEventsMessagingService modelEventsMessagingService;


    public ChannelsService(ChannelCache cache) {
        super(cache);
    }

    public Collection<Channel> getAll() {
        return ChannelUtils.convert(channelsRepository.getAll());
    }

    public Channel get(Identifier id) {
        return tryGet(id).orElseThrow(() -> Errors.CHANNEL_NOT_FOUND);
    }

    public Channel getOrDefault(Identifier id, Channel defaultValue) {
        return tryGet(id).orElse(defaultValue);
    }

    public Optional<Channel> tryGet(Identifier id) {
        ModelInstanceId rowId = IdUtils.convert(id);
        return Optional.ofNullable(cache.get(rowId)).map(ChannelUtils::convert);
    }

    public Channel addChannel(Channel channel) {
        if (channel == null) {
            throw new IllegalArgumentException("Channel should be specified");
        }

        com.haulmont.shamrock.client.marketing.prefs.model.Channel newChannel = ChannelUtils.convert(channel);

        if (!channelsRepository.add(newChannel)) {
            throw new ServiceException(ErrorCode.SERVER_ERROR, "Fail to insert channel " + channel);
        }

        Channel res = ChannelUtils.convert(newChannel);

        modelEventsMessagingService.publish(ChannelCreated.create(res, ChannelCreated::new));

        return res;
    }

    public Channel updateChannel(Identifier id, Channel channel) {
        ModelInstanceId channelId = IdUtils.convert(id);

        doCacheMutatingAction(channelId, () -> {
            if (channelsRepository.update(channelId, ChannelUtils.convert(channel)) <= 0) {
                throw Errors.CHANNEL_NOT_FOUND;
            }
        });

        Channel res = get(id);

        modelEventsMessagingService.publish(ChannelUpdated.create(res, ChannelUpdated::new));

        return res;
    }

    public void delete(Identifier id) {
        ModelInstanceId rowId = IdUtils.convert(id);
        Channel channel = get(id);

        doCacheMutatingAction(rowId, () -> {
            if (!channelsRepository.delete(rowId)) {
                throw Errors.CHANNEL_NOT_FOUND;
            }
        });

        modelEventsMessagingService.publish(ChannelDeleted.create(channel, ChannelDeleted::new));
    }

    public static class Errors {
        public static final RuntimeException CHANNEL_NOT_FOUND = new ServiceException(ErrorCode.NOT_FOUND, "Channel not found");
    }
}
