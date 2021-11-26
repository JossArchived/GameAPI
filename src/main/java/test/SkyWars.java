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
