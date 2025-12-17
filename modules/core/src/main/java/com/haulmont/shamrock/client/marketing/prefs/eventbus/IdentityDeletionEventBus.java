/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.client.marketing.prefs.eventbus;

import com.haulmont.monaco.eventbus.AbstractEventBus;
import com.haulmont.monaco.eventbus.DirectExecutor;
import com.haulmont.monaco.eventbus.Dispatcher;
import org.picocontainer.annotations.Component;

@Component
public class IdentityDeletionEventBus extends AbstractEventBus {
    public IdentityDeletionEventBus() {
        super(IdentityDeletionEventBus.class.getSimpleName(), DirectExecutor.INSTANCE, Dispatcher.perThreadDispatchQueue());
    }
}
