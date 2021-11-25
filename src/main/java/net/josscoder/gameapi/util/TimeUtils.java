package net.josscoder.gameapi.util;

import java.time.Instant;

public class TimeUtils {

  public static long getTimestamp() {
    return Instant.now().getEpochSecond();
  }

  public static long getTimestampMilli() {
    return Instant.now().toEpochMilli();
  }

  public static String timeToString(int time) {
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
