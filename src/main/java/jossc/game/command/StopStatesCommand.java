package jossc.game.command;

import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import net.minikloon.fsmgasm.StateSeries;

public class StopStatesCommand extends StateCommand {

  public StopStatesCommand(StateSeries mainState) {
    super("stopStates", "Stop all states", mainState);
  }

  @Override
  public boolean execute(CommandSender sender, String label, String[] args) {
    super.execute(sender, label, args);

    if (!mainState.getFrozen()) {
      mainState.setFrozen(true);
      sender
        .getServer()
        .broadcast(
          TextFormat.RED + sender.getName() + " stopped the states!",
          getPermission()
        );
    } else {
      sender.sendMessage(TextFormat.RED + "The states are already stopped!");
    }

    return true;
  }
}
