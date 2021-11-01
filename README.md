# GameAPI [![Build](https://github.com/Josscoder/GameAPI/actions/workflows/build.yml/badge.svg)](https://github.com/Josscoder/GameAPI/actions/workflows/build.yml) [![](https://jitpack.io/v/Josscoder/GameAPI.svg)](https://jitpack.io/#Josscoder/GameAPI)
A simple API to create games based on game states

My idea to create this API was because of inspiration from [this spigot thread.](https://www.spigotmc.org/threads/organizing-your-minigame-code-using-fsmgasm.235786/)
All credits for the idea goes to Minikloon.

The game design this API is based on TheHive Bedrock game design

## Add as maven dependency
Repository:

```xml
	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
```
Dependency:
```xml
	<dependency>
	    <groupId>com.github.Josscoder</groupId>
	    <artifactId>GameAPI</artifactId>
	    <version>1.1-SNAPSHOT</version>
	</dependency>
```

## What is this?

This is an API to create mini-games in easy steps, to avoid repeating the process every time you make a game and avoid clutter when creating a very large game. This API is designed for large Minecraft Bedrock networks, by this I mean that each map will be a server, to avoid lag.
In short, it is an API for semi-long servers.

## How to use this?

In the next part I will show you a couple of examples, of how to use each method and class.
 
- Create a game class and and configure game attributes

```java
package jossc.game.test;

import cn.nukkit.Player;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import jossc.game.Game;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;

public class SkyWars extends Game {

  @Override
  public void init() {
    setDefaultGameMode(Player.SURVIVAL); //When preparing the game, this is the game mode that will give the player

    setMapBackupFile(new File(getDataFolder() + "/Trees.zip")); //This is the path of the backup .zip file of your map, this is optional, but if you want to resetBackup later, you won't be able to do it because you don't have a defined path.

    setMapName("Trees"); //With this method you can define the name of the map

    setMaxPlayers(24); //With this method you can define the maximum number of players on this map

    setMinPlayers(4); //With this method you will define the minimum number of players to start the game

    setSpawns(new LinkedList<Vector3>(){{ //These are the spawns that the player will teleport to when the game is preparing
      add(new Vector3(13, 56, 132));
      add(new Vector3(0, 5, 0));
      //and more...
    }});

    setTips(new ArrayList<String>(){{ //When you start the game, it will give you a random tip of these that you have here
      add("tip 1");
      add("tip 2");
      add("tip 3");
      add("tip 4");
      //you can add more
    }});

    prepareMap("world"); //With this method you can prepare a world, so that it is playable, it will load the level, remove the storm, remove the rain, and change the time of the map to day

    setWaitingLobby(getServer().getDefaultLevel().getSafeSpawn()); //This will be the level or position to which the player will be sent to wait for others

    resetMapBackup(); //If you defined a backup path, you can reset that backup. I recommend using an async task or something like that when copying the file to reduce lag

    prepareMap(mapName, Level.TIME_SUNRISE); //You can spend the time you want me to put when preparing it

    setDevelopmentMode(false); // By default it is false, this is just an example
  }

  @Override
  public String getGameName() {
    return "Sky Wars"; //That is the name of your game, it will be used in scoreboards, bossbar, messages, etc.
  }

  @Override
  public String getInstruction() {
    return "You have to be the last person standing, for this you have to kill your opponents, you can equip yourself with the best loot you find in the chests and FIGHT!";
    // This is an instruction on how to play this game, this is for new players to orient themselves a bit, this is optional.
  }

  @Override
  public void close() {
    resetMapBackup();
  }
}

```

#### More documentation will be added soon...