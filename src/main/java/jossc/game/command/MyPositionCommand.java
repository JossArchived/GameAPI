package jossc.game.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;

public class MyPositionCommand extends Command {

  public MyPositionCommand() {
    super("mypositon", "Show your position");
  }

  @Override
  public boolean execute(CommandSender sender, String label, String[] args) {
    if (!(sender instanceof Player)) {
      return false;
    }

    Player player = (Player) sender;

    sender.sendMessage(
      TextFormat.YELLOW +
      "x: " +
      (int) player.x +
      ", y: " +
      (int) player.y +
      ", z: " +
      (int) player.z
    );

    return true;
  }
}
