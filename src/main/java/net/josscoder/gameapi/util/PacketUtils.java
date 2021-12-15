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

package net.josscoder.gameapi.util;

import cn.nukkit.Player;
import cn.nukkit.network.protocol.PlaySoundPacket;

public class PacketUtils {

  public static void playSoundDataPacket(
    Player player,
    String soundName,
    float pitch,
    float volume
  ) {
    PlaySoundPacket pk = new PlaySoundPacket();
    pk.name = soundName;
    pk.x = (int) player.x;
    pk.y = (int) player.y;
    pk.z = (int) player.z;
    pk.pitch = pitch;
    pk.volume = volume;

    player.dataPacket(pk);
  }
}
