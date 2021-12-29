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

package net.josscoder.gameapi.map.manager;

import cn.nukkit.Player;
import java.util.*;
import lombok.Getter;
import lombok.Setter;
import net.josscoder.gameapi.Game;
import net.josscoder.gameapi.map.GameMap;

@Getter
@Setter
public class GameMapManager {

  private final Game game;

  private GameMap mainMap = null;

  private Map<String, GameMap> maps;

  private final Map<Player, String> voters;

  public GameMapManager(Game game) {
    this.game = game;

    maps = new HashMap<>();
    voters = new HashMap<>();
  }

  public int mapsSize() {
    return maps.size();
  }

  public boolean containsMap(String mapName) {
    return maps.containsKey(mapName);
  }

  public GameMap getMap(String mapName) {
    return !containsMap(mapName) ? null : maps.get(mapName);
  }

  public void addMap(GameMap gameMap) {
    maps.put(gameMap.getName(), gameMap);
  }

  public void removeMap(String mapName) {
    maps.remove(mapName);
  }

  public boolean hasVoted(Player player, String mapName) {
    String mapVoted = voters.get(player);

    return (mapVoted != null && mapVoted.equalsIgnoreCase(mapName));
  }

  public boolean hasVoted(Player player) {
    return voters.containsKey(player);
  }

  public void ifHasVotedRemoveAndVoteFor(String mapName, Player player) {
    removeVoteIfHasVoted(player);
    addVote(mapName, player);
  }

  public void removeVoteIfHasVoted(Player player) {
    if (hasVoted(player)) {
      removeVote(voters.get(player), player);
    }
  }

  public void addVote(String mapName, Player player) {
    GameMap gameMap = getMap(mapName);

    if (gameMap == null) {
      return;
    }

    gameMap.addVote();
    voters.put(player, mapName);
  }

  public void removeVote(String mapName, Player player) {
    if (!hasVoted(player)) {
      return;
    }

    GameMap gameMap = getMap(mapName);

    if (gameMap == null) {
      return;
    }

    gameMap.removeVote();
    voters.remove(player);
  }

  public GameMap getMapWinner() {
    if (mainMap != null) {
      return mainMap;
    }

    if (mapsSize() <= 0) {
      return null;
    }

    Map<String, Integer> availableMaps = new HashMap<>();

    maps
      .values()
      .forEach(map -> availableMaps.put(map.getName(), map.getVotes()));

    if (availableMaps.size() == 0) {
      return null;
    }

    return getMap(
      Collections
        .max(availableMaps.entrySet(), Map.Entry.comparingByValue())
        .getKey()
    );
  }
}
