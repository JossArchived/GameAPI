package jossc.game.command;

import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import jossc.game.phase.PhaseSeries;

public class SkipPhaseCommand extends PhaseCommand {

  public SkipPhaseCommand(PhaseSeries series) {
    super("skipphase", "Skip to the next game phase", series);
  }

  @Override
  public boolean execute(CommandSender sender, String label, String[] args) {
    super.execute(sender, label, args);

    broadcast(
      TextFormat.RED + sender.getName() + " has skipped to the next game phase"
    );
    series.skip();

    return true;
  }
}
