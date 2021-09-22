package jossc.game;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.Listener;
import cn.nukkit.network.protocol.PlaySoundPacket;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.scheduler.TaskHandler;
import cn.nukkit.utils.TextFormat;
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

    if (!super.getEnded()) {
      return;
    }

    listeners.forEach(HandlerList::unregisterAll);
    tasks.forEach(TaskHandler::cancel);
    listeners.clear();
    tasks.clear();
  }

  protected final Collection<? extends Player> getPlayers() {
    return Server.getInstance().getOnlinePlayers().values();
  }
  
  protected final void playSound(Player player, String soundName) {
    PlaySoundPacket pk = new PlaySoundPacket();
    pk.name = soundName;
    pk.x = (int) player.x;
    pk.y = (int) player.y;
    pk.z = (int) player.z;
    pk.pitch = 1;
    pk.volume = 1;

    player.dataPacket(pk);
  }

  protected final void broadcastSound(String soundName) {
    getPlayers()
        .forEach(player -> playSound(player, soundName));
  }

  protected final void broadcastMessage(String message) {
    getPlayers()
      .forEach(player -> player.sendMessage(TextFormat.colorize(message)));
  }

  protected final void broadcastActionBar(String title) {
    getPlayers()
      .forEach(player -> player.sendActionBar(TextFormat.colorize(title)));
  }

  protected final void broadcastTitle(String title) {
    getPlayers()
        .forEach(player -> player.sendTitle(TextFormat.colorize(title)));
  }

  protected final void broadcastTitle(String title, String subTitle) {
    getPlayers()
        .forEach(player -> player.sendTitle(TextFormat.colorize(title), TextFormat.colorize(subTitle)));
  }

  protected final void broadcastTitle(String title, String subTitle, int fadeIn, int stay, int fadeOut) {
    getPlayers()
        .forEach(player -> player.sendTitle(TextFormat.colorize(title), TextFormat.colorize(subTitle), fadeIn, stay, fadeOut));
  }

  protected final void broadcastPopup(String message) {
    getPlayers()
        .forEach(player -> player.sendPopup(TextFormat.colorize(message)));
  }

  protected final void broadcastTip(String message) {
    getPlayers()
        .forEach(player -> player.sendTip(TextFormat.colorize(message)));
  }

  protected final void broadcastActionBar(
    String title,
    int fadeIn,
    int stay,
    int fadeOut
  ) {
    getPlayers()
      .forEach(
        player ->
          player.sendActionBar(
            TextFormat.colorize(title),
            fadeIn,
            stay,
            fadeOut
          )
      );
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
