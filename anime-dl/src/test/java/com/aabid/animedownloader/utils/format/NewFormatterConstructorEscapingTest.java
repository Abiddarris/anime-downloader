package com.aabid.animedownloader.utils.format;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NewFormatterConstructorEscapingTest {

    @Nested
    @DisplayName("Constructor: Escaping Curly Braces")
    class ConstructorEscapingTest {

        @Test
        @DisplayName("Happy path: single escaped opening brace")
        void happyPathSingleEscapedOpeningBrace() {
            NewFormatter formatter = new NewFormatter("Text {{ with escaped brace");

            assertTrue(formatter instanceof NewFormatter);
        }

        @Test
        @DisplayName("Happy path: single escaped closing brace")
        void happyPathSingleEscapedClosingBrace() {
            NewFormatter formatter = new NewFormatter("Text }} with escaped brace");

            assertTrue(formatter instanceof NewFormatter);
        }

        @Test
        @DisplayName("Happy path: escaped braces mixed with placeholders")
        void happyPathEscapedBracesWithPlaceholders() {
            NewFormatter formatter = new NewFormatter("Hello {{name}}, you are {age} years old");

            assertTrue(formatter instanceof NewFormatter);
        }

        @Test
        @DisplayName("Happy path: multiple consecutive escaped braces")
        void happyPathMultipleConsecutiveEscapedBraces() {
            NewFormatter formatter = new NewFormatter("Text {{ {{ and }} }}");

            assertTrue(formatter instanceof NewFormatter);
        }

        @Test
        @DisplayName("Happy path: escaping at start, middle, end")
        void happyPathEscapingPositions() {
            NewFormatter formatter = new NewFormatter("{{start}} middle {end}");

            assertTrue(formatter instanceof NewFormatter);
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
        @DisplayName("Error: illegal } without opening bracket")
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
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> new NewFormatter("{{{name")
            );
            assertTrue(ex.getMessage().contains("Missing }"));
        }

        @Test
        @DisplayName("Error: unmatched closing brace after escaping")
        void errorUnmatchedClosingAfterEscaping() {
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
            assertTrue(ex.getMessage().contains("Illegal } character without opening bracket"));
        }
    }
}
