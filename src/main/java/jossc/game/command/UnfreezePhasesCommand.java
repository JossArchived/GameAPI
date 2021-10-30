package jossc.game.command;

import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import jossc.game.phase.PhaseSeries;

public class UnfreezePhasesCommand extends PhaseCommand {

  public UnfreezePhasesCommand(PhaseSeries series) {
    super("unfreezephases", "Unfreeze the game phases", series);
  }

  @Override
  public boolean execute(CommandSender sender, String label, String[] args) {
    super.execute(sender, label, args);

    if (series.getFrozen()) {
      series.setFrozen(false);
      broadcast(
        TextFormat.GREEN + sender.getName() + " unfrozen the game phases"
      );

      return true;
    }

    sender.sendMessage(TextFormat.RED + "The game phases are not frozen!");

    return false;
  }
}
