package dev.koifysh.archipelago.network.client;

public enum CreateAsHint {
    NO(0),
    BROADCAST_ALWAYS(1),
    BROADCAST_NEW(2);

    public final int value;

    private CreateAsHint(int value)
    {
        this.value = value;
    }
}
