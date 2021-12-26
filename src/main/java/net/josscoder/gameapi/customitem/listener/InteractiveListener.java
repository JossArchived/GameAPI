package net.josscoder.gameapi.customitem.listener;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.inventory.transaction.data.TransactionData;
import cn.nukkit.inventory.transaction.data.UseItemData;
import cn.nukkit.inventory.transaction.data.UseItemOnEntityData;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.InventoryTransactionPacket;
import cn.nukkit.network.protocol.PlayerActionPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import net.josscoder.gameapi.Game;
import net.josscoder.gameapi.customitem.CustomItem;
import net.josscoder.gameapi.customitem.factory.CustomItemFactory;
import net.josscoder.gameapi.user.User;
import net.josscoder.gameapi.util.TimeUtils;

public class InteractiveListener extends CustomItemListener {

  public InteractiveListener(Game game) {
    super(game);
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onInteract(DataPacketReceiveEvent event) {
    DataPacket packet = event.getPacket();

    if (
      packet.pid() != ProtocolInfo.INVENTORY_TRANSACTION_PACKET &&
      packet.pid() != ProtocolInfo.PLAYER_ACTION_PACKET
    ) {
      return;
    }

    if (
      packet instanceof PlayerActionPacket &&
      ((PlayerActionPacket) packet).action ==
      PlayerActionPacket.ACTION_START_BREAK
    ) {
      if (handleAction(event.getPlayer())) {
        return;
      }
    }

    if (!(packet instanceof InventoryTransactionPacket)) {
      return;
    }

    InventoryTransactionPacket inventoryPacket = (InventoryTransactionPacket) packet;

    if (
      inventoryPacket.transactionType !=
      InventoryTransactionPacket.TYPE_USE_ITEM ||
      !(inventoryPacket.transactionData instanceof UseItemData)
    ) {
      return;
    }

    handleAction(event.getPlayer());
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onDamageInteract(DataPacketReceiveEvent event) {
    DataPacket packet = event.getPacket();

    if (packet.pid() != ProtocolInfo.INVENTORY_TRANSACTION_PACKET) {
      return;
    }

    if (!(packet instanceof InventoryTransactionPacket)) {
      return;
    }

    Player player = event.getPlayer();
    Level level = player.getLevel();

    User user = userFactory.get(player);

    if (user == null) {
      return;
    }

    InventoryTransactionPacket inventoryPacket = (InventoryTransactionPacket) packet;

    if (
      inventoryPacket.transactionType !=
      InventoryTransactionPacket.TYPE_USE_ITEM_ON_ENTITY
    ) {
      return;
    }

    TransactionData data = inventoryPacket.transactionData;

    if (!(data instanceof UseItemOnEntityData)) {
      return;
    }

    UseItemOnEntityData useItemOnEntityData = ((UseItemOnEntityData) data);

    Entity entity = level.getEntity(useItemOnEntityData.entityRuntimeId);

    if (entity == null) {
      return;
    }

    Item item = player.getInventory().getItemInHand();

    if (item == null || item.getCustomBlockData() == null) {
      return;
    }

    String uuid = item.getCustomBlockData().getString("customItem");

    if (uuid == null) {
      return;
    }

    CustomItem customItem = CustomItemFactory.get(uuid);

    if (
      customItem == null ||
      user.getLocalStorage().getLong("item") +
      500 >=
      TimeUtils.getTimestampMilli()
    ) {
      return;
    }

    if (customItem.getSound() != null) {
      user.playSound(customItem.getSound());
    }

    user.getLocalStorage().set("item", TimeUtils.getTimestampMilli());

    if (
      useItemOnEntityData.actionType ==
      InventoryTransactionPacket.USE_ITEM_ON_ENTITY_ACTION_INTERACT
    ) {
      customItem.executeEntityInteract(user, player, entity, event);
    } else {
      customItem.handleEntityDamage(user, player, entity, event);
    }
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onBreak(BlockBreakEvent event) {
    handleCancel(event.getItem(), event);
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onPlace(BlockPlaceEvent event) {
    handleCancel(event.getItem(), event);
  }
}
