package jossc.game.command;

import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import jossc.game.phase.PhaseSeries;

public abstract class PhaseCommand extends Command {

  protected final PhaseSeries series;

  public PhaseCommand(String name, PhaseSeries series) {
    this(name, "", series);
  }

  public PhaseCommand(String name, String description, PhaseSeries series) {
    this(name, description, "", series);
  }

  public PhaseCommand(
    String name,
    String description,
    String usageMessage,
    PhaseSeries series
  ) {
    this(name, description, usageMessage, new String[] {}, series);
  }

  public PhaseCommand(
    String name,
    String description,
    String usageMessage,
    String[] aliases,
    PhaseSeries series
  ) {
    super(name, description, usageMessage, aliases);
    this.series = series;

    setPermission("minigame.admin.permission");
    setPermissionMessage(
      TextFormat.RED +
      "You need to have an administrator rank or a higher rank to execute this command!"
    );
  }

  @Override
  public boolean execute(CommandSender sender, String label, String[] args) {
    if (!sender.hasPermission(getPermission())) {
      sender.sendMessage(getPermissionMessage());

      return false;
    }

    return true;
  }

  public void broadcast(String message) {
    Server.getInstance().broadcastMessage(message);
  }
}
