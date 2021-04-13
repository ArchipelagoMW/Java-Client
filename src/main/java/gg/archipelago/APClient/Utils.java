package gg.archipelago.APClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.logging.Logger;

class Utils {

    private final static Logger LOGGER = Logger.getLogger(Utils.class.getName());

    static String GetUUID() {
        String OS = System.getProperty("os.name").toLowerCase();
        if (OS.contains("win")) {
            StringBuilder output = new StringBuilder();
            Process process;
            String[] cmd = {"wmic", "csproduct", "get", "UUID"};
            try {
                process = Runtime.getRuntime().exec(cmd);
                process.waitFor();
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return output.toString();
        } else if (OS.contains("nix") || OS.contains("nux") || OS.contains("aix")) {

            StringBuilder output = new StringBuilder();
            Process process;
            String[] cmd = {"cat /sys/class/dmi/id/product_uuid"};
            try {
                process = Runtime.getRuntime().exec(cmd);
                process.waitFor();
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return output.toString();
        }

        return "";
    }
}
