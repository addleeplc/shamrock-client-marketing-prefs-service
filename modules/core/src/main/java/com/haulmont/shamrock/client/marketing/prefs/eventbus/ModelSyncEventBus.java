/*
 * Copyright 2008 - 2022 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.client.marketing.prefs.eventbus;

import com.haulmont.monaco.eventbus.AbstractEventBus;
import com.haulmont.monaco.eventbus.Dispatcher;
import com.haulmont.monaco.executors.ExecutorsService;
import org.picocontainer.annotations.Component;

@Component
public class ModelSyncEventBus extends AbstractEventBus {
    public ModelSyncEventBus(ExecutorsService executorsService) {
        super(
                ModelSyncEventBus.class.getSimpleName(),
                executorsService.get(ModelSyncEventBus.class.getSimpleName()),
                Dispatcher.perThreadDispatchQueue()
        );
    }
}
