package com.greatsage.client;

/** Item da lista. kind: CHARACTER (ex.: Rimuru Tempest) ou TECHNIQUE (tecnica absorvida). */
public record Entry(String id, String name, Kind kind, String source) {
    public enum Kind { CHARACTER, TECHNIQUE }
}
