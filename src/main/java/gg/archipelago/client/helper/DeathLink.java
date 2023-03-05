package gg.archipelago.client.helper;

import gg.archipelago.client.ArchipelagoClient;
import gg.archipelago.client.events.DeathLinkEvent;
import gg.archipelago.client.network.client.BouncePacket;
import gg.archipelago.client.network.server.BouncedPacket;

import java.util.HashMap;


public class DeathLink {

    static private double lastDeath = 0;

    private static ArchipelagoClient archipelagoClient;

    public DeathLink(ArchipelagoClient archipelagoClient) {
        DeathLink.archipelagoClient = archipelagoClient;
    }

    public static boolean isDeathLink(BouncedPacket bounced) {
        return bounced.tags.contains("DeathLink");
    }

    public static void receiveDeathLink(BouncedPacket bounced) {
        try {
            if ((Double) bounced.data.getOrDefault("time", 0d) == lastDeath)
                return;

            DeathLinkEvent dl = new DeathLinkEvent((String)bounced.data.get("source"), (String)bounced.data.get("cause"), (Double)bounced.data.get("time"));
            archipelagoClient.getEventManager().callEvent(dl);
        } catch (ClassCastException ex) {
            System.out.println("Error Receiving DeathLink, possible malformed bounce packet");
        }
    }

    public static void SendDeathLink(String source, String cause) {
        lastDeath = (double)System.currentTimeMillis() / 1000D;

        BouncePacket deathLinkPacket = new BouncePacket();
        deathLinkPacket.tags = new String[]{"DeathLink"};
        deathLinkPacket.setData(new HashMap<String, Object>(){{
            put("cause",cause);
            put("time", lastDeath);
            put("source",source);
        }});
        archipelagoClient.sendBounce(deathLinkPacket);
    }

    public static void setDeathLinkEnabled(boolean enabled) {
        if(enabled)
            archipelagoClient.addTag("DeathLink");
        else
            archipelagoClient.removeTag("DeathLink");
    }
}
