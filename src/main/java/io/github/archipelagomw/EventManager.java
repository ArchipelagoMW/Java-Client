package io.github.archipelagomw;

import io.github.archipelagomw.events.ArchipelagoEventListener;
import io.github.archipelagomw.events.Event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages registering and calling events
 * @see #registerListener(Object)
 */
public class EventManager {

    private final Map<Method, Object> registeredListeners = new ConcurrentHashMap<>();

    /**
     * Use to register for Events that come from the Archipelago server.
     * supplied Object must have at least 1 method annotated with {@link ArchipelagoEventListener}
     * and have 1 parameter that extends {@link Event}
     * @param listener the object containing a listener method.
     */
    public void registerListener(Object listener) {
        Method[] methods = listener instanceof Class ? ((Class<?>)listener).getMethods() : listener.getClass().getMethods();
        for (Method method : methods) {
            if (isNotEventListenerMethod(listener, method)) continue;

            registeredListeners.put(method, listener);
        }
    }

    /**
     * Use to unregister for Events that come from the Archipelago server.
     * supplied Object must have at least 1 method annotated with {@link ArchipelagoEventListener}
     * and have 1 parameter that extends {@link Event}
     * @param listener the object containing a listener method.
     */
    public void unRegisterListener(Object listener) {
        Method[] methods = listener instanceof Class ? ((Class<?>)listener).getMethods() : listener.getClass().getMethods();
        for (Method method : methods) {
            if (isNotEventListenerMethod(listener, method)) continue;

            registeredListeners.remove(method, listener);
        }
    }

    private boolean isNotEventListenerMethod(Object listener, Method method) {
        if(listener instanceof Class<?>)
            if (!Modifier.isStatic(method.getModifiers()))
                return true;
        if (!method.isAnnotationPresent(ArchipelagoEventListener.class))
            return true;
        if (method.getParameterTypes().length != 1)
            return true;
        if (!Event.class.isAssignableFrom(method.getParameterTypes()[0]))
            return true;
        return false;
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
