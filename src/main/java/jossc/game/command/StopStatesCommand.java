package jossc.game.command;

import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import jossc.game.state.ScheduledStateSeries;

public class StopStatesCommand extends StateCommand {

  public StopStatesCommand(ScheduledStateSeries mainState) {
    super("stopstates", "Stop all states", mainState);
  }

  @Override
  public boolean execute(CommandSender sender, String label, String[] args) {
    super.execute(sender, label, args);

    if (!mainState.getFrozen()) {
      mainState.setFrozen(true);
      sender
        .getServer()
        .broadcastMessage(
          TextFormat.RED + sender.getName() + " stopped the states!"
        );
    } else {
      sender.sendMessage(TextFormat.RED + "The states are already stopped!");
    }

    return true;
  }
}
