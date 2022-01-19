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
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.TextFormat;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.josscoder.gameapi.Game;
import net.josscoder.gameapi.event.GameStartEvent;
import net.josscoder.gameapi.map.GameMap;
import net.josscoder.gameapi.map.WaitingRoomMap;
import net.josscoder.gameapi.mode.GameMode;
import net.josscoder.gameapi.team.Team;
import net.josscoder.gameapi.user.User;
import net.josscoder.gameapi.util.CharUtils;
import net.josscoder.gameapi.util.MathUtils;

public class PreGamePhase extends LobbyPhase<Game> {

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
    game.getMapWinner().prepare(mapTime);
    spawnPlayers();
  }

  private void spawnPlayers() {
    GameMap map = game.getMapWinner();

    boolean isTeam = game.getGameMode() == GameMode.TEAM;

    if (isTeam) {
      game
        .getTeams()
        .forEach(
          team ->
            team
              .getMembers()
              .forEach(
                member -> {
                  List<Vector3> spawns = map.getSpawns(team.getId());
                  Set<Integer> spawnsUsed = new HashSet<>();

                  spawnPlayer(member, map, spawns, spawnsUsed);
                }
              )
        );
    } else {
      List<Vector3> spawns = map.getSpawns();
      Set<Integer> spawnsUsed = new HashSet<>();

      getNeutralPlayers()
        .forEach(player -> spawnPlayer(player, map, spawns, spawnsUsed));
    }
  }

  private void spawnPlayer(
    Player player,
    GameMap map,
    List<Vector3> spawns,
    Set<Integer> spawnsUsed
  ) {
    if (spawns.isEmpty()) {
      map.teleportToSafeSpawn(player);
    } else {
      int i;

      do {
        i = MathUtils.nextInt(spawns.size());
      } while (spawnsUsed.contains(i));

      Vector3 spawn = spawns.get(i);

      player.teleport(Position.fromObject(spawn, map.toLevel()));

      spawnsUsed.add(i);
    }

    User user = userFactory.get(player);

    if (user == null) {
      return;
    }

    user.clearAllInventory();
    user.lookAt(map.getSafeSpawn());

    if (!game.canMoveInPreGame()) {
      player.setImmobile(true);
    }
  }

  @Override
  public void onUpdate() {
    countdown--;

    if (countdown > 0) {
      broadcastActionBar(
        "&eGame Start: &l»&r " +
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
    WaitingRoomMap waitingRoomMap = game.getWaitingRoomMap();

    if (waitingRoomMap != null && waitingRoomMap.getExitEntity() != null) {
      game
        .getCitizenLibrary()
        .getFactory()
        .remove(waitingRoomMap.getExitEntity().getEntityId());
    }

    getNeutralPlayers()
      .forEach(
        player -> {
          player.setGamemode(game.getDefaultPlayerGamemode());
          player.setImmobile(false);
        }
      );

    String gameName = game.getGameName();

    if (!gameName.isEmpty()) {
      broadcastMessage("&d&l» " + gameName);
    }

    String instruction = game.getInstruction();

    if (!instruction.isEmpty()) {
      broadcastMessage("&7&l» &r&f" + instruction);
    }

    if (game.getGameMode() == GameMode.TEAM) {
      getNeutralPlayers()
        .forEach(
          player -> {
            Team team = game.getSortedTeams().get(0);

            String color = team.getColor();

            player.sendMessage(
              TextFormat.BOLD +
              color +
              "» " +
              TextFormat.RESET +
              "You are on the " +
              color +
              team.getId() +
              " Team!"
            );
          }
        );
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
}
