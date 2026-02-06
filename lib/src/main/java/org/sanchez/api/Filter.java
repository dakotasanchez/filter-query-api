package org.sanchez.api;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * A filter that determines whether a resource matches a set of criteria.
 *
 * <p>Resources are expected to be a {@code Map<String, String>} where keys are
 * case-sensitive property names and values are case-insensitive.
 *
 * <p>Filters are composed in the following manner:
 * <pre>{@code
 * Filter filter = Filter.and(
 *     Filter.equalTo("language", "spanish"),
 *     Filter.greaterThan("age", "30")
 * );
 * boolean result = filter.matches(user);
 * }</pre>
 */
public sealed interface Filter {

    // Used to enforce case-insensitivity for regex patterns
    String REGEX_CASE_INSENSITIVE = "(?i)";

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

    /** Returns a filter that always matches. */
    static Filter trueValue() {
        return new True();
    }

    /** Returns a filter that never matches. */
    static Filter falseValue() {
        return new False();
    }

    /** Returns a filter that matches only if all supplied filters match. */
    static Filter and(final Filter... filters) {
        return new And(List.of(filters));
    }

    /** Returns a filter that matches if any of the supplied filters match. */
    static Filter or(final Filter... filters) {
        return new Or(List.of(filters));
    }

    /** Returns a filter that matches when the supplied filter does not match and vice versa. */
    static Filter not(final Filter filter) {
        return new Not(filter);
    }

    /** Returns a filter that matches if the attribute exists in the resource. */
    static Filter present(final String attribute) {
        return new Present(attribute);
    }

    /** Returns a filter that matches if the resource value equals the supplied value (case-insensitive). */
    static Filter equalTo(final String attribute, final String value) {
        return new EqualTo(attribute, value);
    }

    /**
     * Returns a filter that matches if the resource value is less than the supplied value.
     * Numeric values are compared numerically. Non-numeric values fall back to lexicographical comparison.
     */
    static Filter lessThan(final String attribute, final String value) {
        return new LessThan(attribute, value);
    }

    /**
     * Returns a filter that matches if the resource value is greater than the supplied value.
     * Numeric values are compared numerically. Non-numeric values fall back to lexicographical comparison.
     */
    static Filter greaterThan(final String attribute, final String value) {
        return new GreaterThan(attribute, value);
    }

    /** Returns a filter that matches if the resource value matches the given regex expression (case-insensitive). */
    static Filter regexMatches(final String attribute, final String expression) {
        return new RegexMatches(attribute, expression);
    }

    /**
     * Evaluates this filter against the given resource.
     *
     * @param resource a map of attribute names to values representing the resource
     * @return {@code true} if the resource matches this filter
     */
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
                    yield Double.parseDouble(resourceVal) < Double.parseDouble(lessThan.value());
                } catch (NumberFormatException e) {
                    yield resourceVal.compareToIgnoreCase(lessThan.value()) < 0;
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
