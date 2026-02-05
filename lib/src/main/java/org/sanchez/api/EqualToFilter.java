package org.sanchez.api;

import java.util.Map;

final class EqualToFilter implements Filter {

    private final String property;
    private final String value;

    EqualToFilter(String property, String value) {
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
