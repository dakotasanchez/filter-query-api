package org.sanchez.api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FilterTest {

    private static Map<String, String> createUser() {
        final Map<String, String> user = new LinkedHashMap<>();
        user.put("firstname", "Joe");
        user.put("surname", "Bloggs");
        user.put("role", "administrator");
        user.put("age", "35");
        return user;
    }

    @Test
    void equalToMatchesCaseInsensitive() {
        Filter filter = Filter.equalTo("role", "ADMINISTRATOR");
        assertTrue(filter.matches(createUser()));
    }

    @Test
    void equalToReturnsFalseForMissingAttribute() {
        Filter filter = Filter.equalTo("missing", "value");
        assertFalse(filter.matches(createUser()));
    }

    @Test
    void equalToAndGreaterThan() {
        Map<String, String> user = createUser();

        Filter filter = Filter.and(
                Filter.equalTo("role", "administrator"),
                Filter.greaterThan("age", "30")
        );

        assertTrue(filter.matches(user));
        user.put("age", "25");
        assertFalse(filter.matches(user));
    }

    @Test
    void equalToOrGreaterThan() {
        Map<String, String> user = createUser();

        Filter filter = Filter.or(
                Filter.equalTo("role", "administrator"),
                Filter.greaterThan("age", "40")
        );

        assertTrue(filter.matches(user));
    }

    @Test
    void presentReturnsFalseForMissingAttribute() {
        Filter filter = Filter.present("missing");
        assertFalse(filter.matches(createUser()));
    }

    @Test
    void notInvertsInnerFilter() {
        Filter filter = Filter.not(Filter.falseValue());
        assertTrue(filter.matches(createUser()));
    }
}
