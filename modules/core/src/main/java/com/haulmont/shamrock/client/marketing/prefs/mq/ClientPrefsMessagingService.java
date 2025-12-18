/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.client.marketing.prefs.mq;

import com.haulmont.monaco.config.ConfigPropertyStorage;
import com.haulmont.monaco.config.annotations.Config;
import com.haulmont.monaco.config.annotations.Property;
import com.haulmont.monaco.executors.ExecutorsService;
import com.haulmont.monaco.mq.messages.AbstractMessage;
import com.haulmont.monaco.rabbit.mq.RabbitMqResourceSupplier;
import com.haulmont.shamrock.client.marketing.prefs.model.Preferences;
import com.haulmont.shamrock.client.marketing.prefs.mq.dto.ClientMarketingPrefsUpdated;
import org.picocontainer.annotations.Component;
import org.picocontainer.annotations.Inject;
import org.slf4j.Logger;

import java.util.concurrent.ExecutorService;

@Component
public class ClientPrefsMessagingService {
    private static final String MQ_SERVER_NAME = Configuration.MQ_CLIENT_MARKETING_PREFERENCES + ".serverName";

    private final ExecutorService executorService;
    private final RabbitMqResourceSupplier rabbitMqSupplier;

    @Inject
    private Logger logger;

    @Inject
    private Configuration configuration;

    public ClientPrefsMessagingService(ExecutorsService service, ConfigPropertyStorage propertyStorage) {
        this.executorService = service.get(getClass().getSimpleName());
        this.rabbitMqSupplier = new RabbitMqResourceSupplier(propertyStorage, MQ_SERVER_NAME);
    }

    public void publish(Preferences preferences) {
        ClientMarketingPrefsUpdated message = new ClientMarketingPrefsUpdated();
        message.setData(preferences);

        publish(message);
    }

    public <T extends AbstractMessage> void publish(T message) {
        executorService.execute(() -> {
            try {
                rabbitMqSupplier.get().publish(configuration.getClientMarketingPreferencesMqExchange(), message.getClass().getSimpleName(), message);
            } catch (Exception e) {
                logger.warn("Couldn't publish message (type: {}, messageId: {})", message.getClass().getSimpleName(), message.getId(), e);
            }
        });
    }

    @Config
    @Component
    public interface Configuration {
        String MQ_CLIENT_MARKETING_PREFERENCES = "mq.clientMarketingPreferences";

        @Property(MQ_CLIENT_MARKETING_PREFERENCES + ".exchange")
        String getClientMarketingPreferencesMqExchange();
    }
}
