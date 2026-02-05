package org.sanchez.api;

import java.util.Map;

final class TrueFilter implements Filter {

    @Override
    public boolean matches(Map<String, String> resource) {
        return false;
    }
}
