package io.github.archipelagomw.network.client;

import io.github.archipelagomw.utils.IntEnum;

public enum HintStatus implements IntEnum {
    HINT_UNSPECIFIED(0),
    HINT_NO_PRIORITY(10),
    HINT_AVOID(20),
    HINT_PRIORITY(30),
    HINT_FOUND(40);

    private final int value;
    HintStatus(int value) {
        this.value=value;
    }

    @Override
    public int getValue()
    {
        return value;
    }

}