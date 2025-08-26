package io.github.archipelagomw.network.client;

import io.github.archipelagomw.network.APPacket;
import io.github.archipelagomw.network.APPacketType;

import java.util.ArrayList;
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
}
