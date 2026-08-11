package com.aabid.animedownloader.utils.format;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NewFormatterFormatMethodTest {

    @Test
    @DisplayName("Happy path: all values present")
    void happyPathAllValuesPresent() {
        NewFormatter formatter = new NewFormatter("Hello {name}, you are {age} years old");
        Map<String, Object> values = new HashMap<>();
        values.put("name", "Alice");
        values.put("age", 30);
        String result = formatter.format(values);
        assertEquals("Hello Alice, you are 30 years old", result);
    }

    @Test
    @DisplayName("Happy path: repeated names")
    void happyPathRepeatedNames() {
        NewFormatter formatter = new NewFormatter("Hello {name}, hello {name}");
        Map<String, Object> values = new HashMap<>();
        values.put("name", "Bob");
        String result = formatter.format(values);
        assertEquals("Hello Bob, hello Bob", result);
    }

    @Test
    @DisplayName("Happy path: no placeholders")
    void happyPathNoPlaceholders() {
        NewFormatter formatter = new NewFormatter("Just static text");
        Map<String, Object> values = new HashMap<>();
        String result = formatter.format(values);
        assertEquals("Just static text", result);
    }

    @Test
    @DisplayName("Happy path: null values")
    void happyPathNullValues() {
        NewFormatter formatter = new NewFormatter("Hello {name}");
        Map<String, Object> values = new HashMap<>();
        values.put("name", null);
        String result = formatter.format(values);
        assertEquals("Hello null", result);
    }

    @Test
    @DisplayName("Happy path: various types")
    void happyPathVariousTypes() {
        NewFormatter formatter = new NewFormatter("Int: {i}, Str: {s}, Bool: {b}");
        Map<String, Object> values = new HashMap<>();
        values.put("i", 42);
        values.put("s", "test");
        values.put("b", true);
        String result = formatter.format(values);
        assertEquals("Int: 42, Str: test, Bool: true", result);
    }

    @Test
    @DisplayName("Edge case: missing key results in null in output")
    void edgeCaseMissingKeyResultsInNull() {
        NewFormatter formatter = new NewFormatter("Hello {missing}");
        Map<String, Object> values = new HashMap<>();
        String result = formatter.format(values);
        assertEquals("Hello null", result);
    }

    @Test
    @DisplayName("Happy path: extra keys in map (ignored)")
    void happyPathExtraKeysIgnored() {
        NewFormatter formatter = new NewFormatter("Hello {name}");
        Map<String, Object> values = new HashMap<>();
        values.put("name", "Charlie");
        values.put("extra", "should be ignored");
        String result = formatter.format(values);
        assertEquals("Hello Charlie", result);
    }

    @Test
    @DisplayName("Happy path: upper transformation works correctly")
    void happyPathUpperTransformationWorks() {
        NewFormatter formatter = new NewFormatter("Hello {name:upper}");
        Map<String, Object> values = new HashMap<>();
        values.put("name", "alice");
        String result = formatter.format(values);
        assertEquals("Hello ALICE", result);
    }

    @Test
    @DisplayName("Happy path: lower transformation works correctly")
    void happyPathLowerTransformationWorks() {
        NewFormatter formatter = new NewFormatter("Hello {name:lower}");
        Map<String, Object> values = new HashMap<>();
        values.put("name", "ALICE");
        String result = formatter.format(values);
        assertEquals("Hello alice", result);
    }

    @Test
    @DisplayName("Happy path: upper transformation with mixed case")
    void happyPathUpperTransformationMixedCase() {
        NewFormatter formatter = new NewFormatter("Hello {name:upper}");
        Map<String, Object> values = new HashMap<>();
        values.put("name", "AlIce");
        String result = formatter.format(values);
        assertEquals("Hello ALICE", result);
    }

    @Test
    @DisplayName("Happy path: lower transformation with mixed case")
    void happyPathLowerTransformationMixedCase() {
        NewFormatter formatter = new NewFormatter("Hello {name:lower}");
        Map<String, Object> values = new HashMap<>();
        values.put("name", "AlIce");
        String result = formatter.format(values);
        assertEquals("Hello alice", result);
    }

    @Test
    @DisplayName("Happy path: upper transformation with numbers and special chars")
    void happyPathUpperTransformationWithNumbers() {
        NewFormatter formatter = new NewFormatter("User: {id:upper}");
        Map<String, Object> values = new HashMap<>();
        values.put("id", "user123!@#");
        String result = formatter.format(values);
        assertEquals("User: USER123!@#", result);
    }

    @Test
    @DisplayName("Happy path: lower transformation with numbers and special chars")
    void happyPathLowerTransformationWithNumbers() {
        NewFormatter formatter = new NewFormatter("User: {id:lower}");
        Map<String, Object> values = new HashMap<>();
        values.put("id", "USER123!@#");
        String result = formatter.format(values);
        assertEquals("User: user123!@#", result);
    }

    @Test
    @DisplayName("Happy path: multiple placeholders with different transformations")
    void happyPathMultiplePlaceholdersDifferentTransformations() {
        NewFormatter formatter = new NewFormatter("Hello {name:upper}, you are {age:lower} years old and live in {city:upper}");
        Map<String, Object> values = new HashMap<>();
        values.put("name", "John Doe");
        values.put("age", "25");
        values.put("city", "new york");
        String result = formatter.format(values);
        assertEquals("Hello JOHN DOE, you are 25 years old and live in NEW YORK", result);
    }

    @Test
    @DisplayName("Happy path: transformation with null value")
    void happyPathTransformationWithNullValue() {
        NewFormatter formatter = new NewFormatter("Hello {name:upper}");
        Map<String, Object> values = new HashMap<>();
        values.put("name", null);
        String result = formatter.format(values);
        assertEquals("Hello NULL", result);
    }

    @Test
    @DisplayName("Happy path: transformation with empty string")
    void happyPathTransformationWithEmptyString() {
        NewFormatter formatter = new NewFormatter("Hello {name:upper}");
        Map<String, Object> values = new HashMap<>();
        values.put("name", "");
        String result = formatter.format(values);
        assertEquals("Hello ", result);
    }

    @Test
    @DisplayName("Edge case: missing key with transformation results in null in output")
    void edgeCaseMissingKeyWithTransformationResultsInNull() {
        NewFormatter formatter = new NewFormatter("Hello {missing:upper}");
        Map<String, Object> values = new HashMap<>();
        String result = formatter.format(values);
        assertEquals("Hello NULL", result);
    }
}
