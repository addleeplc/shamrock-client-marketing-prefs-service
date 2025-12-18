/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.client.marketing.prefs.model;

public class ClientPrefs extends ClientId {
    private Preferences preferences;

    public ClientPrefs(ClientId clientId, Preferences preferences) {
        this.setId(clientId.getId());
        this.setUid(clientId.getUid());
        this.setEmail(clientId.getEmail());

        this.preferences = preferences;
    }

    public Preferences getPreferences() {
        return preferences;
    }

    public void setPreferences(Preferences preferences) {
        this.preferences = preferences;
    }
}
