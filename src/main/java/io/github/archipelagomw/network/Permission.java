package io.github.archipelagomw.network;

import io.github.archipelagomw.utils.IntEnum;

public enum Permission implements IntEnum {
    disabled(0b000),
    enabled(0b001),
    goal(0b010),
    auto(0b110),
    auto_enabled(0b111);

    public final int value;
    Permission(int value) {
        this.value=value;
    }

    @Override
    public int getValue() {
        return value;
    }
}
