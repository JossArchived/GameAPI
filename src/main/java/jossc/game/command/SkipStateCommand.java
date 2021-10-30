package jossc.game.command;

import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import jossc.game.state.ScheduledStateSeries;

public class SkipStateCommand extends StateCommand {

  public SkipStateCommand(ScheduledStateSeries mainState) {
    super("skipstate", "Skip states", mainState);
  }

  @Override
  public boolean execute(CommandSender sender, String label, String[] args) {
    super.execute(sender, label, args);

    sender
      .getServer()
      .broadcastMessage(
        TextFormat.RED + sender.getName() + " has skipped to the next state!"
      );

    mainState.skip();

    return false;
  }
}
