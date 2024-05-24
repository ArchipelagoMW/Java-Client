package dev.koifysh.archipelago;

import dev.koifysh.archipelago.events.ArchipelagoEventListener;
import dev.koifysh.archipelago.events.Event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages registering and calling events
 * @see #registerListener(Object)
 */
public class EventManager {

    private final Map<Method, Object> registeredListeners = new HashMap<>();

    /**
     * Use to register for Events that come from the Archipelago server.
     * supplied Object must have at least 1 method annotated with {@link ArchipelagoEventListener}
     * and have 1 parameter that extends {@link Event}
     * @param listener the object containing a listener method.
     */
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
