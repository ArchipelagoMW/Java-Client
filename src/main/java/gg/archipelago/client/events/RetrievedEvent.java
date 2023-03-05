package gg.archipelago.client.events;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.internal.Primitives;
import com.google.gson.internal.bind.JsonTreeReader;

import java.lang.reflect.Type;
import java.util.HashMap;

public class RetrievedEvent implements Event {

    public HashMap<String, Object> data;
    private final int requestID;
    private final JsonObject jsonValue;

    public RetrievedEvent(HashMap<String, Object> keys, JsonObject jsonValue, int requestID) {
        data = keys;
        this.jsonValue = jsonValue;
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
        return (String) data.get(key);
    }

    public boolean getBoolean(String key) {
        return (boolean) data.get(key);
    }

    public Object getObject(String key) {
        return data.get(key);
    }

    public boolean containsKey(String key) {
        return data.containsKey(key);
    }

    public <T> T getValueAsObject(String key, Class<T> classOfT) {
        Object value = new Gson().fromJson(jsonValue.get(key), classOfT);
        return Primitives.wrap(classOfT).cast(value);
    }

    public <T> T getValueAsObject(String key, Type typeOfT) {
        return jsonValue == null ? null : new Gson().fromJson(new JsonTreeReader(jsonValue.get(key)), typeOfT);
    }

    public int getRequestID() {
        return requestID;
    }
}