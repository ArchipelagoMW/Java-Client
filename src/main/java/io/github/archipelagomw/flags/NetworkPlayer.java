package io.github.archipelagomw.flags;

/**
 * Flags that will tell you more about the slot type.<br>
 * {@link #SPECTATOR},
 * {@link #PLAYER},
 * {@link #GROUP}
 */
public class NetworkPlayer {
    /**
     * If set, indicates the slot is a spectator
     */
    public final static int SPECTATOR = 0b001;

    /**
     * If set, indicates the slot is a player
     */
    public final static int PLAYER = 0b010;

    /**
     * If set, indicates the slot is a group.
     */
    public final static int GROUP = 0b100;
}
