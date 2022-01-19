/*
 * Copyright 2021-2055 Josscoder
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package net.josscoder.gameapi;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.Event;
import cn.nukkit.event.Listener;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.Level;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.scheduler.Task;
import cn.nukkit.scheduler.TaskHandler;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.josscoder.gameapi.command.base.MyPositionCommand;
import net.josscoder.gameapi.command.base.SoundCommand;
import net.josscoder.gameapi.command.base.TeleporterCommand;
import net.josscoder.gameapi.command.base.VoteCommand;
import net.josscoder.gameapi.command.base.phase.FreezePhasesCommand;
import net.josscoder.gameapi.command.base.phase.SkipPhaseCommand;
import net.josscoder.gameapi.command.base.phase.UnfreezePhasesCommand;
import net.josscoder.gameapi.customitem.CustomItem;
import net.josscoder.gameapi.customitem.factory.CustomItemFactory;
import net.josscoder.gameapi.customitem.listener.InteractiveListener;
import net.josscoder.gameapi.customitem.listener.TransferableListener;
import net.josscoder.gameapi.map.GameMap;
import net.josscoder.gameapi.map.WaitingRoomMap;
import net.josscoder.gameapi.map.manager.GameMapManager;
import net.josscoder.gameapi.phase.GamePhase;
import net.josscoder.gameapi.phase.PhaseSeries;
import net.josscoder.gameapi.phase.base.EndGamePhase;
import net.josscoder.gameapi.phase.base.LobbyCountdownPhase;
import net.josscoder.gameapi.phase.base.LobbyWaitingPhase;
import net.josscoder.gameapi.phase.base.PreGamePhase;
import net.josscoder.gameapi.team.Team;
import net.josscoder.gameapi.team.Teamable;
import net.josscoder.gameapi.user.factory.UserFactory;
import net.josscoder.gameapi.user.listener.UserEventListener;
import net.josscoder.gameapi.util.ZipUtils;
import net.josscoder.gameapi.util.entity.CustomItemFirework;
import org.citizen.CitizenLibrary;

@Getter
public abstract class Game extends PluginBase {

  private static final VersionInfo versionInfo = loadVersion();

  @Setter
  protected boolean developmentMode = false;

  @Setter
  protected WaitingRoomMap waitingRoomMap;

  @Setter
  protected int defaultPlayerGamemode = 0;

  @Setter
  protected boolean started = false;

  @Setter
  protected boolean mapVoteFinished = false;

  @Setter
  protected int minPlayers = 2;

  @Setter
  protected int maxPlayers = 12;

  @Setter
  protected boolean canMoveInPreGame = false;

  @Setter
  protected boolean canVoteMap = true;

  protected List<String> tips;

  protected PhaseSeries phaseSeries = null;

  protected UserFactory userFactory;

  protected CustomItemFactory customItemFactory;

  protected GameMapManager gameMapManager;

  private CitizenLibrary citizenLibrary;

  @Setter
  protected Map<Integer, CustomItem> waitingLobbyItems;

  @Setter
  protected Map<Integer, CustomItem> spectatorItems;

  @Setter
  protected List<Team> teams;

  protected GameType gameType = GameType.SOLO;

  private Thread mainThread;

  private ExecutorService threadPool;

  private String unexpectedMessage;

  public abstract String getId();

  public abstract String getGameName();

  public abstract String getInstruction();

  public abstract void init();

  public abstract void close();

  @SneakyThrows
  @Override
  public void onEnable() {
    Config config = getConfig();

    if (!config.exists("thread_pool_size")) {
      config.set("thread_pool_size", 20);
      config.save();
    }

    mainThread = Thread.currentThread();

    threadPool =
      Executors.newFixedThreadPool(config.getInt("thread_pool_size"));

    tips = new ArrayList<>();

    waitingLobbyItems = new HashMap<>();

    spectatorItems = new HashMap<>();

    teams = new ArrayList<>();

    userFactory = new UserFactory(this);

    gameMapManager = new GameMapManager(this);

    citizenLibrary = new CitizenLibrary(this);

    registerListener(
      new UserEventListener(this),
      new InteractiveListener(this),
      new TransferableListener(this)
    );

    if (canVoteMap) {
      registerCommand(new VoteCommand(this));
    }

    registerCommand(new TeleporterCommand(this));

    Entity.registerEntity("CustomItemFirework", CustomItemFirework.class, true);

    init();

    initItems();

    if (developmentMode) {
      getLogger().info(TextFormat.GOLD + "Development mode is enabled!");
      registerCommand(new SoundCommand(), new MyPositionCommand());
    }

    scheduleThread(
      () ->
        gameMapManager
          .getMaps()
          .keySet()
          .forEach(mapName -> reset(mapName, false))
    );

    if (canVoteMap) {
      int mapsSize = gameMapManager.mapsSize();

      getLogger()
        .info(TextFormat.AQUA.toString() + mapsSize + " map(s) registered");
    }

    settingUpServer();

    getLogger().info(TextFormat.GREEN + "This game has been enabled!");

    getLogger()
      .info(
        TextFormat.GREEN +
        "Running GameAPI v" +
        versionInfo.getSnapshot() +
        ", by " +
        versionInfo.getAuthor() +
        ", commit " +
        versionInfo.getCommitId()
      );
  }

  public boolean isTeamable() {
    return this instanceof Teamable;
  }

  private static VersionInfo loadVersion() {
    InputStream inputStream =
      Game.class.getClassLoader().getResourceAsStream("git.properties");
    if (inputStream == null) {
      return VersionInfo.unknown();
    }

    Properties properties = new Properties();
    try {
      properties.load(inputStream);
    } catch (IOException e) {
      return VersionInfo.unknown();
    }

    String branchName = properties.getProperty("git.branch", "unknown");
    String commitId = properties.getProperty("git.commit.id.abbrev", "unknown");
    return new VersionInfo(branchName, commitId);
  }

  private void settingUpServer() {
    getServer()
      .getNetwork()
      .setName(
        TextFormat.RESET.toString() + TextFormat.GRAY + "game-" + getId()
      );

    unexpectedMessage =
      TextFormat.colorize(
        "&8Unexpected? Report this &7(" + getId() + ")&8: &c"
      );

    getServer()
      .setPropertyString("shutdown-message", unexpectedMessage + "Game Reset!");
    getServer()
      .setPropertyString(
        "whitelist-reason",
        unexpectedMessage + "Game in Maintenance!"
      );
    getServer().setPropertyBoolean("achievements", false);
    getServer().setPropertyBoolean("announce-player-achievements", false);
    getServer().setPropertyInt("spawn-protection", 0);
    getServer().setPropertyBoolean("spawn-animals", false);
    getServer().setPropertyBoolean("spawn-mobs", false);
    getServer().setPropertyBoolean("bed-spawnpoints", false);
    getServer().setPropertyBoolean("save-player-data", false);
    getServer().setPropertyBoolean("nether", false);
    getServer().setPropertyBoolean("end", false);
    getServer().setPropertyBoolean("dimensions", true);
  }

  protected void initItems() {
    initWaitingLobbyItems();
    initSpectatorItems();
  }

  protected void initWaitingLobbyItems() {
    if (canVoteMap) {
      CustomItem voteMapItem = new CustomItem(
        Item.get(ItemID.PAPER),
        "&r&bVote for Map &7[Use]"
      );
      voteMapItem.setTransferable(false).addCommands("vote");
      waitingLobbyItems.put(0, voteMapItem);
    }

    CustomItem hubItem = new CustomItem(
      Item.get(ItemID.DRAGON_BREATH),
      "&r&eBack to Hub &7[Use]"
    );
    hubItem
      .setTransferable(false)
      .setInteractHandler(((user, player) -> sendToHub(player)));
    waitingLobbyItems.put(8, hubItem);
  }

  protected void initSpectatorItems() {
    CustomItem teleporterItem = new CustomItem(
      Item.get(Item.COMPASS),
      "&r&bTeleporter &7[Use]"
    );
    teleporterItem.setTransferable(false).addCommands("teleporter");
    spectatorItems.put(0, teleporterItem);

    CustomItem leaveGameItem = new CustomItem(
      Item.get(ItemID.DRAGON_BREATH),
      "&r&cLeave Game &7[Use]"
    );
    leaveGameItem
      .setTransferable(false)
      .setInteractHandler(((user, player) -> sendToHub(player)));
    spectatorItems.put(8, leaveGameItem);

    CustomItem newGameItem = new CustomItem(
      Item.get(Item.HEART_OF_THE_SEA),
      "&r&aNew game &7[Use]"
    );
    newGameItem
      .setTransferable(false)
      .setInteractHandler(
        (user, player) -> {
          player.sendMessage(
            TextFormat.colorize("&l&b»&r&7 We'll find a new game shortly...")
          );
          searchNewGameFor(player);
        }
      );
    spectatorItems.put(7, newGameItem);
  }

  public boolean isFull() {
    return getServer().getOnlinePlayers().size() >= maxPlayers;
  }

  public boolean isAvailable() {
    return !isFull() && !started;
  }

  public void unregisterAllCommands() {
    getServer()
      .getCommandMap()
      .getCommands()
      .values()
      .forEach(this::unregisterCommand);
  }

  protected List<GamePhase<Game>> createPreGamePhase() {
    return createPreGamePhase(Duration.ofSeconds(20));
  }

  protected List<GamePhase<Game>> createPreGamePhase(
    Duration lobbyCountdownDuration
  ) {
    return createPreGamePhase(lobbyCountdownDuration, 10);
  }

  protected List<GamePhase<Game>> createPreGamePhase(
    Duration lobbyCountdownDuration,
    int preGameCountdown
  ) {
    return createPreGamePhase(
      lobbyCountdownDuration,
      preGameCountdown,
      Level.TIME_DAY
    );
  }

  protected List<GamePhase<Game>> createPreGamePhase(
    Duration lobbyCountdownDuration,
    int preGameCountdown,
    int mapTime
  ) {
    List<GamePhase<Game>> gamePhases = new LinkedList<>();
    gamePhases.add(new LobbyWaitingPhase(this));
    gamePhases.add(new LobbyCountdownPhase(this, lobbyCountdownDuration));
    gamePhases.add(new PreGamePhase(this, preGameCountdown, mapTime));

    return gamePhases;
  }

  protected void registerPhaseCommands() {
    if (phaseSeries == null) {
      return;
    }

    registerCommand(
      new SkipPhaseCommand(phaseSeries),
      new FreezePhasesCommand(phaseSeries),
      new UnfreezePhasesCommand(phaseSeries)
    );
  }

  public void unregisterCommand(Command command) {
    command.unregister(getServer().getCommandMap());
  }

  public void registerCommand(Command command) {
    getServer().getCommandMap().register(command.getName(), command);
  }

  public void registerCommand(Command... commands) {
    Arrays.stream(commands).forEach(this::registerCommand);
  }

  public void registerListener(Listener listener) {
    getServer().getPluginManager().registerEvents(listener, this);
  }

  public void registerListener(Listener... listeners) {
    Arrays.stream(listeners).forEach(this::registerListener);
  }

  public void addTip(String... tip) {
    Arrays.stream(tip).forEach(this::addTip);
  }

  public void addTip(String tip) {
    tips.add(tip);
  }

  public <T extends Event> T callEvent(T event) {
    getServer().getPluginManager().callEvent(event);

    return event;
  }

  public void schedule(Runnable runnable, int delay) {
    getServer().getScheduler().scheduleDelayedTask(this, runnable, delay);
  }

  public boolean isMainThread() {
    return isMainThread(Thread.currentThread());
  }

  public boolean isMainThread(Thread thread) {
    return thread == mainThread;
  }

  public void scheduleOnMainThread(Runnable runnable) {
    if (isMainThread()) {
      runnable.run();
    } else {
      schedule(i -> runnable.run());
    }
  }

  public TaskHandler schedule(Task task) {
    return Server.getInstance().getScheduler().scheduleTask(task);
  }

  public TaskHandler schedule(Handler task) {
    return schedule(
      new Task() {
        @Override
        public void onRun(int i) {
          task.handle(i);
        }
      }
    );
  }

  public void scheduleThread(Handler task) {
    scheduleThread(() -> task.handle(0));
  }

  public void scheduleThread(Runnable task) {
    if (isMainThread()) {
      threadPool.execute(task);
    } else {
      task.run();
    }
  }

  public void sleep(long milliseconds) {
    try {
      Thread.sleep(milliseconds);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public void reset() {
    reset("", true);
  }

  public void reset(String mapName, boolean shutdown) {
    if (!mapName.isEmpty() && mapHasBackup(mapName)) {
      resetMapBackup(mapName);
    }

    if (shutdown) {
      schedule(() -> getServer().forceShutdown(), 20 * 5);
    }
  }

  public String getWorldsFolder() {
    return getServer().getDataPath() + "/worlds/";
  }

  public String getBackupFolder() {
    return getServer().getDataPath() + "/backups/";
  }

  public void storeMapBackup(String mapName) {
    unloadMap(mapName);

    String backupFolder = getBackupFolder();

    File backupFile = new File(backupFolder);

    if (!backupFile.isDirectory()) {
      backupFile.mkdirs();
    }

    if (mapHasBackup(mapName)) {
      removeMapBackup(mapName);
    }

    try {
      ZipUtils.zip(
        getWorldsFolder() + mapName,
        backupFolder + mapName + ".zip"
      );
    } catch (Exception e) {
      getLogger().error(e.getMessage(), e);
    }
  }

  public void resetMapBackup(String mapName) {
    unloadMap(mapName);

    try {
      ZipUtils.unzip(getBackupFolder() + mapName + ".zip", getWorldsFolder());
    } catch (Exception e) {
      getLogger().error(e.getMessage(), e);
    }
  }

  public boolean mapHasBackup(String mapName) {
    return (new File(getBackupFolder() + mapName + ".zip")).exists();
  }

  public void removeMapBackup(String mapName) {
    (new File(getBackupFolder() + mapName + ".zip")).delete();
  }

  public void unloadMap(String mapName) {
    if (!getServer().isLevelLoaded(mapName)) {
      return;
    }

    Level level = getServer().getLevelByName(mapName);

    if (level == null) {
      return;
    }

    level.setAutoSave(false);

    getServer().unloadLevel(level);
  }

  public void end() {
    end(null);
  }

  public void end(Map<Player, Integer> pedestalWinners) {
    end(pedestalWinners, 20);
  }

  public void end(Map<Player, Integer> pedestalWinners, int time) {
    if (phaseSeries != null) {
      phaseSeries.end();
      phaseSeries.setFrozen(true);
      phaseSeries.cleanup();
    }

    phaseSeries = new PhaseSeries(this);
    phaseSeries.add(
      new EndGamePhase(this, Duration.ofSeconds(time), pedestalWinners)
    );
    phaseSeries.start();
  }

  public void searchNewGameFor(Player player) {}

  public void searchNewGameFor(List<Player> players) {
    players.forEach(this::searchNewGameFor);
  }

  public void sendToHub(Player player) {}

  public boolean canMoveInPreGame() {
    return canMoveInPreGame;
  }

  public List<Path> getPathsFromResourceJAR(String folder)
    throws URISyntaxException, IOException {
    List<Path> result;

    String jarPath = getClass()
      .getProtectionDomain()
      .getCodeSource()
      .getLocation()
      .toURI()
      .getPath();

    URI uri = URI.create("jar:file:" + jarPath);
    try (
      FileSystem fs = FileSystems.newFileSystem(uri, Collections.emptyMap())
    ) {
      result =
        Files
          .walk(fs.getPath(folder))
          .filter(Files::isRegularFile)
          .collect(Collectors.toList());
    }

    return result;
  }

  public void moveResourcesToDataPath(String folderName) {
    moveResourcesToPath(folderName, getDataFolder().toString());
  }

  public void moveResourcesToPath(String folderName, String path) {
    File destinationPath = new File(path + "/" + folderName);

    if (!destinationPath.exists()) {
      destinationPath.mkdirs();
    }

    try {
      List<Path> result = getPathsFromResourceJAR(folderName + "/");

      for (Path resultPath : result) {
        String filePathName = resultPath.toString();

        File destinationFile = new File(path + filePathName);

        if (destinationFile.exists()) {
          continue;
        }

        saveResource(filePathName.substring(1));
      }
    } catch (URISyntaxException | IOException e) {
      e.printStackTrace();
    }
  }

  public void kick(Player player, String reason) {
    player.kick(unexpectedMessage + reason + "!", false);
  }

  public GameMap getMapWinner() {
    return gameMapManager.getMapWinner();
  }

  public void addTeam(Team team) {
    teams.add(team);
  }

  public boolean isTeamMember(Player from, Player to) {
    if (!isTeamable()) {
      return false;
    }

    for (Team team : teams) {
      if (team.isMember(from) && team.isMember(to)) {
        return true;
      }
    }

    return false;
  }

  public List<Team> getSortedTeams() {
    return getSortedTeams(true);
  }

  public List<Team> getSortedTeams(boolean asc) {
    //Ty @DenzelCode

    return teams
      .stream()
      .sorted(
        Comparator.comparing(
          Team::countMembers,
          asc ? Comparator.naturalOrder() : Comparator.reverseOrder()
        )
      )
      .collect(Collectors.toList());
  }

  public Team getTeamWithoutMembers() {
    for (Team team : teams) {
      if (team.countMembers() <= 0) {
        return team;
      }
    }

    return null;
  }

  public boolean thereIsATeamWithoutMembers() {
    return getTeamWithoutMembers() != null;
  }

  public Team getAliveTeam() {
    for (Team team : teams) {
      if (
        getTeamWithoutMembers() != null &&
        !team.getId().equals(getTeamWithoutMembers().getId())
      ) {
        return team;
      }
    }

    return null;
  }

  public Team getTeam(Player player) {
    for (Team team : teams) {
      if (team.isMember(player)) {
        return team;
      }
    }

    return null;
  }

  public void removeIfHasTeam(Player player) {
    if (!hasTeam(player)) {
      return;
    }

    getTeam(player).removeMember(player);
  }

  public boolean hasTeam(Player player) {
    return getTeam(player) != null;
  }

  public GameType getGameType() {
    return (
      gameType == GameType.TEAM && teams.isEmpty() ? GameType.SOLO : gameType
    );
  }

  @Override
  public void onDisable() {
    close();

    getLogger().info(TextFormat.RED + "This game has been disabled!");
  }
}
