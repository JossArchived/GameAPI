package jossc.game.state;

import cn.nukkit.plugin.PluginBase;
import java.time.Duration;
import org.jetbrains.annotations.NotNull;

public abstract class TimeState extends GameState {

  protected Duration duration;

  public TimeState(PluginBase plugin, Duration duration) {
    super(plugin);
    this.duration = duration;
  }

  @NotNull
  @Override
  public Duration getDuration() {
    return duration;
  }
}
