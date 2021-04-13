package gg.archipelago.APClient;

import com.google.gson.Gson;
import gg.archipelago.APClient.itemmanager.ItemManager;
import gg.archipelago.APClient.locationmanager.LocationManager;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataManager {

    private final static Logger LOGGER = Logger.getLogger(DataManager.class.getName());

    private final String saveDataLocation = "./APData/SaveData.db";
    private final Gson gson = new Gson();

    LocationManager locationManager;
    ItemManager itemManager;

    SaveData save = new SaveData();

    public DataManager(LocationManager locationManager, ItemManager itemManager) {
        this.locationManager = locationManager;
        this.itemManager = itemManager;
    }

    public void load(String saveID, int slotID) {
        try {

            Path path = Paths.get(saveDataLocation);
            String json = Files.readAllLines(path).get(0);
            save = gson.fromJson(json,SaveData.class);

            //check if our saved ID matches the one we got from AP
            if(save.id.equals(saveID) && save.slotID == slotID) {
                locationManager.writeFromSave(save.checkedLocations);
                itemManager.writeFromSave(save.receivedItems, save.index);
            }
            else {
                save = new SaveData();
                save();
            }

        } catch (IOException e) {
            LOGGER.info("no SavePackage found creating a new one.");
            save = new SaveData();
            save();
        }
    }

    void save() {
        try {
            File saveDataPackage = new File(saveDataLocation);

            //noinspection ResultOfMethodCallIgnored
            saveDataPackage.getParentFile().mkdirs();
            //noinspection ResultOfMethodCallIgnored
            saveDataPackage.createNewFile();

            FileOutputStream fileOut = new FileOutputStream(saveDataPackage);

            String json = gson.toJson(save);
            fileOut.write(json.getBytes());

            fileOut.close();

        } catch (IOException e) {
            LOGGER.log(Level.WARNING,"unable to save DataPackage.",e);
        }
    }


}
