package io.github.archipelagomw.events;

/**
 * event that is fired whenever you receive a death link from another player. must first enable death links via {@link dev.koifysh.archipelago.helper.DeathLink}
 */
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
