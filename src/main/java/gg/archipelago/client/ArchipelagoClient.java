package gg.archipelago.client;

import gg.archipelago.client.Print.APPrint;
import gg.archipelago.client.helper.DeathLink;
import gg.archipelago.client.network.client.*;
import gg.archipelago.client.network.server.ConnectUpdatePacket;
import gg.archipelago.client.network.server.RoomInfoPacket;
import gg.archipelago.client.parts.DataPackage;
import gg.archipelago.client.parts.NetworkItem;
import gg.archipelago.client.parts.Version;
import org.apache.hc.core5.net.URIBuilder;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class ArchipelagoClient {

    private final static Logger LOGGER = Logger.getLogger(ArchipelagoClient.class.getName());

    private final String dataPackageLocation = "./APData/DataPackage.ser";

    private int hintPoints;

    private ArchipelagoWebSocket archipelagoWebSocket;

    private String password;

    private final String UUID;

    private RoomInfoPacket roomInfo;

    private DataPackage dataPackage;

    public static ArchipelagoClient archipelagoClient;

    private final LocationManager locationManager;
    private final ItemManager itemManager;
    private final EventManager eventManager;

    public static final Version protocolVersion = new Version(0, 3, 7);

    private int team;
    private int slot;
    private String name = "Name not set";
    private String game = "Game not set";
    private String alias;
    private Set<String> tags = new HashSet<>();
    private int itemsHandlingFlags = 0b000;

    public ArchipelagoClient() {
        loadDataPackage();

        UUID = dataPackage.getUUID();

        eventManager = new EventManager();
        locationManager = new LocationManager(this);
        itemManager = new ItemManager(this);
        archipelagoClient = this;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public void setTags(Set<String> tags) {
        if (!this.tags.equals(tags)) {
            this.tags = tags;
            if (isConnected()) {
                ConnectUpdatePacket packet = new ConnectUpdatePacket();
                packet.tags = this.tags;
                archipelagoWebSocket.sendPacket(packet);
            }
        }
    }

    public void addTag(String tag) {
        if (!this.tags.contains(tag)) {
            tags.add(tag);
            if (isConnected()) {
                ConnectUpdatePacket packet = new ConnectUpdatePacket();
                packet.tags = this.tags;
                archipelagoWebSocket.sendPacket(packet);
            }
        }
    }

    public void removeTag(String tag) {
        if (this.tags.contains(tag)) {
            tags.remove(tag);
            if (isConnected()) {
                ConnectUpdatePacket packet = new ConnectUpdatePacket();
                packet.tags = this.tags;
                archipelagoWebSocket.sendPacket(packet);
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
            LOGGER.log(Level.WARNING, "unable to save DataPackage.", e);
        }
    }

    public boolean isConnected() {
        return archipelagoWebSocket != null && archipelagoWebSocket.isOpen();
    }

    public void close() {
        if (archipelagoWebSocket != null)
            archipelagoWebSocket.close();
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

    public void connect(String address) throws URISyntaxException {
        URIBuilder builder = new URIBuilder((!address.contains("//")) ? "//" + address : address);
        if (builder.getPort() == -1) { //set default port if not included
            builder.setPort(38281);
        }

        if (archipelagoWebSocket != null && archipelagoWebSocket.isOpen()) {
            LOGGER.fine("previous WebSocket is open, closing.");
            archipelagoWebSocket.close();
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

    public void connect(URI address, boolean allowDowngrade) {
        LOGGER.fine("attempting WebSocket connection to " + address.toString());
        archipelagoWebSocket = new ArchipelagoWebSocket(address, this);
        locationManager.setAPWebSocket(archipelagoWebSocket);
        itemManager.setAPWebSocket(archipelagoWebSocket);
        archipelagoWebSocket.connect(allowDowngrade);
    }

    public void sendChat(String message) {
        if (archipelagoWebSocket == null)
            return;
        if (archipelagoWebSocket.isAuthenticated()) {
            archipelagoWebSocket.sendChat(message);
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
        archipelagoWebSocket.scoutLocation(locationIDs);
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

    /**
     * this should not need to be called externally but is left public just in case.
     */
    public void reconnect() {
        archipelagoWebSocket.reconnect();
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
        if (archipelagoWebSocket == null)
            return;
        if (archipelagoWebSocket.isAuthenticated())
            archipelagoWebSocket.sendPacket(new StatusUpdatePacket(status));
    }

    public void sync() {
        archipelagoWebSocket.sendPacket(new SyncPacket());
    }

    public void sendBounce(BouncePacket bouncePacket) {
        if (archipelagoWebSocket == null)
            return;
        if (archipelagoWebSocket.isAuthenticated())
            archipelagoWebSocket.sendPacket(bouncePacket);
    }

    public void disconnect() {
        archipelagoWebSocket.close();
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
        if (archipelagoWebSocket == null || !archipelagoWebSocket.isAuthenticated())
            return 0;

        archipelagoWebSocket.sendPacket(setPacket);
        return setPacket.getRequestID();
    }

    /**
     * Registers to receive updates of when a key in the Datastorage has been changed on the server.
     *
     * @param keys List of Keys to be notified of.
     */
    public void dataStorageSetNotify(Collection<String> keys) {
        if (archipelagoWebSocket == null || !archipelagoWebSocket.isAuthenticated())
            return;
        archipelagoWebSocket.sendPacket(new SetNotifyPacket(keys));
    }

    /**
     * Uses DataStorage to reterieve a value from the server will get value back though a
     * {@link gg.archipelago.client.events.RetrievedEvent RetrievedEvent}. <br>
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
        if (archipelagoWebSocket == null || !archipelagoWebSocket.isAuthenticated())
            return 0;

        GetPacket getPacket = new GetPacket(keys);
        archipelagoWebSocket.sendPacket(getPacket);
        return getPacket.getRequestID();
    }
}
