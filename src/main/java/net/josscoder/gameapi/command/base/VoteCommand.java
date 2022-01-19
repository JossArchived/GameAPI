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

package net.josscoder.gameapi.command.base;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import com.denzelcode.form.element.Button;
import com.denzelcode.form.element.ImageType;
import com.denzelcode.form.window.SimpleWindowForm;
import net.josscoder.gameapi.Game;
import net.josscoder.gameapi.command.GameCommand;
import net.josscoder.gameapi.map.manager.GameMapManager;
import net.josscoder.gameapi.util.Utils;

public class VoteCommand extends GameCommand<Game> {

  public VoteCommand(Game game) {
    super(game, "vote", "Vote");
  }

  @Override
  public boolean execute(CommandSender sender, String label, String[] args) {
    if (
      !(sender instanceof Player) ||
      game.isStarted() ||
      game.isMapVoteFinished() ||
      !game.isCanVoteMap()
    ) {
      return false;
    }

    Player player = (Player) sender;

    Utils.playSound(player, "random.pop", 0.5f, 1);

    GameMapManager mapManager = game.getGameMapManager();

    SimpleWindowForm form = new SimpleWindowForm(
      null,
      TextFormat.BOLD.toString() + TextFormat.DARK_PURPLE + "Choice of map",
      TextFormat.GRAY + "Choose the map you want to play!"
    );

    mapManager
      .getMaps()
      .values()
      .forEach(
        map -> {
          String mapName = map.getName();
          boolean isUrl = map.getImage().startsWith("http");

          form.addButton(
            mapName,
            mapName,
            (isUrl ? ImageType.URL : ImageType.PATH),
            (mapName.isEmpty() ? "https://i.imgur.com/iRXE3aY.png" : mapName)
          );
        }
      );

    form.addHandler(
      event -> {
        Button button = event.getButton();

        if (button == null) {
          return;
        }

        String mapName = button.getName();

        if (game.isStarted() || game.isMapVoteFinished()) {
          return;
        }

        if (mapManager.hasVoted(player, mapName)) {
          return;
        }

        mapManager.ifHasVotedRemoveAndVoteFor(mapName, player);
        player.sendMessage(
          TextFormat.colorize("&l&b» &r&7You voted for &e") + mapName
        );
      }
    );

    form.sendTo(player);

    return true;
  }
}
