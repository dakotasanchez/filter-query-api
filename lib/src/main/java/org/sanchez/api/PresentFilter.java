package org.sanchez.api;

import java.util.Map;

final class PresentFilter implements Filter {

    private final String property;

    PresentFilter(String property) {
        this.property = property;
    }

    String property() {
        return property;
    }

    @Override
    public boolean matches(Map<String, String> resource) {
        return false;
    }
}
