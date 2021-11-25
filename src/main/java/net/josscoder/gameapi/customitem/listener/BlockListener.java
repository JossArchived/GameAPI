package net.josscoder.gameapi.customitem.listener;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.item.Item;
import net.josscoder.gameapi.Game;
import net.josscoder.gameapi.customitem.CustomItem;
import net.josscoder.gameapi.customitem.factory.CustomItemFactory;
import net.josscoder.gameapi.listener.GameListener;

public class BlockListener extends GameListener {

  public BlockListener(Game game) {
    super(game);
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onBreak(BlockBreakEvent event) {
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
  public void onPlace(BlockPlaceEvent event) {
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
}
