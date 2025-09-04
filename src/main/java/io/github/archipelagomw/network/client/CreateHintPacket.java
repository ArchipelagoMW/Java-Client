package io.github.archipelagomw.network.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.archipelagomw.network.APPacket;
import io.github.archipelagomw.network.APPacketType;
import io.github.archipelagomw.utils.IntEnum;
import io.github.archipelagomw.utils.IntEnumAdapterFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CreateHintPacket extends APPacket {

    public List<Long> locations = new ArrayList<>();
    public int player;
    public HintStatus status = HintStatus.HINT_UNSPECIFIED;

    public CreateHintPacket(List<Long> locations, int player) {
        this(locations, player, HintStatus.HINT_UNSPECIFIED);
    }

    public CreateHintPacket(List<Long> locations, int player, HintStatus status) {
        super(APPacketType.CreateHints);
        this.locations = locations;
        this.player = player;
        this.status = status;
    }

    public static void main(String[] args) {
        Gson gson = new GsonBuilder().registerTypeAdapterFactory(new IntEnumAdapterFactory()).create();
        CreateHintPacket packet = new CreateHintPacket(Arrays.asList(10L), 1, HintStatus.HINT_UNSPECIFIED);
        String result;
        System.out.println(result = gson.toJson(packet));
        CreateHintPacket round = gson.fromJson(result, CreateHintPacket.class);
        System.out.println(round.status);

    }
}
