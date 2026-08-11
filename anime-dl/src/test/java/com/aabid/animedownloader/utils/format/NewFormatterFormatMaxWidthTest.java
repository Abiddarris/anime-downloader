package com.aabid.animedownloader.utils.format;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NewFormatterFormatMaxWidthTest {

    @Test
    @DisplayName("Happy path: basic max width transformation")
    void happyPathBasicMaxWidth() {
        NewFormatter formatter = new NewFormatter("Hello {name:8}");
        Map<String, Object> values = new HashMap<>();
        values.put("name", "Alex");
        String result = formatter.format(values);
        assertEquals("Hello Alex", result);
    }

    @Test
    @DisplayName("Happy path: string longer than max width")
    void happyPathLongerThanMaxWidth() {
        NewFormatter formatter = new NewFormatter("Hello {name:8}");
        Map<String, Object> values = new HashMap<>();
        values.put("name", "AlexanderSuperLong");
        String result = formatter.format(values);
        assertEquals("Hello Alexande", result);
    }

    @Test
    @DisplayName("Happy path: string exactly max width")
    void happyPathExactlyMaxWidth() {
        NewFormatter formatter = new NewFormatter("Hello {name:8}");
        Map<String, Object> values = new HashMap<>();
        values.put("name", "12345678");
        String result = formatter.format(values);
        assertEquals("Hello 12345678", result);
    }

    @Test
    @DisplayName("Happy path: zero width transformation")
    void happyPathZeroWidth() {
        NewFormatter formatter = new NewFormatter("Hello {name:0}");
        Map<String, Object> values = new HashMap<>();
        values.put("name", "Anything");
        String result = formatter.format(values);
        assertEquals("Hello ", result);
    }

    @Test
    @DisplayName("Happy path: max width with empty string")
    void happyPathMaxWidthWithEmptyString() {
        NewFormatter formatter = new NewFormatter("Hello {name:5}");
        Map<String, Object> values = new HashMap<>();
        values.put("name", "");
        String result = formatter.format(values);
        assertEquals("Hello ", result);
    }

    @Test
    @DisplayName("Edge case: negative width treated as literal (based on Variable parsing)")
    void edgeCaseNegativeWidthAsLiteral() {
        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> new NewFormatter("Hello {name:-5}")
        );
        assertTrue(ex.getMessage().contains("width transformation should be positive"));
    }

    @Test
    @DisplayName("Edge case: missing key with max width transformation")
    void edgeCaseMissingKeyWithMaxWidth() {
        NewFormatter formatter = new NewFormatter("Hello {missing:8}");
        Map<String, Object> values = new HashMap<>();
        String result = formatter.format(values);
        assertEquals("Hello null", result);
    }

    @Test
    @DisplayName("Edge case: missing key with max width transformation (longer NULL)")
    void edgeCaseMissingKeyWithMaxWidthLongerNull() {
        NewFormatter formatter = new NewFormatter("Hello {missing:2}");
        Map<String, Object> values = new HashMap<>();
        String result = formatter.format(values);
        assertEquals("Hello nu", result);
    }

    @Test
    @DisplayName("Edge case: format with multiple width transformation")
    void happyPathMultipleTransformations() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new NewFormatter("Hello {name:20:20}"));
        assertTrue(ex.getMessage().contains("only one width transformation can be specified"));
    }
}
