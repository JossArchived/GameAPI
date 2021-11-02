package jossc.game.phase.lobby;

import cn.nukkit.Player;
import cn.nukkit.level.Position;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import jossc.game.Game;
import jossc.game.event.GameEndEvent;
import jossc.game.top.Top;

public class EndGamePhase extends LobbyPhase {

  private final Map<Player, Integer> winners;
  private final List<Top> topList;
  private int countdown = 11;

  public EndGamePhase(Game game, Map<Player, Integer> winners) {
    super(game, Duration.ZERO);
    this.winners = winners;
    this.topList = new ArrayList<>();
  }

  @Override
  protected void onStart() {
    getPlayers()
      .forEach(
        player -> {
          game.convertPlayer(player, true);
          player.teleport(game.getPedestalPosition());
        }
      );

    if (winners == null) {
      return;
    }

    winners.forEach(
      (winner, ranking) -> {
        Position pedestalPosition = game.getPedestalList().get(ranking);
        topList.add(ranking, new Top(ranking, winner, pedestalPosition));
      }
    );

    topList.forEach(Top::spawnEntity);
  }

  @Override
  public void onUpdate() {
    countdown--;

    if (countdown > 0) {
      broadcastActionBar("&dEnding game...");
    } else if (countdown == 0) {
      broadcastActionBar("&6Game over, thanks for playing!");
    }
  }

  @Override
  public boolean isReadyToEnd() {
    return (
      super.isReadyToEnd() ||
      neutralPlayersSize() < 1 ||
      countdown == 0 ||
      winners == null
    );
  }

  @Override
  protected void onEnd() {
    super.onEnd();

    topList.forEach(Top::despawnEntity);

    game.callEvent(new GameEndEvent(getPlayers()));

    game.shutdown();
  }
}
