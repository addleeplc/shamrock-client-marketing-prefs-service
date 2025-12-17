package com.haulmont.shamrock.client.marketing.prefs.mq;

import com.haulmont.monaco.AppContext;
import com.haulmont.monaco.executors.ExecutorsService;
import com.haulmont.monaco.mq.MqClient;
import com.haulmont.monaco.rabbit.mq.RabbitMqClient;
import com.haulmont.shamrock.client.marketing.prefs.ServiceConfiguration;
import com.haulmont.shamrock.client.marketing.prefs.mq.dto.AbstractPersonalDataDeletionMessage;
import org.picocontainer.annotations.Component;
import org.picocontainer.annotations.Inject;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

@Component
public class ClientDataIdentityPublishService {
    @Inject
    private Logger log;

    @Inject
    private ServiceConfiguration configuration;

    private final ExecutorService executorService;


    public ClientDataIdentityPublishService(ExecutorsService service) {
        this.executorService = service.get(getClass().getSimpleName());
    }

    public void publish(AbstractPersonalDataDeletionMessage message) {
        MqClient<?> mqClient = getMqClient();

        try {
            mqClient.publish(configuration.getMQPersonalDataDeletionExchange(), message.getClass().getSimpleName(), message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void publishAsync(AbstractPersonalDataDeletionMessage message) {
        executorService.execute(() -> {
            try {
                publish(message);
            } catch (Throwable e) {
                log.error(e.getMessage(), e);
            }
        });
    }

    private RabbitMqClient getMqClient() {
        return AppContext.getResources().get(configuration.getMQPersonalDataDeletionServerName(), RabbitMqClient.class);
    }
}
