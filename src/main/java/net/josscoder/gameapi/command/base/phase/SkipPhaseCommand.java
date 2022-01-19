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

package net.josscoder.gameapi.command.base.phase;

import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import net.josscoder.gameapi.phase.PhaseSeries;

public class SkipPhaseCommand extends PhaseCommand {

  public SkipPhaseCommand(PhaseSeries phaseSeries) {
    super("skipphase", "Skip to the next game phase", phaseSeries);
  }

  @Override
  public boolean execute(CommandSender sender, String label, String[] args) {
    super.execute(sender, label, args);

    broadcast(
      TextFormat.RED + sender.getName() + " has skipped to the next game phase"
    );
    phaseSeries.skip();

    return true;
  }
}
