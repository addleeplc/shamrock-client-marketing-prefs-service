package com.haulmont.shamrock.client.marketing.prefs;

import com.haulmont.monaco.config.annotations.Config;
import com.haulmont.monaco.config.annotations.Property;
import org.picocontainer.annotations.Component;

@Config
@Component
public interface ServiceConfiguration {

    @Property("caches.channels.expireAfterWriteSeconds")
    Long getChannelsCacheExpirySeconds();

    @Property("caches.categories.expireAfterWriteSeconds")
    Long getCategoriesCacheExpirySeconds();

    @Property("caches.clientsPrefs.expireAfterWriteSeconds")
    Long getClientsPrefsCacheExpirySeconds();

    @Property("caches.clientsPrefs.maxSize")
    Long getClientsPrefsCacheMaxSize();

    @Property("caches.clients.byId.expireAfterWriteSeconds")
    Long getClientsCacheByIdExpirySeconds();

    @Property("caches.clients.byId.maxSize")
    Long getClientsCacheByIdMaxSize();

    @Property("caches.clients.byEmail.expireAfterWriteSeconds")
    Long getClientsCacheByEmailExpirySeconds();

    @Property("caches.client.byEmail.maxSize")
    Long getClientsCacheByEmailMaxSize();

    @Property("prefs.optIn.byDefault")
    Boolean getPrefsOptInByDefault();

    @Property("prefs.channels.optIn.byDefault")
    Boolean getPrefsChannelsOptInByDefault();
}
