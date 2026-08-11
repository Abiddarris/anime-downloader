package com.aabid.animedownloader.utils.format;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NewFormatterFormatEscapingTest {

    @Test
    @DisplayName("Happy path: single escaped opening brace")
    void happyPathSingleEscapedOpeningBrace() {
        NewFormatter formatter = new NewFormatter("Text {{ with escaped brace");
        Map<String, Object> values = new HashMap<>();
        String result = formatter.format(values);
        assertEquals("Text { with escaped brace", result);
    }

    @Test
    @DisplayName("Happy path: single escaped closing brace")
    void happyPathSingleEscapedClosingBrace() {
        NewFormatter formatter = new NewFormatter("Text }} with escaped brace");
        Map<String, Object> values = new HashMap<>();
        String result = formatter.format(values);
        assertEquals("Text } with escaped brace", result);
    }

    @Test
    @DisplayName("Happy path: escaped braces mixed with placeholders")
    void happyPathEscapedBracesWithPlaceholders() {
        NewFormatter formatter = new NewFormatter("Hello {{name}}, you are {age} years old");
        Map<String, Object> values = new HashMap<>();
        values.put("name", "Alice");
        values.put("age", 30);
        String result = formatter.format(values);
        assertEquals("Hello {name}, you are 30 years old", result);
    }

    @Test
    @DisplayName("Happy path: multiple consecutive escaped braces")
    void happyPathMultipleConsecutiveEscapedBraces() {
        NewFormatter formatter = new NewFormatter("Text {{ {{ and }} }}");
        Map<String, Object> values = new HashMap<>();
        String result = formatter.format(values);
        assertEquals("Text { { and } }", result);
    }

    @Test
    @DisplayName("Happy path: escaping at start, middle, end")
    void happyPathEscapingPositions() {
        NewFormatter formatter = new NewFormatter("{{start}} middle {end}");
        Map<String, Object> values = new HashMap<>();
        values.put("start", "BEGIN");
        values.put("end", "END");
        String result = formatter.format(values);
        assertEquals("{start} middle END", result);
    }

    @Test
    @DisplayName("Edge case: only escaped braces")
    void edgeCaseOnlyEscapedBraces() {
        NewFormatter formatter = new NewFormatter("{{}}{{}}");
        Map<String, Object> values = new HashMap<>();
        String result = formatter.format(values);
        assertEquals("{}{}", result);
    }

    @Test
    @DisplayName("Edge case: no actual placeholders, only escaping")
    void edgeCaseNoPlaceholdersOnlyEscaping() {
        NewFormatter formatter = new NewFormatter("Hello {{world}}!");
        Map<String, Object> values = new HashMap<>();
        String result = formatter.format(values);
        assertEquals("Hello {world}!", result);
    }

    @Test
    @DisplayName("Happy path: escaped braces with transformation inside")
    void happyPathEscapedBracesWithTransformationInside() {
        NewFormatter formatter = new NewFormatter("Hello {{name:upper}}");
        Map<String, Object> values = new HashMap<>();
        String result = formatter.format(values);
        assertEquals("Hello {name:upper}", result);
    }

    @Test
    @DisplayName("Happy path: mixed escaped and actual braces with transformations")
    void happyPathMixedEscapedAndActualBracesWithTransformations() {
        NewFormatter formatter = new NewFormatter("Hello {{name:upper}}, you are {age:lower} years old");
        Map<String, Object> values = new HashMap<>();
        values.put("name", "alice");
        values.put("age", "25");
        String result = formatter.format(values);
        assertEquals("Hello {name:upper}, you are 25 years old", result);
    }
}
