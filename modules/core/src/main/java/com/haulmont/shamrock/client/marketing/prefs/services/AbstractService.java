/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.client.marketing.prefs.services;

import com.haulmont.bali.lang.StringUtils;
import com.haulmont.monaco.AppContext;
import com.haulmont.monaco.ServiceException;
import com.haulmont.monaco.config.Properties;
import com.haulmont.monaco.response.ErrorCode;
import com.haulmont.monaco.response.Response;
import com.haulmont.monaco.unirest.ServiceCallUtils;
import com.haulmont.monaco.unirest.UnirestCommand;
import kong.unirest.HttpRequest;
import kong.unirest.HttpResponse;
import kong.unirest.HttpStatus;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public abstract class AbstractService {
    private static final String SERVICES_CONFIG_PATH = "/services";
    private static final Map<String, String> RQ_HEADERS = Map.of(
            "x-api-key", "X-Api-Key"
    );

    protected final String serviceName;

    protected AbstractService(String serviceName) {
        this.serviceName = serviceName;
    }

    protected <T> T getServiceProperty(String name, Class<T> type) {
        final Properties properties = AppContext.getConfig().getProperties(SERVICES_CONFIG_PATH);
        return properties.getProperty(serviceName + "." + name, type);
    }

    protected abstract class Command<T extends Response> extends UnirestCommand<T> {
        public Command(Class<T> responseClass) {
            super(serviceName, responseClass);
        }

        @Override
        protected final HttpRequest<?> createRequest(String s, Path path) {
            HttpRequest<?> request = prepareRequest(s, path);
            request = addHeaders(request);
            return request;
        }

        protected abstract HttpRequest<?> prepareRequest(String url, Path path);

        public <R> R getOne(Set<Integer> notFoundCodes, Function<T, R> extractor) {
            try {
                T response = execute();
                if (response.getCode() != null && notFoundCodes != null && notFoundCodes.contains(response.getCode())) {
                    return null;
                }

                return extractor.apply(response);
            } catch (ServiceException ex) {
                if (ex.getErrorCode().getCode() == ErrorCode.NOT_FOUND.getCode()) {
                    return null;
                }
                throw ex;
            } catch (Throwable t) {
                throw new ServiceException(ErrorCode.FAILED_DEPENDENCY, "Fail to call " + serviceName, t);
            }
        }

        @Override
        protected T handleError(Path path, HttpResponse<T> response) {
            if (response.getStatus() == HttpStatus.NOT_FOUND) {
                throw new ServiceException(ErrorCode.NOT_FOUND);
            }

            return super.handleError(path, response);
        }

        public <R extends List<?>> R getList(Function<T, R> extractor) {
            return ServiceCallUtils.call(() -> this, r -> ServiceCallUtils.extract(r, extractor));
        }

        @SuppressWarnings("unchecked")
        protected <R extends HttpRequest<?>> R addHeaders(R request) {
            final Properties properties = AppContext.getConfig().getProperties(SERVICES_CONFIG_PATH);

            for (String headerName : RQ_HEADERS.keySet()) {
                String headerValue = properties.getProperty(headerName);
                if (StringUtils.isNoneBlank(headerValue)) {
                    request = (R) request.header(headerName, headerValue);
                }
            }

            return request;
        }
    }
}
