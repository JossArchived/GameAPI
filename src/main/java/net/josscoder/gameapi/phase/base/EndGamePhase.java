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
import cn.nukkit.scheduler.Task;
import cn.nukkit.utils.TextFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import net.josscoder.gameapi.Game;
import net.josscoder.gameapi.event.GameEndEvent;
import net.josscoder.gameapi.map.WaitingRoomMap;
import net.josscoder.gameapi.user.User;

public class EndGamePhase extends LobbyPhase<Game> {

  private final Map<Player, Integer> pedestalPlayers;

  private boolean inPedestalCenter = false;

  public EndGamePhase(
    Game game,
    Duration duration,
    Map<Player, Integer> pedestalPlayers
  ) {
    super(game, duration);
    this.pedestalPlayers = pedestalPlayers;
  }

  @Override
  protected void onStart() {
    game.callEvent(new GameEndEvent());

    WaitingRoomMap waitingRoomMap = game.getWaitingRoomMap();
    waitingRoomMap.prepare();

    waitingRoomMap.generatePedestalEntities(
      pedestalPlayers == null ? new HashMap<>() : pedestalPlayers
    );

    getOnlinePlayers()
      .forEach(
        player -> {
          User user = userFactory.get(player);

          if (user == null) {
            return;
          }

          user.convertPlayer();

          player.sendTitle(TextFormat.colorize("&l&6»&r&c Game Over &l&6«"));
          player.sendMessage(TextFormat.colorize("&l&c» Game OVER!"));
          playSound(player, "mob.ghast.fireball");

          schedule(
            () -> {
              waitingRoomMap.teleportToPedestalCenter(player);
              inPedestalCenter = true;
            },
            20 * 4
          );
        }
      );

    schedule(
      new Task() {
        private int modifiableInterval = 4;

        @Override
        public void onRun(int i) {
          modifiableInterval--;

          if (modifiableInterval == 0) {
            waitingRoomMap.spawnFireworks(
              pedestalPlayers == null ? 0 : pedestalPlayers.size()
            );

            modifiableInterval = 4;
          }
        }
      },
      20 * 4,
      20
    );
  }

  @Override
  public void onUpdate() {
    if (!inPedestalCenter) {
      return;
    }

    broadcastActionBar(
      "&eFinding a new game in &f" +
      getRemainingDuration().getSeconds() +
      "&e..."
    );
  }

  @Override
  public boolean isReadyToEnd() {
    return (
      super.isReadyToEnd() ||
      countNeutralPlayers() < 1 &&
      pedestalPlayers == null
    );
  }

  @Override
  protected void onEnd() {
    super.onEnd();

    broadcastActionBar("&bSearching for new game.");

    game.searchNewGameFor(new ArrayList<>(getOnlinePlayers()));

    game.schedule(
      () ->
        game.scheduleThread(
          () -> game.reset(game.getMapWinner().getName(), true)
        ),
      20 * 10
    );
  }
}
