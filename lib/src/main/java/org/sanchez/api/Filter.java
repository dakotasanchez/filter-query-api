package org.sanchez.api;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public interface Filter {

    boolean matches(Map<String, String> resource);

    // Boolean literals

    static Filter alwaysTrue() {
        return new TrueFilter();
    }

    static Filter alwaysFalse() {
        return new FalseFilter();
    }

    // Logical operators

    static Filter and(Filter... filters) {
        return new AndFilter(List.copyOf(Arrays.asList(filters)));
    }

    static Filter or(Filter... filters) {
        return new OrFilter(List.copyOf(Arrays.asList(filters)));
    }

    static Filter not(Filter filter) {
        return new NotFilter(filter);
    }

    // Comparison operators

    static Filter present(String property) {
        return new PresentFilter(property);
    }

    static Filter equalTo(String property, String value) {
        return new EqualToFilter(property, value);
    }

    static Filter lessThan(String property, String value) {
        return new LessThanFilter(property, value);
    }

    static Filter greaterThan(String property, String value) {
        return new GreaterThanFilter(property, value);
    }

    static Filter matches(String property, String regex) {
        return new MatchesFilter(property, regex);
    }
}
