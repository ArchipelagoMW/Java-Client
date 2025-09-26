package io.github.archipelagomw.network.client;

import io.github.archipelagomw.utils.IntEnum;

public enum CreateAsHint implements IntEnum {
    NO(0),
    BROADCAST_ALWAYS(1),
    BROADCAST_NEW(2);

    public final int value;

    private CreateAsHint(int value)
    {
        this.value = value;
    }

    @Override
    public int getValue() {
        return value;
    }
}
