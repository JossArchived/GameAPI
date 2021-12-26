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

import cn.nukkit.event.player.PlayerEvent;
import lombok.Getter;
import net.josscoder.gameapi.user.User;

public abstract class UserEvent extends PlayerEvent {

  @Getter
  private final User user;

  public UserEvent(User user) {
    this.user = user;
    player = user.getPlayer();
  }
}
