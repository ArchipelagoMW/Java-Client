package dev.koifysh.archipelago.network;

public enum RemainingMode {
    enabled,
    disabled,
    goal;

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
}
