package jossc.game.phase.lobby;

import jossc.game.Game;

public class LobbyWaitingPhase extends LobbyPhase {

  private final int neededPlayers;

  public LobbyWaitingPhase(Game game) {
    super(game);
    this.neededPlayers = game.getMinPlayers();
  }

  @Override
  public void onUpdate() {
    int restMorePlayers = (neutralPlayersSize() - neededPlayers);

    broadcastActionBar("&f" + restMorePlayers + "&e more players to start...");
  }

  @Override
  public boolean isReadyToEnd() {
    return super.isReadyToEnd() && neutralPlayersSize() >= neededPlayers;
  }

  @Override
  protected void onEnd() {
    super.onEnd();

    broadcastActionBar("§•&aPreparing countdown...");
  }
}
