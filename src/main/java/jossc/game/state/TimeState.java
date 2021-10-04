package jossc.game.state;

import cn.nukkit.plugin.PluginBase;
import java.time.Duration;
import org.jetbrains.annotations.NotNull;

public abstract class TimeState extends GameState {

  protected int secondsToEnd;

  public TimeState(PluginBase plugin, int secondsToEnd) {
    super(plugin);
    this.secondsToEnd = secondsToEnd;
  }

  @NotNull
  @Override
  public Duration getDuration() {
    return Duration.ofSeconds(secondsToEnd);
  }

  protected int playersSize() {
    return getPlayers().size();
  }
}
