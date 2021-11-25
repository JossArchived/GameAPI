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
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import java.util.HashMap;
import java.util.Map;

import net.josscoder.gameapi.Game;
import net.josscoder.gameapi.map.manager.GameMapManager;
import net.josscoder.gameapi.util.PacketUtils;
import nl.apocalypsje.bedrock.FormAPI;
import nl.apocalypsje.bedrock.element.ElementButton;
import nl.apocalypsje.bedrock.window.SimpleWindow;
import org.jetbrains.annotations.NotNull;

public class VoteCommand extends GameCommand {

  public VoteCommand(Game game) {
    super(game, "vote", "Vote");
  }

  @Override
  public boolean execute(CommandSender sender, String label, String[] args) {
    if (
      !(sender instanceof Player) ||
      game.isStarted() ||
      game.isMapVoteFinished()
    ) {
      return false;
    }

    Player player = (Player) sender;

    PacketUtils.playSoundDataPacket(player, "random.pop", 0.5f, 1);

    Map<String, ElementButton> buttons = new HashMap<>();

    GameMapManager mapManager = game.getGameMapManager();

    mapManager
      .getMaps()
      .values()
      .forEach(
        map -> {
          String mapName = map.getName();

          buttons.put(
            map.getName(),
            new ElementButton(mapName, mapName)
              .imageType(ElementButton.ImageType.URL)
              .imageData("https://i.imgur.com/iRXE3aY.png")
          );
        }
      );

    @NotNull
    SimpleWindow form = FormAPI.simpleWindow(
      TextFormat.BOLD.toString() + TextFormat.DARK_PURPLE + "Choice of map",
      TextFormat.GRAY + "Choose the map you want to play!",
      buttons
    );

    form
      .getFormButtons()
      .values()
      .forEach(
        button ->
          button.onClick(
            () -> {
              String mapName = button.getElementId();

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
          )
      );

    form.send(player);

    return true;
  }
}
