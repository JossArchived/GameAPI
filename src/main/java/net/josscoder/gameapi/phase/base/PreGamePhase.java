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

package net.josscoder.gameapi.phase.base;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import net.josscoder.gameapi.Game;
import net.josscoder.gameapi.map.GameMap;
import net.josscoder.gameapi.phase.event.GameStartEvent;
import net.josscoder.gameapi.user.User;
import net.josscoder.gameapi.util.CharUtils;
import net.josscoder.gameapi.util.MathUtils;

public class PreGamePhase extends LobbyPhase {

  private final int initialCountdown;
  private int countdown;
  private final int mapTime;

  public PreGamePhase(Game game, int countdown, int mapTime) {
    super(game, Duration.ZERO);
    this.initialCountdown = countdown;
    this.countdown = countdown;
    this.mapTime = mapTime;
  }

  @Override
  protected void onStart() {
    game.setStarted(true);

    game.getGameMapManager().getMapWinner().prepare(mapTime);

    spawnPlayers();
  }

  private void spawnPlayers() {
    List<Vector3> spawns = game.getGameMapManager().getMapWinner().getSpawns();
    Set<Integer> spawnsUsed = new HashSet<>();

    getNeutralPlayers()
      .forEach(player -> spawnPlayer(player, spawns, spawnsUsed));
  }

  private void spawnPlayer(
    Player player,
    List<Vector3> spawns,
    Set<Integer> spawnsUsed
  ) {
    int i;

    do {
      i = MathUtils.nextInt(spawns.size());
    } while (spawnsUsed.contains(i));

    Vector3 spawn = spawns.get(i);

    GameMap mapWinner = game.getGameMapManager().getMapWinner();

    player.teleport(Position.fromObject(spawn, mapWinner.toLevel()));

    spawnsUsed.add(i);

    User user = userFactory.get(player);

    if (user == null) {
      return;
    }

    user.clearAllInventory();
    user.lookAt(mapWinner.getSafeSpawn());
  }

  @Override
  public void onUpdate() {
    countdown--;

    if (countdown > 0) {
      broadcastActionBar(
        "&eStart game: &l»&r " +
        CharUtils.timeToChar(countdown, initialCountdown) +
        "&f " +
        countdown
      );

      if (countdown <= 3) {
        broadcastSound("note.harp", 2, 2);
      }
    }
  }

  @Override
  public boolean isReadyToEnd() {
    return super.isReadyToEnd() && countdown == 0;
  }

  @Override
  protected void onEnd() {
    getNeutralPlayers().forEach(player -> player.setGamemode(game.getDefaultGamemode()));

    String gameName = game.getGameName();

    if (!gameName.isEmpty()) {
      broadcastMessage("&d&l» " + gameName);
    }

    String instruction = game.getInstruction();

    if (!instruction.isEmpty()) {
      broadcastMessage("&7&l» &r&f" + instruction);
    }

    game.callEvent(new GameStartEvent());

    List<String> tips = game.getTips();

    if (tips == null || tips.isEmpty()) {
      return;
    }

    Random rand = new Random();

    String tip = tips.get(rand.nextInt(tips.size()));

    if (tip == null) {
      return;
    }

    game.schedule(
      () -> {
        broadcastMessage("&b&l» &r&bTip: &7" + tip);
        broadcastSound("random.toast");
      },
      20 * 3
    );
  }

  @Override
  @EventHandler
  public void onMove(PlayerMoveEvent event) {
    if (!game.isMoveInPreGame()) {
      event.setTo(event.getFrom());
    }
  }
}
