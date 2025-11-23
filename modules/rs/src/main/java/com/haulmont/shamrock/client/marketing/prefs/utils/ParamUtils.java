package com.haulmont.shamrock.client.marketing.prefs.utils;

import java.util.regex.Pattern;

public final class ParamUtils {
    private static final Pattern UUID_PATTERN = Pattern.compile(com.haulmont.monaco.rs.utils.ParamUtils.UUID_PATTERN_STRING, Pattern.CASE_INSENSITIVE);
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.!$%&'*+/=?^_`{|}~-]+@([\\w-]+\\.)+[A-Za-z]{2,}$", Pattern.CASE_INSENSITIVE);

    private ParamUtils() {
    }

    public static boolean isUUID(String str) {
        return str != null && UUID_PATTERN.matcher(str).matches();
    }

    public static boolean isEmail(String str) {
        return str != null && ParamUtils.EMAIL_PATTERN.matcher(str).matches();
    }
}
