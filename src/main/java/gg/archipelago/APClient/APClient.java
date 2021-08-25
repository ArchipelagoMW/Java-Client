package gg.archipelago.APClient;

import gg.archipelago.APClient.Print.APPrint;
import gg.archipelago.APClient.events.ConnectionAttemptEvent;
import gg.archipelago.APClient.events.ConnectionResultEvent;
import gg.archipelago.APClient.itemmanager.ItemManager;
import gg.archipelago.APClient.locationmanager.LocationManager;
import gg.archipelago.APClient.network.*;
import gg.archipelago.APClient.parts.DataPackage;
import gg.archipelago.APClient.parts.Game;
import gg.archipelago.APClient.parts.NetworkItem;
import gg.archipelago.APClient.parts.Version;

import java.io.*;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public abstract class APClient {

    private final static Logger LOGGER = Logger.getLogger(APClient.class.getName());

    private final String dataPackageLocation = "./APData/DataPackage.ser";

    private int hintPoints;

    private APWebSocket apWebSocket;

    private String password;

    private final String UUID;

    private RoomInfoPacket roomInfo;

    private DataPackage dataPackage;

    private final LocationManager locationManager;
    private final ItemManager itemManager;
    private final DataManager dataManager;

    public static final Version protocolVersion = new Version(0,1,6);

    private int team;
    private int slot;
    private String name = "Name not set";
    private String game = "Game not set";
    private String alias;
    private String[] tags = {};

    public APClient(String saveID, int slotID) {
        loadDataPackage();

        UUID = dataPackage.getUUID();

        locationManager = new LocationManager(this);
        itemManager = new ItemManager(this);
        dataManager = new DataManager(locationManager,itemManager, this);
        dataManager.load(saveID,slotID);
    }

    public void setGame(String game) {
        this.game = game;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    private void loadDataPackage() {
        try {
            FileInputStream fileInput = new FileInputStream(dataPackageLocation);
            ObjectInputStream objectInput = new ObjectInputStream(fileInput);

            dataPackage = (DataPackage) objectInput.readObject();
            fileInput.close();
            objectInput.close();

        } catch (IOException e) {
            LOGGER.info("no dataPackage found creating a new one.");
            dataPackage = new DataPackage();
            saveDataPackage();
        }
        catch (ClassNotFoundException e) {
            LOGGER.warning("uhh ohh failed to absorb dataPackage.");
            dataPackage = new DataPackage();
        }
    }

    void saveDataPackage() {
        try {
            File dataPackageFile = new File(dataPackageLocation);

            //noinspection ResultOfMethodCallIgnored
            dataPackageFile.getParentFile().mkdirs();
            //noinspection ResultOfMethodCallIgnored
            dataPackageFile.createNewFile();

            FileOutputStream fileOut = new FileOutputStream(dataPackageFile);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);

            objectOut.writeObject(dataPackage);


            fileOut.close();
            objectOut.close();

        } catch (IOException e) {
            LOGGER.log(Level.WARNING,"unable to save DataPackage.",e);
        }
    }

    public boolean isConnected() {
        return apWebSocket != null && apWebSocket.isOpen();
    }

    public void close() {
        if (apWebSocket != null)
            apWebSocket.close();
    }

    public void setPassword(String password) {
        this.password = password;
    }

    void setHintPoints(int hintPoints) {
        this.hintPoints = hintPoints;
    }

    public void setName(String name) {
        this.name = name;
    }

    void setSlot(int slot) {
        this.slot = slot;
    }

    void setTeam(int team) {
        this.team = team;
    }

    void setRoomInfo(RoomInfoPacket roomInfo) {
        this.roomInfo = roomInfo;
    }

    void updateDataPackage(DataPackage newData) {
        dataPackage.update(newData);
    }

    public int getTeam() {
        return team;
    }

    public int getSlot() {
        return slot;
    }

    public RoomInfoPacket getRoomInfo() {
        return roomInfo;
    }

    public void connect(String address) {
        LOGGER.fine("attempting WebSocket connection to " + address);
        if (apWebSocket != null && apWebSocket.isOpen()) {
            LOGGER.fine("previous WebSocket is open, closing.");
            apWebSocket.close();
        }
        //regex match port on end of address
        String pattern = ":[0-9]+";
        Pattern p = Pattern.compile(pattern);
        if(!p.matcher(address).find()) {
            LOGGER.fine("no port set assuming default of 38281");
            address += ":38281";
        }
        URI uri = URI.create("ws://" + address);
        apWebSocket = new APWebSocket(uri, this);
        locationManager.setAPWebSocket(apWebSocket);
        itemManager.setAPWebSocket(apWebSocket);
        apWebSocket.connect();
    }

    public void sendChat(String message) {
        if(apWebSocket.isAuthenticated()) {
            apWebSocket.sendChat(message);
        }
    }

    public boolean checkLocation(int advancementID) {
        return locationManager.checkLocation(advancementID);
    }

    public abstract void onConnectResult(ConnectionResultEvent event);

    public abstract void onJoinRoom();

    public abstract void onPrint(String print);

    public abstract void onPrintJson(APPrint apPrint, String type, int sending, NetworkItem receiving);

    public abstract void onBounced(BouncedPacket bp);

    public abstract void onError(Exception ex);

    public abstract void onClose(String Reason, int attemptingReconnect);

    public abstract void onReceiveItem(int item, String location, String player);

    public DataPackage getDataPackage() {
        return dataPackage;
    }

    public String getMyName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public int getHintPoints() {
        return hintPoints;
    }

    public String getGame() {
        return game;
    }

    /**
     * this should not need to be called externally but is left public just in case.
     */
    public void reconnect() {
        apWebSocket.reconnect();
    }

    public String getUUID() {
        return UUID;
    }

    public String getAlias() {
        return alias;
    }

    void setAlias(String alias) {
        this.alias = alias;
    }

    public LocationManager getLocationManager() {
        return locationManager;
    }

    public ItemManager getItemManager() {
        return itemManager;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public void setGameState(ClientStatus status) {
        if(apWebSocket.isAuthenticated())
            apWebSocket.sendPacket(new ClientStatusPacket(status));
    }

    public void sync() {
        apWebSocket.sendPacket(new SyncPacket());
    }

    public void sendBounce(BouncePacket bouncePacket) {
        if(apWebSocket.isAuthenticated())
            apWebSocket.sendPacket(bouncePacket);
    }

    public void disconnect() {
        apWebSocket.close();
    }

    public String[] getTags() {
        return tags;
    }

    public abstract void onAttemptConnection(ConnectionAttemptEvent event);
}
