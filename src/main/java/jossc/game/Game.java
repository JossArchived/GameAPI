package jossc.game;

import cn.nukkit.command.Command;
import cn.nukkit.event.Listener;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import jossc.game.command.FreezePhasesCommand;
import jossc.game.command.MyPositionCommand;
import jossc.game.command.SkipPhaseCommand;
import jossc.game.command.UnfreezePhasesCommand;
import jossc.game.listener.MainEventListener;
import jossc.game.phase.PhaseSeries;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Game extends PluginBase {

  @Getter
  protected static Game instance;

  protected int minPlayers = 2;

  protected int maxPlayers = 12;

  protected String mapName;

  protected Vector3 waitingLobby;

  protected List<Vector3> slots = new LinkedList<>();

  protected boolean developmentMode = false;

  @Override
  public void onEnable() {
    super.onEnable();

    instance = this;

    registerListener(new MainEventListener());

    if (developmentMode) {
      registerCommand(new MyPositionCommand());
    }

    init();

    getLogger().info(TextFormat.GREEN + "This game has been enabled!");
  }

  @Override
  public void onDisable() {
    super.onDisable();

    close();

    getLogger().info(TextFormat.RED + "This game has been disabled!");
  }

  protected void registerDefaultCommands(PhaseSeries series) {
    registerCommands(
      new SkipPhaseCommand(series),
      new FreezePhasesCommand(series),
      new UnfreezePhasesCommand(series)
    );
  }

  protected void unregisterAllCommands() {
    getServer()
      .getCommandMap()
      .getCommands()
      .values()
      .forEach(this::unregisterCommand);
  }

  protected void unregisterCommand(Command command) {
    command.unregister(getServer().getCommandMap());
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

  public abstract void init();

  public abstract void close();
}
