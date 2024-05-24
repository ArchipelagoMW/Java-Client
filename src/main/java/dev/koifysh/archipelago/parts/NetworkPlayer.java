package dev.koifysh.archipelago.parts;

public class NetworkPlayer {
    public int team;

    public int slot;

    public String alias;

    public String name;

    public NetworkPlayer(int team, int slot, String name) {
        this.slot = slot;
        this.name = name;
        this.alias = name;
        this.team = team;
    }
}
