package gg.archipelago.client;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import gg.archipelago.client.Print.APPrint;
import gg.archipelago.client.Print.APPrintType;
import gg.archipelago.client.events.*;
import gg.archipelago.client.helper.DeathLink;
import gg.archipelago.client.network.APPacket;
import gg.archipelago.client.network.ConnectionResult;
import gg.archipelago.client.network.client.ConnectPacket;
import gg.archipelago.client.network.client.GetDataPackagePacket;
import gg.archipelago.client.network.client.LocationScouts;
import gg.archipelago.client.network.client.SayPacket;
import gg.archipelago.client.network.server.*;
import gg.archipelago.client.parts.DataPackage;
import gg.archipelago.client.parts.NetworkItem;
import gg.archipelago.client.parts.NetworkPlayer;
import org.apache.hc.core5.net.URIBuilder;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import javax.net.ssl.SSLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.logging.Level;

public class ArchipelagoWebSocket extends WebSocketClient {

    private final static java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(ArchipelagoWebSocket.class.getName());

    private final ArchipelagoClient archipelagoClient;

    private final Gson gson = new Gson();

    private boolean authenticated = false;

    private int reconnectAttempt = 0;

    private String seedName;
    private static Timer reconnectTimer;
    private boolean downgrade = false;

    public ArchipelagoWebSocket(URI serverUri, ArchipelagoClient archipelagoClient) {
        super(serverUri);
        this.archipelagoClient = archipelagoClient;
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
                switch (cmd.getCmd()) {
                    case RoomInfo:
                        RoomInfoPacket roomInfo = gson.fromJson(packet, RoomInfoPacket.class);

                        //save room info
                        archipelagoClient.setRoomInfo(roomInfo);

                        checkDataPackage(roomInfo.datapackageChecksums, roomInfo.games);

                        seedName = roomInfo.seedName;

                        ConnectPacket connectPacket = new ConnectPacket();
                        connectPacket.version = ArchipelagoClient.protocolVersion;
                        connectPacket.name = archipelagoClient.getMyName();
                        connectPacket.password = (archipelagoClient.getPassword() == null) ? "" : archipelagoClient.getPassword();
                        connectPacket.uuid = archipelagoClient.getUUID();
                        connectPacket.game = archipelagoClient.getGame();
                        connectPacket.tags = archipelagoClient.getTags();
                        connectPacket.itemsHandling = archipelagoClient.getItemsHandlingFlags();

                        //send reply
                        sendPacket(connectPacket);
                        archipelagoClient.setRoomInfo(roomInfo);
                        break;
                    case Connected:
                        ConnectedPacket connectedPacket = gson.fromJson(packet, ConnectedPacket.class);

                        archipelagoClient.setTeam(connectedPacket.team);
                        archipelagoClient.setSlot(connectedPacket.slot);
                        archipelagoClient.setSlotInfo(connectedPacket.slotInfo);

                        archipelagoClient.getRoomInfo().networkPlayers.addAll(connectedPacket.players);
                        archipelagoClient.getRoomInfo().networkPlayers.add(new NetworkPlayer(connectedPacket.team, 0, "Archipelago"));
                        archipelagoClient.setAlias(archipelagoClient.getRoomInfo().getPlayer(connectedPacket.team, connectedPacket.slot).alias);

                        JsonElement slotData = packet.getAsJsonObject().get("slot_data");

                        ConnectionAttemptEvent attemptConnectionEvent = new ConnectionAttemptEvent(connectedPacket.team, connectedPacket.slot, seedName, slotData);
                        archipelagoClient.getEventManager().callEvent(attemptConnectionEvent);

                        if (!attemptConnectionEvent.isCanceled()) {
                            authenticated = true;
                            //only send locations if the connection is not canceled.
                            archipelagoClient.getLocationManager().addCheckedLocations(connectedPacket.checkedLocations);
                            archipelagoClient.getLocationManager().setMissingLocations(connectedPacket.missingLocations);
                            archipelagoClient.getLocationManager().sendIfChecked(connectedPacket.missingLocations);

                            ConnectionResultEvent connectionResultEvent = new ConnectionResultEvent(ConnectionResult.Success, connectedPacket.team, connectedPacket.slot, seedName, slotData);
                            archipelagoClient.getEventManager().callEvent(connectionResultEvent);
                        } else {
                            this.close();
                            //close out of this loop because we are no longer interested in further commands from the server.
                            break;
                        }
                        break;
                    case ConnectionRefused:
                        ConnectionRefusedPacket error = gson.fromJson(cmdList.get(i), ConnectionRefusedPacket.class);
                        archipelagoClient.getEventManager().callEvent(new ConnectionResultEvent(error.errors[0]));
                        break;
                    case Print:
                        archipelagoClient.onPrint(gson.fromJson(packet, PrintPacket.class).getText());
                        break;
                    case DataPackage:
                        JsonElement data = packet.getAsJsonObject().get("data");
                        DataPackage dataPackage = gson.fromJson(data, DataPackage.class);
                        dataPackage.uuid = archipelagoClient.getUUID();
                        archipelagoClient.updateDataPackage(dataPackage);
                        archipelagoClient.saveDataPackage();
                        break;
                    case PrintJSON:
                        LOGGER.finest("PrintJSON packet");
                        APPrint print = gson.fromJson(packet, APPrint.class);
                        //filter though all player IDs and replace id with alias.
                        for (int p = 0; print.parts.length > p; ++p) {
                            if (print.parts[p].type == APPrintType.playerID) {
                                int playerID = Integer.parseInt((print.parts[p].text));
                                NetworkPlayer player = archipelagoClient.getRoomInfo().getPlayer(archipelagoClient.getTeam(), playerID);

                                print.parts[p].text = player.alias;
                            } else if (print.parts[p].type == APPrintType.itemID) {
                                long itemID = Long.parseLong(print.parts[p].text);
                                print.parts[p].text = archipelagoClient.getDataPackage().getItem(itemID,archipelagoClient.getSlotInfo().get(print.parts[i].player).game);
                            } else if (print.parts[p].type == APPrintType.locationID) {
                                long locationID = Long.parseLong(print.parts[p].text);
                                print.parts[p].text = archipelagoClient.getDataPackage().getLocation(locationID,archipelagoClient.getSlotInfo().get(print.parts[i].player).game);
                            }
                        }
                        archipelagoClient.onPrintJson(print, print.type, print.receiving, print.item);
                        break;
                    case RoomUpdate:
                        RoomUpdatePacket updatePacket = gson.fromJson(packet, RoomUpdatePacket.class);
                        updateRoom(updatePacket);
                        break;
                    case ReceivedItems:
                        ReceivedItemsPacket items = gson.fromJson(packet, ReceivedItemsPacket.class);
                        ItemManager itemManager = archipelagoClient.getItemManager();
                        itemManager.receiveItems(items.items, items.index);
                        break;
                    case Bounced:
                        BouncedPacket bounced = gson.fromJson(packet, BouncedPacket.class);
                        if (DeathLink.isDeathLink(bounced))
                            DeathLink.receiveDeathLink(bounced);
                        else
                            archipelagoClient.getEventManager().callEvent(new BouncedEvent(bounced.games, bounced.tags, bounced.slots, bounced.data));
                        break;
                    case LocationInfo:
                        LocationInfoPacket locations = gson.fromJson(packet, LocationInfoPacket.class);
                        for (NetworkItem item : locations.locations) {
                            item.itemName = archipelagoClient.getDataPackage().getItem(item.itemID, archipelagoClient.getSlotInfo().get(item.playerID).game);
                            item.locationName = archipelagoClient.getDataPackage().getLocation(item.locationID, archipelagoClient.getSlotInfo().get(archipelagoClient.getSlot()).game);
                            item.playerName = archipelagoClient.getRoomInfo().getPlayer(archipelagoClient.getTeam(), item.playerID).alias;
                        }
                        archipelagoClient.getEventManager().callEvent(new LocationInfoEvent(locations.locations));
                        break;
                    case Retrieved:
                        RetrievedPacket retrievedPacket = gson.fromJson(packet, RetrievedPacket.class);
                        archipelagoClient.getEventManager().callEvent(new RetrievedEvent(retrievedPacket.keys, packet.getAsJsonObject().get("keys").getAsJsonObject(), retrievedPacket.requestID));
                        break;
                    case SetReply:
                        SetReplyPacket setReplyPacket = gson.fromJson(packet, SetReplyPacket.class);
                        archipelagoClient.getEventManager().callEvent(new SetReplyEvent(setReplyPacket.key, setReplyPacket.value, setReplyPacket.original_Value, packet.getAsJsonObject().get("value"), setReplyPacket.requestID));
                        break;
                    case InvalidPacket:
                        InvalidPacket invalidPacket = gson.fromJson(packet, InvalidPacket.class);
                        archipelagoClient.getEventManager().callEvent(new InvalidPacketEvent(invalidPacket.type, invalidPacket.Original_cmd, invalidPacket.text));
                    default:

                }
            }
        } catch (Exception e) {
            LOGGER.warning("Error proccessing incoming packet: ");
            e.printStackTrace();
        }
    }

    private void updateRoom(RoomUpdatePacket updateRoomPacket) {
        if (!updateRoomPacket.networkPlayers.isEmpty()) {
            archipelagoClient.getRoomInfo().networkPlayers = updateRoomPacket.networkPlayers;
            archipelagoClient.getRoomInfo().networkPlayers.add(new NetworkPlayer(archipelagoClient.getTeam(), 0, "Archipelago"));
        }

        archipelagoClient.setHintPoints(updateRoomPacket.hintPoints);
        archipelagoClient.setAlias(archipelagoClient.getRoomInfo().getPlayer(archipelagoClient.getTeam(), archipelagoClient.getSlot()).alias);

        archipelagoClient.getEventManager().callEvent(new CheckedLocationsEvent(updateRoomPacket.checkedLocations));
    }


    private void checkDataPackage(HashMap<String, String> versions, List<String> games) {
        Set<String> gamesToUpdate = new HashSet<>();
        Map<String, String> checksums = archipelagoClient.getDataPackage().getChecksums();
        for (Map.Entry<String, String> game : versions.entrySet()) {
            if (!games.contains(game.getKey()))
                continue;
            if (!checksums.containsKey(game.getKey()))
                gamesToUpdate.add(game.getKey());
            if (!checksums.get(game.getKey()).equals(game.getValue()))
                gamesToUpdate.add(game.getKey());
        }


        if (!gamesToUpdate.isEmpty()) {
            fetchDataPackageFromAP(gamesToUpdate);
        }
    }

    private void fetchDataPackageFromAP(Set<String> games) {
        sendPacket(new GetDataPackagePacket(games));
    }

    public void sendPacket(APPacket packet) {
        sendManyPackets(new APPacket[]{packet});
    }

    private void sendManyPackets(APPacket[] packet) {
        if (!isOpen())
            return;
        String json = gson.toJson(packet);
        LOGGER.fine("Sent Packet: " + json);
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
                    archipelagoClient.connect(new URIBuilder(uri).setScheme("ws").build());
                } catch (URISyntaxException ignored) {
                    archipelagoClient.onClose("(AP-275) " + reason, 0);
                }
                return;
            }
            archipelagoClient.onClose("(AP-279) " + reason, 0);
            return;
        }
        if (code == 1000) {
            reconnectTimer.cancel();
            archipelagoClient.onClose("(AP-284) Disconnected.", 0);
        }

        if (code == 1006) {
            reason = "Lost connection to the Archipelago server.";
            if (reconnectAttempt <= 10) {
                int reconnectDelay = (int) (5000 * Math.pow(2, reconnectAttempt));
                reconnectAttempt++;
                TimerTask reconnectTask = new TimerTask() {
                    @Override
                    public void run() {
                        archipelagoClient.reconnect();
                    }
                };

                reconnectTimer.cancel();
                reconnectTimer = new Timer();
                reconnectTimer.schedule(reconnectTask, reconnectDelay);
                archipelagoClient.onClose("(AP-302)  " + reason, reconnectDelay / 1000);
                return;
            }
        }

        reconnectTimer.cancel();
        archipelagoClient.onClose("(AP-308) "+reason, 0);
    }

    @Override
    public void onError(Exception ex) {
        if (ex instanceof SSLException) {
            LOGGER.info(String.format("SSL Error: %s", ex.getMessage()));
            return;
        }
        archipelagoClient.onError(ex);
        LOGGER.log(Level.WARNING, "Error in websocket connection");
        ex.printStackTrace();
    }

    public void connect(boolean allowDowngrade) {
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
