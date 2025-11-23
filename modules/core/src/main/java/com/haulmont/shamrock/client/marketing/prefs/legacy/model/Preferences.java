/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.client.marketing.prefs.legacy.model;

import com.haulmont.shamrock.client.marketing.prefs.model.ClientId;

public class Preferences extends ClientId {
    private String prefs;
    private Boolean emailOptOut;
    private Boolean smsOptOut;
    private Boolean callOptOut;
    private Boolean pushOptOut;

    public String getPrefs() {
        return prefs;
    }

    public void setPrefs(String prefs) {
        this.prefs = prefs;
    }

    public Boolean getSmsOptOut() {
        return smsOptOut;
    }

    public void setSmsOptOut(Boolean smsOptOut) {
        this.smsOptOut = smsOptOut;
    }

    public Boolean getEmailOptOut() {
        return emailOptOut;
    }

    public void setEmailOptOut(Boolean emailOptOut) {
        this.emailOptOut = emailOptOut;
    }

    public Boolean getCallOptOut() {
        return callOptOut;
    }

    public void setCallOptOut(Boolean callOptOut) {
        this.callOptOut = callOptOut;
    }

    public Boolean getPushOptOut() {
        return pushOptOut;
    }

    public void setPushOptOut(Boolean pushOptOut) {
        this.pushOptOut = pushOptOut;
    }
}
