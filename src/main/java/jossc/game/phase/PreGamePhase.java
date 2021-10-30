package jossc.game.phase;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.math.Vector3;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import jossc.game.Game;
import jossc.game.utils.math.MathUtils;

public class PreGamePhase extends GamePhase {

  private final int initialCountdown;

  private int countdown;

  public PreGamePhase(PluginBase plugin, int countdown) {
    super(plugin, Duration.ZERO);
    this.initialCountdown = countdown;
    this.countdown = countdown;
  }

  @Override
  protected void onStart() {
    broadcastMessage(
      TextFormat.colorize(
        "&d&l» &r&fThe game will begin in &d" + initialCountdown + "&f seconds!"
      )
    );

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
    if (countdown > 0) {
      countdown--;

      if (countdown < 6) {
        broadcastActionBar(
          TextFormat.GREEN +
          "The game starts in " +
          TextFormat.BOLD +
          (countdown == 1 ? TextFormat.RED : TextFormat.GOLD) +
          countdown
        );
        broadcastSound("note.bassattack", 1, 2);
      }

      broadcastActionBar(
        TextFormat.GREEN + "The game starts in " + TextFormat.BOLD + countdown
      );

      broadcastSound("note.snare", 1, 2);

      return;
    }

    if (countdown == 0) {
      broadcastSound("liquid.lavapop", 2, 2);

      String instruction = game.getInstruction();

      if (!instruction.isEmpty()) {
        broadcastMessage(TextFormat.colorize("&7&l» &r" + instruction));
      }

      broadcastMessage(
        TextFormat.BOLD.toString() + TextFormat.AQUA + "The game has begun!"
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
