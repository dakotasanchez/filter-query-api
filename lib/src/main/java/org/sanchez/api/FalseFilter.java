package org.sanchez.api;

import java.util.Map;

final class FalseFilter implements Filter {

    @Override
    public boolean matches(Map<String, String> resource) {
        return false;
    }
}
