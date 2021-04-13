package gg.archipelago.APClient.network;

public enum ForfeitMode {
    auto("auto"),
    enabled("enabled"),
    disabled("disabled"),
    autoEnabled("auto-enabled");

    public final String label;

    private ForfeitMode(String label) {
        this.label = label;
    }
}
