package org.sanchez.api;

import java.util.Map;

final class MatchesFilter implements Filter {

    private final String property;
    private final String regex;

    MatchesFilter(String property, String regex) {
        this.property = property;
        this.regex = regex;
    }

    String property() {
        return property;
    }

    String regex() {
        return regex;
    }

    @Override
    public boolean matches(Map<String, String> resource) {
        return false;
    }
}
