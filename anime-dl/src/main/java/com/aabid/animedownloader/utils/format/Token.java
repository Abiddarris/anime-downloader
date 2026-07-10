package com.aabid.animedownloader.utils.format;

import org.jspecify.annotations.NonNull;

class Token {

    private final char character;
    private final @NonNull Type type;

    public Token(char c, @NonNull Type type) {
        this.character = c;
        this.type = type;
    }

    public char getCharacter() {
        return character;
    }

    @NonNull
    public Type getType() {
        return type;
    }

    static enum Type {
        LITERAL, BRACKET_START, BRACKET_END
    }
}
