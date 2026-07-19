/*
 * Copyright 2026 Aabid Darris
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.aabid.animedownloader.utils.format;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jspecify.annotations.NonNull;

import com.aabid.animedownloader.utils.format.Token.Type;

public class NewFormatter {

    private final List<Statement> statements;

    public NewFormatter(@NonNull String format) {
        this.statements = createStatements(tokenize(format));
    }

    private List<Statement> createStatements(List<Token> tokens) {
        List<Statement> statements = new ArrayList<>();
        StringBuilder temp = new StringBuilder();
        boolean insideBracket = false;
        for (Token token : tokens) {
            switch (token.getType()) {
                case LITERAL -> temp.append(token.getCharacter());
                case BRACKET_START -> {
                    if (insideBracket) {
                        throw new IllegalArgumentException("Illegal { character inside bracket");
                    }
                    insideBracket = true;

                    if (!temp.isEmpty()) {
                        statements.add(new Literal(temp.toString()));
                        temp.delete(0, temp.length());
                    }
                }
                case BRACKET_END -> {
                    if (!insideBracket) {
                        throw new IllegalArgumentException("Illegal } character without opening bracket");
                    }
                    insideBracket = false;

                    if (temp.isEmpty()) {
                        throw new IllegalArgumentException(String.format("{} block should not be empty"));
                    }

                    statements.add(new Variable(temp.toString()));
                    temp.delete(0, temp.length());
                }
            }
        }

        if (insideBracket) {
            throw new IllegalArgumentException("Missing }");
        }

        if (!temp.isEmpty()) {
            statements.add(new Literal(temp.toString()));
        }

        return statements;
    }

    private List<Token> tokenize(String format) {
        List<Token> tokens = new ArrayList<>();
        for (int i = 0; i < format.length();) {
            char c = format.charAt(i);
            if (!(c == '{' || c == '}')) {
                tokens.add(createLiteralToken(c));
                i++;
                continue;
            }

            if (i + 1 >= format.length()) {
                tokens.add(createCurlyBracketToken(c));
                i++;
                continue;
            }

            char nextChar = format.charAt(i + 1);
            if (nextChar == c) {
                tokens.add(createLiteralToken(c));
                i += 2;
                continue;
            }

            tokens.add(createCurlyBracketToken(c));
            i++;
        }
        return tokens;
    }

    private Token createLiteralToken(char c) {
        return new Token(c, Type.LITERAL);
    }

    private Token createCurlyBracketToken(char c) {
        if (c == '{') {
            return new Token(c, Type.BRACKET_START);
        }

        return new Token(c, Type.BRACKET_END);
    }

    @NonNull
    public String format(Map<String, Object> values) {
        StringBuilder result = new StringBuilder();
        for (Statement statement : statements) {
            result.append(statement.evaluate(values));
        }

        return result.toString();
    }

}
