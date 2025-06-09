package dev.koifysh.archipelago.bounce;

import dev.koifysh.archipelago.Client;
import dev.koifysh.archipelago.events.DeathLinkEvent;
import dev.koifysh.archipelago.network.client.BouncePacket;
import dev.koifysh.archipelago.network.server.BouncedPacket;

import java.util.HashMap;
import java.util.Map;

public class DeathLinkHandler implements BouncedPacketHandler {
    public static final String DEATHLINK_TAG = "DeathLink";

    private final Client client;

    private double lastDeath = 0d;

    public DeathLinkHandler(Client client) {
        this.client = client;
    }

    @Override
    public boolean canHandle(BouncedPacket packet) {
        return packet.tags.contains(DEATHLINK_TAG);
    }

    @Override
    public void handle(BouncedPacket packet) {
        Map<String, Object> data = packet.data;
        DeathLinkEvent event = new DeathLinkEvent((String) data.get("source"),
                (String) data.get("cause"),
                (Double) data.getOrDefault("time", 0d));

        if(Math.abs(lastDeath - event.time) <= 1e-6)
        {
            // We already died, go away!
            return;
        }
        lastDeath = Math.max(event.time, lastDeath);
        client.getEventManager().callEvent(event);
    }

    public void sendDeathLink(String source, String cause)
    {
        lastDeath = (double)System.currentTimeMillis() / 1000D;

        BouncePacket deathLinkPacket = new BouncePacket();
        deathLinkPacket.tags = new String[]{DEATHLINK_TAG};

        HashMap<String, Object> data = new HashMap<>();
        data.put("cause",cause);
        data.put("time", lastDeath);
        data.put("source",source);
        deathLinkPacket.setData(data);

        client.sendBounce(deathLinkPacket);
    }
}
