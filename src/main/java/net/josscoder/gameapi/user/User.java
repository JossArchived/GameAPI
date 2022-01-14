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

package net.josscoder.gameapi.user;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.item.Item;
import cn.nukkit.level.Location;
import cn.nukkit.math.Vector3;
import cn.nukkit.potion.Effect;
import cn.nukkit.utils.DummyBossBar;
import cn.nukkit.utils.TextFormat;
import lombok.Getter;
import net.josscoder.gameapi.Game;
import net.josscoder.gameapi.api.event.user.UserConvertSpectatorEvent;
import net.josscoder.gameapi.user.storage.LocalStorage;
import net.josscoder.gameapi.util.Utils;

@Getter
public class User {

  private final Game game;

  private final String name;

  private final LocalStorage localStorage;

  private DummyBossBar bossbar;

  public User(Game game, String name) {
    this.game = game;
    this.name = name;

    localStorage = new LocalStorage();
  }

  public void init() {}

  public Player getPlayer() {
    return Server.getInstance().getPlayer(name);
  }

  public DeviceOS getDeviceOS() {
    Player player = getPlayer();

    if (player == null) {
      return DeviceOS.UNKNOWN;
    }

    return DeviceOS.getDeviceOS(player.getLoginChainData().getDeviceOS());
  }

  public String getDeviceOSString() {
    return getDeviceOS().toString();
  }

  public PlayerInventory getInventory() {
    return getPlayer().getInventory();
  }

  public void updateInventory() {
    PlayerInventory inventory = getInventory();

    inventory.sendArmorContents(getPlayer());
    inventory.sendContents(getPlayer());
    inventory.sendHeldItem(getPlayer());
  }

  public void clearAllArmorInventory() {
    Item air = Item.get(Item.AIR);

    PlayerInventory inventory = getInventory();
    inventory.setHelmet(air);
    inventory.setChestplate(air);
    inventory.setLeggings(air);
    inventory.setBoots(air);

    updateInventory();
  }

  public void clearAllInventory() {
    getInventory().clearAll();
    clearAllArmorInventory();
    updateInventory();
  }

  public void giveDefaultAttributes() {
    Player player = getPlayer();

    if (player == null) {
      return;
    }

    player.setAllowFlight(false);
    player.setHealth(20);
    player.setMaxHealth(20);
    player.setOnFire(0);
    player.extinguish();
    player.setFoodEnabled(false);
    player.getFoodData().setLevel(20);
    player.setExperience(0);
    player.setImmobile(false);
    player.sendExperienceLevel(0);
    player.getInventory().clearAll();
    player.getCraftingGrid().clearAll();
    player.getCursorInventory().clearAll();
    player.getUIInventory().clearAll();
    player.getOffhandInventory().clearAll();
    player.getEnderChestInventory().clearAll();
    player.removeAllEffects();

    clearAllInventory();
  }

  public void convertSpectator() {
    convertSpectator(false, false);
  }

  public void convertSpectator(boolean hasLost, boolean teleport) {
    Player player = getPlayer();

    if (player == null) {
      return;
    }

    player.setGamemode(Player.SPECTATOR);
    giveDefaultAttributes();

    if (teleport) {
      teleportToMapWinnerSafeSpawn();
    }

    game
      .getSpectatorItems()
      .forEach(
        (slot, customItem) -> getInventory().setItem(slot, customItem.build())
      );
    getInventory().setItem(8, game.getWaitingLobbyItems().get(8).build());

    updateInventory();

    if (hasLost) {
      Effect blindness = Effect
        .getEffect(Effect.BLINDNESS)
        .setDuration(20 * 5)
        .setAmplifier(2)
        .setVisible(false);

      Effect slowness = Effect
        .getEffect(Effect.SLOWNESS)
        .setDuration(20 * 3)
        .setAmplifier(1)
        .setVisible(false);

      player.addEffect(blindness);
      player.addEffect(slowness);

      player.sendTitle(
        TextFormat.RED + "You have lost!",
        TextFormat.YELLOW + "Now spectating."
      );
    }

    game.callEvent(new UserConvertSpectatorEvent(this, hasLost));
  }

  public void convertPlayer() {
    Player player = getPlayer();

    if (player == null) {
      return;
    }

    player.setGamemode(Player.ADVENTURE);
    giveDefaultAttributes();

    player.setNameTag(TextFormat.GRAY + player.getName());
  }

  public void playSound(String soundName) {
    playSound(soundName, 1);
  }

  public void playSound(String soundName, float pitch) {
    playSound(soundName, pitch, 1);
  }

  public void playSound(String soundName, float pitch, float volume) {
    Player player = getPlayer();

    if (player == null) {
      return;
    }

    Utils.playSound(player, soundName, pitch, volume);
  }

  public void sendMessage(String message) {
    getPlayer().sendMessage(TextFormat.colorize(message));
  }

  public void close() {}

  public void lookAt(Vector3 vector3) {
    Player player = getPlayer();

    if (player == null) {
      return;
    }

    double xDist = (vector3.x - player.getPosition().x);
    double zDist = (vector3.z - player.getPosition().z);

    player.yaw = Math.atan2(zDist, xDist) / Math.PI * 180 - 90;

    if (player.yaw < 0) {
      player.yaw += 360.0;
    }

    player.teleport(new Location(player.x, player.y, player.z, player.yaw, 0));
  }

  public void teleportToMapWinnerSafeSpawn() {
    Player player = getPlayer();

    if (player == null) {
      return;
    }

    player.teleport(
      game.getGameMapManager().getMapWinner().getSafeSpawn().add(0, 1)
    );
  }

  public void sendBossBar(String title, float length) {
    Player player = getPlayer();

    if (player == null) {
      return;
    }

    game.scheduleOnMainThread(
      () -> {
        if (bossbar == null) {
          bossbar =
            new DummyBossBar.Builder(player)
              .length(length)
              .text(title)
              .build();

          player.createBossBar(bossbar);
        } else {
          bossbar.setText(title);
          bossbar.setLength(length);
        }
      }
    );
  }

  public void removeBossBar() {
    Player player = getPlayer();

    if (player == null) {
      return;
    }

    if (bossbar == null) {
      return;
    }

    bossbar.destroy();
    bossbar = null;
  }
}
