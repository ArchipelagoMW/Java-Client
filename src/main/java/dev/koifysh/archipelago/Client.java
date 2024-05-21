package dev.koifysh.archipelago;

import dev.koifysh.archipelago.Print.APPrint;
import dev.koifysh.archipelago.events.RetrievedEvent;
import dev.koifysh.archipelago.network.server.ConnectUpdatePacket;
import dev.koifysh.archipelago.network.server.RoomInfoPacket;
import dev.koifysh.archipelago.parts.DataPackage;
import dev.koifysh.archipelago.parts.NetworkItem;
import dev.koifysh.archipelago.parts.NetworkSlot;
import dev.koifysh.archipelago.parts.Version;
import dev.koifysh.archipelago.network.client.*;
import org.apache.hc.core5.net.URIBuilder;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.logging.Logger;

public abstract class Client {

    private final static Logger LOGGER = Logger.getLogger(Client.class.getName());

    private final String dataPackageLocation = "./APData/DataPackage.ser";

    private int hintPoints;

    private WebSocket webSocket;

    private String password;

    private final String UUID;

    private RoomInfoPacket roomInfo;

    private DataPackage dataPackage;

    public static Client client;

    private final LocationManager locationManager;
    private final ItemManager itemManager;
    private final EventManager eventManager;

    public static final Version protocolVersion = new Version(0, 4, 7);

    private int team;
    private int slot;
    private HashMap<Integer, NetworkSlot> slotInfo;
    private String name = "Name not set";
    private String game = "Game not set";
    private String alias;
    private Set<String> tags = new HashSet<>();
    private int itemsHandlingFlags = 0b000;

    public Client() {
        loadDataPackage();

        UUID = dataPackage.getUUID();

        eventManager = new EventManager();
        locationManager = new LocationManager(this);
        itemManager = new ItemManager(this);
        client = this;
    }

    /**
     * Sets the name of the game to send to Archipelago's servers
     * @param game the name of your game.
     */
    public void setGame(String game) {
        this.game = game;
    }

    /**
     * overwrite, and set all tags sent to the Archipelago server.
     * this will overwrite any previous tags that have been set.
     * @param tags a Set of tags to send.
     */
    public void setTags(Set<String> tags) {
        if (!this.tags.equals(tags)) {
            this.tags = tags;
            if (isConnected()) {
                ConnectUpdatePacket packet = new ConnectUpdatePacket();
                packet.tags = this.tags;
                webSocket.sendPacket(packet);
            }
        }
    }

    /**
     * add a tag to your list, keeping all previous tags intact.
     * @param tag String tag to be added.
     */
    public void addTag(String tag) {
        if (!this.tags.contains(tag)) {
            tags.add(tag);
            if (isConnected()) {
                ConnectUpdatePacket packet = new ConnectUpdatePacket();
                packet.tags = this.tags;
                webSocket.sendPacket(packet);
            }
        }
    }

    /**
     * removes supplied tag, if it exists.
     * @param tag String tag to be removed.
     */
    public void removeTag(String tag) {
        if (this.tags.contains(tag)) {
            tags.remove(tag);
            if (isConnected()) {
                ConnectUpdatePacket packet = new ConnectUpdatePacket();
                packet.tags = this.tags;
                webSocket.sendPacket(packet);
            }
        }
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
        } catch (ClassNotFoundException e) {
            LOGGER.info("Failed to Read Previous datapackage. Creating new one.");
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
            LOGGER.warning("unable to save DataPackage.");
        }
    }

    /**
     * Returns true only if connected to an Archipelago server.
     * @return true if connected, otherwise false
     */
    public boolean isConnected() {
        return webSocket != null && webSocket.isOpen();
    }

    /**
     * closes a connection to the Archipelago server if connected.
     */
    public void close() {
        if (webSocket != null)
            webSocket.close();
    }

    /**
     * sets a password to authenticate with to join a password protected room.
     * @param password room password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    void setHintPoints(int hintPoints) {
        this.hintPoints = hintPoints;
    }

    /**
     * sets the slot name to connect to an Archcipelago server with.
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    void setSlot(int slot) {
        this.slot = slot;
    }

    void setTeam(int team) {
        this.team = team;
    }

    void setSlotInfo(HashMap<Integer, NetworkSlot> slotInfo) {
        this.slotInfo = slotInfo;
    }

    void setRoomInfo(RoomInfoPacket roomInfo) {
        this.roomInfo = roomInfo;
    }

    void updateDataPackage(DataPackage newData) {
        dataPackage.update(newData);
    }

    /**
     *
     * @return team ID
     */
    public int getTeam() {
        return team;
    }

    /**
     *
     * @return Slot ID
     */
    public int getSlot() {
        return slot;
    }

    /**
     * fetches the
     * @return Room info.
     */
    public RoomInfoPacket getRoomInfo() {
        return roomInfo;
    }

    public HashMap<Integer, NetworkSlot> getSlotInfo() {return slotInfo;}

    public void connect(String address) throws URISyntaxException {
        URIBuilder builder = new URIBuilder((!address.contains("//")) ? "//" + address : address);
        if (builder.getPort() == -1) { //set default port if not included
            builder.setPort(38281);
        }

        if (webSocket != null && webSocket.isOpen()) {
            LOGGER.fine("previous WebSocket is open, closing.");
            webSocket.close();
        }

        if (builder.getScheme() == null) {
            builder.setScheme("wss");
            connect(builder.build(), true);
            return;
        }

        connect(builder.build());
    }

    public void connect(URI address) {
        connect(address, false);
    }

    /**
     * Connects to an Archipelago server with previously provided info.
     * <br>
     * supply the following info before calling this method
     * <br>
     * game: {@link #setGame(String)}<br>
     * slot name: {@link #setName(String)}<br>
     * <br>
     * if no protocol <code>wss://</code> or <code>ws://</code> is given will attempt a ssl connection
     * to the supplied address, if that fails it will then try a non-ssl connection, unless <code>allowDowngrade</code> is false. <br>
     * <br>
     * Do not prefix <code>address</code> with <code>wss://</code> or <code>ws://</code>. let the user enter a protocol to use.
     * by default ssl will be tried first, if that fails then non-ssl will be used. unless <code>allowDowngrade</code> is set to false.
     * @param address address of the archipelago server.
     * @param allowDowngrade if set to false will prevent auto downgrade of ssl connection.
     */
    public void connect(URI address, boolean allowDowngrade) {
        LOGGER.fine("attempting WebSocket connection to " + address.toString());
        webSocket = new WebSocket(address, this);
        locationManager.setAPWebSocket(webSocket);
        itemManager.setAPWebSocket(webSocket);
        webSocket.connect(allowDowngrade);
    }

    public void sendChat(String message) {
        if (webSocket == null)
            return;
        if (webSocket.isAuthenticated()) {
            webSocket.sendChat(message);
        }
    }

    public boolean checkLocation(long locationID) {
        return locationManager.checkLocation(locationID);
    }

    public boolean checkLocations(Collection<Long> locationIDs) {
        return locationManager.checkLocations(locationIDs);
    }

    public void scoutLocations(ArrayList<Long> locationIDs) {
        HashMap<Long, String> locations = dataPackage.getLocationsForGame(game);
        locationIDs.removeIf( location -> !locations.containsKey(location));
        webSocket.scoutLocation(locationIDs);
    }

    public abstract void onPrint(String print);

    public abstract void onPrintJson(APPrint apPrint, String type, int sending, NetworkItem receiving);

    public abstract void onError(Exception ex);

    public abstract void onClose(String Reason, int attemptingReconnect);

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

    public String getConnectedAddress() {
        if (isConnected())
            return webSocket.getRemoteSocketAddress().getHostName()+":"+ webSocket.getRemoteSocketAddress().getPort();
        else
            return "";
    }

    /**
     * this should not need to be called externally but is left public just in case.
     */
    public void reconnect() {
        webSocket.reconnect();
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

    public void setGameState(ClientStatus status) {
        if (webSocket == null)
            return;
        if (webSocket.isAuthenticated())
            webSocket.sendPacket(new StatusUpdatePacket(status));
    }

    public void sync() {
        webSocket.sendPacket(new SyncPacket());
    }

    public void sendBounce(BouncePacket bouncePacket) {
        if (webSocket == null)
            return;
        if (webSocket.isAuthenticated())
            webSocket.sendPacket(bouncePacket);
    }

    public void disconnect() {
        webSocket.close();
    }

    public Set<String> getTags() {
        return tags;
    }

    public int getItemsHandlingFlags() {
        return itemsHandlingFlags;
    }

    public void setItemsHandlingFlags(int itemsHandlingFlags) {
        this.itemsHandlingFlags = itemsHandlingFlags;
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    /**
     * Uses DataStorage to save a value on the AP server.
     *
     * @param setPacket
     */
    public int dataStorageSet(SetPacket setPacket) {
        if (webSocket == null || !webSocket.isAuthenticated())
            return 0;

        webSocket.sendPacket(setPacket);
        return setPacket.getRequestID();
    }

    /**
     * Registers to receive updates of when a key in the Datastorage has been changed on the server.
     *
     * @param keys List of Keys to be notified of.
     */
    public void dataStorageSetNotify(Collection<String> keys) {
        if (webSocket == null || !webSocket.isAuthenticated())
            return;
        webSocket.sendPacket(new SetNotifyPacket(keys));
    }

    /**
     * Uses DataStorage to reterieve a value from the server will get value back though a
     * {@link RetrievedEvent RetrievedEvent}. <br>
     * see following table for list of reserved keys.
     * <table>
     *     <tr>
     *         <th>Name</th>
     *         <th>Type</th>
     *         <th>Notes</th>
     *     </tr>
     *     <tr>
     *         <td> hints_{team}_{slot} </td>
     *         <td> list[Hint] </td>
     *         <td> All Hints belonging to the requested Player. </td>
     *     </tr>
     *     <tr>
     *         <td> slot_data_{slot} </td>
     *         <td> dict[str, any] </td>
     *         <td> slot_data belonging to the requested slot. </td>
     *     </tr>
     *     <tr>
     *         <td> item_name_groups_{game_name} </td>
     *         <td> dict[str, list[str]] </td>
     *         <td> item_name_groups belonging to the requested game. </td>
     *     </tr>
     * </table>
     *
     * @param keys a list of keys to retrieve values for
     */
    public int dataStorageGet(Collection<String> keys) {
        if (webSocket == null || !webSocket.isAuthenticated())
            return 0;

        GetPacket getPacket = new GetPacket(keys);
        webSocket.sendPacket(getPacket);
        return getPacket.getRequestID();
    }

}
