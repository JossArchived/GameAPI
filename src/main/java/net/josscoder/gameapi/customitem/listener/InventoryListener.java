package net.josscoder.gameapi.customitem.listener;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.inventory.InventoryMoveItemEvent;
import cn.nukkit.event.inventory.InventoryPickupItemEvent;
import cn.nukkit.event.inventory.InventoryTransactionEvent;
import cn.nukkit.event.player.PlayerDropItemEvent;
import cn.nukkit.inventory.transaction.action.InventoryAction;
import cn.nukkit.item.Item;
import net.josscoder.gameapi.Game;
import net.josscoder.gameapi.customitem.CustomItem;
import net.josscoder.gameapi.customitem.factory.CustomItemFactory;
import net.josscoder.gameapi.listener.GameListener;
import net.josscoder.gameapi.user.User;

public class InventoryListener extends GameListener {

  public InventoryListener(Game game) {
    super(game);
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onDrop(PlayerDropItemEvent event) {
    Item item = event.getItem();

    if (item == null) {
      return;
    }

    if (item.getCustomBlockData() == null) {
      return;
    }

    String uuid = item.getCustomBlockData().getString("customItem");

    if (uuid == null) {
      return;
    }

    CustomItem customItem = CustomItemFactory.get(uuid);

    if (customItem == null) {
      return;
    }

    event.setCancelled();
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onPickup(InventoryPickupItemEvent event) {
    Item item = event.getItem().getItem();

    if (item == null) {
      return;
    }

    Player player = event.getViewers()[0];

    User user = userFactory.get(player);

    if (user == null) {
      return;
    }

    if (item.getCustomBlockData() == null) {
      return;
    }

    String uuid = item.getCustomBlockData().getString("customItem");

    if (uuid == null) {
      return;
    }

    CustomItem customItem = CustomItemFactory.get(uuid);

    if (customItem == null) {
      return;
    }

    event.setCancelled();
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onInventoryMove(InventoryMoveItemEvent event) {
    Item item = event.getItem();

    if (item == null) {
      return;
    }

    if (item.getCustomBlockData() == null) {
      return;
    }

    String uuid = item.getCustomBlockData().getString("customItem");

    if (uuid == null) {
      return;
    }

    CustomItem customItem = CustomItemFactory.get(uuid);

    if (customItem == null || customItem.isTransferable()) {
      return;
    }

    event.setCancelled();
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onInventoryTransaction(InventoryTransactionEvent event) {
    for (InventoryAction action : event.getTransaction().getActions()) {
      Item item = action.getSourceItem() != null
        ? action.getSourceItem()
        : action.getTargetItem();

      if (item == null) {
        return;
      }

      if (item.getCustomBlockData() == null) {
        return;
      }

      String uuid = item.getCustomBlockData().getString("customItem");

      if (uuid == null) {
        return;
      }

      CustomItem customItem = CustomItemFactory.get(uuid);

      if (customItem == null || customItem.isTransferable()) {
        return;
      }

      event.setCancelled();
    }
  }
}
