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

package net.josscoder.gameapi.phase;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerChatEvent;
import cn.nukkit.scheduler.TaskHandler;
import cn.nukkit.utils.TextFormat;
import java.time.Duration;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.josscoder.gameapi.Game;
import net.josscoder.gameapi.api.event.user.UserJoinServerEvent;
import net.josscoder.gameapi.api.event.user.UserQuitServerEvent;
import net.josscoder.gameapi.map.WaitingRoomMap;
import net.josscoder.gameapi.phase.base.EndGamePhase;
import net.josscoder.gameapi.user.User;
import net.josscoder.gameapi.user.factory.UserFactory;
import net.josscoder.gameapi.util.Utils;
import net.minikloon.fsmgasm.State;
import org.jetbrains.annotations.NotNull;

public abstract class GamePhase extends State implements Listener {

  protected final Game game;

  protected final Duration duration;

  protected final UserFactory userFactory;

  protected final Set<Listener> listeners;

  protected final Set<TaskHandler> tasks;

  protected final Set<Command> commands;

  public GamePhase(Game game) {
    this(game, Duration.ZERO);
  }

  public GamePhase(Game game, Duration duration) {
    userFactory = game.getUserFactory();

    listeners = new HashSet<>();
    tasks = new HashSet<>();
    commands = new HashSet<>();

    this.game = game;
    this.duration = duration;
  }

  @NotNull
  @Override
  public Duration getDuration() {
    return duration;
  }

  @Override
  public void start() {
    super.start();

    register(this);
  }

  protected Server getServer() {
    return game.getServer();
  }

  protected void register(Listener... listeners) {
    Arrays.stream(listeners).forEach(this::register);
  }

  protected void register(Listener listener) {
    game.registerListener(listener);

    listeners.add(listener);
  }

  protected void register(Command command) {
    game.registerCommand(command);

    commands.add(command);
  }

  protected void register(Command... commands) {
    Arrays.stream(commands).forEach(this::register);
  }

  protected void schedule(Runnable runnable, int delay) {
    TaskHandler task = getServer()
      .getScheduler()
      .scheduleDelayedTask(game, runnable, delay);

    tasks.add(task);
  }

  protected void schedule(Runnable runnable, int delay, int interval) {
    TaskHandler task = getServer()
      .getScheduler()
      .scheduleDelayedRepeatingTask(game, runnable, delay, interval);

    tasks.add(task);
  }

  protected Collection<Player> getOnlinePlayers() {
    return getServer().getOnlinePlayers().values();
  }

  protected List<Player> getPlayers(Predicate<? super Player> condition) {
    return getOnlinePlayers()
      .stream()
      .filter(condition)
      .collect(Collectors.toList());
  }

  protected List<Player> getSpectators() {
    return getPlayers(Player::isSpectator);
  }

  protected List<Player> getNeutralPlayers() {
    return getPlayers(player -> !player.isSpectator());
  }

  protected int countSpectators() {
    return getSpectators().size();
  }

  protected int countNeutralPlayers() {
    return getNeutralPlayers().size();
  }

  protected void playSound(Player player, String soundName) {
    playSound(player, soundName, 1f);
  }

  protected void playSound(Player player, String soundName, float pitch) {
    playSound(player, soundName, pitch, 1f);
  }

  protected void playSound(
    Player player,
    String soundName,
    float pitch,
    float volume
  ) {
    Utils.playSound(player, soundName, pitch, volume);
  }

  protected void broadcastSound(
    String soundName,
    float pitch,
    float volume,
    Predicate<? super Player> condition
  ) {
    getPlayers(condition)
      .forEach(player -> playSound(player, soundName, pitch, volume));
  }

  protected void broadcastSound(
    String soundName,
    float pitch,
    Predicate<? super Player> condition
  ) {
    broadcastSound(soundName, pitch, 1f, condition);
  }

  protected void broadcastSound(
    String soundName,
    Predicate<? super Player> condition
  ) {
    broadcastSound(soundName, 1f, condition);
  }

  protected void broadcastSound(String soundName, float pitch, float volume) {
    broadcastSound(
      soundName,
      pitch,
      volume,
      player -> getOnlinePlayers().contains(player)
    );
  }

  protected void broadcastSound(String soundName, float pitch) {
    broadcastSound(soundName, pitch, 1f);
  }

  protected void broadcastSound(String soundName) {
    broadcastSound(soundName, 1f);
  }

  protected void broadcastMessage(
    String message,
    Predicate<? super Player> condition
  ) {
    getPlayers(condition)
      .forEach(player -> player.sendMessage(TextFormat.colorize(message)));
  }

  protected void broadcastMessage(String message) {
    broadcastMessage(message, player -> getOnlinePlayers().contains(player));
  }

  protected void broadcastActionBar(
    String title,
    int fadeIn,
    int duration,
    int fadeOut,
    Predicate<? super Player> condition
  ) {
    getPlayers(condition)
      .forEach(
        player ->
          player.sendActionBar(
            TextFormat.colorize(title),
            fadeIn,
            duration,
            fadeOut
          )
      );
  }

  protected void broadcastActionBar(
    String title,
    int fadeIn,
    int duration,
    int fadeOut
  ) {
    broadcastActionBar(
      title,
      fadeIn,
      duration,
      fadeOut,
      player -> getOnlinePlayers().contains(player)
    );
  }

  protected void broadcastActionBar(String title) {
    broadcastActionBar(title, 1, 0, 1);
  }

  protected void broadcastActionBar(
    String title,
    Predicate<? super Player> condition
  ) {
    broadcastActionBar(title, 1, 0, 1, condition);
  }

  protected void broadcastTitle(
    String title,
    String subTitle,
    int fadeIn,
    int stay,
    int fadeOut,
    Predicate<? super Player> condition
  ) {
    getPlayers(condition)
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

  protected void broadcastTitle(
    String title,
    String subTitle,
    int fadeIn,
    int stay,
    Predicate<? super Player> condition
  ) {
    broadcastTitle(title, subTitle, fadeIn, stay, 5, condition);
  }

  protected void broadcastTitle(
    String title,
    String subTitle,
    int fadeIn,
    Predicate<? super Player> condition
  ) {
    broadcastTitle(title, subTitle, fadeIn, 20, condition);
  }

  protected void broadcastTitle(
    String title,
    String subTitle,
    Predicate<? super Player> condition
  ) {
    broadcastTitle(title, subTitle, 20, condition);
  }

  protected void broadcastTitle(
    String title,
    Predicate<? super Player> condition
  ) {
    broadcastTitle(title, "", condition);
  }

  protected void broadcastTitle(
    String title,
    String subTitle,
    int fadeIn,
    int stay,
    int fadeOut
  ) {
    broadcastTitle(
      title,
      subTitle,
      fadeIn,
      stay,
      fadeOut,
      player -> getOnlinePlayers().contains(player)
    );
  }

  protected void broadcastTitle(String title, String subTitle) {
    broadcastTitle(title, subTitle, 20, 20, 5);
  }

  protected void broadcastTitle(String title) {
    broadcastTitle(title, "");
  }

  protected void broadcastTip(
    String message,
    Predicate<? super Player> condition
  ) {
    getPlayers(condition)
      .forEach(player -> player.sendTip(TextFormat.colorize(message)));
  }

  protected void broadcastTip(String message) {
    broadcastTip(message, player -> getOnlinePlayers().contains(player));
  }

  protected void broadcastPopup(
    String message,
    String subTitle,
    Predicate<? super Player> condition
  ) {
    getPlayers(condition)
      .forEach(
        player ->
          player.sendPopup(
            TextFormat.colorize(message),
            TextFormat.colorize(subTitle)
          )
      );
  }

  protected void broadcastPopup(
    String message,
    Predicate<? super Player> condition
  ) {
    broadcastPopup(message, "", condition);
  }

  protected void broadcastPopup(String message, String subTitle) {
    broadcastPopup(
      message,
      subTitle,
      player -> getOnlinePlayers().contains(player)
    );
  }

  protected void broadcastPopup(String message) {
    broadcastPopup(message, "");
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onJoin(UserJoinServerEvent event) {
    Player player = event.getPlayer();
    User user = event.getUser();

    WaitingRoomMap waitingRoomMap = game.getWaitingRoomMap();

    if (!game.isAvailable()) {
      user.convertSpectator();

      if (this instanceof EndGamePhase) {
        waitingRoomMap.teleportToPedestalCenter(player);
      } else {
        game.getGameMapManager().getMapWinner().teleportToSafeSpawn(player);
      }

      user
        .getInventory()
        .setItem(0, game.getWaitingLobbyItems().get(8).build());
      user.updateInventory();

      return;
    }

    user.convertPlayer();
    waitingRoomMap.teleportToSafeSpawn(player);

    game
      .getWaitingLobbyItems()
      .forEach(
        (slot, customItem) ->
          user.getInventory().setItem(slot, customItem.build())
      );
    user.updateInventory();

    int neutralPlayers = countNeutralPlayers();
    int maxPlayers = game.getMaxPlayers();

    broadcastMessage(
      "&a&l» &r&7" +
      player.getName() +
      " has joined. &8[" +
      neutralPlayers +
      "/" +
      maxPlayers +
      "]"
    );
  }

  @EventHandler
  public void onQuit(UserQuitServerEvent event) {
    Player player = event.getPlayer();

    if (player.isSpectator()) {
      return;
    }

    int neutralPlayers = (countNeutralPlayers() - 1);
    int maxPlayers = game.getMaxPlayers();

    broadcastMessage(
      "&c&l» &r&7" +
      player.getName() +
      " has left. &8[" +
      neutralPlayers +
      "/" +
      maxPlayers +
      "]"
    );
  }

  @EventHandler
  public void onChat(PlayerChatEvent event) {
    Player player = event.getPlayer();

    String message =
      TextFormat.GRAY +
      player.getName() +
      TextFormat.colorize("&l »&r&f ") +
      event.getMessage();

    getPlayers(gamePlayer -> gamePlayer.getGamemode() == player.getGamemode())
      .forEach(onlinePlayer -> onlinePlayer.sendMessage(message));

    event.setCancelled();
  }

  @Override
  public void end() {
    super.end();

    if (!super.getEnded()) {
      return;
    }

    cleanup();
  }

  protected void cleanup() {
    listeners.forEach(HandlerList::unregisterAll);
    listeners.clear();

    tasks.forEach(TaskHandler::cancel);
    tasks.clear();
  }
}
