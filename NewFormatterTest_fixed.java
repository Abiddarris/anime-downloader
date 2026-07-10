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
            // Constructor should succeed without throwing exception
            assertTrue(formatter instanceof NewFormatter);
        }

        @Test
        @DisplayName("Happy path: repeated placeholder names")
        void happyPathRepeatedNames() {
            NewFormatter formatter = new NewFormatter("Hello {name}, hello {name} again");
            // Constructor should succeed without throwing exception
            assertTrue(formatter instanceof NewFormatter);
        }

        @Test
        @DisplayName("Happy path: no placeholders")
        void happyPathNoPlaceholders() {
            NewFormatter formatter = new NewFormatter("Just plain text");
            // Constructor should succeed without throwing exception
            assertTrue(formatter instanceof NewFormatter);
        }

        @Test
        @DisplayName("Happy path: empty format")
        void happyPathEmptyFormat() {
            NewFormatter formatter = new NewFormatter("");
            // Constructor should succeed without throwing exception
            assertTrue(formatter instanceof NewFormatter);
        }

        @Test
        @DisplayName("Happy path: placeholders at start, middle, end")
        void happyPathPlaceholderPositions() {
            NewFormatter formatter = new NewFormatter("{start} middle {end}");
            // Constructor should succeed without throwing exception
            assertTrue(formatter instanceof NewFormatter);
        }

        @Test
        @DisplayName("Edge case: format with only braces and text")
        void edgeCaseBracesAndText() {
            NewFormatter formatter = new NewFormatter("a{b}c{d}e");
            // Constructor should succeed without throwing exception
            assertTrue(formatter instanceof NewFormatter);
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
    @DisplayName("Constructor: Escaping Curly Braces")
    class ConstructorEscapingTest {

        @Test
        @DisplayName("Happy path: single escaped opening brace")
        void happyPathSingleEscapedOpeningBrace() {
            NewFormatter formatter = new NewFormatter("Text {{ with escaped brace");
            // Constructor should succeed without throwing exception
            assertTrue(formatter instanceof NewFormatter);
        }

        @Test
        @DisplayName("Happy path: single escaped closing brace")
        void happyPathSingleEscapedClosingBrace() {
            NewFormatter formatter = new NewFormatter("Text }} with escaped brace");
            // Constructor should succeed without throwing exception
            assertTrue(formatter instanceof NewFormatter);
        }

        @Test
        @DisplayName("Happy path: escaped braces mixed with placeholders")
        void happyPathEscapedBracesWithPlaceholders() {
            NewFormatter formatter = new NewFormatter("Hello {{name}}, you are {age} years old");
            // Constructor should succeed without throwing exception
            assertTrue(formatter instanceof NewFormatter);
        }

        @Test
        @DisplayName("Happy path: multiple consecutive escaped braces")
        void happyPathMultipleConsecutiveEscapedBraces() {
            NewFormatter formatter = new NewFormatter("Text {{ {{ and }} }}");
            // Constructor should succeed without throwing exception
            assertTrue(formatter instanceof NewFormatter);
        }

        @Test
        @DisplayName("Happy path: escaping at start, middle, end")
        void happyPathEscapingPositions() {
            NewFormatter formatter = new NewFormatter("{{start}} middle {end}");
            // Constructor should succeed without throwing exception
            assertTrue(formatter instanceof NewFormatter);
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
            Map<String, Object> values = new HashMap<>(); // empty map
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
    }

    @Nested
    @DisplayName("format() method: Escaping Curly Braces")
    class FormatMethodEscapingTest {

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
    }

    @Nested
    @DisplayName("Constructor: Escaping Curly Braces Error Cases")
    class ConstructorEscapingErrorTest {

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

        @Test
        @DisplayName("Error: unmatched opening brace after escaping")
        void errorUnmatchedOpeningAfterEscaping() {
            // Test case: {{name - this should fail because we have {{ (escaped {) followed by {name (unclosed bracket)
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> new NewFormatter("{{{name")
            );
            assertTrue(ex.getMessage().contains("Missing }"));
        }

        @Test
        @DisplayName("Error: unmatched closing brace after escaping")
        void errorUnmatchedClosingAfterEscaping() {
            // Test case: name}} - this should fail because we have name followed by }} (escaped }) and an extra }
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> new NewFormatter("name}}}")
            );
            assertTrue(ex.getMessage().contains("Illegal } character without opening bracket"));
        }

        @Test
        @DisplayName("Error: complex escaping scenario - invalid bracket structure")
        void errorComplexEscapingScenario() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> new NewFormatter("{{{{{{name}}}}} {value} }}}}}")
            );
            assertTrue(ex.getMessage().contains("Illegal { character inside bracket"));
        }
    }
}