package org.sanchez.api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

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
        Filter filter = Filter.and(
                Filter.equalTo("role", "administrator"),
                Filter.falseValue()
        );

        assertFalse(filter.matches(createUser()));
        filter = Filter.not(filter);
        assertTrue(filter.matches(createUser()));
    }

    @Test
    void complexFilterTest() {
        Filter filter = Filter.and(
                Filter.or(
                        Filter.falseValue(),
                        Filter.equalTo("role", "read_only"),
                        Filter.greaterThan("firstname", "Bob")
                ),
                Filter.trueValue(),
                Filter.not(
                        Filter.and(
                                Filter.falseValue(),
                                Filter.equalTo("surname", "Bloggs")
                        )
                ),
                Filter.present("age"),
                Filter.regexMatches("role", "^admin.*")
        );
        assertTrue(filter.matches(createUser()));
    }

    @ParameterizedTest
    @CsvSource({
            "age, 30, true",
            "age, 35, false",
            "age, 40, false",
            "age, 8, true"
    })
    void greaterThanComparesNumerically(String attribute, String value, boolean expected) {
        Filter filter = Filter.greaterThan(attribute, value);
        assertEquals(expected, filter.matches(createUser()));
    }

    @ParameterizedTest
    @CsvSource({
            "age, 40, true",
            "age, 35, false",
            "age, 30, false",
            "age, 8, false"
    })
    void lessThanComparesNumerically(String attribute, String value, boolean expected) {
        Filter filter = Filter.lessThan(attribute, value);
        assertEquals(expected, filter.matches(createUser()));
    }

    @ParameterizedTest
    @CsvSource({
            "firstname, J.*, true",
            "firstname, j.*, true",
            "firstname, ^Joe$, true",
            "firstname, ^Bob$, false",
            "surname, B.*s, true",
    })
    void regexMatchesWithVariousPatterns(String attribute, String pattern, boolean expected) {
        Filter filter = Filter.regexMatches(attribute, pattern);
        assertEquals(expected, filter.matches(createUser()));
    }
}
