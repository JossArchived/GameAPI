/*
 * Copyright 2021-2055 Josscoder
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

package net.josscoder.gameapi.util;

import cn.nukkit.utils.TextFormat;

public class CharUtils {

  public static String timeToChar(int time, int size) {
    String character = "▌";

    StringBuilder stringBuilder = new StringBuilder();

    for (int i = 1; i <= size; i++) {
      stringBuilder
        .append(
          i <= time
            ? (
              time <= 3
                ? TextFormat.RED
                : time <= 10
                  ? TextFormat.YELLOW
                  : time <= 15 ? TextFormat.GOLD : TextFormat.GREEN
            )
            : TextFormat.GRAY
        )
        .append(character);
    }

    return stringBuilder.toString();
  }
}
