package gg.archipelago.APClient;

import com.google.gson.*;
import gg.archipelago.APClient.Print.APPrint;
import gg.archipelago.APClient.Print.APPrintType;
import gg.archipelago.APClient.events.ConnectionAttemptEvent;
import gg.archipelago.APClient.events.ConnectionResultEvent;
import gg.archipelago.APClient.itemmanager.ItemManager;
import gg.archipelago.APClient.network.ConnectionResult;
import gg.archipelago.APClient.network.*;
import gg.archipelago.APClient.network.APPacketType;
import gg.archipelago.APClient.parts.DataPackage;
import gg.archipelago.APClient.parts.NetworkItem;
import gg.archipelago.APClient.parts.NetworkPlayer;
import org.apache.hc.core5.net.URIBuilder;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.logging.Level;

public class APWebSocket extends WebSocketClient {

    private final static java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(APWebSocket.class.getName());

    private final APClient apClient;

    private final Gson gson = new Gson();

    private boolean authenticated = false;

    private int reconnectAttempt = 0;

    private String seedName;
    private static Timer reconnectTimer;
    private boolean downgrade = false;

    public APWebSocket(URI serverUri, APClient apClient) {
        super(serverUri);
        this.apClient = apClient;
        if (reconnectTimer != null) {
            reconnectTimer.cancel();
        }
        reconnectTimer = new Timer();
    }

    @Override
    public void onOpen(ServerHandshake handshakeData) {
    }

    @Override
    public void onMessage(String message) {
        try {
            LOGGER.fine("Got Packet: " + message);
            JsonElement element = JsonParser.parseString(message);

            JsonArray cmdList = element.getAsJsonArray();

            for (int i = 0; cmdList.size() > i; ++i) {
                JsonElement packet = cmdList.get(i);
                //parse the packet first to see what command has been sent.
                APPacket cmd = gson.fromJson(packet, APPacket.class);


                //check if room info packet
                if (cmd.cmd == APPacketType.RoomInfo) {
                    RoomInfoPacket roomInfo = gson.fromJson(packet, RoomInfoPacket.class);

                    //save room info
                    apClient.setRoomInfo(roomInfo);

                    checkDataPackage(roomInfo.datapackageVersions);

                    seedName = roomInfo.seedName;

                    ConnectPacket connectPacket = new ConnectPacket();
                    connectPacket.version = APClient.protocolVersion;
                    connectPacket.name = apClient.getMyName();
                    connectPacket.password = (apClient.getPassword() == null) ? "" : apClient.getPassword();
                    connectPacket.uuid = apClient.getUUID();
                    connectPacket.game = apClient.getGame();
                    connectPacket.tags = apClient.getTags();
                    connectPacket.itemsHandling = apClient.getItemsHandlingFlags();

                    //send reply
                    sendPacket(connectPacket);
                    apClient.setRoomInfo(roomInfo);
                } else if (cmd.cmd == APPacketType.Connected) {
                    ConnectedPacket connectedPacket = gson.fromJson(packet, ConnectedPacket.class);

                    apClient.setTeam(connectedPacket.team);
                    apClient.setSlot(connectedPacket.slot);

                    apClient.getRoomInfo().networkPlayers.addAll(connectedPacket.players);
                    apClient.getRoomInfo().networkPlayers.add(new NetworkPlayer(connectedPacket.team, 0, "Archipelago"));
                    apClient.setAlias(apClient.getRoomInfo().getPlayer(connectedPacket.team, connectedPacket.slot).alias);

                    JsonElement data = packet.getAsJsonObject().get("slot_data");

                    ConnectionAttemptEvent attemptConnectionEvent = new ConnectionAttemptEvent(connectedPacket.team, connectedPacket.slot, seedName, data);
                    //dont need to load the save here,
                    //apClient.loadSave(seedName, connectedPacket.slot);
                    apClient.onAttemptConnection(attemptConnectionEvent);

                    if (!attemptConnectionEvent.isCanceled()) {
                        authenticated = true;
                        //only send locations if the connection is not canceled.
                        apClient.getLocationManager().addCheckedLocations(connectedPacket.checkedLocations);
                        apClient.getLocationManager().sendIfChecked(connectedPacket.missingLocations);

                        ConnectionResultEvent connectionResultEvent = new ConnectionResultEvent(ConnectionResult.Success, connectedPacket.team, connectedPacket.slot, seedName, data);
                        apClient.onConnectResult(connectionResultEvent);
                    } else {
                        this.close();
                        //close out of this loop because we are no longer interested in further commands from the server.
                        break;
                    }

                } else if (cmd.cmd == APPacketType.ConnectionRefused) {
                    ConnectionRefusedPacket error = gson.fromJson(cmdList.get(i), ConnectionRefusedPacket.class);
                    apClient.onConnectResult(new ConnectionResultEvent(error.errors[0]));
                } else if (cmd.cmd == APPacketType.Print) {
                    PrintPacket print = gson.fromJson(packet, PrintPacket.class);

                    apClient.onPrint(print.getText());
                } else if (cmd.cmd == APPacketType.DataPackage) {
                    JsonElement data = packet.getAsJsonObject().get("data");
                    DataPackage dataPackage = gson.fromJson(data, DataPackage.class);
                    dataPackage.uuid = apClient.getUUID();
                    apClient.updateDataPackage(dataPackage);
                    if (dataPackage.getVersion() != 0) {
                        apClient.saveDataPackage();
                    }
                } else if (cmd.cmd == APPacketType.PrintJSON) {
                    LOGGER.finest("PrintJSON packet");
                    APPrint print = gson.fromJson(packet, APPrint.class);

                    //filter though all player IDs and replace id with alias.
                    for (int p = 0; print.parts.length > p; ++p) {
                        if (print.parts[p].type == APPrintType.playerID) {
                            int playerID = Integer.parseInt((print.parts[p].text));
                            NetworkPlayer player = apClient.getRoomInfo().getPlayer(apClient.getTeam(), playerID);

                            print.parts[p].text = player.alias;
                        } else if (print.parts[p].type == APPrintType.itemID) {
                            int itemID = Integer.parseInt((print.parts[p].text));
                            print.parts[p].text = apClient.getDataPackage().getItem(itemID);
                        } else if (print.parts[p].type == APPrintType.locationID) {
                            int locationID = Integer.parseInt((print.parts[p].text));
                            print.parts[p].text = apClient.getDataPackage().getLocation(locationID);
                        }
                    }
                    apClient.onPrintJson(print, print.type, print.receiving, print.item);
                } else if (cmd.cmd == APPacketType.RoomUpdate) {
                    RoomUpdatePacket updatePacket = gson.fromJson(packet, RoomUpdatePacket.class);
                    updateRoom(updatePacket);
                } else if (cmd.cmd == APPacketType.ReceivedItems) {
                    RecivedItems items = gson.fromJson(packet, RecivedItems.class);
                    ItemManager itemManager = apClient.getItemManager();
                    itemManager.receiveItems(items.items, items.index);
                } else if (cmd.cmd == APPacketType.Bounced) {
                    apClient.onBounced(gson.fromJson(packet, BouncedPacket.class));
                } else if (cmd.cmd == APPacketType.LocationInfo) {
                    LocationInfo locations = gson.fromJson(packet, LocationInfo.class);
                    for (NetworkItem item : locations.locations) {
                        item.itemName = apClient.getDataPackage().getItem(item.itemID);
                        item.locationName = apClient.getDataPackage().getLocation(item.locationID);
                        item.playerName = apClient.getRoomInfo().getPlayer(apClient.getTeam(), item.playerID).alias;
                    }
                    apClient.onLocationInfo(locations.locations);
                }
            }
        }
        catch (Exception e) {
            LOGGER.warning("Error proccessing incoming packet: ");
            e.printStackTrace();
        }
    }

    private void updateRoom(RoomUpdatePacket updateRoomPacket) {
        if (updateRoomPacket.networkPlayers.size() != 0) {
            apClient.getRoomInfo().networkPlayers = updateRoomPacket.networkPlayers;
            apClient.getRoomInfo().networkPlayers.add(new NetworkPlayer(apClient.getTeam(), 0, "Archipelago"));
        }

        checkDataPackage(updateRoomPacket.datapackageVersions);

        apClient.setHintPoints(updateRoomPacket.hintPoints);
        apClient.setAlias(apClient.getRoomInfo().getPlayer(apClient.getTeam(), apClient.getSlot()).alias);

        for (long location : updateRoomPacket.checkedLocations) {
            if(apClient.getLocationManager().getCheckedLocations().contains(location))
                continue;
            apClient.onLocationChecked(location);
        }
    }

    private void checkDataPackage(HashMap<String,Integer> versions) {
        HashSet<String> exclusions = new HashSet<>();
        for (Map.Entry<String, Integer> game : versions.entrySet()) {
            //the game does NOT need updating add it to the exclusion list.
            int myGameVersion = apClient.getDataPackage().getVersions().getOrDefault(game.getKey(),0);
            int newGameVersion = game.getValue();
            if( newGameVersion <= myGameVersion && newGameVersion != 0) {
                exclusions.add(game.getKey());
            }
        }

        if (exclusions.size() != versions.size()) {
            fetchDataPackageFromAP(exclusions);
        }
    }

    private void fetchDataPackageFromAP(Set<String> exclusions) {
        sendPacket(new GetDataPackagePacket(exclusions));
    }

    public void sendPacket(APPacket packet) {
        sendManyPackets(new APPacket[]{packet});
    }

    private void sendManyPackets(APPacket[] packet) {
        String json = gson.toJson(packet);
        LOGGER.fine("Sent Packet: "+json);
        send(json);
    }

    @Override
    public void onClose(int code, String wsReason, boolean remote) {
        LOGGER.fine(String.format("Connection closed by %s Code: %s Reason: %s", (remote ? "remote peer" : "us"), code, wsReason));
        String reason = (wsReason.isEmpty()) ? "Connection refused by the Archipelago server." : wsReason;
        if (code == -1) {
            reconnectTimer.cancel();

            // attempt to reconnect using non-secure web socket if we are failing to connect with a secure socket.
            if (uri.getScheme().equalsIgnoreCase("wss") && downgrade) {
                try {
                    apClient.connect(new URIBuilder(uri).setScheme("ws").build());
                } catch (URISyntaxException ignored) {
                    apClient.onClose(reason, 0);
                }
                return;
            }
            apClient.onClose(reason, 0);
            return;
        }
        if(code == 1000) {
            reconnectTimer.cancel();
            apClient.onClose("Disconnected.", 0);
        }

        if (code == 1006) {
            reason = "Lost connection to the Archipelago server.";
            if( reconnectAttempt <= 10) {
                int reconnectDelay = (int) (5000 * Math.pow(2, reconnectAttempt));
                reconnectAttempt++;
                TimerTask reconnectTask = new TimerTask() {
                    @Override
                    public void run() {
                        apClient.reconnect();
                    }
                };

                reconnectTimer.cancel();
                reconnectTimer = new Timer();
                reconnectTimer.schedule(reconnectTask, reconnectDelay);
                apClient.onClose(reason, reconnectDelay / 1000);
                return;
            }
        }

        reconnectTimer.cancel();
        apClient.onClose(reason, 0);
    }

    @Override
    public void onError(Exception ex) {
        apClient.onError(ex);
        LOGGER.log(Level.WARNING, "Error in websocket connection");
        ex.printStackTrace();
    }

    public void connect(boolean allowDowngrade){
        super.connect();
        reconnectTimer.cancel();
        reconnectAttempt = 0;
        this.downgrade = allowDowngrade;
    }

    public void sendChat(String message) {
        SayPacket say = new SayPacket(message);
        sendPacket(say);
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void scoutLocation(ArrayList<Long> locationIDs) {
        LocationScouts packet = new LocationScouts(locationIDs);
        sendPacket(packet);
    }
}
