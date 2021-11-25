package net.josscoder.gameapi.user;

import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum DeviceOS {
  ANDROID(1),
  IOS(2),
  OSX(3),
  FIREOS(4),
  GEARVR(5),
  HOLOLENS(6),
  WIN10(7),
  WIN32(8),
  DEDICATED(9),
  TVOS(10),
  ORBIS(11),
  NX(12),
  UNKNOWN(-1);

  private final int value;

  public static DeviceOS getDeviceOS(int value) {
    return Arrays
      .stream(DeviceOS.values())
      .filter(deviceOS -> deviceOS.getValue() == value)
      .findFirst()
      .orElse(DeviceOS.UNKNOWN);
  }
}
