package jossc.game.command;

import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import jossc.game.state.ScheduledStateSeries;

public class ContinueStatesCommand extends StateCommand {

  public ContinueStatesCommand(ScheduledStateSeries mainState) {
    super("continuestates", "Continue with the states", mainState);
  }

  @Override
  public boolean execute(CommandSender sender, String label, String[] args) {
    super.execute(sender, label, args);

    if (mainState.getFrozen()) {
      mainState.setFrozen(false);
      sender
        .getServer()
        .broadcastMessage(
          TextFormat.GREEN + sender.getName() + " continued with all states!"
        );
    } else {
      sender.sendMessage(TextFormat.RED + "The states are not stopped!");
    }

    return true;
  }
}
