package gg.archipelago.client.events;

import java.util.HashMap;

public class RetrievedEvent implements Event {

    public HashMap<String, Object> data;
    private final int requestID;
    public RetrievedEvent(HashMap<String, Object> keys , int requestID) {
        data = keys;
        this.requestID = requestID;
    }

    public int getInt(String key) {
        return (Integer) data.get(key);
    }

    public float getFloat(String key) {
        return (Float) data.get(key);
    }

    public double getDouble(String key) {
        return (Double) data.get(key);
    }

    public String getString(String key) {
        return (String)data.get(key);
    }

    public boolean getBoolean(String key) {
        return (boolean)data.get(key);
    }

    public Object getObject(String key) {
        return data.get(key);
    }

    public boolean containsKey(String key) {
        return data.containsKey(key);
    }

    public int getRequestID() {
        return requestID;
    }
}