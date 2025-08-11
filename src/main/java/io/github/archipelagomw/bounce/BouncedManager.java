package io.github.archipelagomw.bounce;

import io.github.archipelagomw.events.BouncedEvent;
import io.github.archipelagomw.network.server.BouncedPacket;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A handler for managing protocol specific bounce packets.  If a handler registered with the BouncedManager
 * can handle a packet, then the BouncedPacket does not get propagated as
 * a {@link BouncedEvent}
 *
 * This class is intended to help create protocols over the Bounce Packet.
 */
public class BouncedManager {

    private final static Logger LOGGER = Logger.getLogger(BouncedManager.class.getCanonicalName());

    // Assuming the array doesn't change that often, copy on write should be ok
    private final List<BouncedPacketHandler> handlers = new CopyOnWriteArrayList<>();

    /**
     * Adds a handler to intercept bounce packets with
     * @param handler The handler to register
     */
    public void addHandler(BouncedPacketHandler handler)
    {
        handlers.add(handler);
    }

    /**
     * Removes a handler from intercepting packets with.
     * @param handler the handler to remove
     */
    public void removeHandler(BouncedPacketHandler handler)
    {
        handlers.remove(handler);
    }

    public boolean handle(BouncedPacket packet)
    {
        for(BouncedPacketHandler handler : handlers)
        {
            try {
                if (handler.canHandle(packet)) {
                    handler.handle(packet);
                    return true;
                }
            }
            catch(RuntimeException ex)
            {
                LOGGER.log(Level.WARNING, "Error while handling bounce packet", ex);
                // Well we were supposed to handle it...
                return true;
            }
        }
        return false;
    }
}
