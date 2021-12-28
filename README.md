# GameAPI [![Build](https://github.com/Josscoder/GameAPI/actions/workflows/build.yml/badge.svg?branch=thehivemc)](https://github.com/Josscoder/GameAPI/actions/workflows/build.yml) [![](https://jitpack.io/v/Josscoder/GameAPI.svg)](https://jitpack.io/#Josscoder/GameAPI)

## 📙 Description

A simple API to create games based on states

My idea to create this API was because of inspiration from [this spigot thread.](https://www.spigotmc.org/threads/organizing-your-minigame-code-using-fsmgasm.235786/)
All credits for the idea goes to Minikloon.

The game design this API is based on TheHive Bedrock game design.

## 🌏 Add as maven dependency
Repository:

```xml
<repositories>
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>

<dependency>
 <groupId>com.github.Josscoder</groupId>
   <artifactId>GameAPI</artifactId>
 <version>thehivemc-SNAPSHOT</version>
</dependency>
```

## 🤔 What is this?

It is an API to create mini-games in simple steps, to avoid repeating the process every time you make a game and avoid clutter when creating a very large game. This API is designed for large Minecraft Bedrock networks, by this I mean that one map will be played per server, but you can vote for maps or always have a map, to avoid lag.
In short, it is an API for semi-long or long servers.

## 📜 How to use this?

In the next part I will show you a couple of examples, of how to use each method and class.
 
- Create a game class and and configure game attributes

```java
package test;

import cn.nukkit.Player;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.TextFormat;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import net.josscoder.gameapi.Game;
import net.josscoder.gameapi.map.WaitingRoomMap;
import net.josscoder.gameapi.phase.GamePhase;
import test.phase.FightPhase;

public class SkyWars extends Game {

  @Override
  public String getId() {
    return UUID.randomUUID().toString();
  }

  @Override
  public String getGameName() {
    // This is the name that the game will receive and will be shown to the player when starting the game.
    return TextFormat.AQUA + "SkyWars: " + TextFormat.LIGHT_PURPLE + "Solo";
  }

  @Override
  public String getInstruction() {
    // These are the instructions that will be shown to the player when starting the game (THIS IS OPTIONAL)
    return "You have to be the last person standing! For this you have to equip yourself with the best of the loot found in the chests!";
  }

  @Override
  public void init() {
    initGameSettings();

    List<GamePhase> lobbyPhases = createPreGamePhase();

    phaseSeries.addAll(lobbyPhases);
    phaseSeries.add(new FightPhase(this, Duration.ofMinutes(30)));
    phaseSeries.start();

    registerPhaseCommands();
  }

  private void initGameSettings() {
    /*
    If the development mode is enabled,
    the following will be enabled: SoundCommand(so you can play sounds and listen to them with the sound or pitch you want),
    MyPositionCommand(to get the position you are in, this counts yaw and pitch) and
    SetWaitingRoomCommand(to configure the game waiting room)
     */

    setDevelopmentMode(true);

    /*
    This new call from WaitingRoomMap helps us to register (as the name implies) the waiting room. The waiting room as such needs:
    - Exit Entity Spawn: this is the coordinate to know where to generate the exit entity.
    - Pedestal Center Spawn: this is the coordinate to know where to spawn the players at the end of the game, this has use in EndGamePhase.
    - Pedestal One Spawn: this is the coordinate to know where the first entity or the winning entity or # 1 of the game is generated.
    - Two Spawn Pedestal: this serves the same as the above, but this time it will be for the winner # 2 or player in the second position.
    - Three Spawn Pedestal: like the last data, this will serve to generate the entity to show the winner # 3.

    The above were necessary data ...

    Now the optional data:

    - Corner One & Corner Two: this serves to generate an invisible barrier in which the player will not be able to pass.
    - MaxY & minY: this is used to set the maximum number of blocks that a player can build and the minimum number that a player can be in.
     */
    WaitingRoomMap waitingRoom = new WaitingRoomMap(
        this,
        "WaitingMap",
        new Vector3(0, 100, 0)
    );
    waitingRoom.setExitEntitySpawn(new Vector3(0, 100, 0));
    waitingRoom.setPedestalCenterSpawn(new Vector3(0, 100, 0));
    waitingRoom.setPedestalOneSpawn(new Vector3(0, 100, 0));
    waitingRoom.setPedestalTwoSpawn(new Vector3(0, 100, 10));
    waitingRoom.setPedestalThreeSpawn(new Vector3(0, 100, 0));
    waitingRoom.setCornerOne(new Vector3(0, 100, 0));
    waitingRoom.setCornerTwo(new Vector3(0, 100, 0));
    waitingRoom.setMaxY(256);
    waitingRoom.setMinY(2);

    // This is the help method to give the game the aforementioned data.

    setWaitingRoomMap(waitingRoom);

    // If this method is passed a false variable, it will not show messages and/or items about map voting.

    setCanVoteMap(true);

    // This will be the game mode that will be awarded to the player at the start of the game.

    setDefaultGamemode(Player.SURVIVAL);

    // This is the minimum number of players required to start the game.

    setMinPlayers(3);

    // This is the maximum number of players this game can handle.

    setMaxPlayers(12);

    // if true is passed as a parameter, the player will be able to move or move when the game is starting, that is, in PreGamePhase

    setCanMoveInPreGame(false);
  }

  @Override
  public void close() {
    getLogger().info(TextFormat.BLUE + "Closing Game...");
  }
}

```
- Create the phases of the game!

```java
package test.phase;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.player.PlayerDeathEvent;
import cn.nukkit.item.Item;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import net.josscoder.gameapi.Game;
import net.josscoder.gameapi.phase.GamePhase;
import net.josscoder.gameapi.user.User;
import net.josscoder.gameapi.util.TimeUtils;

public class FightPhase extends GamePhase {

  public FightPhase(Game game, Duration duration) {
    super(game, duration);
  }

  @Override
  protected void onStart() {
    broadcastMessage("&l&6»&r &fThe game has started, good luck!");
  }

  @Override
  public void onUpdate() {
    broadcastActionBar(
        "&b&lGame ends in &r&b" +
            TimeUtils.timeToString((int) getRemainingDuration().getSeconds())
    );
  }

  @Override
  public boolean isReadyToEnd() {
    return super.isReadyToEnd() || countNeutralPlayers() <= 1;
  }

  @Override
  protected void onEnd() {
    Map<Player, Integer> pedestalWinners = new HashMap<>();

    if (countNeutralPlayers() == 1) {
      Player winner = getNeutralPlayers().get(0);

      if (winner != null) {
        pedestalWinners.put(winner, 1);
        broadcastMessage("&l&b»&r &7" + winner.getName() + " is the winner!");
      }
    } else {
      broadcastMessage("&l&c»&r &cThere are no winners!");
    }

    game.end(pedestalWinners);
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onDeath(PlayerDeathEvent event) {
    event.setDeathMessage("");
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onEntityDamage(EntityDamageEvent event) {
    if (event.isCancelled()) {
      return;
    }

    Entity entity = event.getEntity();

    if (!(entity instanceof Player)) {
      return;
    }

    Player player = (Player) entity;

    User user = userFactory.get(player);

    if (user == null) {
      return;
    }

    if (event.getFinalDamage() < entity.getHealth()) {
      return;
    }

    event.setCancelled();

    for (Item drop : player.getDrops()) {
      entity.getLevel().dropItem(entity, drop);
    }

    user.convertSpectator(true, true);

    String message = "";

    switch (event.getCause()) {
      case ENTITY_ATTACK:
        if (event instanceof EntityDamageByEntityEvent) {
          Entity damager = ((EntityDamageByEntityEvent) event).getDamager();

          if (damager instanceof Player) {
            message = "was killed by " + damager.getName();
          }
        }
        break;
      case PROJECTILE:
        message = "was shot by projectile";
        break;
      case FALL:
        message = "has hit on the ground";
        break;
      case VOID:
        message = "fell into the void";
        break;
      case ENTITY_EXPLOSION:
      case BLOCK_EXPLOSION:
        message = "has exploded";
        break;
      case LAVA:
        message = "did not survive in the lava";
        break;
      case FIRE:
      case FIRE_TICK:
        message = "did not survive the fire";
        break;
      case MAGIC:
        message = "died by a witchcraft";
        break;
      case DROWNING:
        message = "has drowned";
        break;
      case SUFFOCATION:
        message = "died suffocated somewhere";
        break;
      case SUICIDE:
        message = "couldn't take it anymore and he committed suicide";
        break;
      case HUNGER:
        message = "had no food";
        break;
      case LIGHTNING:
        message = "was struck by lightning";
        break;
      default:
        message = "has died";
        break;
    }

    broadcastMessage("&l&c»&r &8" + player.getName() + " &7" + message + "&f!");
    broadcastSound("mob.guardian.death");
  }
}

```

## Could you show me an example?

Of course 😉 

This is the most recent version:

[![Watch the video](https://i.imgur.com/k2nVeg8.png)](https://youtu.be/Rr-WE7pSW_k)

This is an old version:

[![Watch the video](https://i.imgur.com/6UbLrVs.png)](https://youtu.be/WKgsZYjWeOU)

#### You can do thousands of other things... What are you waiting for? Discover them! 🐱
