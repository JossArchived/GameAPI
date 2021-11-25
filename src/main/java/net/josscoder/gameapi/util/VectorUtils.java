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

package net.josscoder.gameapi.util;

import cn.nukkit.math.Vector3;
import java.util.ArrayList;
import java.util.List;

public class VectorUtils {

  public static String vectorToString(Vector3 vector) {
    return vector.x + ":" + vector.y + ":" + vector.z;
  }

  public static Vector3 stringToVector(String string) {
    if (string == null) return null;

    String[] pos = string.split(":");

    return new Vector3(
      Double.parseDouble(pos[0]),
      Double.parseDouble(pos[1]),
      Double.parseDouble(pos[2])
    );
  }

  public static List<Vector3> stringListToVectorList(List<String> list) {
    List<Vector3> vector3List = new ArrayList<>();

    for (String i : list) vector3List.add(stringToVector(i));

    return vector3List;
  }

  public static List<String> vectorListToStringList(List<Vector3> list) {
    List<String> stringList = new ArrayList<>();

    for (Vector3 i : list) stringList.add(vectorToString(i));

    return stringList;
  }
}
