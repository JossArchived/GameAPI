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
import net.josscoder.gameapi.user.User;

public class TransferableListener extends CustomItemListener {

  public TransferableListener(Game game) {
    super(game);
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onDrop(PlayerDropItemEvent event) {
    handleCancel(event.getItem(), event);
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onPickup(InventoryPickupItemEvent event) {
    Player player = event.getViewers()[0];

    User user = userFactory.get(player);

    if (user == null) {
      return;
    }

    handleCancel(event.getItem().getItem(), event);
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onInventoryMove(InventoryMoveItemEvent event) {
    handleCancel(event.getItem(), event);
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onInventoryTransaction(InventoryTransactionEvent event) {
    for (InventoryAction action : event.getTransaction().getActions()) {
      Item item = action.getSourceItem() != null
        ? action.getSourceItem()
        : action.getTargetItem();

      handleCancel(item, event);
    }
  }
}
