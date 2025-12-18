package com.haulmont.shamrock.client.marketing.prefs.mq;

import com.haulmont.monaco.config.ConfigPropertyStorage;
import com.haulmont.monaco.config.annotations.Config;
import com.haulmont.monaco.config.annotations.Property;
import com.haulmont.monaco.executors.ExecutorsService;
import com.haulmont.monaco.rabbit.mq.RabbitMqClient;
import com.haulmont.monaco.rabbit.mq.RabbitMqResourceSupplier;
import com.haulmont.shamrock.client.marketing.prefs.ClientMarketingPrefsModule;
import com.haulmont.shamrock.client.marketing.prefs.mq.dto.AbstractPersonalDataDeletionMessage;
import org.picocontainer.annotations.Component;
import org.picocontainer.annotations.Inject;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

@Component
public class IdentityDataDeletionMessagingService {

    @Inject
    private Logger logger;

    @Inject
    private Configuration configuration;

    //

    private final ExecutorService executorService;
    private final Supplier<RabbitMqClient> mq;

    //

    public IdentityDataDeletionMessagingService(ConfigPropertyStorage propertyStorage, ExecutorsService service) {
        this.executorService = service.get(getClass().getSimpleName());
        this.mq = new RabbitMqResourceSupplier(propertyStorage, ClientMarketingPrefsModule.IDENTITY_DELETION_MQ + ".serverName", "shamrock-mq");
    }

    public void publish(AbstractPersonalDataDeletionMessage message) {
        try {
            mq.get().publish(configuration.getExchangeName(), message.getClass().getSimpleName(), message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void asyncPublish(AbstractPersonalDataDeletionMessage message) {
        executorService.execute(() -> {
            try {
                publish(message);
            } catch (Throwable e) {
                logger.error(e.getMessage(), e);
            }
        });
    }

    //

    @Config
    @Component
    public interface Configuration {
        @Property(ClientMarketingPrefsModule.IDENTITY_DELETION_MQ + ".exchange")
        String getExchangeName();
    }

}
