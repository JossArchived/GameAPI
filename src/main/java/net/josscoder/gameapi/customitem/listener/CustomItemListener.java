package net.josscoder.gameapi.customitem.listener;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.inventory.InventoryEvent;
import cn.nukkit.event.inventory.InventoryTransactionEvent;
import cn.nukkit.event.player.PlayerDropItemEvent;
import cn.nukkit.item.Item;
import net.josscoder.gameapi.Game;
import net.josscoder.gameapi.api.listener.GameListener;
import net.josscoder.gameapi.customitem.CustomItem;
import net.josscoder.gameapi.customitem.factory.CustomItemFactory;
import net.josscoder.gameapi.user.User;
import net.josscoder.gameapi.util.TimeUtils;

abstract class CustomItemListener extends GameListener {

  public CustomItemListener(Game game) {
    super(game);
  }

  public void handleCancel(Item item, Cancellable event) {
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

    if (
      (
        event instanceof InventoryEvent ||
        event instanceof PlayerDropItemEvent ||
        event instanceof InventoryTransactionEvent
      ) &&
      customItem.isTransferable()
    ) {
      return;
    }

    event.setCancelled();
  }

  public boolean handleInteraction(Player player) {
    User user = userFactory.get(player);

    if (user == null) {
      return false;
    }

    Item item = player.getInventory().getItemInHand();

    if (item == null || item.getCustomBlockData() == null) {
      return false;
    }

    String uuid = item.getCustomBlockData().getString("customItem");

    if (uuid == null) {
      return false;
    }

    CustomItem customItem = CustomItemFactory.get(uuid);

    if (
      customItem == null ||
      user.getLocalStorage().getLong("item") +
      500 >=
      TimeUtils.getTimestampMilli()
    ) {
      return false;
    }

    if (customItem.getSound() != null) {
      user.playSound(customItem.getSound());
    }

    user.getLocalStorage().set("item", TimeUtils.getTimestampMilli());

    customItem.executeAction(user, player);

    return true;
  }
}
