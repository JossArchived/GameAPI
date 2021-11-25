/*
 * Copyright 2021 Josscoder
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

public class MyPositionCommand extends Command {

  public MyPositionCommand() {
    super("mypositon", "Show your position");
  }

  @Override
  public boolean execute(CommandSender sender, String label, String[] args) {
    if (!(sender instanceof Player)) {
      return false;
    }

    Player player = (Player) sender;

    String levelFolderName = player.getLevel().getFolderName();

    sender.sendMessage(
      TextFormat.colorize(
        "&9" +
        levelFolderName +
        " &6X: &b" +
        player.x +
        " &6Y: &b" +
        player.y +
        " &6Z: &b" +
        player.z +
        " &6Yaw: &b" +
        player.yaw +
        " &6Pitch: &b" +
        player.pitch
      )
    );

    return true;
  }
}
