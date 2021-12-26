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
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import com.denzelcode.form.element.Button;
import com.denzelcode.form.window.SimpleWindowForm;
import net.josscoder.gameapi.Game;
import net.josscoder.gameapi.api.command.GameCommand;
import net.josscoder.gameapi.util.Utils;

public class TeleporterCommand extends GameCommand {

  public TeleporterCommand(Game game) {
    super(game, "teleporter", "Display a list of players to teleport to");
  }

  @Override
  public boolean execute(CommandSender sender, String label, String[] args) {
    if (!(sender instanceof Player)) {
      return false;
    }

    Player player = (Player) sender;

    if (!game.isStarted() || !player.isSpectator()) {
      return false;
    }

    Utils.playSoundDataPacket(player, "random.pop", 0.5f, 1);

    SimpleWindowForm form = new SimpleWindowForm(
      null,
      "Player teleporter",
      "Click on a name to teleport!"
    );

    for (Player onlinePlayer : game.getServer().getOnlinePlayers().values()) {
      if (onlinePlayer.isSpectator()) {
        continue;
      }

      form.addButton(
        onlinePlayer.getName(),
        onlinePlayer.getName() + "\n" + TextFormat.GRAY + "Select to teleport"
      );
    }

    form.addHandler(
      event -> {
        Button button = event.getButton();

        if (button == null) {
          return;
        }

        String targetName = button.getName();

        Player target = game.getServer().getPlayer(targetName);

        if (target == null) {
          return;
        }

        player.teleport(target.getPosition());
      }
    );

    form.sendTo(player);

    return true;
  }
}
