package jossc.game.phase.lobby;

import java.time.Duration;
import jossc.game.Game;

public class LobbyCountdownPhase extends LobbyPhase {

  public LobbyCountdownPhase(Game game, Duration duration) {
    super(game, duration);
  }

  @Override
  public void onUpdate() {
    if (neutralPlayersSize() < game.getMinPlayers()) {
      setFrozen(true);

      if (neutralPlayersSize() < 1) {
        end();
      }

      return;
    }

    setFrozen(false);

    int remainingDuration = (int) getRemainingDuration().getSeconds();

    if (remainingDuration > 0) {
      if (remainingDuration <= 3) {
        broadcastSound("note.hat", 1, 2);
      }

      broadcastActionBar(
        "&aThe game starts in &l" +
        (remainingDuration <= 3 ? "&c" : "&a") +
        remainingDuration
      );
    }
  }

  @Override
  public boolean isReadyToEnd() {
    return super.isReadyToEnd() && neutralPlayersSize() >= game.getMinPlayers();
  }

  @Override
  protected void onEnd() {
    super.onEnd();
    broadcastActionBar("§•&aPreparing map...");
  }
}
