package jossc.game.phase;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.scheduler.TaskHandler;
import java.util.LinkedList;
import java.util.List;
import net.minikloon.fsmgasm.StateSeries;

public class PhaseSeries extends StateSeries {

  private final PluginBase plugin;
  private final int interval;
  protected TaskHandler scheduledTask;

  protected List<Runnable> onUpdate = new LinkedList<>();

  public PhaseSeries(PluginBase plugin) {
    this(plugin, 20);
  }

  public PhaseSeries(PluginBase plugin, int interval) {
    this.plugin = plugin;
    this.interval = interval;
  }

  @Override
  public final void onStart() {
    super.onStart();

    scheduledTask =
      plugin
        .getServer()
        .getScheduler()
        .scheduleRepeatingTask(
          plugin,
          () -> {
            update();
            onUpdate.forEach(Runnable::run);
          },
          interval
        );
  }

  @Override
  public final void onEnd() {
    super.onEnd();

    scheduledTask.cancel();
  }

  public final void addOnUpdate(Runnable runnable) {
    onUpdate.add(runnable);
  }
}