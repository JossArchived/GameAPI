package jossc.game.phase.lobby;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.math.Vector3;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import jossc.game.Game;
import jossc.game.utils.math.MathUtils;

public class PreGamePhase extends LobbyPhase {

  private final int initialCountdown;
  private int countdown;

  public PreGamePhase(Game game, int countdown) {
    super(game, Duration.ZERO);
    this.initialCountdown = countdown;
    this.countdown = countdown;
  }

  @Override
  protected void onStart() {
    game.setStarting(true);

    spawnPlayers();
  }

  private void spawnPlayers() {
    List<Vector3> spawns = game.getSpawns();
    Set<Integer> spawnsUsed = new HashSet<>();

    getNeutralPlayers()
      .forEach(player -> spawnPlayer(player, spawns, spawnsUsed));
  }

  private void spawnPlayer(
    Player player,
    List<Vector3> spawns,
    Set<Integer> spawnsUsed
  ) {
    player.setGamemode(game.getDefaultGameMode());

    int i;

    do {
      i = MathUtils.nextInt(spawns.size());
    } while (spawnsUsed.contains(i));

    player.teleport(spawns.get(i));
    spawnsUsed.add(i);
  }

  @Override
  public void onUpdate() {
    countdown--;

    if (countdown > 0) {
      if (countdown <= 3) {
        broadcastSound("note.harp", 2, 2);
      }

      broadcastActionBar(
        "&eStart game: &l»&r " +
        game.getTimeInCharacter(countdown, initialCountdown) +
        "&f " +
        countdown
      );
    } else if (countdown == 0) {
      String instruction = game.getInstruction();

      if (!instruction.isEmpty()) {
        broadcastMessage("&7&l» &r&f" + instruction);
      }

      List<String> tips = game.getTips();

      if (tips == null || tips.isEmpty()) {
        return;
      }

      Random rand = new Random();

      String tip = tips.get(rand.nextInt(tips.size()));

      if (tip == null) {
        return;
      }

      schedule(
        () -> {
          broadcastMessage("&b&l» &r&bTip: &7" + tip);
          broadcastSound("random.toast");
        },
        20 * 5
      );
    }
  }

  @Override
  public boolean isReadyToEnd() {
    return super.isReadyToEnd() && countdown == 0;
  }

  @EventHandler
  public void onMove(PlayerMoveEvent event) {
    event.setTo(event.getFrom());
  }
}
