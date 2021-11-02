package jossc.game.top;

import cn.nukkit.Player;
import cn.nukkit.level.Position;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Top {

  private final int ranking;
  private final Player winner;
  private final Position pedestalPosition;

  public void spawnEntity() {
    //TODO: add citizen lib
  }

  public void despawnEntity() {
    //TODO: add citizen lib
  }
}
