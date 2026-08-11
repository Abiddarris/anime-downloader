package com.aabid.animedownloader.utils.format;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NewFormatterConstructorTest {

    @Nested
    @DisplayName("Constructor")
    class ConstructorTest {

        @Test
        @DisplayName("Happy path: valid format with placeholders")
        void happyPathValidFormat() {
            NewFormatter formatter = new NewFormatter("Hello {name}, you are {age} years old");

            assertTrue(formatter instanceof NewFormatter);
        }

        @Test
        @DisplayName("Happy path: repeated placeholder names")
        void happyPathRepeatedNames() {
            NewFormatter formatter = new NewFormatter("Hello {name}, hello {name} again");

            assertTrue(formatter instanceof NewFormatter);
        }

        @Test
        @DisplayName("Happy path: no placeholders")
        void happyPathNoPlaceholders() {
            NewFormatter formatter = new NewFormatter("Just plain text");

            assertTrue(formatter instanceof NewFormatter);
        }

        @Test
        @DisplayName("Happy path: empty format")
        void happyPathEmptyFormat() {
            NewFormatter formatter = new NewFormatter("");

            assertTrue(formatter instanceof NewFormatter);
        }

        @Test
        @DisplayName("Happy path: placeholders at start, middle, end")
        void happyPathPlaceholderPositions() {
            NewFormatter formatter = new NewFormatter("{start} middle {end}");

            assertTrue(formatter instanceof NewFormatter);
        }

        @Test
        @DisplayName("Edge case: format with only braces and text")
        void edgeCaseBracesAndText() {
            NewFormatter formatter = new NewFormatter("a{b}c{d}e");

            assertTrue(formatter instanceof NewFormatter);
        }

        @Test
        @DisplayName("Happy path: format with multiple transformations (should fail)")
        void happyPathMultipleTransformations() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> new NewFormatter("Hello {name:upper:lower}")
            );
            assertTrue(ex.getMessage().contains("upper and lower are mutually exclusive"));
        }

        @Test
        @DisplayName("Happy path: format with unknown transformation")
        void happyPathUnknownTransformation() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> new NewFormatter("Hello {name:unknown}")
            );
            assertTrue(ex.getMessage().contains("Unknown specifier: unknown"));
        }

        @Test
        @DisplayName("Error: variable name with invalid characters (non A-Za-z_)")
        void errorVariableNameInvalidCharacters() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> new NewFormatter("Hello {name-1}")
            );
            assertTrue(ex.getMessage().contains("Variable name must contain only letters and underscores"));
        }

        @Test
        @DisplayName("Error: variable name starting with number")
        void errorVariableNameStartingWithNumber() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> new NewFormatter("Hello {1name}")
            );
            assertTrue(ex.getMessage().contains("Variable name must contain only letters and underscores"));
        }

        @Test
        @DisplayName("Error: variable name with special characters")
        void errorVariableNameSpecialCharacters() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> new NewFormatter("Hello {name@}")
            );
            assertTrue(ex.getMessage().contains("Variable name must contain only letters and underscores"));
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
        @DisplayName("Error: Illegal } without opening bracket")
        void errorIllegalBracketInsideBracket() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> new NewFormatter("Hello {na}me}")
            );
            assertTrue(ex.getMessage().contains("Illegal } character without opening bracket"));
        }
    }
}
