package gg.archipelago.client;

import gg.archipelago.client.events.Event;
import gg.archipelago.client.events.ArchipelagoEventListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class EventManager {

    private final Map<Method, Object> registeredListeners = new HashMap<>();

    public void registerListener(Object listener) {
        for (Method method : listener.getClass().getMethods()) {
            if (!method.isAnnotationPresent(ArchipelagoEventListener.class))
                continue;
            if (method.getParameterTypes().length != 1)
                continue;
            if (!Event.class.isAssignableFrom(method.getParameterTypes()[0]))
                continue;

            registeredListeners.put(method, listener);
        }
    }

    public void callEvent(final Event event) {
        for (Map.Entry<Method, Object> methodObjectEntry : registeredListeners.entrySet()) {
            Method method = methodObjectEntry.getKey();
            try {
                if( method.getParameterTypes()[0] == event.getClass())
                    method.invoke(methodObjectEntry.getValue(), event);
            } catch (InvocationTargetException | IllegalAccessException ex) {
                ex.printStackTrace();
            }
        }
    }
}
