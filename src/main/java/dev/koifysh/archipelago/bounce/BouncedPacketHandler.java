package dev.koifysh.archipelago.bounce;

import dev.koifysh.archipelago.network.server.BouncedPacket;

/**
 * Used to facilitate protocols over bounce packets.  Clients should use this when they want to implement
 * protocol specific bounce packets, such as deathlink, ringlink, and traplink.
 */
public interface BouncedPacketHandler {

    /**
     * Whether this BouncedPacketHandler can handle the provided packet.  If it can, no other
     * packet handler will be called, and the {@link dev.koifysh.archipelago.events.BouncedEvent} will not
     * be emitted.
     * @param packet The packet to check
     * @return true if this handler should process the packet
     */
    boolean canHandle(BouncedPacket packet);

    /**
     * Called after {@link #canHandle}.
     * @param packet The BouncedPacket.
     */
    void handle(BouncedPacket packet);
}
