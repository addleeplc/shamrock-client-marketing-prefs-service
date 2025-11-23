/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.client.marketing.prefs.utils;

import com.haulmont.shamrock.client.marketing.prefs.dto.Identifier;
import com.haulmont.shamrock.client.marketing.prefs.model.ModelInstanceId;

public final class IdUtils {
    private IdUtils() {
    }

    public static ModelInstanceId convert(Identifier id) {
        if (id == null) {
            return null;
        }

        ModelInstanceId res = new ModelInstanceId();

        res.setId(id.getId());
        res.setCode(id.getCode());

        return res;
    }
}
