package me.dynmie.aoc.yukino.utils;

public record Token(String token) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Token token1)) return false;
        return token.equals(token1.token);
    }

    @Override
    public String toString() {
        return "Token{" +
                "token='" + "REDACTED" + '\'' +
                '}';
    }

}
