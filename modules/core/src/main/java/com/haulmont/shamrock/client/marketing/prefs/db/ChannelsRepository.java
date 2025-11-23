package com.haulmont.shamrock.client.marketing.prefs.db;

import com.haulmont.shamrock.client.marketing.prefs.db.mybatis.ClientMarketingPrefsSqlSessionFactory;
import com.haulmont.shamrock.client.marketing.prefs.db.mybatis.Constants;
import com.haulmont.shamrock.client.marketing.prefs.model.Channel;
import com.haulmont.shamrock.client.marketing.prefs.model.ModelInstanceId;
import org.apache.ibatis.session.SqlSession;
import org.joda.time.DateTime;
import org.picocontainer.annotations.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class ChannelsRepository extends AbstractRepository<ClientMarketingPrefsSqlSessionFactory> {
    public ChannelsRepository(ClientMarketingPrefsSqlSessionFactory sqlSessionFactory) {
        super(sqlSessionFactory);
    }

    public List<Channel> getAll() {
        return new LoadChannelsCommand().execute();
    }

    public Channel get(ModelInstanceId id) {
        return new LoadChannelCommand(id).execute();
    }

    public boolean add(Channel channel) {
        return new InsertChannelCommand(channel).execute() > 0;
    }

    public boolean delete(ModelInstanceId id) {
        return new DeleteChannelCommand(id).execute() > 0;
    }

    public int update(ModelInstanceId id, Channel channel) {
        if (channel == null || (channel.getCode() == null && channel.getName() == null)) {
            throw new IllegalArgumentException("No real update found");
        }

        return new UpdateChannelCommand(id, channel).execute();
    }

    private class LoadChannelsCommand extends Command<List<Channel>> {
        LoadChannelsCommand() {
            super("getChannels");
        }

        @Override
        protected List<Channel> __execute(SqlSession sqlSession) {
            return sqlSession.selectList(getName());
        }
    }

    private class LoadChannelCommand extends Command<Channel> {
        private final ModelInstanceId id;

        LoadChannelCommand(ModelInstanceId id) {
            super("getChannel");

            this.id = id;
        }

        @Override
        protected Channel __execute(SqlSession sqlSession) {
            return sqlSession.selectOne(getName(), id);
        }
    }

    private class InsertChannelCommand extends Command<Integer> {
        private final Channel channel;

        InsertChannelCommand(Channel channel) {
            super("addChannel");

            if (channel.getId() == null) {
                channel.setId(UUID.randomUUID());
            }

            channel.setCreateTs(DateTime.now());
            channel.setCreatedBy(Constants.USER_NAME);

            this.channel = channel;
        }

        @Override
        protected Integer __execute(SqlSession sqlSession) {
            return sqlSession.insert(getName(), channel);
        }
    }

    private class UpdateChannelCommand extends Command<Integer> {
        private final ModelInstanceId id;
        private final Channel patch;

        UpdateChannelCommand(ModelInstanceId id, Channel patch) {
            super("updateChannel");

            this.id = id;
            this.patch = patch;
        }

        @Override
        protected Integer __execute(SqlSession sqlSession) {
            Map<String, Object> params = Map.of(
                    "key", id,
                    "value", patch
            );

            return sqlSession.update(getName(), params);
        }
    }

    private class DeleteChannelCommand extends Command<Integer> {
        private final ModelInstanceId id;

        DeleteChannelCommand(ModelInstanceId id) {
            super("deleteChannel");

            this.id = id;
        }

        @Override
        protected Integer __execute(SqlSession sqlSession) {
            return sqlSession.delete(getName(), id);
        }
    }
}
