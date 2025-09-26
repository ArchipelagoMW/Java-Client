package io.github.archipelagomw;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.archipelagomw.utils.IntEnumAdapterFactory;

// Default visibility until a good way of having Gson shared is decided.
class GsonUtils {

    // TODO: for some reason, a lot of instances of this class get created everywhere, and it's unnecessary
    private static final Gson gson = new GsonBuilder().registerTypeAdapterFactory(new IntEnumAdapterFactory()).create();

    static Gson getGson()
    {
        return gson;
    }
}
