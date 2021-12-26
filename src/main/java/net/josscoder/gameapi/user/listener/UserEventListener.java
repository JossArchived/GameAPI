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

package net.josscoder.gameapi.user.listener;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerLoginEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.utils.TextFormat;
import net.josscoder.gameapi.Game;
import net.josscoder.gameapi.api.event.user.UserJoinServerEvent;
import net.josscoder.gameapi.api.event.user.UserQuitServerEvent;
import net.josscoder.gameapi.api.listener.GameListener;
import net.josscoder.gameapi.user.User;
import net.josscoder.gameapi.user.factory.UserFactory;

public class UserEventListener extends GameListener {

  private final UserFactory userFactory;

  public UserEventListener(Game game) {
    super(game);
    userFactory = game.getUserFactory();
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onLogin(PlayerLoginEvent event) {
    Player player = event.getPlayer();

    if (game.isFull()) {
      player.kick(
        TextFormat.colorize(
          "&8Unexpected? Report this &7(" + game.getId() + ")&8: &cSever full!"
        ),
        false
      );
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onJoin(PlayerJoinEvent event) {
    event.setJoinMessage("");

    Player player = event.getPlayer();

    User user = new User(game, player.getName());
    user.init();

    userFactory.add(user);

    game.callEvent(new UserJoinServerEvent(user));
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onQuit(PlayerQuitEvent event) {
    event.setQuitMessage("");

    Player player = event.getPlayer();

    if (!userFactory.contains(player)) {
      return;
    }

    User user = userFactory.get(player);
    user.close();

    game.callEvent(new UserQuitServerEvent(user));

    userFactory.remove(player);

    game.getGameMapManager().removeVoteIfHasVoted(player);
  }
}
