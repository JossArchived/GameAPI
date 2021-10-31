package jossc.game.phase;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerLoginEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.network.protocol.PlaySoundPacket;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.scheduler.TaskHandler;
import cn.nukkit.utils.TextFormat;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;
import jossc.game.Game;
import jossc.game.utils.PlayerUtils;
import net.minikloon.fsmgasm.State;
import org.jetbrains.annotations.NotNull;

public abstract class GamePhase extends State implements Listener {

  protected final PluginBase plugin;
  protected final Duration duration;

  protected final Set<Listener> listeners = new HashSet<>();
  protected final Set<TaskHandler> tasks = new HashSet<>();

  protected final Game game;

  public GamePhase(PluginBase plugin) {
    this(plugin, Duration.ZERO);
  }

  public GamePhase(PluginBase plugin, Duration duration) {
    this.plugin = plugin;
    this.duration = duration;
    this.game = Game.getInstance();
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

    cleanup();
  }

  protected final void cleanup() {
    listeners.forEach(HandlerList::unregisterAll);
    listeners.clear();

    tasks.forEach(TaskHandler::cancel);
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

  @Override
  protected void onEnd() {}

  @EventHandler
  public void onLogin(PlayerLoginEvent event) {
    Player player = event.getPlayer();

    if (player.hasPermission("allow.login.to.spectate")) {
      return;
    }

    if (game.isStarting()) {
      player.kick(TextFormat.RED + "This game is already in progress", false);

      return;
    }

    if (game.isFull()) {
      player.kick(TextFormat.RED + "This game is full", false);
    }
  }

  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
    Player player = event.getPlayer();

    if (!game.isAvailable()) {
      event.setJoinMessage("");

      PlayerUtils.convertSpectator(player);
      player.teleport(game.getMap().getSafeSpawn().add(0, 1));

      return;
    }

    player.teleport(game.getWaitingLobby());
    player.setGamemode(Player.ADVENTURE);

    int players = neutralPlayersSize();
    int maxPlayers = game.getMaxPlayers();

    event.setJoinMessage(
      TextFormat.colorize(
        "&a&l» &r&7" +
        player.getName() +
        " has joined. &8[" +
        players +
        "/" +
        maxPlayers +
        "]"
      )
    );
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent event) {
    Player player = event.getPlayer();

    if (player.isSpectator()) {
      event.setQuitMessage("");

      return;
    }

    int players = (neutralPlayersSize() - 1);
    int maxPlayers = game.getMaxPlayers();

    event.setQuitMessage(
      TextFormat.colorize(
        "&c&l» &r&7" +
        player.getName() +
        " has left. &8[" +
        players +
        "/" +
        maxPlayers +
        "]"
      )
    );
  }
}
