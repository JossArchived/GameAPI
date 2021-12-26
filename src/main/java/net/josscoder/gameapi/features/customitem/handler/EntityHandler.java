package net.josscoder.gameapi.features.customitem.handler;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import net.josscoder.gameapi.user.User;

public interface EntityHandler {
  void execute(
    DataPacketReceiveEvent event,
    User fromUser,
    Player from,
    Entity to
  );
}
