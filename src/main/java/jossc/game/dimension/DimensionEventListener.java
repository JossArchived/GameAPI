package jossc.game.dimension;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityLevelChangeEvent;
import cn.nukkit.network.protocol.ChangeDimensionPacket;
import cn.nukkit.network.protocol.PlayStatusPacket;

public class DimensionEventListener implements Listener {

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onEntityLevelChange(EntityLevelChangeEvent event) {
    Entity entity = event.getEntity();

    if (!(entity instanceof Player) || event.isCancelled()) {
      return;
    }

    sendChangeDimensionPacket(
      ((Player) entity).getPlayer(),
      DimensionIds.THE_END
    );
  }

  public void sendChangeDimensionPacket(Player player, int dimension) {
    ChangeDimensionPacket changeDimensionPacket = new ChangeDimensionPacket();

    changeDimensionPacket.dimension = dimension;
    changeDimensionPacket.x = (float) player.x;
    changeDimensionPacket.y = (float) player.y;
    changeDimensionPacket.z = (float) player.z;

    player.dataPacket(changeDimensionPacket);

    player
      .getServer()
      .getScheduler()
      .scheduleDelayedTask(
        () -> {
          PlayStatusPacket playStatusPacket = new PlayStatusPacket();
          playStatusPacket.status = PlayStatusPacket.PLAYER_SPAWN;
          player.dataPacket(playStatusPacket);
        },
        10
      );
  }
}
