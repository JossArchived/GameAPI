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
    broadcastActionBar("&6Waiting for players");
  }

  @Override
  public boolean isReadyToEnd() {
    return super.isReadyToEnd() && neutralPlayersSize() >= neededPlayers;
  }

  @Override
  protected void onEnd() {
    broadcastActionBar("§•&aPreparing countdown...");
    broadcastSound("note.pling", 2, 1);
  }
}
