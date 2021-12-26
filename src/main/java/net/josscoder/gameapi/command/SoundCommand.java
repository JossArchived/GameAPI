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

package net.josscoder.gameapi.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import net.josscoder.gameapi.util.Utils;

public class SoundCommand extends Command {

  public SoundCommand() {
    super(
      "sound",
      "Play a sound",
      TextFormat.RED +
      "Usage: /sound <sound name> <optional: pitch> <optional: volume>",
      new String[] { "s" }
    );
  }

  @Override
  public boolean execute(CommandSender sender, String s, String[] args) {
    if (!(sender instanceof Player)) {
      return false;
    }

    if (args.length < 1) {
      sender.sendMessage(getUsage());

      return false;
    }

    Player player = (Player) sender;

    String soundName = args[0];

    float pitch = 1.0f;
    float volume = 1.0f;

    String message;

    if (args.length == 1) {
      message = TextFormat.DARK_GREEN + "SOUND= " + soundName;
    } else if (args.length == 2) {
      pitch = Float.parseFloat(args[1]);
      message = TextFormat.BLUE + "SOUND= " + soundName + ", PITCH= " + pitch;
    } else if (args.length == 3) {
      pitch = Float.parseFloat(args[1]);
      volume = Float.parseFloat(args[2]);
      message =
        TextFormat.GOLD +
        "SOUND= " +
        soundName +
        ", PITCH= " +
        pitch +
        ", VOLUME= " +
        volume;
    } else {
      message = getUsage();
    }

    Utils.playSound(player, soundName, pitch, volume);
    sender.sendMessage(message);

    return false;
  }
}
