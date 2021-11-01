package jossc.game.utils.math;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class MathUtils {

  public static int nextInt(final int min, final int max) {
    return (new Random()).nextInt((max - min) + 1) + min;
  }

  public static int nextInt(int bound) {
    return ThreadLocalRandom.current().nextInt(bound);
  }
}
