package jossc.game;

import cn.nukkit.command.Command;
import cn.nukkit.command.SimpleCommandMap;
import cn.nukkit.event.Listener;
import cn.nukkit.level.Level;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;
import java.util.Arrays;
import jossc.game.command.ContinueStatesCommand;
import jossc.game.command.SkipStateCommand;
import jossc.game.command.StopStatesCommand;
import jossc.game.state.ScheduledStateSeries;

public abstract class Game extends PluginBase implements IGame {

  @Override
  public void onEnable() {
    super.onEnable();

    init();

    getLogger().info(TextFormat.GREEN + "This game has been enabled!");
  }

  @Override
  public void onDisable() {
    super.onDisable();

    close();

    getLogger().info(TextFormat.RED + "This game has been disabled!");
  }

  protected void registerDefaultCommands(ScheduledStateSeries mainState) {
    registerCommands(
      new SkipStateCommand(mainState),
      new StopStatesCommand(mainState),
      new ContinueStatesCommand(mainState)
    );
  }

  protected void unregisterAllCommands() {
    SimpleCommandMap commandMap = getServer().getCommandMap();

    commandMap
      .getCommands()
      .values()
      .forEach(command -> command.unregister(commandMap));
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

  protected void prepareMap(String mapName) {
    prepareMap(mapName, Level.TIME_DAY);
  }

  protected void prepareMap(String mapName, int time) {
    if (!getServer().isLevelLoaded(mapName)) {
      getServer().loadLevel(mapName);
    }

    Level level = getServer().getLevelByName(mapName);

    if (level == null) {
      return;
    }

    level.setTime(time);
    level.stopTime();
    level.setRaining(false);
    level.setThundering(false);
  }

  @Override
  public void init() {}

  @Override
  public void close() {}
}
