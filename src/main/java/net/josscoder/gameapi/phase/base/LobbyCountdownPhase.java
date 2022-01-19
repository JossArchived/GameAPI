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

import java.time.Duration;
import lombok.SneakyThrows;
import net.josscoder.gameapi.Game;
import net.josscoder.gameapi.map.GameMap;

public class LobbyCountdownPhase extends LobbyPhase<Game> {

  public LobbyCountdownPhase(Game game, Duration duration) {
    super(game, duration);
  }

  @SneakyThrows
  @Override
  public void onUpdate() {
    if (countNeutralPlayers() < 1) {
      game.reset();

      return;
    }

    int remainingDuration = (int) getRemainingDuration().getSeconds();

    if (remainingDuration <= 0) {
      broadcastActionBar("§•&aPreparing map...");

      return;
    }

    if (remainingDuration == 3 && game.isCanVoteMap()) {
      game.setMapVoteFinished(true);
      broadcastMessage("&l&b» &aVoting has ended!");

      GameMap mapWinner = game.getMapWinner();

      if (mapWinner != null) {
        broadcastMessage(
          "&l&b» &r&e" +
          mapWinner.getName() +
          "&7 won with &f" +
          mapWinner.getVotes() +
          "&7 votes!"
        );
      }
    }

    if (remainingDuration <= 5) {
      broadcastSound("note.hat", 1, 2);
    }

    broadcastActionBar(
      "&aStarting game in in &l" +
      (remainingDuration <= 5 ? "&c" : "&a") +
      remainingDuration
    );
  }
}
