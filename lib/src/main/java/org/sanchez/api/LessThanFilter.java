package org.sanchez.api;

import java.util.Map;

final class LessThanFilter implements Filter {

    private final String property;
    private final String value;

    LessThanFilter(String property, String value) {
        this.property = property;
        this.value = value;
    }

    String property() {
        return property;
    }

    String value() {
        return value;
    }

    @Override
    public boolean matches(Map<String, String> resource) {
        return false;
    }
}
