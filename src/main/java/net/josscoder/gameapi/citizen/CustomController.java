package net.josscoder.gameapi.citizen;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.entity.EntityLevelChangeEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.inventory.transaction.data.TransactionData;
import cn.nukkit.inventory.transaction.data.UseItemOnEntityData;
import cn.nukkit.level.Level;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.InventoryTransactionPacket;
import org.citizen.CitizenLibrary;
import org.citizen.controller.Controllers;
import org.citizen.entity.Citizen;

public class CustomController extends Controllers {

  public CustomController(CitizenLibrary library) {
    super(library);
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void handle(DataPacketReceiveEvent event) {
    DataPacket packet = event.getPacket();
    Player player = event.getPlayer();

    if (
      packet instanceof InventoryTransactionPacket &&
      ((InventoryTransactionPacket) packet).transactionType ==
      InventoryTransactionPacket.TYPE_USE_ITEM_ON_ENTITY
    ) {
      TransactionData data =
        ((InventoryTransactionPacket) packet).transactionData;

      if (data instanceof UseItemOnEntityData) {
        Citizen citizen = getLibrary()
          .getFactory()
          .getCitizen(((UseItemOnEntityData) data).entityRuntimeId);

        if (citizen == null) {
          return;
        }

        citizen.callInvoke(player);
      }
    }
  }

  @Override
  @EventHandler(priority = EventPriority.NORMAL)
  public void handle(PlayerJoinEvent event) {
    Player player = event.getPlayer();

    getLibrary()
      .getFactory()
      .getCitizens()
      .forEach(
        (id, citizen) -> {
          if (
            citizen
              .getPosition()
              .getLevel()
              .getName()
              .equals(player.getLevel().getName())
          ) {
            citizen.spawnTo(player);
          }
        }
      );
  }

  @Override
  @EventHandler(priority = EventPriority.NORMAL)
  public void handle(PlayerQuitEvent event) {
    Player player = event.getPlayer();

    getLibrary()
      .getFactory()
      .getCitizens()
      .forEach((id, citizen) -> citizen.despairFrom(player));
  }

  @Override
  @EventHandler(priority = EventPriority.NORMAL)
  public void handle(EntityLevelChangeEvent event) {
    Entity entity = event.getEntity();

    if (!(entity instanceof Player)) return;

    Player player = ((Player) entity);
    Level origin = event.getOrigin();
    Level target = event.getTarget();

    getLibrary()
      .getFactory()
      .getCitizens()
      .forEach(
        (id, citizen) -> {
          String citizenLevelName = citizen.getPosition().getLevel().getName();
          if (citizenLevelName.equals(origin.getName())) {
            citizen.despairFrom(player);
          }

          if (citizenLevelName.equals(target.getName())) {
            citizen.spawnTo(player);
          }
        }
      );
  }
}
