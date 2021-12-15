/*
 * Copyright 2021-2055 Josscoder
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package net.josscoder.gameapi.phase.base;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.block.*;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityExplodeEvent;
import cn.nukkit.event.entity.EntityExplosionPrimeEvent;
import cn.nukkit.event.inventory.*;
import cn.nukkit.event.player.*;
import cn.nukkit.inventory.*;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import java.time.Duration;
import net.josscoder.gameapi.Game;
import net.josscoder.gameapi.phase.GamePhase;

public abstract class LobbyPhase extends GamePhase {

  public LobbyPhase(Game game) {
    super(game);
  }

  public LobbyPhase(Game game, Duration duration) {
    super(game, duration);
  }

  @Override
  protected void onStart() {}

  @Override
  protected void onEnd() {}

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onMove(PlayerMoveEvent event) {
    Player player = event.getPlayer();

    if (player.y <= 1) {
      game.getWaitingRoomMap().teleportToSafeSpawn(player);
    }
  }

  @EventHandler
  public void onInventoryTransaction(InventoryTransactionEvent event) {
    event.setCancelled();
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onBreak(BlockBreakEvent event) {
    event.setCancelled();
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlace(BlockPlaceEvent event) {
    event.setCancelled();
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerDropItem(PlayerDropItemEvent event) {
    event.setCancelled();
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onEnchantItem(EnchantItemEvent event) {
    event.setCancelled();
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onFurnaceBurn(FurnaceBurnEvent event) {
    event.setCancelled();
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onFurnaceSmelt(FurnaceSmeltEvent event) {
    event.setCancelled();
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPickupArrow(InventoryPickupArrowEvent event) {
    event.setCancelled();
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPickupTrident(InventoryPickupTridentEvent event) {
    event.setCancelled();
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onBrew(BrewEvent event) {
    event.setCancelled();
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onInventoryClick(InventoryClickEvent event) {
    event.setCancelled();
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onInventoryMoveItem(InventoryMoveItemEvent event) {
    event.setCancelled();
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onIgnite(BlockIgniteEvent event) {
    event.setCancelled();
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onSpread(BlockSpreadEvent event) {
    event.setCancelled();
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onLeavesDecay(LeavesDecayEvent event) {
    event.setCancelled();
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onLiquidFlow(LiquidFlowEvent event) {
    event.setCancelled();
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onEntityExplode(EntityExplodeEvent event) {
    event.setCancelled();
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onEntityExplosionPrimed(EntityExplosionPrimeEvent event) {
    event.setCancelled();
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onDeath(PlayerDeathEvent event) {
    event.setCancelled();
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onEntityDamage(EntityDamageEvent event) {
    event.setCancelled();
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onInteractBlock(PlayerInteractEvent event) {
    Block block = event.getBlock();

    if (block == null || block.getId() == Block.AIR) {
      return;
    }

    event.setCancelled();
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onInteractItem(PlayerInteractEvent event) {
    Item item = event.getItem();

    if (item == null || item.getId() == Item.AIR) {
      return;
    }

    CompoundTag nbt = item.getCustomBlockData();

    if (nbt != null && nbt.contains("customItem")) {
      return;
    }

    event.setCancelled();
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onItemFrameDrop(ItemFrameDropItemEvent event) {
    event.setCancelled();
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onPickupItem(InventoryPickupItemEvent event) {
    event.setCancelled();
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onBedEnter(PlayerBedEnterEvent event) {
    event.setCancelled();
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onBedLeave(PlayerBedLeaveEvent event) {
    event.setCancelled();
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onBucketFill(PlayerBucketFillEvent event) {
    event.setCancelled();
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onOpenCraftingTable(CraftingTableOpenEvent event) {
    event.setCancelled();
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onInventoryOpen(InventoryOpenEvent event) {
    Inventory inventory = event.getInventory();
    if (
      inventory instanceof AnvilInventory ||
      inventory instanceof BeaconInventory ||
      inventory instanceof BrewingInventory ||
      inventory instanceof CraftingGrid ||
      inventory instanceof DispenserInventory ||
      inventory instanceof DropperInventory ||
      inventory instanceof EnchantInventory ||
      inventory instanceof FurnaceInventory ||
      inventory instanceof FurnaceRecipe ||
      inventory instanceof HopperInventory ||
      inventory instanceof ShapedRecipe ||
      inventory instanceof ShapelessRecipe ||
      inventory instanceof MixRecipe
    ) {
      event.setCancelled();
    }
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onCraftItem(CraftItemEvent event) {
    event.setCancelled();
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onPlayerAchievementAwarded(PlayerAchievementAwardedEvent event) {
    event.setCancelled();
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onLevelFoodChange(PlayerFoodLevelChangeEvent event) {
    event.setCancelled();
  }
}
