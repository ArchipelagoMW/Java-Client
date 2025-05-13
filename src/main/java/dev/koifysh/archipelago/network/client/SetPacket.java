package dev.koifysh.archipelago.network.client;

import com.google.gson.annotations.SerializedName;
import dev.koifysh.archipelago.events.SetReplyEvent;
import dev.koifysh.archipelago.network.APPacket;
import dev.koifysh.archipelago.network.APPacketType;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class SetPacket extends APPacket {
    private static final AtomicInteger requestIdGen = new AtomicInteger();
    /**
     * The key to manipulate. Can never start with "_read".
     */
    @SerializedName("key")
    public String key;

    /**
     * The default value to use in case the key has no value on the server.
     */
    @SerializedName("default")
    public Object defaultValue;

    /**
     * If true, the server will send a {@link SetReplyEvent SetReplyEvent} response back to the client.
     */
    @SerializedName("want_reply")
    public boolean want_reply = false;


    /**
     * Operations to apply to the value on the server, multiple operations can be present,
     * and they will be executed in order of appearance.
     */
    @SerializedName("operations")
    public ArrayList<DataStorageOperation> operations = new ArrayList<>();

    @SerializedName("request_id")
    private final int requestID;

    public SetPacket(String key, Object defaultValue) {
        super(APPacketType.Set);
        this.key = key;
        this.defaultValue = defaultValue;
        requestID = requestIdGen.getAndIncrement();
    }

    /**
     * adds a {@link DataStorageOperation} to be performed to the value on the server, these are applied
     * in the order that you add them.
     * @param operation {@link Operation} to apply
     * @param value a `value` for use with the {@link Operation}
     */
    public void addDataStorageOperation(Operation operation, Object value) {
        operations.add(new DataStorageOperation(operation, value));
    }

    public int getRequestID() {
        return requestID;
    }

    /**
     * A DataStorageOperation manipulates or alters the value of a key in the servers data storage.
     * If the operation transforms the value from one state to another then the current value
     * of the key is used as the starting point otherwise the Set's package default is used if
     * the key does not exist on the server already. DataStorageOperations consist of an object
     * containing both the operation to be applied, and the value to be used for that operation.
     */
    private static class DataStorageOperation {
        @SerializedName("operation")
        Operation operation;
        @SerializedName("value")
        Object value;
        DataStorageOperation(Operation operation, Object value) {
            this.operation = operation;
            this.value = value;
        }
    }

    /**
     * A List of operations that can be performed on the value stored on the server.<br>
     * {@link #REPLACE},
     * {@link #DEFAULT},
     * {@link #ADD},
     * {@link #MULTIPLY},
     * {@link #POWER},
     * {@link #MODULO},
     * {@link #MAX},
     * {@link #MIN},
     * {@link #AND},
     * {@link #OR},
     * {@link #XOR},
     * {@link #LEFT_SHIFT},
     * {@link #RIGHT_SHIFT},
     * {@link #REMOVE},
     * {@link #POP},
     * {@link #UPDATE}
     */
    public enum Operation {
        /**
         * Sets the current value of the key on the server to the value given in {@link SetPacket#addDataStorageOperation addDataStorageOperation(Operation, Value)}.
         */
        @SerializedName("replace")
        REPLACE,

        /**
         * If the key has no value yet, Sets the current value of the key on the server to the default of the
         * Set's package (@value is ignored).
         */
        @SerializedName("default")
        DEFAULT,

        /**
         * Adds value to the current value of the key on the server, if both the current value and value
         * are arrays then value will be appended to the current @value.
         */
        @SerializedName("add")
        ADD,

        /**
         * Multiplies the current value of the key on the server by @value.
         */
        @SerializedName("mul")
        MULTIPLY,

        /**
         * Multiplies the current value of the key on the server to the power of @value.
         */
        @SerializedName("pow")
        POWER,

        /**
         * Sets the current value of the key on the server to the remainder after division by @value.
         */
        @SerializedName("mod")
        MODULO,

        /**
         * Sets the current value of the key on the server to @value if @value is bigger.
         */
        @SerializedName("max")
        MAX,

        /**
         * Sets the current value of the key on the server to @value if @value is lower.
         */
        @SerializedName("min")
        MIN,

        /**
         * Applies a bitwise AND to the current value on the server of the key with @value.
         */
        @SerializedName("and")
        AND,

        /**
         * Applies a bitwise OR to the current value of the key on the server with @value.
         */
        @SerializedName("default")
        OR,

        /**
         * Applies a bitwise Exclusive OR to the current value of the key on the server with @value.
         */
        @SerializedName("xor")
        XOR,

        /**
         * Applies a bitwise left-shift to the current value of the key on the server by @value.
         */
        @SerializedName("left_shift")
        LEFT_SHIFT,

        /**
         * Applies a bitwise right-shift to the current value of the key on the server by @value.
         */
        @SerializedName("right_shift")
        RIGHT_SHIFT,

        /**
         * List only: removes the first instance of @value found in the list.
         */
        @SerializedName("remove")
        REMOVE,

        /**
         * List or Dict: for lists it will remove the index of the @value given. for dicts it removes the element with the specified key of value.
         */
        @SerializedName("pop")
        POP,

        /**
         * Dict only: Updates the dictionary with the specified elements given in @value creating new keys, or updating old ones if they previously existed.
         */
        @SerializedName("update")
        UPDATE

    }
}
