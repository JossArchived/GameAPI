/*
 * Copyright 2021 Josscoder
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package net.josscoder.gameapi.phase;

import cn.nukkit.scheduler.TaskHandler;
import java.util.LinkedList;
import java.util.List;
import net.josscoder.gameapi.Game;
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
  protected void onStart() {
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
  protected void onEnd() {
    super.onEnd();

    scheduledTask.cancel();
  }

  public final void addOnUpdate(Runnable runnable) {
    onUpdate.add(runnable);
  }
}
