package com.aabid.animedownloader.utils.format;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NewFormatterFormatJustifyTest {

    @Test
    @DisplayName("Happy path: left justify with spaces")
    void happyPathLeftJustify() {
        NewFormatter formatter = new NewFormatter("Hello {name:<8}");
        Map<String, Object> values = new HashMap<>();
        values.put("name", "Bob");
        String result = formatter.format(values);
        assertEquals("Hello Bob     ", result); // "Bob" + 5 spaces
    }

    @Test
    @DisplayName("Happy path: left justify exact fit")
    void happyPathLeftJustifyExactFit() {
        NewFormatter formatter = new NewFormatter("Hello {name:<8}");
        Map<String, Object> values = new HashMap<>();
        values.put("name", "12345678");
        String result = formatter.format(values);
        assertEquals("Hello 12345678", result);
    }

    @Test
    @DisplayName("Happy path: left justify truncation")
    void happyPathLeftJustifyTruncation() {
        NewFormatter formatter = new NewFormatter("Hello {name:<8}");
        Map<String, Object> values = new HashMap<>();
        values.put("name", "AlexanderTheGreat");
        String result = formatter.format(values);
        assertEquals("Hello Alexande", result); // Truncated to 8 chars
    }

    @Test
    @DisplayName("Happy path: left justify zero width")
    void happyPathLeftJustifyZeroWidth() {
        NewFormatter formatter = new NewFormatter("Hello {name:<0}");
        Map<String, Object> values = new HashMap<>();
        values.put("name", "Anything");
        String result = formatter.format(values);
        assertEquals("Hello ", result);
    }

    @Test
    @DisplayName("Happy path: left justify with empty string")
    void happyPathLeftJustifyEmptyString() {
        NewFormatter formatter = new NewFormatter("Hello {name:<5}");
        Map<String, Object> values = new HashMap<>();
        values.put("name", "");
        String result = formatter.format(values);
        assertEquals("Hello      ", result); // 5 spaces
    }

    @Test
    @DisplayName("Happy path: right justify with spaces")
    void happyPathRightJustify() {
        NewFormatter formatter = new NewFormatter("Hello {name:>8}");
        Map<String, Object> values = new HashMap<>();
        values.put("name", "Bob");
        String result = formatter.format(values);
        assertEquals("Hello      Bob", result); // 5 spaces + "Bob"
    }

    @Test
    @DisplayName("Happy path: right justify exact fit")
    void happyPathRightJustifyExactFit() {
        NewFormatter formatter = new NewFormatter("Hello {name:>8}");
        Map<String, Object> values = new HashMap<>();
        values.put("name", "12345678");
        String result = formatter.format(values);
        assertEquals("Hello 12345678", result);
    }

    @Test
    @DisplayName("Happy path: right justify truncation")
    void happyPathRightJustifyTruncation() {
        NewFormatter formatter = new NewFormatter("Hello {name:>8}");
        Map<String, Object> values = new HashMap<>();
        values.put("name", "AlexanderTheGreat");
        String result = formatter.format(values);
        assertEquals("Hello TheGreat", result); // Truncated to 8 chars, right-aligned
    }

    @Test
    @DisplayName("Happy path: right justify zero width")
    void happyPathRightJustifyZeroWidth() {
        NewFormatter formatter = new NewFormatter("Hello {name:>0}");
        Map<String, Object> values = new HashMap<>();
        values.put("name", "Anything");
        String result = formatter.format(values);
        assertEquals("Hello ", result);
    }

    @Test
    @DisplayName("Happy path: right justify with empty string")
    void happyPathRightJustifyEmptyString() {
        NewFormatter formatter = new NewFormatter("Hello {name:>5}");
        Map<String, Object> values = new HashMap<>();
        values.put("name", "");
        String result = formatter.format(values);
        assertEquals("Hello      ", result); // 5 spaces
    }

    @Test
    @DisplayName("Happy path: left justify with upper case transformation")
    void happyPathLeftJustifyWithUpper() {
        NewFormatter formatter = new NewFormatter("Hello {name:<8:upper}");
        Map<String, Object> values = new HashMap<>();
        values.put("name", "bob");
        String result = formatter.format(values);
        assertEquals("Hello BOB     ", result); // "BOB" + 5 spaces
    }

    @Test
    @DisplayName("Happy path: right justify with lower case transformation")
    void happyPathRightJustifyWithLower() {
        NewFormatter formatter = new NewFormatter("Hello {name:>8:lower}");
        Map<String, Object> values = new HashMap<>();
        values.put("name", "BOB");
        String result = formatter.format(values);
        assertEquals("Hello      bob", result); // 5 spaces + "bob"
    }

    @Test
    @DisplayName("Edge case: missing key with left justify")
    void edgeCaseMissingKeyLeftJustify() {
        NewFormatter formatter = new NewFormatter("Hello {missing:<8}");
        Map<String, Object> values = new HashMap<>();
        String result = formatter.format(values);
        assertEquals("Hello null    ", result); // "null" + 4 spaces
    }

    @Test
    @DisplayName("Edge case: missing key with right justify")
    void edgeCaseMissingKeyRightJustify() {
        NewFormatter formatter = new NewFormatter("Hello {missing:>8}");
        Map<String, Object> values = new HashMap<>();
        String result = formatter.format(values);
        assertEquals("Hello     null", result); // 4 spaces + "null"
    }
}
