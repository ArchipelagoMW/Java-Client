package io.github.archipelagomw.network;

public enum Permission {
    disabled(0b000),
    enabled(0b001),
    goal(0b010),
    auto(0b110),
    auto_enabled(0b111);

    public final int value;
    Permission(int value) {
        this.value=value;
    }
}
