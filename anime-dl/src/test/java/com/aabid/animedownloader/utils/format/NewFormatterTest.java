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

    @Nested
    @DisplayName("format() method: Max Width Transformation")
    class FormatMethodMaxWidthTest {

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

    @Nested
    @DisplayName("format() method: Justify Transformation")
    class FormatMethodJustifyTest {

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
}