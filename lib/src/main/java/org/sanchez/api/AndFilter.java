package org.sanchez.api;

import java.util.List;
import java.util.Map;

final class AndFilter implements Filter {

    private final List<Filter> filters;

    AndFilter(List<Filter> filters) {
        this.filters = filters;
    }

    List<Filter> filters() {
        return filters;
    }

    @Override
    public boolean matches(Map<String, String> resource) {
        return false;
    }
}
