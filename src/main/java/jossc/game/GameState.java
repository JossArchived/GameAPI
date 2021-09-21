package jossc.game;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.Listener;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.scheduler.TaskHandler;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import net.minikloon.fsmgasm.State;

public abstract class GameState extends State implements Listener {

  protected final PluginBase plugin;

  protected final Set<Listener> listeners = new HashSet<>();
  protected final Set<TaskHandler> tasks = new HashSet<>();

  public GameState(PluginBase plugin) {
    this.plugin = plugin;
  }

  @Override
  public final void start() {
    super.start();
    register(this);
  }

  @Override
  public final void end() {
    super.end();
    if (!super.getEnded()) return;
    listeners.forEach(HandlerList::unregisterAll);
    tasks.forEach(TaskHandler::cancel);
    listeners.clear();
    tasks.clear();
  }

  protected final Collection<? extends Player> getPlayers() {
    return Server.getInstance().getOnlinePlayers().values();
  }

  protected final void broadcast(String message) {
    getPlayers().forEach(player -> player.sendMessage(message));
  }

  protected void register(Listener listener) {
    listeners.add(listener);
    plugin.getServer().getPluginManager().registerEvents(listener, plugin);
  }

  protected void schedule(Runnable runnable, int delay) {
    TaskHandler task = plugin
      .getServer()
      .getScheduler()
      .scheduleDelayedTask(plugin, runnable, delay);
    tasks.add(task);
  }

  protected void scheduleRepeating(Runnable runnable, int delay, int interval) {
    TaskHandler task = plugin
      .getServer()
      .getScheduler()
      .scheduleDelayedRepeatingTask(plugin, runnable, delay, interval);
    tasks.add(task);
  }
}
