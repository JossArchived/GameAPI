package net.josscoder.gameapi.command;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import net.josscoder.gameapi.Game;
import net.josscoder.gameapi.configurator.waitingroom.WaitingRoomConfigurator;

public class SetWaitingRoomCommand extends GameCommand {

  public SetWaitingRoomCommand(Game game) {
    super(
      game,
      "setwaitingroom",
      "Set waiting room",
      TextFormat.RED + "Usage /setwaitingroom <mapName>"
    );
  }

  @Override
  public boolean execute(CommandSender sender, String label, String[] args) {
    if (!(sender instanceof Player)) {
      return false;
    }

    if (args.length < 1) {
      sender.sendMessage(getUsage());

      return false;
    }

    WaitingRoomConfigurator configurator = new WaitingRoomConfigurator(
      game,
      (Player) sender,
      args[0]
    );
    configurator.run();

    return true;
  }
}
