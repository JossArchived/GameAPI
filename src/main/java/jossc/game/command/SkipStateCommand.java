package jossc.game.command;

import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import net.minikloon.fsmgasm.StateSeries;

public class SkipStateCommand extends StateCommand {

  public SkipStateCommand(StateSeries mainState) {
    super("skipState", "Skip states", mainState);
  }

  @Override
  public boolean execute(CommandSender sender, String label, String[] args) {
    super.execute(sender, label, args);

    sender
      .getServer()
      .broadcast(
        TextFormat.RED + sender.getName() + " has skipped to the next state!",
        getPermission()
      );

    mainState.skip();

    return false;
  }
}
