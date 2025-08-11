package io.github.archipelagomw.bounce;

import io.github.archipelagomw.Client;
import io.github.archipelagomw.events.DeathLinkEvent;
import io.github.archipelagomw.network.client.BouncePacket;
import io.github.archipelagomw.network.server.BouncedPacket;
import io.github.archipelagomw.utils.AtomicDouble;

import java.util.HashMap;
import java.util.Map;

public class DeathLinkHandler implements BouncedPacketHandler {
    public static final String DEATHLINK_TAG = "DeathLink";

    private final Client client;

    private final AtomicDouble lastDeath = new AtomicDouble(0d);

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
        double recentDeath = lastDeath.getAndUpdate(d -> Math.max(d, event.time));
        if(Math.abs(recentDeath - event.time) <= 1e-6)
        {
            // We already died, go away!
            return;
        }
        client.getEventManager().callEvent(event);
    }

    public void sendDeathLink(String source, String cause)
    {
        lastDeath.set((double)System.currentTimeMillis() / 1000D);

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
