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

package net.josscoder.gameapi.map;

import cn.nukkit.math.Vector3;
import lombok.Getter;
import lombok.Setter;
import net.josscoder.gameapi.Game;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Getter
@Setter
public class GameMap extends Map {

  public static String SOLO = "solo";

  protected int votes = 0;

  protected LinkedHashMap<String, List<Vector3>> spawns;

  protected String image = "";

  public GameMap(Game game, String name, Vector3 safeSpawn) {
    super(game, name, safeSpawn);

    spawns = new LinkedHashMap<>();
    spawns.put(SOLO, new ArrayList<>());
  }

  public void addVote() {
    votes += 1;
  }

  public void removeVote() {
    votes = Math.max((votes - 1), 0);
  }

  public void resetVotes() {
    votes = 0;
  }

  public Vector3 getSpawn(int index) {
    return getSpawn(SOLO, index);
  }

  public Vector3 getSpawn(String team, int index) {
    if (spawns.get(team) == null) {
      return new Vector3(0, 100, 0);
    }

    return spawns.get(team).get(index);
  }

  public void addSpawn(Vector3 position) {
    addSpawn(SOLO, position);
  }

  public void addSpawn(String team, Vector3 position) {
    if (spawns.get(team) != null) {
      spawns.get(team).add(position);
    }
  }
}
