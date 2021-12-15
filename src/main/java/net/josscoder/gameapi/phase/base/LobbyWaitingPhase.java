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

import net.josscoder.gameapi.Game;

public class LobbyWaitingPhase extends LobbyPhase {

  private final int neededPlayers;

  public LobbyWaitingPhase(Game game) {
    super(game);
    this.neededPlayers = game.getMinPlayers();
  }

  @Override
  protected void onStart() {
    game.getWaitingRoomMap().prepare();
  }

  @Override
  public void onUpdate() {
    int restMorePlayers = (neededPlayers - countNeutralPlayers());

    broadcastActionBar(
      "&f" +
      restMorePlayers +
      "&e more player" +
      (restMorePlayers == 1 ? "" : "s") +
      " to start..."
    );
  }

  @Override
  public boolean isReadyToEnd() {
    return super.isReadyToEnd() && countNeutralPlayers() >= neededPlayers;
  }
}
