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

import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import net.josscoder.gameapi.phase.PhaseSeries;

public abstract class PhaseCommand extends Command {

  protected final PhaseSeries phaseSeries;

  public PhaseCommand(String name, PhaseSeries phaseSeries) {
    this(name, "", phaseSeries);
  }

  public PhaseCommand(
    String name,
    String description,
    PhaseSeries phaseSeries
  ) {
    this(name, description, "", phaseSeries);
  }

  public PhaseCommand(
    String name,
    String description,
    String usageMessage,
    PhaseSeries phaseSeries
  ) {
    this(name, description, usageMessage, new String[] {}, phaseSeries);
  }

  public PhaseCommand(
    String name,
    String description,
    String usageMessage,
    String[] aliases,
    PhaseSeries phaseSeries
  ) {
    super(name, description, usageMessage, aliases);
    this.phaseSeries = phaseSeries;

    setPermission("minigame.admin.permission");
    setPermissionMessage(
      TextFormat.RED +
      "You need to have an administrator rank or a higher rank to execute this command!"
    );
  }

  @Override
  public boolean execute(CommandSender sender, String label, String[] args) {
    if (!sender.hasPermission(getPermission())) {
      sender.sendMessage(getPermissionMessage());

      return false;
    }

    return true;
  }

  public void broadcast(String message) {
    Server.getInstance().broadcastMessage(message);
  }
}
