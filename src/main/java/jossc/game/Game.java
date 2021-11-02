package jossc.game;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.event.Event;
import cn.nukkit.event.Listener;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.ChangeDimensionPacket;
import cn.nukkit.network.protocol.PlayStatusPacket;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.potion.Effect;
import cn.nukkit.utils.TextFormat;
import java.io.File;
import java.util.*;
import jossc.game.command.FreezePhasesCommand;
import jossc.game.command.MyPositionCommand;
import jossc.game.command.SkipPhaseCommand;
import jossc.game.command.UnfreezePhasesCommand;
import jossc.game.event.ConvertPlayerEvent;
import jossc.game.event.ConvertSpectatorEvent;
import jossc.game.phase.PhaseSeries;
import jossc.game.utils.zipper.Zipper;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Game extends PluginBase {

  protected int minPlayers = 2;

  protected int maxPlayers = 12;

  protected String mapName;

  protected Position waitingLobby;

  protected List<Vector3> spawns;

  protected boolean developmentMode = false;

  protected boolean starting = false;

  protected int defaultGameMode = Player.SURVIVAL;

  protected List<String> tips;

  protected File mapBackupFile = null;

  protected Map<Integer, Position> pedestalList;

  protected Position pedestalPosition;

  protected PhaseSeries phaseSeries = null;

  @Override
  public void onEnable() {
    super.onEnable();

    spawns = new LinkedList<>();
    tips = new ArrayList<>();
    pedestalList = new HashMap<>();

    if (developmentMode) {
      registerCommand(new MyPositionCommand());
    }

    init();

    getLogger().info(TextFormat.GREEN + "This game has been enabled!");
  }

  public Level getMap() {
    return getServer().getLevelByName(mapName);
  }

  public boolean isFull() {
    return getServer().getOnlinePlayers().size() >= maxPlayers;
  }

  public boolean isAvailable() {
    return !isFull() && !starting;
  }

  @Override
  public void onDisable() {
    super.onDisable();

    close();

    getLogger().info(TextFormat.RED + "This game has been disabled!");
  }

  protected void registerDefaultCommands() {
    if (phaseSeries == null) {
      return;
    }

    registerCommands(
      new SkipPhaseCommand(phaseSeries),
      new FreezePhasesCommand(phaseSeries),
      new UnfreezePhasesCommand(phaseSeries)
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

  public String getTimeInCharacter(int currentTime, int initialTime) {
    String character = "▌";

    StringBuilder finalResult = new StringBuilder();

    for (int i = 1; i <= initialTime; i++) {
      finalResult
        .append(
          i <= currentTime
            ? (currentTime <= 3 ? TextFormat.RED : TextFormat.YELLOW)
            : TextFormat.GRAY
        )
        .append(character);
    }

    return finalResult.toString();
  }

  public String formatTime(int time) {
    int minutes = Math.floorDiv(time, 60);
    int seconds = (int) Math.floor(time % 60);
    return (
      (minutes < 10 ? "0" : "") +
      minutes +
      ":" +
      (seconds < 10 ? "0" : "") +
      seconds
    );
  }

  public void clearAllPlayerArmorInventory(Player player) {
    Item air = Item.get(Item.AIR);

    PlayerInventory inventory = player.getInventory();
    inventory.setHelmet(air);
    inventory.setChestplate(air);
    inventory.setLeggings(air);
    inventory.setBoots(air);

    updatePlayerInventory(player);
  }

  public void clearAllPlayerInventory(Player player) {
    player.getInventory().clearAll();
    clearAllPlayerArmorInventory(player);
    updatePlayerInventory(player);
  }

  public void updatePlayerInventory(Player player) {
    PlayerInventory inventory = player.getInventory();
    inventory.sendArmorContents(player);
    inventory.sendContents(player);
    inventory.sendHeldItem(player);
  }

  public void giveDefaultAttributes(Player player) {
    player.setAllowFlight(false);
    player.setHealth(20);
    player.setMaxHealth(20);
    player.setOnFire(0);
    player.extinguish();
    player.setFoodEnabled(false);
    player.getFoodData().setLevel(20);
    player.setExperience(0);
    player.setImmobile(false);
    player.setMovementSpeed(0.1F);
    player.sendExperienceLevel(0);
    player.getInventory().clearAll();
    player.getCraftingGrid().clearAll();
    player.getCursorInventory().clearAll();
    player.getUIInventory().clearAll();
    player.getOffhandInventory().clearAll();
    player.getEnderChestInventory().clearAll();
    player.removeAllEffects();
    clearAllPlayerArmorInventory(player);
    updatePlayerInventory(player);
  }

  public void convertSpectator(Player player) {
    convertSpectator(player, false);
  }

  public void convertSpectator(Player player, boolean haveLost) {
    player.setGamemode(Player.SPECTATOR);
    giveDefaultAttributes(player);
    player.getServer().removeOnlinePlayer(player);

    if (haveLost) {
      player.addEffect(
        Effect
          .getEffect(Effect.BLINDNESS)
          .setDuration(20 * 5)
          .setAmplifier(2)
          .setVisible(false)
      );
      player.sendTitle(
        TextFormat.RED + "You have lost!",
        TextFormat.YELLOW + "Now spectating."
      );

      callEvent(new ConvertSpectatorEvent(player, true));

      return;
    }

    callEvent(new ConvertSpectatorEvent(player, false));
  }

  public void convertPlayer(Player player) {
    convertPlayer(player, false);
  }

  public void convertPlayer(Player player, boolean forcedToEnd) {
    player.setGamemode(Player.ADVENTURE);
    giveDefaultAttributes(player);

    player.setNameTag(TextFormat.GRAY + player.getName());

    callEvent(new ConvertPlayerEvent(player, forcedToEnd));
  }

  public void callEvent(Event event) {
    getServer().getPluginManager().callEvent(event);
  }

  public boolean existMapBackup() {
    return mapBackupFile != null && mapBackupFile.exists();
  }

  public void removeMapBackup() {
    if (!existMapBackup()) {
      return;
    }

    mapBackupFile.delete();
  }

  public void resetMapBackup() {
    if (mapName.equalsIgnoreCase("world")) {
      getLogger().error("You can not use world default as game Map!");

      return;
    }

    if (!existMapBackup()) {
      return;
    }

    if (getServer().isLevelLoaded(mapName)) {
      getServer().unloadLevel(getMap());
    }

    try {
      Zipper.unzip(
        mapBackupFile.toString(),
        getServer().getDataPath() + "/worlds/"
      );
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void schedule(Runnable runnable, int delay) {
    getServer().getScheduler().scheduleDelayedTask(this, runnable, delay);
  }

  public void sendChangeDimensionPacket(Player player, int dimension) {
    ChangeDimensionPacket changeDimensionPacket = new ChangeDimensionPacket();

    changeDimensionPacket.dimension = dimension;
    changeDimensionPacket.x = (float) player.x;
    changeDimensionPacket.y = (float) player.y;
    changeDimensionPacket.z = (float) player.z;

    player.dataPacket(changeDimensionPacket);

    getServer()
      .getScheduler()
      .scheduleDelayedTask(
        () -> {
          PlayStatusPacket playStatusPacket = new PlayStatusPacket();
          playStatusPacket.status = PlayStatusPacket.PLAYER_SPAWN;
          player.dataPacket(playStatusPacket);
        },
        10
      );
  }

  public void shutdown() {
    getServer().forceShutdown();
  }

  public abstract String getGameName();

  public abstract String getInstruction();

  public abstract void init();

  public abstract void close();
}
