package net.josscoder.gameapi.customitem.handler;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import net.josscoder.gameapi.user.User;

public interface EntityHandler {
  void handle(
    DataPacketReceiveEvent event,
    User fromUser,
    Player from,
    Entity to
  );
}
