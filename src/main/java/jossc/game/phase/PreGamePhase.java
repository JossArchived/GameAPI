package jossc.game.phase;

import cn.nukkit.plugin.PluginBase;

import java.time.Duration;

public class PreGamePhase extends GamePhase {

  public PreGamePhase(PluginBase plugin) {
    super(plugin);
  }

  public PreGamePhase(PluginBase plugin, Duration duration) {
    super(plugin, duration);
  }

  @Override
  protected void onStart() {

  }

  @Override
  public void onUpdate() {

  }

  @Override
  protected void onEnd() {

  }
}
