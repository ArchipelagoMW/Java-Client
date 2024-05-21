package dev.koifysh.archipelago.events;

public class InvalidPacketEvent implements Event {

    private final String type;
    private final String originalCommand;
    private final String text;

    public InvalidPacketEvent(String type, String originalCommand, String text) {
        this.type = type;
        this.text = text;
        this.originalCommand = originalCommand;
    }


    public String getType() {
        return type;
    }

    public String getOriginalCommand() {
        return originalCommand;
    }

    public boolean hasOriginalCommand() {
        return originalCommand != null;
    }

    public String getText() {
        return text;
    }
}
