package com.aabid.animedownloader.utils.format;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NewFormatterTest {

    @Nested
    @DisplayName("Constructor")
    class ConstructorTest {

        @Test
        @DisplayName("Happy path: valid format with placeholders")
        void happyPathValidFormat() {
            NewFormatter formatter = new NewFormatter("Hello {name}, you are {age} years old");
            assertEquals("Hello %1$s, you are %2$s years old", formatter.getJavaFormat());
        }

        @Test
        @DisplayName("Happy path: repeated placeholder names")
        void happyPathRepeatedNames() {
            NewFormatter formatter = new NewFormatter("Hello {name}, hello {name} again");
            assertEquals("Hello %1$s, hello %1$s again", formatter.getJavaFormat());
        }

        @Test
        @DisplayName("Happy path: no placeholders")
        void happyPathNoPlaceholders() {
            NewFormatter formatter = new NewFormatter("Just plain text");
            assertEquals("Just plain text", formatter.getJavaFormat());
        }

        @Test
        @DisplayName("Happy path: empty format")
        void happyPathEmptyFormat() {
            NewFormatter formatter = new NewFormatter("");
            assertEquals("", formatter.getJavaFormat());
        }

        @Test
        @DisplayName("Happy path: placeholders at start, middle, end")
        void happyPathPlaceholderPositions() {
            NewFormatter formatter = new NewFormatter("{start} middle {end}");
            assertEquals("%1$s middle %2$s", formatter.getJavaFormat());
        }

        @Test
        @DisplayName("Edge case: format with only braces and text")
        void edgeCaseBracesAndText() {
            NewFormatter formatter = new NewFormatter("a{b}c{d}e");
            assertEquals("a%1$sc%2$se", formatter.getJavaFormat());
        }
    }

    @Nested
    @DisplayName("Constructor: Error Cases")
    class ConstructorErrorTest {

        @Test
        @DisplayName("Error: unmatched opening brace")
        void errorUnmatchedOpeningBrace() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> new NewFormatter("Hello {world")
            );
            assertTrue(ex.getMessage().contains("Missing }"));
        }

        @Test
        @DisplayName("Error: unmatched closing brace")
        void errorUnmatchedClosingBrace() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> new NewFormatter("Hello } world")
            );
            assertTrue(ex.getMessage().contains("Illegal } character without opening bracket"));
        }

        @Test
        @DisplayName("Error: empty placeholder")
        void errorEmptyPlaceholder() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> new NewFormatter("Hello {}")
            );
            assertTrue(ex.getMessage().contains("block should not be empty"));
        }

        @Test
        @DisplayName("Error: illegal { inside bracket")
        void errorIllegalBraceInsideBracket() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> new NewFormatter("Hello {na{me}")
            );
            assertTrue(ex.getMessage().contains("Illegal { character inside bracket"));
        }

        @Test
        @DisplayName("Error: illegal } inside bracket")
        void errorIllegalBracketInsideBracket() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> new NewFormatter("Hello {na}me}")
            );
            assertTrue(ex.getMessage().contains("Illegal } character without opening bracket"));
        }
    }

    @Nested
    @DisplayName("format() method")
    class FormatMethodTest {

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
            NewFormatter formatter = new NewFormatter("Value: {value}");
            Map<String, Object> values = new HashMap<>();
            values.put("value", null);
            String result = formatter.format(values);
            assertEquals("Value: null", result);
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
        @DisplayName("Edge case: missing key results in null in output")
        void edgeCaseMissingKeyResultsInNull() {
            NewFormatter formatter = new NewFormatter("Hello {missing}");
            Map<String, Object> values = new HashMap<>(); // empty map
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
    }
}