package com.haulmont.shamrock.client.marketing.prefs.resources.v1;

import com.haulmont.bali.lang.StringUtils;
import com.haulmont.monaco.ServiceException;
import com.haulmont.monaco.response.ErrorCode;
import com.haulmont.shamrock.client.marketing.prefs.dto.Identifier;
import com.haulmont.shamrock.client.marketing.prefs.utils.ParamUtils;

import java.util.UUID;

public abstract class AbstractResource {
    protected Identifier getIdentifier(String input) {
        if (StringUtils.isBlank(input)) {
            throw new ServiceException(ErrorCode.BAD_REQUEST, "An identifier was expected, but the request doesn't have one");
        }

        Identifier id = new Identifier();
        if (ParamUtils.isUUID(input)) {
            id.setId(UUID.fromString(input));
        } else {
            id.setCode(input);
        }

        return id;
    }
}
