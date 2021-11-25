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

import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import net.josscoder.gameapi.phase.PhaseSeries;

public class FreezePhasesCommand extends PhaseCommand {

  public FreezePhasesCommand(PhaseSeries phaseSeries) {
    super("freezephases", "Freeze game phases", phaseSeries);
  }

  @Override
  public boolean execute(CommandSender sender, String label, String[] args) {
    super.execute(sender, label, args);

    if (!phaseSeries.getFrozen()) {
      phaseSeries.setFrozen(true);
      broadcast(
        TextFormat.RED + sender.getName() + " has frozen the game phases"
      );

      return true;
    }

    sender.sendMessage(
      TextFormat.RED + "The phases of the game are already frozen!"
    );

    return false;
  }
}
