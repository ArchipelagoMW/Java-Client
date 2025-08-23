package io.github.archipelagomw;

import io.github.archipelagomw.bounce.BouncedManager;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.archipelagomw.events.LocationInfoEvent;
import io.github.archipelagomw.events.RetrievedEvent;
import io.github.archipelagomw.flags.ItemsHandling;
import io.github.archipelagomw.bounce.DeathLinkHandler;
import io.github.archipelagomw.network.client.*;
import io.github.archipelagomw.network.server.ConnectUpdatePacket;
import io.github.archipelagomw.network.server.RoomInfoPacket;
import io.github.archipelagomw.parts.DataPackage;
import io.github.archipelagomw.parts.Game;
import io.github.archipelagomw.parts.NetworkSlot;
import io.github.archipelagomw.parts.Version;
import org.apache.hc.core5.net.URIBuilder;

import com.google.gson.Gson;

import javax.net.SocketFactory;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Client {

    private final static Logger LOGGER = Logger.getLogger(Client.class.getName());

    public static final Version protocolVersion = new Version(0, 6, 1);
    private final static Gson gson = new Gson();

    private static final String OS = System.getProperty("os.name").toLowerCase();

    private static final Path cachePath;
    private static final Path datapackageCachePath;

    static
    {
        String appData = System.getenv("LOCALAPPDATA");
        String userHome = System.getProperty("user.home");
        String xdg = System.getenv("XDG_CACHE_HOME");

        if(OS.startsWith("windows"))
        {
            cachePath = Paths.get(appData, "Archipelago", "Cache");
        }
        else if(OS.startsWith("Mac") || OS.startsWith("Darwin"))
        {
            cachePath = Paths.get(userHome, "Library", "Caches", "Archipelago");
        }
        else if(xdg == null || xdg.isEmpty() )
        {
            cachePath = Paths.get(userHome, ".cache", "Archipelago");
        }
        else
        {
            cachePath = Paths.get(xdg, "Archipelago");
        }
        datapackageCachePath = cachePath.resolve("datapackage");

    }

    private static String uuid = null;

    private static Path dataPackageLocation;

    protected Map<String,String> versions;

    protected List<String> games;

    private int hintPoints;

    private WebSocket webSocket;

    private String password;

    private RoomInfoPacket roomInfo;

    private final DataPackage dataPackage = new DataPackage();

    public static Client client;

    private final LocationManager locationManager;
    private final ItemManager itemManager;
    private final EventManager eventManager;
    private final BouncedManager bouncedManager;
    private final DeathLinkHandler deathLinkHandler;

    private int team;
    private int slot;
    private Map<Integer, NetworkSlot> slotInfo;
    private String name = "Name not set";
    private String game = "Game not set";
    private String alias;
    private final Set<String> tags = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private int itemsHandlingFlags = 0b000;

    public Client() {
        dataPackageLocation = datapackageCachePath;
        eventManager = new EventManager();
        locationManager = new LocationManager(this);
        itemManager = new ItemManager(this);
        bouncedManager = new BouncedManager();
        deathLinkHandler = new DeathLinkHandler(this);
        bouncedManager.addHandler(deathLinkHandler);
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
     * @return Whether the send was successful or not
     */
    public APResult<Void> setTags(Set<String> tags) {
        if (!this.tags.equals(tags)) {
            this.tags.clear();
            this.tags.addAll(tags);
            APResult<Void> ret = ensureConnectedAndAuth();
            if(ret == null) {
                ConnectUpdatePacket packet = new ConnectUpdatePacket();
                packet.tags = this.tags;
                webSocket.sendPacket(packet);
                ret = APResult.success();
            }
            return ret;
        }
        return APResult.success();
    }

    /**
     * add a tag to your list, keeping all previous tags intact.
     * @param tag String tag to be added.
     * @return Whether the send was successful or not
     */
    public APResult<Void> addTag(String tag) {
        if(tags.add(tag)) {
            APResult<Void> ret = ensureConnectedAndAuth();
            if(ret == null) {
                ConnectUpdatePacket packet = new ConnectUpdatePacket();
                packet.tags = this.tags;
                webSocket.sendPacket(packet);
                ret = APResult.success();
            }
            return ret;
        }
        return APResult.success();
    }

    /**
     * removes supplied tag, if it exists.
     * @param tag String tag to be removed.
     * @return Whether the send was successful or not
     */
    public APResult<Void> removeTag(String tag) {
        if(tags.remove(tag)) {
            APResult<Void> ret = ensureConnectedAndAuth();
            if(ret == null) {
                ConnectUpdatePacket packet = new ConnectUpdatePacket();
                packet.tags = this.tags;
                webSocket.sendPacket(packet);
                ret = APResult.success();
            }
            return ret;
        }
        return APResult.success();
    }


    /**
     * Gets the UUID of clients on this machine
     * @return UUID of the client, this should theoretically never change.
     */
    public static String getUUID() {
        if(uuid == null)
        {
            synchronized (DataPackage.class)
            {
                if(uuid != null) {
                    return uuid;
                }
                String tmp = null;
                File common = cachePath.resolve("common.json").toFile();
                JsonObject data = new JsonObject();
                if(common.exists())
                {
                    try(BufferedReader reader = Files.newBufferedReader(common.toPath(), StandardCharsets.UTF_8))
                    {
                        data = gson.fromJson(reader, JsonObject.class);
                    }
                    catch(IOException ex)
                    {
                        LOGGER.log(Level.WARNING,"Failed to load common uuid", ex);
                        // We probably will fail to write
                        return uuid = UUID.randomUUID().toString();
                    }
                }
                JsonElement uuidEle = data.get("uuid");
                if(uuidEle != null && !uuidEle.isJsonNull())
                {
                    tmp = uuidEle.getAsString();
                }
                if(tmp != null)
                {
                    return uuid = tmp;
                }

                tmp = UUID.randomUUID().toString();
                data.addProperty("uuid", tmp);
                try(BufferedWriter writer = Files.newBufferedWriter(common.toPath(),StandardCharsets.UTF_8))
                {
                    writer.write(gson.toJson(data));
                }
                catch(IOException ex)
                {
                    LOGGER.log(Level.WARNING,"Failed to save common uuid", ex);
                }
                return uuid = tmp;
            }
        }
        return uuid;
    }

    protected void loadDataPackage() {
        synchronized (Client.class){
            File directoryPath = dataPackageLocation.toFile();

            if(!directoryPath.exists())
            {
                boolean success = directoryPath.mkdirs();
                if(success){
                    LOGGER.info("DataPackage directory didn't exist. Starting from a new one.");
                } else{
                    LOGGER.severe("Failed to make directories for datapackage cache.");
                }
                return;
            }

            //ensure the path to the cache exists
            if(!directoryPath.isDirectory()) {
                return;
            }
            //loop through all Archipelago cache folders to find valid data package files
            Map<String,File> localGamesList = new HashMap<String,File>();

            for(File gameDir : directoryPath.listFiles()){
                if(gameDir.isDirectory()){
                    localGamesList.put(gameDir.getName(), gameDir);
                }
            }

            if(localGamesList.isEmpty()){
                LOGGER.info("Datapackage is empty");
                return;
            }

            for(String gameName : games) {
                String safeName = Utils.getFileSafeName(gameName);
                File dir = localGamesList.get(safeName);

                if(null == dir){
                    continue;
                }

                //check all checksums
                for(File version : dir.listFiles()){
                    String versionStr = versions.get(gameName);
                    if(versionStr != null && versionStr.equals(version.getName())) {
                        try(FileReader reader = new FileReader(version)){
                            Game game = gson.fromJson(reader, Game.class);
                            dataPackage.update(gameName, game);
                            LOGGER.info("Read datapackage for Game: ".concat(gameName).concat(" Checksum: ").concat(version.getName()));
                        } catch (IOException e){
                            LOGGER.info("Failed to read a datapackage. Starting with a new one.");
                        }
                    }
                }
            }
        }
    }

    public void saveDataPackage() {
        synchronized (Client.class){
            //Loop through games to ensure we have folders for each of them in the cache
            for(String gameName : games){
                String safeName = Utils.getFileSafeName(gameName);
                File gameFolder = dataPackageLocation.resolve(safeName).toFile();
                if(!gameFolder.exists()){
                    //game folder not found. Make it
                    gameFolder.mkdirs();
                }

                //save the datapackage
                String gameVersion = versions.get(gameName); 
                if(gameVersion == null) { 
                    continue; 
                }

                //if key is for this game
                File filePath = dataPackageLocation.resolve(safeName).resolve(gameVersion).toFile();

                try (Writer writer = Files.newBufferedWriter(filePath.toPath(), StandardCharsets.UTF_8)){
                    //if game is in list of games, save it
                    gson.toJson(dataPackage.getGame(gameName), writer);
                    LOGGER.info("Saving datapackage for Game: ".concat(gameName).concat(" Checksum: ").concat(gameVersion));
                } catch (IOException e) {
                    LOGGER.warning("unable to save DataPackage.");
                }

            }
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

    void setSlotInfo(Map<Integer, NetworkSlot> slotInfo) {
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

    public Map<Integer, NetworkSlot> getSlotInfo() {return slotInfo;}

    /**
     * Works exactly like {@link #connect(URI, boolean)} with allowDowngrade set to true;
     * @param address
     * @throws URISyntaxException on malformed address
     */
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

    /**
     * Works exactly like {@link #connect(URI, boolean)} but allowDowngrade is False
     * @param address Address to connect to
     */
    public void connect(URI address) {
        connect(address, false);
    }

    /**
     * Equivalent to {@link #connect(URI, boolean, SocketFactory)} socketFactory being null.
     */
    public void connect(URI address, boolean allowDowngrade) {
        connect(address, allowDowngrade, null);
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
    public void connect(URI address, boolean allowDowngrade, SocketFactory socketFactory) {
        LOGGER.fine("attempting WebSocket connection to " + address.toString());
        webSocket = new WebSocket(address, this);
        if(null != socketFactory)
        {
            webSocket.setSocketFactory(socketFactory);
        }
        locationManager.setAPWebSocket(webSocket);
        itemManager.setAPWebSocket(webSocket);
        webSocket.connect(allowDowngrade);
    }


    /**
     * Sends a Chat message to all other connected Clients.
     * @param message Message to send.
     * @return Whether the send was successful or not
     */
    public APResult<Void> sendChat(String message) {
        APResult<Void> ret = ensureConnectedAndAuth();
        if(ret == null)
        {
            webSocket.sendChat(message);
            ret = APResult.success();
        }
        return ret;
    }

    /**
     * inform the Archipelago server that a location ID has been checked.
     * @param locationID id of a location.
     * @return Whether send was successful or not
     */
    public APResult<Void> checkLocation(long locationID) {
        return locationManager.checkLocation(locationID);
    }

    /**
     * inform the Archipelago server that a collection of location ID has been checked.
     * @param locationIDs a collection of a locations.
     * @return Whether send was successful or not
     */
    public APResult<Void> checkLocations(Collection<Long> locationIDs) {
        return locationManager.checkLocations(locationIDs);
    }

    /**
     * Ask the server for information about what is in locations. you will get a response in the {@link LocationInfoEvent} event.
     * @param locationIDs List of location ID's to request info on.
     * @return Whether the send was successful or not
     */
    public APResult<Void> scoutLocations(ArrayList<Long> locationIDs) {
        APResult<Void> ret = ensureConnectedAndAuth();
        if(ret == null)
        {
            locationIDs.removeIf( location -> !dataPackage.getGame(game).locationNameToId.containsValue(location));
            webSocket.scoutLocation(locationIDs);
            ret = APResult.success();
        }
        return ret;
    }

    /**
     *
     * Ask the server for information about what is in locations. you will get a response in the {@link LocationInfoEvent} event.
     * @param locationIDs List of location ID's to request info on.
     * @param createAsHint Whether to create hints for the provided locations
     * @return Whether the send was successful or not
     */
    public APResult<Void> scoutLocations(ArrayList<Long> locationIDs, CreateAsHint createAsHint)
    {
        APResult<Void> ret = ensureConnectedAndAuth();
        if(ret == null)
        {
            locationIDs.removeIf(location -> !dataPackage.getGame(game).locationNameToId.containsValue(location));
            webSocket.scoutlocations(locationIDs, createAsHint.value);
            ret = APResult.success();
        }
        return ret;
    }

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

    /**
     * gets the alias of this slot.
     * @return Alias of the slot connected to.
     */
    public String getAlias() {
        return alias;
    }

    /**
     * sets an Alias for this slot on the Archipelago server.
     * @param alias Name to set the alias to.
     */
    void setAlias(String alias) {
        this.alias = alias;
    }

    public LocationManager getLocationManager() {
        return locationManager;
    }

    public ItemManager getItemManager() {
        return itemManager;
    }

    protected <T> APResult<T> ensureConnectedAndAuth()
    {
        if(!isConnected()) {
            return APResult.disconnected();
        }
        if(!webSocket.isAuthenticated())
        {
            return APResult.unauthenticated();
        }
        return null;
    }
    /**
     * Update the current game status.
     * @see ClientStatus
     *
     * @param status a {@link ClientStatus} to send to the server.
     * @return Whether the send was successful or not
     */
    public APResult<Void> setGameState(ClientStatus status) {
        APResult<Void> ret = ensureConnectedAndAuth();
        if(ret == null) {
            webSocket.sendPacket(new StatusUpdatePacket(status));
            ret = APResult.success();
        }
        return ret;
    }

    /**
     * manually trigger a resync to the Archipelago server. this should be done automatically if the library detects a desync.
     * @return Whether the send was successful or not
     */
    public APResult<Void> sync() {
        APResult<Void> ret = ensureConnectedAndAuth();
        if(ret == null) {
            webSocket.sendPacket(new SyncPacket());
            ret = APResult.success();
        }
        return ret;
    }

    /**
     * sends a bouncepacket to the server, such as for deathlink
     * @param bouncePacket The packet to send
     * @return Whether the send was successful or not
     */
    public APResult<Void> sendBounce(BouncePacket bouncePacket) {
        APResult<Void> ret = ensureConnectedAndAuth();
        if(ret == null) {
            webSocket.sendPacket(bouncePacket);
            ret = APResult.success();
        }
        return ret;
    }

    /**
     * disconnects from a connected Archipelago server.
     */
    public void disconnect() {
        webSocket.close();
    }

    /**
     * @return set of tags currently in use.
     */
    public Set<String> getTags() {
        return tags;
    }

    /**
     * fetch the itemflags that have been set, bitwise Or against {@link ItemsHandling} to read.
     * @return items handling int.
     */
    public int getItemsHandlingFlags() {
        return itemsHandlingFlags;
    }

    /**
     * fetch the itemflags that have been set, bitwise Or against {@link ItemsHandling} to read.
     */
    public void setItemsHandlingFlags(int itemsHandlingFlags) {
        this.itemsHandlingFlags = itemsHandlingFlags;
    }

    /**
     * @return the event manager.
     */
    public EventManager getEventManager() {
        return eventManager;
    }

    /**
     * @return the bounced packet handler
     */
    public BouncedManager getBouncedManager()
    {
        return bouncedManager;
    }

    /**
     * Uses DataStorage to save a value on the AP server.
     * @return Whether the send was successful or not, and the requestId
     */
    public APResult<Integer> dataStorageSet(SetPacket setPacket) {
        APResult<Integer> ret = ensureConnectedAndAuth();
        if(ret == null) {
            webSocket.sendPacket(setPacket);
            ret = APResult.success(setPacket.getRequestID());
        }
        return ret;
    }

    /**
     * Registers to receive updates of when a key in the Datastorage has been changed on the server.
     *
     * @param keys List of Keys to be notified of.
     * @return Whether the send was successful or not
     */
    public APResult<Void> dataStorageSetNotify(Collection<String> keys) {
        APResult<Void> ret = ensureConnectedAndAuth();
        if(ret == null) {
            webSocket.sendPacket(new SetNotifyPacket(keys));
            ret = APResult.success();
        }
        return ret;
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
     *     <tr>
     *         <td> location_name_groups_{game_name} </td>
     *         <td> dict[str, list[str]] </td>
     *         <td> location_name_groups belonging to the requested game. </td>
     *     </tr>
     *     <tr>
     *         <td> client_status_{team}_{slot} </td>
     *         <td> ClientStatus </td>
     *         <td> The current game status of the requested player. </td>
     *     </tr>
     *     <tr>
     *         <td> race_mode </td>
     *         <td> int </td>
     *         <td> 0 if race mode is disabled, and 1 if it's enabled. </td>
     *     </tr>
     * </table>
     *
     * @param keys a list of keys to retrieve values for
     * @return Whether the send was successful or not, and the requestId
     */
    public APResult<Integer> dataStorageGet(Collection<String> keys) {
        GetPacket getPacket = new GetPacket(keys);
        APResult<Integer> ret = ensureConnectedAndAuth();
        if(ret == null) {
            webSocket.sendPacket(getPacket);
            ret = APResult.success(getPacket.getRequestID());
        }
        return ret;
    }

    /**
     * Helper for sending a death link bounce packet. You can send these without enabling death link first, but it is frowned upon.
     * @param source A String that is the name of the player sending the death link (does not have to be slot name)
     * @param cause A String that is the cause of this death. may be empty.
     * @return Whether the send was successful or not
     */
    public APResult<Void> sendDeathlink(String source, String cause)
    {
        APResult<Void> ret = ensureConnectedAndAuth();
        if(ret == null) {
            deathLinkHandler.sendDeathLink(source, cause);
            ret = APResult.success();
        }
        return ret;
    }

    /**
     * Enable or disable receiving death links.
     * @param enabled set to TRUE to enable death links, FALSE to disable.
     * @return Whether the send was successful or not
     */
    public APResult<Void> setDeathLinkEnabled(boolean enabled) {
        if(enabled)
            return addTag(DeathLinkHandler.DEATHLINK_TAG);
        else
            return removeTag(DeathLinkHandler.DEATHLINK_TAG);
    }


}
