package jossc.game.state;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.Listener;
import cn.nukkit.network.protocol.PlaySoundPacket;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.scheduler.TaskHandler;
import cn.nukkit.utils.TextFormat;
import java.util.*;
import java.util.stream.Collectors;
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

  protected final Collection<? extends Player> getSpectators() {
    return new ArrayList<Player>(getPlayers(Player.SPECTATOR));
  }

  protected final Collection<? extends Player> getPlayers() {
    List<Player> players = new ArrayList<>();

    players.addAll(getPlayers(Player.SURVIVAL));
    players.addAll(getPlayers(Player.ADVENTURE));
    players.addAll(getPlayers(Player.CREATIVE));

    return players;
  }

  protected final Collection<? extends Player> getPlayers(int gamemode) {
    return Server
      .getInstance()
      .getOnlinePlayers()
      .values()
      .stream()
      .filter(player -> player.getGamemode() == gamemode)
      .collect(Collectors.toList());
  }

  protected int playersSize() {
    return getPlayers().size();
  }

  protected int spectatorsSize() {
    return getSpectators().size();
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
    broadcastSound(soundName, false);
  }

  protected final void broadcastSound(String soundName, boolean toSpectator) {
    if (toSpectator) {
      getSpectators().forEach(spectator -> playSound(spectator, soundName));
    } else {
      getPlayers().forEach(player -> playSound(player, soundName));
    }
  }

  protected final void broadcastMessage(String message) {
    broadcastMessage(message, false);
  }

  protected final void broadcastMessage(String message, boolean toSpectator) {
    if (toSpectator) {
      getSpectators()
        .forEach(
          spectator -> spectator.sendMessage(TextFormat.colorize(message))
        );
    } else {
      getPlayers()
        .forEach(player -> player.sendMessage(TextFormat.colorize(message)));
    }
  }

  protected final void broadcastActionBar(String title) {
    broadcastActionBar(title, false);
  }

  protected final void broadcastActionBar(String title, boolean toSpectator) {
    if (toSpectator) {
      getSpectators()
        .forEach(
          spectator -> spectator.sendActionBar(TextFormat.colorize(title))
        );
    } else {
      getPlayers()
        .forEach(player -> player.sendActionBar(TextFormat.colorize(title)));
    }
  }

  protected final void broadcastActionBar(
    String title,
    int fadeIn,
    int stay,
    int fadeOut
  ) {
    broadcastActionBar(title, fadeIn, stay, fadeOut, false);
  }

  protected final void broadcastActionBar(
    String title,
    int fadeIn,
    int stay,
    int fadeOut,
    boolean toSpectator
  ) {
    if (toSpectator) {
      getSpectators()
        .forEach(
          spectator ->
            spectator.sendActionBar(
              TextFormat.colorize(title),
              fadeIn,
              stay,
              fadeOut
            )
        );
    } else {
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
  }

  protected final void broadcastTitle(String title) {
    broadcastTitle(title, false);
  }

  protected final void broadcastTitle(String title, String subTitle) {
    broadcastTitle(title, subTitle, false);
  }

  protected final void broadcastTitle(
    String title,
    String subTitle,
    int fadeIn,
    int stay,
    int fadeOut
  ) {
    broadcastTitle(title, subTitle, fadeIn, stay, fadeOut, false);
  }

  protected final void broadcastTitle(String title, boolean toSpectator) {
    if (toSpectator) {
      getSpectators()
        .forEach(spectator -> spectator.sendTitle(TextFormat.colorize(title)));
    } else {
      getPlayers()
        .forEach(player -> player.sendTitle(TextFormat.colorize(title)));
    }
  }

  protected final void broadcastTitle(
    String title,
    String subTitle,
    boolean toSpectator
  ) {
    if (toSpectator) {
      getSpectators()
        .forEach(
          spectator ->
            spectator.sendTitle(
              TextFormat.colorize(title),
              TextFormat.colorize(subTitle)
            )
        );
    } else {
      getPlayers()
        .forEach(
          player ->
            player.sendTitle(
              TextFormat.colorize(title),
              TextFormat.colorize(subTitle)
            )
        );
    }
  }

  protected final void broadcastTitle(
    String title,
    String subTitle,
    int fadeIn,
    int stay,
    int fadeOut,
    boolean toSpectator
  ) {
    if (toSpectator) {
      getSpectators()
        .forEach(
          spectator ->
            spectator.sendTitle(
              TextFormat.colorize(title),
              TextFormat.colorize(subTitle),
              fadeIn,
              stay,
              fadeOut
            )
        );
    } else {
      getPlayers()
        .forEach(
          player ->
            player.sendTitle(
              TextFormat.colorize(title),
              TextFormat.colorize(subTitle),
              fadeIn,
              stay,
              fadeOut
            )
        );
    }
  }

  protected final void broadcastPopup(String message) {
    broadcastPopup(message, false);
  }

  protected final void broadcastPopup(String message, boolean toSpectator) {
    if (toSpectator) {
      getSpectators()
        .forEach(
          spectator -> spectator.sendPopup(TextFormat.colorize(message))
        );
    } else {
      getPlayers()
        .forEach(player -> player.sendPopup(TextFormat.colorize(message)));
    }
  }

  protected final void broadcastTip(String message) {
    broadcastTip(message, false);
  }

  protected final void broadcastTip(String message, boolean toSpectator) {
    if (toSpectator) {
      getSpectators()
        .forEach(spectator -> spectator.sendTip(TextFormat.colorize(message)));
    } else {
      getPlayers()
        .forEach(player -> player.sendTip(TextFormat.colorize(message)));
    }
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
