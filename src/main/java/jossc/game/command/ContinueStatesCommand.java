package jossc.game.command;

import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import net.minikloon.fsmgasm.StateSeries;

public class ContinueStatesCommand extends StateCommand {

  public ContinueStatesCommand(StateSeries mainState) {
    super("continueStates", "Continue with the states", mainState);
  }

  @Override
  public boolean execute(CommandSender sender, String label, String[] args) {
    super.execute(sender, label, args);

    if (mainState.getFrozen()) {
      mainState.setFrozen(false);
      sender
        .getServer()
        .broadcast(
          TextFormat.GREEN + sender.getName() + " continued with all states!",
          getPermission()
        );
    } else {
      sender.sendMessage(TextFormat.RED + "The states are not stopped!");
    }

    return true;
  }
}
