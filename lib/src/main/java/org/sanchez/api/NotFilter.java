package org.sanchez.api;

import java.util.Map;

final class NotFilter implements Filter {

    private final Filter filter;

    NotFilter(Filter filter) {
        this.filter = filter;
    }

    Filter filter() {
        return filter;
    }

    @Override
    public boolean matches(Map<String, String> resource) {
        return false;
    }
}
