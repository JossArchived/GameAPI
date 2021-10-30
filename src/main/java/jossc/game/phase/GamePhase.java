package jossc.game.phase;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.Listener;
import cn.nukkit.network.protocol.PlaySoundPacket;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.scheduler.TaskHandler;
import cn.nukkit.utils.TextFormat;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;
import net.minikloon.fsmgasm.State;
import org.jetbrains.annotations.NotNull;

public abstract class GamePhase extends State implements Listener {

  protected final PluginBase plugin;
  protected final Duration duration;

  protected final Set<Listener> listeners = new HashSet<>();
  protected final Set<TaskHandler> tasks = new HashSet<>();

  public GamePhase(PluginBase plugin) {
    this(plugin, Duration.ZERO);
  }

  public GamePhase(PluginBase plugin, Duration duration) {
    this.plugin = plugin;
    this.duration = duration;
  }

  @NotNull
  @Override
  public Duration getDuration() {
    return duration;
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

  protected final Collection<? extends Player> getSpectators() {
    return new ArrayList<Player>(filterPlayerByGamemode(Player.SPECTATOR));
  }

  protected final Collection<? extends Player> getNeutralPlayers() {
    List<Player> players = new ArrayList<>();

    players.addAll(filterPlayerByGamemode(Player.SURVIVAL));
    players.addAll(filterPlayerByGamemode(Player.ADVENTURE));
    players.addAll(filterPlayerByGamemode(Player.CREATIVE));

    return players;
  }

  private Collection<? extends Player> filterPlayerByGamemode(int gamemode) {
    return getPlayers()
      .stream()
      .filter(player -> player.getGamemode() == gamemode)
      .collect(Collectors.toList());
  }

  protected int neutralPlayersSize() {
    return getNeutralPlayers().size();
  }

  protected int spectatorsSize() {
    return getSpectators().size();
  }

  protected final void playSound(Player player, String soundName) {
    playSound(player, soundName, 1, 1);
  }

  protected final void playSound(
    Player player,
    String soundName,
    int pitch,
    int volume
  ) {
    PlaySoundPacket pk = new PlaySoundPacket();
    pk.name = soundName;
    pk.x = (int) player.x;
    pk.y = (int) player.y;
    pk.z = (int) player.z;
    pk.pitch = pitch;
    pk.volume = volume;

    player.dataPacket(pk);
  }

  protected final void broadcastSound(String soundName, boolean toSpectator) {
    broadcastSound(soundName, 1, 1, toSpectator);
  }

  protected final void broadcastSound(String soundName) {
    broadcastSound(soundName, 1, 1, false);
  }

  protected final void broadcastSound(String soundName, int pitch, int volume) {
    broadcastSound(soundName, pitch, volume, false);
  }

  protected final void broadcastSound(
    String soundName,
    int pitch,
    int volume,
    boolean toSpectator
  ) {
    if (toSpectator) {
      getSpectators()
        .forEach(spectator -> playSound(spectator, soundName, pitch, volume));
    } else {
      getNeutralPlayers()
        .forEach(player -> playSound(player, soundName, pitch, volume));
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
      getNeutralPlayers()
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
      getNeutralPlayers()
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
      getNeutralPlayers()
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
      getNeutralPlayers()
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
      getNeutralPlayers()
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
      getNeutralPlayers()
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
      getNeutralPlayers()
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
      getNeutralPlayers()
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
