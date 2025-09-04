package io.github.archipelagomw.utils;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.github.archipelagomw.network.client.HintStatus;

import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class IntEnumAdapterFactory implements TypeAdapterFactory {
    private static final Logger logger = Logger.getLogger(IntEnumAdapterFactory.class.getCanonicalName());

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {

        if(IntEnum.class.isAssignableFrom(typeToken.getRawType()) && typeToken.getRawType().isEnum())
        {
            try {
                return new IntEnumAdapter(typeToken.getRawType());
            }
            catch(ReflectiveOperationException ex)
            {
                logger.log(Level.WARNING, "Error while initializing int enum adapter", ex);
            }
        }
        return null;
    }

    public static void main(String[] args) throws Throwable {
        new IntEnumAdapter<>(HintStatus.class);
    }

    private static class IntEnumAdapter<T extends Enum<T> & IntEnum> extends TypeAdapter<T>
    {

        private final Map<Integer, T> lookupMap;

        public IntEnumAdapter(Class<T> clazz) throws ReflectiveOperationException {
            Method m = HintStatus.class.getMethod("values");
            T[] values = (T[]) m.invoke(null);
            lookupMap = Arrays.stream(values).collect(Collectors.<T,Integer, T>toMap(IntEnum::getValue, Function.identity()));
        }

        @Override
        public void write(JsonWriter jsonWriter, T intEnum) throws IOException {
            jsonWriter.value(intEnum.getValue());
        }

        @Override
        public T read(JsonReader jsonReader) throws IOException {
            return lookupMap.get(jsonReader.nextInt());
        }
    };
}
