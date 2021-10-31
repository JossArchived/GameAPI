package jossc.game.utils;

import cn.nukkit.Player;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.item.Item;
import cn.nukkit.utils.TextFormat;

public class PlayerUtils {

  public static void clearAllArmorInventory(Player player) {
    Item air = Item.get(Item.AIR);

    PlayerInventory inventory = player.getInventory();
    inventory.setHelmet(air);
    inventory.setChestplate(air);
    inventory.setLeggings(air);
    inventory.setBoots(air);

    updateInventory(player);
  }

  public static void clearAllInventory(Player player) {
    player.getInventory().clearAll();
    clearAllArmorInventory(player);
    updateInventory(player);
  }

  public static void updateInventory(Player player) {
    PlayerInventory inventory = player.getInventory();
    inventory.sendArmorContents(player);
    inventory.sendContents(player);
    inventory.sendHeldItem(player);
  }

  public static void giveDefaultAttributes(Player player) {
    player.setAllowFlight(false);
    player.setHealth(20);
    player.setMaxHealth(20);
    player.setOnFire(0);
    player.extinguish();
    player.setFoodEnabled(false);
    player.getFoodData().setLevel(20);
    player.setExperience(0);
    player.setImmobile(false);
    player.setMovementSpeed(0.1F);
    player.sendExperienceLevel(0);
    player.getInventory().clearAll();
    player.getCraftingGrid().clearAll();
    player.getCursorInventory().clearAll();
    player.getUIInventory().clearAll();
    player.getOffhandInventory().clearAll();
    player.getEnderChestInventory().clearAll();
    player.removeAllEffects();
    clearAllArmorInventory(player);
    updateInventory(player);
  }

  public static void convertSpectator(Player player) {
    convertSpectator(player, false);
  }

  public static void convertSpectator(Player player, boolean haveLost) {
    player.setGamemode(Player.SPECTATOR);
    giveDefaultAttributes(player);

    if (haveLost) {
      player.sendTitle(
        TextFormat.BOLD.toString() + TextFormat.RED + "You have lost!",
        TextFormat.YELLOW + "Now spectating."
      );
    } else {
      player.getServer().removeOnlinePlayer(player);
    }
  }
}
