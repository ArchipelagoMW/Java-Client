package io.github.archipelagomw;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.github.archipelagomw.Print.APPrint;
import io.github.archipelagomw.Print.APPrintJsonType;
import io.github.archipelagomw.Print.APPrintPart;
import io.github.archipelagomw.Print.APPrintType;
import io.github.archipelagomw.events.*;
import io.github.archipelagomw.flags.NetworkPlayer;
import io.github.archipelagomw.network.APPacket;
import io.github.archipelagomw.network.ConnectionResult;
import io.github.archipelagomw.network.client.ConnectPacket;
import io.github.archipelagomw.network.client.GetDataPackagePacket;
import io.github.archipelagomw.network.client.LocationScouts;
import io.github.archipelagomw.network.client.SayPacket;
import io.github.archipelagomw.network.server.*;
import io.github.archipelagomw.parts.DataPackage;
import io.github.archipelagomw.parts.NetworkItem;

import io.github.archipelagomw.parts.NetworkSlot;
import org.apache.hc.core5.net.URIBuilder;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.extensions.permessage_deflate.PerMessageDeflateExtension;
import org.java_websocket.handshake.ServerHandshake;

import javax.net.ssl.SSLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.logging.Level;

class WebSocket extends WebSocketClient {

    private final static java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(WebSocket.class.getName());

    private final Client client;

    private final Gson gson = new Gson();

    private boolean authenticated = false;

    private int reconnectAttempt = 0;

    private String seedName;
    private static Timer reconnectTimer;
    private boolean downgrade = false;

    private static final Draft perMessageDeflateDraft = new Draft_6455(new PerMessageDeflateExtension());

    public WebSocket(URI serverUri, Client client) {
        super(serverUri, perMessageDeflateDraft);
        this.client = client;
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

            for (int commandNumber = 0; cmdList.size() > commandNumber; ++commandNumber) {
                JsonElement packet = cmdList.get(commandNumber);
                //parse the packet first to see what command has been sent.
                APPacket cmd = gson.fromJson(packet, APPacket.class);


                //check if room info packet
                switch (cmd.getCmd()) {
                    case RoomInfo:
                        RoomInfoPacket roomInfo = gson.fromJson(packet, RoomInfoPacket.class);

                        //save room info
                        client.setRoomInfo(roomInfo);

                        client.versions = roomInfo.datapackageChecksums;
                        client.games = roomInfo.games;

                        client.loadDataPackage();

                        checkDataPackage(roomInfo.datapackageChecksums, roomInfo.games);

                        seedName = roomInfo.seedName;

                        ConnectPacket connectPacket = new ConnectPacket();
                        connectPacket.version = Client.protocolVersion;
                        connectPacket.name = client.getMyName();
                        connectPacket.password = (client.getPassword() == null) ? "" : client.getPassword();
                        connectPacket.uuid = Client.getUUID();
                        connectPacket.game = client.getGame();
                        connectPacket.tags = client.getTags();
                        connectPacket.itemsHandling = client.getItemsHandlingFlags();

                        //send reply
                        sendPacket(connectPacket);
                        client.setRoomInfo(roomInfo);
                        break;
                    case Connected:
                        ConnectedPacket connectedPacket = gson.fromJson(packet, ConnectedPacket.class);

                        client.setTeam(connectedPacket.team);
                        client.setSlot(connectedPacket.slot);
                        connectedPacket.slotInfo.put(0, new NetworkSlot("Archipelago", "Archipelago", NetworkPlayer.SPECTATOR));
                        client.setSlotInfo(connectedPacket.slotInfo);

                        client.getRoomInfo().networkPlayers.addAll(connectedPacket.players);
                        int teams = 1;
                        OptionalInt teamsOptional = client.getRoomInfo().networkPlayers.stream().mapToInt(player -> player.team).max();
                        if (teamsOptional.isPresent()) {
                            teams = teamsOptional.getAsInt() + 1;
                        }
                        for (int i = 0; i < teams; i++) {
                            client.getRoomInfo().networkPlayers.add( new io.github.archipelagomw.parts.NetworkPlayer(i, 0, "Archipelago"));
                        }

                        client.setAlias(client.getRoomInfo().getPlayer(connectedPacket.team, connectedPacket.slot).alias);

                        JsonElement slotData = packet.getAsJsonObject().get("slot_data");

                        ConnectionAttemptEvent attemptConnectionEvent = new ConnectionAttemptEvent(connectedPacket.team, connectedPacket.slot, seedName, slotData);
                        client.getEventManager().callEvent(attemptConnectionEvent);

                        if (!attemptConnectionEvent.isCanceled()) {
                            authenticated = true;
                            //only send locations if the connection is not canceled.
                            client.getLocationManager().addCheckedLocations(connectedPacket.checkedLocations);
                            client.getLocationManager().setMissingLocations(connectedPacket.missingLocations);
                            client.getLocationManager().sendIfChecked(connectedPacket.missingLocations);

                            ConnectionResultEvent connectionResultEvent = new ConnectionResultEvent(ConnectionResult.Success, connectedPacket.team, connectedPacket.slot, seedName, slotData);
                            client.getEventManager().callEvent(connectionResultEvent);
                        } else {
                            this.close();
                            //close out of this loop because we are no longer interested in further commands from the server.
                            break;
                        }
                        break;
                    case ConnectionRefused:
                        ConnectionRefusedPacket error = gson.fromJson(cmdList.get(commandNumber), ConnectionRefusedPacket.class);
                        client.getEventManager().callEvent(new ConnectionResultEvent(error.errors[0]));
                        break;
                    case DataPackage:
                        JsonElement data = packet.getAsJsonObject().get("data");
                        DataPackage dataPackage = gson.fromJson(data, DataPackage.class);
                        client.updateDataPackage(dataPackage);
                        client.saveDataPackage();
                        break;
                    case PrintJSON:
                        LOGGER.finest("PrintJSON packet");
                        APPrint print = gson.fromJson(packet, APPrint.class);
                        if (print.type == null) print.type = APPrintJsonType.Unknown;
                        //filter though all player IDs and replace id with alias.
                        for (int partNumber = 0; print.parts.length > partNumber; ++partNumber) {
                            APPrintPart part = print.parts[partNumber];

                            if (part.type == APPrintType.playerID) {
                                int playerID = Integer.parseInt(part.text);
                                io.github.archipelagomw.parts.NetworkPlayer player = client.getRoomInfo().getPlayer(client.getTeam(), playerID);
                                part.text = player.alias;
                            }
                            else if (part.type == APPrintType.itemID) {
                                long itemID = Long.parseLong(part.text);
                                part.text = client.getDataPackage().getItem(itemID, client.getSlotInfo().get(part.player).game);
                            }
                            else if (part.type == APPrintType.locationID) {
                                long locationID = Long.parseLong(part.text);
                                part.text = client.getDataPackage().getLocation(locationID, client.getSlotInfo().get(part.player).game);
                            }
                        }

                        if (print.item != null) {
                            print.item.itemName = client.getDataPackage().getItem(print.item.itemID, client.getSlotInfo().get(print.item.playerID).game);
                            print.item.locationName = client.getDataPackage().getLocation(print.item.locationID, client.getSlotInfo().get(print.item.playerID).game);
                            print.item.playerName = client.getRoomInfo().getPlayer(client.getTeam(), print.item.playerID).alias;
                        }

                        client.getEventManager().callEvent(new PrintJSONEvent(print, print.type, print.receiving, print.item));

                        break;
                    case RoomUpdate:
                        RoomUpdatePacket updatePacket = gson.fromJson(packet, RoomUpdatePacket.class);
                        updateRoom(updatePacket);
                        break;
                    case ReceivedItems:
                        ReceivedItemsPacket items = gson.fromJson(packet, ReceivedItemsPacket.class);
                        ItemManager itemManager = client.getItemManager();
                        itemManager.receiveItems(items.items, items.index);
                        break;
                    case Bounced:
                        BouncedPacket bounced = gson.fromJson(packet, BouncedPacket.class);
                        if(!client.getBouncedManager().handle(bounced))
                        {
                            client.getEventManager().callEvent(new BouncedEvent(bounced.games, bounced.tags, bounced.slots, bounced.data));
                        }
                        break;
                    case LocationInfo:
                        LocationInfoPacket locations = gson.fromJson(packet, LocationInfoPacket.class);
                        for (NetworkItem item : locations.locations) {
                            item.itemName = client.getDataPackage().getItem(item.itemID, client.getSlotInfo().get(item.playerID).game);
                            item.locationName = client.getDataPackage().getLocation(item.locationID, client.getSlotInfo().get(client.getSlot()).game);
                            item.playerName = client.getRoomInfo().getPlayer(client.getTeam(), item.playerID).alias;
                        }
                        client.getEventManager().callEvent(new LocationInfoEvent(locations.locations));
                        break;
                    case Retrieved:
                        RetrievedPacket retrievedPacket = gson.fromJson(packet, RetrievedPacket.class);
                        client.getEventManager().callEvent(new RetrievedEvent(retrievedPacket.keys, packet.getAsJsonObject().get("keys").getAsJsonObject(), retrievedPacket.requestID));
                        break;
                    case SetReply:
                        SetReplyPacket setReplyPacket = gson.fromJson(packet, SetReplyPacket.class);
                        client.getEventManager().callEvent(new SetReplyEvent(setReplyPacket.key, setReplyPacket.value, setReplyPacket.original_Value, packet.getAsJsonObject().get("value"), setReplyPacket.requestID));
                        break;
                    case InvalidPacket:
                        InvalidPacket invalidPacket = gson.fromJson(packet, InvalidPacket.class);
                        client.getEventManager().callEvent(new InvalidPacketEvent(invalidPacket.type, invalidPacket.Original_cmd, invalidPacket.text));
                    default:

                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error processing incoming packet: ", e);
        }
    }

    private void updateRoom(RoomUpdatePacket updateRoomPacket) {
        if (!updateRoomPacket.networkPlayers.isEmpty()) {
            client.getRoomInfo().networkPlayers = updateRoomPacket.networkPlayers;
        }

        client.setHintPoints(updateRoomPacket.hintPoints);
        client.setAlias(client.getRoomInfo().getPlayer(client.getTeam(), client.getSlot()).alias);

        client.getEventManager().callEvent(new CheckedLocationsEvent(updateRoomPacket.checkedLocations));
    }


    private void checkDataPackage(HashMap<String, String> versions, List<String> games) {
        Set<String> gamesToUpdate = new HashSet<>();
        Map<String, String> checksums = client.getDataPackage().getChecksums();
        for (Map.Entry<String, String> game : versions.entrySet()) {
            if (!games.contains(game.getKey()))
                continue;

            if (!checksums.containsKey(game.getKey())) {
                gamesToUpdate.add(game.getKey());
                continue;
            }

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
                    client.connect(new URIBuilder(uri).setScheme("ws").build());
                } catch (URISyntaxException ignored) {
                    client.onClose("(AP-275) " + reason, 0);
                }
                return;
            }
            client.onClose("(AP-279) " + reason, 0);
            return;
        }
        if (code == 1000) {
            reconnectTimer.cancel();
            client.onClose("(AP-284) Disconnected.", 0);
        }

        if (code == 1006) {
            reason = "Lost connection to the Archipelago server.";
            if (reconnectAttempt <= 10) {
                int reconnectDelay = (int) (5000 * Math.pow(2, reconnectAttempt));
                reconnectAttempt++;
                TimerTask reconnectTask = new TimerTask() {
                    @Override
                    public void run() {
                        client.reconnect();
                    }
                };

                reconnectTimer.cancel();
                reconnectTimer = new Timer();
                reconnectTimer.schedule(reconnectTask, reconnectDelay);
                client.onClose("(AP-302)  " + reason, reconnectDelay / 1000);
                return;
            }
        }

        reconnectTimer.cancel();
        client.onClose("(AP-308) "+reason, 0);
    }

    @Override
    public void onError(Exception ex) {
        if (ex instanceof SSLException) {
            LOGGER.info(String.format("SSL Error: %s", ex.getMessage()));
            return;
        }
        client.onError(ex);
        LOGGER.log(Level.WARNING, "Error in websocket connection: " + ex.getMessage());
        //ex.printStackTrace();
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

    public void scoutlocations(ArrayList<Long> locationIDs, int createAsHint)
    {
        LocationScouts packet = new LocationScouts(locationIDs, createAsHint);
        sendPacket(packet);
    }
}
