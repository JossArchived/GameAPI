package jossc.game.command;

import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import jossc.game.phase.PhaseSeries;

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
