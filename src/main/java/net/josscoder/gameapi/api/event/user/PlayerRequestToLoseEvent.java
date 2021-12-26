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

package net.josscoder.gameapi.api.event.user;

import cn.nukkit.Player;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.player.PlayerEvent;
import lombok.Getter;

public class PlayerRequestToLoseEvent extends PlayerEvent {

  @Getter
  private static final HandlerList handlers = new HandlerList();

  @Getter
  private final LoseCause loseCause;

  public enum LoseCause {
    VOID,
    MIN_Y,
    BORDER
  }

  public PlayerRequestToLoseEvent(Player player, LoseCause loseCause) {
    this.player = player;
    this.loseCause = loseCause;
  }
}
