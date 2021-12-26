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
import java.util.LinkedList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.josscoder.gameapi.Game;

@Getter
@Setter
public class GameMap extends Map {

  protected int votes = 0;

  protected List<Vector3> spawns;

  protected String image = "";

  public GameMap(Game game, String name, Vector3 safeSpawn) {
    super(game, name, safeSpawn);
    spawns = new LinkedList<>();
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
}
