package jossc.game.utils;

public class TimeUtils {

  public static String format(int time) {
    int minutes = Math.floorDiv(time, 60);
    int seconds = (int) Math.floor(time % 60);
    return (
      (minutes < 10 ? "0" : "") +
      minutes +
      ":" +
      (seconds < 10 ? "0" : "") +
      seconds
    );
  }
}
