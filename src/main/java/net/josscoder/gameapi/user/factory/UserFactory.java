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

package net.josscoder.gameapi.user.factory;

import cn.nukkit.Player;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import net.josscoder.gameapi.Game;
import net.josscoder.gameapi.user.User;

@Getter
public class UserFactory {

  private final Game game;
  private final Map<String, User> storage = new ConcurrentHashMap<>();

  public UserFactory(Game game) {
    this.game = game;
  }

  public boolean contains(String username) {
    return storage.containsKey(username);
  }

  public boolean contains(Player player) {
    return contains(player.getName());
  }

  public boolean contains(UUID uuid) {
    return get(uuid) != null;
  }

  public void add(User user) {
    storage.put(user.getName(), user);
  }

  public User get(String username) {
    return storage.get(username);
  }

  public User get(Player player) {
    return get(player.getName());
  }

  public User get(UUID uuid) {
    return get(
      Objects.requireNonNull(game.getServer().getPlayer(uuid).orElse(null))
    );
  }

  public void remove(String username) {
    storage.remove(username);
  }

  public void remove(Player player) {
    storage.remove(player.getName());
  }

  public void remove(UUID uuid) {
    storage.remove(get(uuid).getName());
  }
}
