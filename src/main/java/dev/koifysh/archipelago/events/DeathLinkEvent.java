package dev.koifysh.archipelago.events;

public class DeathLinkEvent implements Event {

    public double time;
    public String cause;
    public String source;

    public DeathLinkEvent(String source, String cause, double time) {
        this.source = source;
        this.cause = cause;
        this.time = time;
    }
}
