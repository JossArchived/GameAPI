/*
 * Copyright 2021 Josscoder
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

package net.josscoder.gameapi.map;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.TextFormat;
import lombok.Getter;
import lombok.Setter;
import net.josscoder.gameapi.Game;
import net.josscoder.gameapi.listener.GameListener;
import net.josscoder.gameapi.user.event.PlayerRequestToLoseEvent;
import net.josscoder.gameapi.util.PacketUtils;

@Getter
@Setter
public abstract class Map extends GameListener {

  protected final String name;

  protected final Vector3 safeSpawn;

  protected int minY = 1;

  protected int maxY = 256;

  protected Vector3 cornerOne;

  protected Vector3 cornerTwo;

  public Map(Game game, String name, Vector3 safeSpawn) {
    this(game, name, safeSpawn, null, null);
  }

  public Map(
    Game game,
    String name,
    Vector3 safeSpawn,
    Vector3 cornerOne,
    Vector3 cornerTwo
  ) {
    super(game);
    this.name = name;
    this.safeSpawn = safeSpawn;
    this.cornerOne = cornerOne;
    this.cornerTwo = cornerTwo;

    game.registerListener(this);
  }

  public Level toLevel() {
    return game.getServer().getLevelByName(name);
  }

  public void handle() {
    Server server = game.getServer();

    if (!server.isLevelLoaded(name)) {
      server.loadLevel(name);
    }
  }

  public void prepare() {
    prepare(Level.TIME_DAY);
  }

  public void prepare(int mapTime) {
    handle();

    Level level = game.getServer().getLevelByName(name);

    if (level == null) {
      return;
    }

    level.setTime(mapTime);
    level.stopTime();
    level.setRaining(false);
    level.setThundering(false);
  }

  public void teleportToSafeSpawn(Player player) {
    if (safeSpawn == null || toLevel() == null) {
      return;
    }

    handle();

    player.teleport(Position.fromObject(safeSpawn, toLevel()).add(0, 1));
  }

  public boolean isSafeZone(Vector3 vector3) {
    if (cornerOne == null || cornerTwo == null) {
      return true;
    }

    int minX = (int) Math.min(cornerOne.x, cornerTwo.x);
    int maxX = (int) Math.max(cornerOne.x, cornerTwo.x);

    int minZ = (int) Math.min(cornerOne.z, cornerTwo.z);
    int maxZ = (int) Math.max(cornerOne.z, cornerTwo.z);

    return (
      minX <= vector3.x &&
      maxX >= vector3.x &&
      minZ <= vector3.z &&
      maxZ >= vector3.z
    );
  }

  public boolean isInThisLevel(Player player) {
    return isInThisLevel(player.getLevel());
  }

  public boolean isInThisLevel(Level level) {
    return level == toLevel();
  }

  private void broadcastAlert(Player player, String message) {
    PacketUtils.playSoundDataPacket(player, "note.bass", 1, 1);
    player.sendMessage(TextFormat.colorize("&c&l»&r&c ") + message);
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onMove(PlayerMoveEvent event) {
    Player player = event.getPlayer();

    if (!isInThisLevel(player)) {
      return;
    }

    if (!isSafeZone(player.asVector3f().asVector3())) {
      event.setTo(event.getFrom());

      broadcastAlert(player, "You can't cross the border!");

      game.callEvent(
        new PlayerRequestToLoseEvent(
          player,
          PlayerRequestToLoseEvent.LoseCause.BORDER
        )
      );

      return;
    }

    if (player.y <= minY) {
      game.callEvent(
        new PlayerRequestToLoseEvent(
          player,
          PlayerRequestToLoseEvent.LoseCause.MIN_Y
        )
      );
    }
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onBlockPlace(BlockPlaceEvent event) {
    Player player = event.getPlayer();

    if (!isInThisLevel(player)) {
      return;
    }

    Block block = event.getBlock();

    if (block.y < maxY) {
      return;
    }

    broadcastAlert(player, "You cannot place blocks outside the play area!");

    event.setCancelled();
  }
}
