/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.client.marketing.prefs.resources.internal;

import com.haulmont.monaco.AppContext;
import com.haulmont.monaco.model.cache.CacheManagement;
import com.haulmont.monaco.response.ErrorCode;
import com.haulmont.monaco.response.Response;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/internal")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public class CachesResources {

    @GET
    @Path("/caches/invalidate-all")
    public Response invalidateAll() {
        return __invalidateAll();
    }

    @POST
    @Path("/caches/invalidate-all")
    public Response postInvalidateAll() {
        return __invalidateAll();
    }

    private static Response __invalidateAll() {
        AppContext.getBeans(CacheManagement.class).forEach(CacheManagement::invalidateAll);
        return new Response(ErrorCode.OK);
    }

}
