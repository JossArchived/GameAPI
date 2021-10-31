package jossc.game.phase.lobby;

import cn.nukkit.plugin.PluginBase;
import java.time.Duration;

public class LobbyCountdownPhase extends LobbyPhase {

  public LobbyCountdownPhase(PluginBase plugin) {
    super(plugin);
  }

  public LobbyCountdownPhase(PluginBase plugin, Duration duration) {
    super(plugin, duration);
  }

  @Override
  public void onUpdate() {}
}
