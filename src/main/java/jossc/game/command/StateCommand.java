package jossc.game.command;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import net.minikloon.fsmgasm.StateSeries;

public abstract class StateCommand extends Command {

  protected final StateSeries mainState;

  public StateCommand(String name, StateSeries mainState) {
    this(name, "", mainState);
  }

  public StateCommand(String name, String description, StateSeries mainState) {
    this(name, description, "", mainState);
  }

  public StateCommand(
    String name,
    String description,
    String usageMessage,
    StateSeries mainState
  ) {
    this(name, description, usageMessage, new String[] {}, mainState);
  }

  public StateCommand(
    String name,
    String description,
    String usageMessage,
    String[] aliases,
    StateSeries mainState
  ) {
    super(name, description, usageMessage, aliases);
    this.mainState = mainState;

    setPermissionMessage(
      TextFormat.RED +
      "You need to have an administrator rank or a higher rank to execute this command!"
    );
    setPermission("minigame.admin.permission");
  }

  @Override
  public boolean execute(CommandSender sender, String label, String[] args) {
    if (!sender.hasPermission(getPermission())) {
      sender.sendMessage(getPermissionMessage());

      return false;
    }

    return true;
  }
}
