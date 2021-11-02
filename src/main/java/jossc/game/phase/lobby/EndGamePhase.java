package jossc.game.phase.lobby;

import cn.nukkit.Player;
import cn.nukkit.level.Position;
import cn.nukkit.utils.TextFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import jossc.game.Game;
import jossc.game.event.GameEndEvent;
import jossc.game.top.Top;
import org.citizen.attributes.EmoteId;

public class EndGamePhase extends LobbyPhase {

  private final Map<Player, Integer> winners;
  private final List<Top> topList;

  public EndGamePhase(
    Game game,
    Duration duration,
    Map<Player, Integer> winners
  ) {
    super(game, duration);
    this.winners = winners;
    this.topList = new ArrayList<>();
  }

  @Override
  protected void onStart() {
    getPlayers()
      .forEach(
        player -> {
          game.convertPlayer(player, true);
          player.sendTitle(TextFormat.colorize("&l&6»&r&c Game Over &l&6«"));
          player.sendMessage(TextFormat.colorize("&l&c» Game Over!"));
          playSound(player, "mob.ghast.fireball");

          schedule(
            () -> {
              Position pedestalPosition = game.getPedestalPosition();

              if (pedestalPosition != null) {
                player.teleport(pedestalPosition);
              }
            },
            20 * 3
          );
        }
      );

    if (winners == null) {
      return;
    }

    winners.forEach(
      (winner, ranking) -> {
        Position pedestalPosition = game.getPedestalList().get(ranking);

        List<String> emoteList = new ArrayList<>();
        emoteList.add(EmoteId.GHAST_DANCE.getId());
        emoteList.add(EmoteId.REBOOTING.getId());
        emoteList.add(EmoteId.VICTORY_CHEER.getId());

        Top top = new Top(ranking, winner, pedestalPosition, emoteList);

        topList.add(ranking, top);

        top.spawnEntity();
      }
    );
  }

  @Override
  public void onUpdate() {
    broadcastActionBar(
      "&eSearching for a new game in &f" +
      getRemainingDuration().getSeconds() +
      "&e..."
    );
  }

  @Override
  public boolean isReadyToEnd() {
    return super.isReadyToEnd() || neutralPlayersSize() < 1 && winners == null;
  }

  @Override
  protected void onEnd() {
    super.onEnd();

    broadcastActionBar("&dSending you to a game.");

    topList.forEach(Top::despawnEntity);
    game.callEvent(new GameEndEvent(getPlayers()));

    schedule(game::shutdown, 20 * 10);
  }
}
