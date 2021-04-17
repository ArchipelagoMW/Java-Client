package gg.archipelago.APClient;

import com.google.gson.*;
import gg.archipelago.APClient.Print.APPrint;
import gg.archipelago.APClient.Print.APPrintType;
import gg.archipelago.APClient.events.ConnectionResultEvent;
import gg.archipelago.APClient.itemmanager.ItemManager;
import gg.archipelago.APClient.network.ConnectionResult;
import gg.archipelago.APClient.network.*;
import gg.archipelago.APClient.network.APPacketType;
import gg.archipelago.APClient.parts.DataPackage;
import gg.archipelago.APClient.parts.NetworkPlayer;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;

public class APWebSocket extends WebSocketClient {

    private final static java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(APWebSocket.class.getName());

    private final APClient apClient;

    private RoomInfoPacket roomInfo;

    private final Gson gson = new Gson();

    private boolean authenticated = false;

    private int reconnectAttempt = 0;

    private boolean attemptingReconnect;

    private String seedName;

    public APWebSocket(URI serverUri, APClient apClient) {
        super(serverUri);
        this.apClient = apClient;
    }

    public APWebSocket(APClient apClient) {
        this(URI.create(""), apClient);
    }

    @Override
    public void onOpen(ServerHandshake handshakeData) {
    }

    @Override
    public void onMessage(String message) {
        LOGGER.info("Got Packet: "+message);
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(message);

        JsonArray cmdList = element.getAsJsonArray();

        for(int i = 0; cmdList.size() > i; ++i) {
            JsonElement packet = cmdList.get(i);
            //parse the packet first to see what command has been sent.
            APPacket cmd = gson.fromJson(packet, APPacket.class);


            //check if room info packet
            if (cmd.cmd == APPacketType.RoomInfo) {
                roomInfo = gson.fromJson(packet, RoomInfoPacket.class);
                //save room info
                apClient.setRoomInfo(roomInfo);

                if (roomInfo.datapackageVersion > apClient.getDataPackage().getVersion() || roomInfo.datapackageVersion == 0)
                    getDataPackage();

                seedName = roomInfo.seedName;

                ConnectPacket connectPacket = new ConnectPacket();
                connectPacket.version = APClient.protocolVersion;
                connectPacket.name = apClient.getMyName();
                connectPacket.password = (apClient.getPassword() == null) ? "" : apClient.getPassword();
                connectPacket.uuid = apClient.getUUID();
                connectPacket.game = apClient.getGame();
                connectPacket.tags = new String[0];

                //send reply
                sendPacket(connectPacket);
            }
            else if (cmd.cmd == APPacketType.Connected) {
                ConnectedPacket connectedPacket = gson.fromJson(packet, ConnectedPacket.class);
                apClient.setTeam(connectedPacket.team);
                apClient.setSlot(connectedPacket.slot);

                apClient.getRoomInfo().networkPlayers = connectedPacket.players;
                apClient.setAlias(apClient.getRoomInfo().getPlayer(connectedPacket.team,connectedPacket.slot).alias);

                ConnectionResultEvent event = new ConnectionResultEvent(ConnectionResult.Success, connectedPacket.team,connectedPacket.slot,seedName);
                apClient.onConnectResult(event);
                if(!event.isCanceled()) {
                    apClient.loadSave(seedName, connectedPacket.slot);
                    apClient.getLocationManager().sendIfChecked(connectedPacket.missingLocations);
                    authenticated = true;
                } else {
                    apClient.close();
                }

            }
            else if (cmd.cmd == APPacketType.ConnectionRefused) {
                ConnectionRefusedPacket error = gson.fromJson(cmdList.get(i), ConnectionRefusedPacket.class);
                apClient.onConnectResult(new ConnectionResultEvent(error.errors[0]));
            }
            else if (cmd.cmd == APPacketType.Print){
                PrintPacket print = gson.fromJson(packet, PrintPacket.class);

                apClient.onPrint(print.getText());
            }
            else if (cmd.cmd == APPacketType.DataPackage){
                JsonElement data = packet.getAsJsonObject().get("data");
                DataPackage dataPackage = gson.fromJson(data, DataPackage.class);
                dataPackage.uuid = apClient.getUUID();
                apClient.setDataPackage(dataPackage);
                if (dataPackage.getVersion() != 0) {

                    apClient.saveDataPackage();
                }
            }
            else if (cmd.cmd == APPacketType.PrintJSON){
                LOGGER.finest("PrintJSON packet");
                APPrint print = gson.fromJson(packet, APPrint.class);

                //filter though all player IDs and replace id with alias.
                for(int p = 0; print.parts.length > p; ++p) {
                    if(print.parts[p].type == APPrintType.playerID) {
                        int playerID = Integer.parseInt((print.parts[p].text));
                        NetworkPlayer player = apClient.getRoomInfo().getPlayer(apClient.getTeam(),playerID);

                        print.parts[p].text = player.alias;
                    }
                    else if(print.parts[p].type == APPrintType.itemID) {
                        int itemID = Integer.parseInt((print.parts[p].text));
                        print.parts[p].text = apClient.getDataPackage().getItem(itemID);
                    }
                    else if(print.parts[p].type == APPrintType.locationID) {
                        int locationID = Integer.parseInt((print.parts[p].text));
                        print.parts[p].text = apClient.getDataPackage().getLocation(locationID);
                    }
                }
                apClient.onPrintJson(print);
            }
            else if (cmd.cmd == APPacketType.RoomUpdate){
                updateRoom(gson.fromJson(packet, RoomUpdatePacket.class));
            }
            else if (cmd.cmd == APPacketType.ReceivedItems){
                RecivedItems items = gson.fromJson(packet, RecivedItems.class);
                ItemManager itemManager = apClient.getItemManager();

                itemManager.receiveItems(items.items, items.index);
            }
        }
    }

    private void updateRoom(RoomUpdatePacket updateRoomPacket) {
        if (updateRoomPacket.networkPlayers != null)
            roomInfo.networkPlayers = updateRoomPacket.networkPlayers;
        if (updateRoomPacket.datapackageVersion > roomInfo.datapackageVersion)
            getDataPackage();
        apClient.setHintPoints(updateRoomPacket.hintPoints);
        apClient.setAlias(apClient.getRoomInfo().getPlayer(apClient.getTeam(), apClient.getSlot()).alias);
    }

    private void getDataPackage() {
        sendPacket(new GetDataPackagePacket());
    }

    public void sendPacket(APPacket packet) {
        sendManyPackets(new APPacket[]{packet});
    }

    private void sendManyPackets(APPacket[] packet) {
        String json = gson.toJson(packet);
        LOGGER.info("Sent Packet: "+json);
        send(json);
    }

    @Override
    public void onClose(int code, String wsReason, boolean remote) {
        LOGGER.info(String.format("Connection closed by %s Code: %s Reason: %s", (remote ? "remote peer" : "us"), code, wsReason));
        String reason = wsReason;
        if (code == -1)
            reason = "Connection refused by the Archipelago server.";
        if (code == 1006) {
            reason = "Lost connection to the Archipelago server.";
            attemptingReconnect = true;

        }

        if(attemptingReconnect && reconnectAttempt <= 10) {
            int reconnectDelay = (int) (5000 * Math.pow(2,reconnectAttempt));
            reconnectAttempt++;
            apClient.onClose(reason, reconnectDelay/1000);

            TimerTask reconnect = new TimerTask() {
                @Override
                public void run() {
                    apClient.reconnect();
                }
            };
            new Timer().schedule(reconnect, reconnectDelay);
        }
        else {
            attemptingReconnect = false;
            apClient.onClose(reason, 0);
        }
    }

    @Override
    public void onError(Exception ex) {
        //apClient.onError(ex);
        LOGGER.log(Level.WARNING, "Error in websocket connection");
    }

    public void sendChat(String message) {
        SayPacket say = new SayPacket(message);
        sendPacket(say);
    }

    public boolean isAuthenticated() {
        return authenticated;
    }
}
