package org.sanchez.api;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public sealed interface Filter {

    static final String REGEX_CASE_INSENSITIVE = "(?i)";

    // Boolean literals
    record True() implements Filter {}
    record False() implements Filter {}

    // Logical operators
    record And(List<Filter> filters) implements Filter {}
    record Or(List<Filter> filters) implements Filter {}
    record Not(Filter filter) implements Filter {}

    // Comparison operators
    record Present(String attribute) implements Filter {}
    record EqualTo(String attribute, String value) implements Filter {}
    record LessThan(String attribute, String value) implements Filter {}
    record GreaterThan(String attribute, String value) implements Filter {}
    record RegexMatches(String attribute, String expression) implements Filter {}

    // API

    static Filter trueValue() {
        return new True();
    }

    static Filter falseValue() {
        return new False();
    }

    static Filter and(final Filter... filters) {
        return new And(List.of(filters));
    }

    static Filter or(final Filter... filters) {
        return new Or(List.of(filters));
    }

    static Filter not(final Filter filter) {
        return new Not(filter);
    }

    static Filter present(final String attribute) {
        return new Present(attribute);
    }

    static Filter equalTo(final String attribute, final String value) {
        return new EqualTo(attribute, value);
    }

    static Filter lessThan(final String attribute, final String value) {
        return new LessThan(attribute, value);
    }

    static Filter greaterThan(final String attribute, final String value) {
        return new GreaterThan(attribute, value);
    }

    static Filter regexMatches(final String attribute, final String expression) {
        return new RegexMatches(attribute, expression);
    }

    default boolean matches(final Map<String, String> resource) {
        return switch(this) {
            case True t -> true;
            case False f -> false;
            case And and -> and.filters().stream().allMatch(f -> f.matches(resource));
            case Or or -> or.filters().stream().anyMatch(f -> f.matches(resource));
            case Not not -> !not.filter().matches(resource);
            case EqualTo equalTo -> {
                final String resourceVal = resource.get(equalTo.attribute());
                yield resourceVal != null && resourceVal.equalsIgnoreCase(equalTo.value());
            }
            case GreaterThan greaterThan -> {
                final String resourceVal = resource.get(greaterThan.attribute());
                try {
                    yield Double.parseDouble(resourceVal) > Double.parseDouble(greaterThan.value());
                } catch (NumberFormatException e) {
                    yield resourceVal.compareToIgnoreCase(greaterThan.value()) > 0;
                }
            }
            case LessThan lessThan -> {
                final String resourceVal = resource.get(lessThan.attribute());
                try {
                    yield Double.parseDouble(resourceVal) > Double.parseDouble(lessThan.value());
                } catch (NumberFormatException e) {
                    yield resourceVal.compareToIgnoreCase(lessThan.value()) > 0;
                }
            }
            case Present present -> resource.containsKey(present.attribute());
            case RegexMatches regexMatches -> {
                final String resourceVal = resource.get(regexMatches.attribute());
                yield resourceVal != null && resourceVal.matches(REGEX_CASE_INSENSITIVE + regexMatches.expression());
            }
        };
    }
}
