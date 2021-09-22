package jossc.game;

import cn.nukkit.command.Command;
import cn.nukkit.event.Listener;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;
import java.util.Arrays;

public abstract class Game extends PluginBase implements IGame {

  @Override
  public void onEnable() {
    super.onEnable();

    getServer().getCommandMap().clearCommands();

    init();

    getLogger().info(TextFormat.GREEN + "This game has been enabled!");
  }

  @Override
  public void onDisable() {
    super.onDisable();

    close();

    getLogger().info(TextFormat.RED + "This game has been disabled!");
  }

  protected void registerCommand(Command command) {
    registerCommands(command);
  }

  protected void registerCommands(Command... commands) {
    Arrays
      .stream(commands)
      .forEach(
        command ->
          getServer().getCommandMap().register(command.getName(), command)
      );
  }

  protected void registerListener(Listener listener) {
    registerListeners(listener);
  }

  protected void registerListeners(Listener... listeners) {
    Arrays
      .stream(listeners)
      .forEach(
        listener ->
          getServer().getPluginManager().registerEvents(listener, this)
      );
  }

  @Override
  public void init() {}

  @Override
  public void close() {}
}
