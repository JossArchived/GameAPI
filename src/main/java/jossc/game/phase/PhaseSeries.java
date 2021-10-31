package jossc.game.phase;

import cn.nukkit.scheduler.TaskHandler;
import java.util.LinkedList;
import java.util.List;
import jossc.game.Game;
import net.minikloon.fsmgasm.StateSeries;

public class PhaseSeries extends StateSeries {

  private final Game game;
  private final int interval;
  protected TaskHandler scheduledTask;

  protected List<Runnable> onUpdate = new LinkedList<>();

  public PhaseSeries(Game game) {
    this(game, 20);
  }

  public PhaseSeries(Game game, int interval) {
    this.game = game;
    this.interval = interval;
  }

  @Override
  public final void onStart() {
    super.onStart();

    scheduledTask =
      game
        .getServer()
        .getScheduler()
        .scheduleRepeatingTask(
          game,
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
