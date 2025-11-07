package com.example.commonservice.util;

import java.text.MessageFormat;
import java.util.HashMap;

public class StringUtils {
    private static final Object LOCK = new Object();
    private static StringUtils instance;
    private HashMap<String, MessageFormat> formats;

    private StringUtils() {
        initialization();
    }

    public static StringUtils getInstance() {
        synchronized (LOCK) {
            if (instance == null) {
                instance = new StringUtils();
            }
            return instance;
        }
    }

    private void initialization() {
        if (formats == null) {
            formats = new HashMap<String, MessageFormat>();
        }
    }

    public boolean isNull(String value) {
        return value == null;
    }

    public boolean isEmpty(String value) {
        return isNull(value) || value.trim().isEmpty();
    }

    public String nvl(String value) {
        return nvl(value, "");
    }

    public String nvl(String value, String nvl) {
        if (isEmpty(value)) {
            return nvl;
        }
        return value.trim();
    }

    public String trim(String value) {
        if (isNull(value)) {
            return null;
        }
        return value.trim();
    }

    public MessageFormat getFormat(String pattern) {
        pattern = trim(pattern);
        if (isEmpty(pattern)) {
            return null;
        }
        if (!formats.containsKey(pattern)) {
            MessageFormat sdf = new MessageFormat(pattern);
            formats.put(pattern, sdf);
        }
        return formats.get(pattern);
    }

    public String format(String pattern, Object... values) {
        MessageFormat mf = getFormat(pattern);
        if (mf == null) {
            return null;
        }
        return mf.format(values);
    }


}
