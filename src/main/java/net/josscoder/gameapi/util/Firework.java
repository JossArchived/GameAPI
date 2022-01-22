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

import cn.nukkit.Server;
import cn.nukkit.item.ItemFirework;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.DyeColor;
import java.util.SplittableRandom;
import net.josscoder.gameapi.util.entity.CustomItemFirework;

public class Firework {

  private static final SplittableRandom random = new SplittableRandom();

  public static void spawnRandom(Position position) {
    spawn(
      position,
      DyeColor.values()[random.nextInt(
          ItemFirework.FireworkExplosion.ExplosionType.values().length
        )],
      random.nextBoolean(),
      random.nextBoolean(),
      ItemFirework.FireworkExplosion.ExplosionType.values()[random.nextInt(
          ItemFirework.FireworkExplosion.ExplosionType.values().length
        )],
      1,
      20
    );
  }

  public static void spawn(
    Position position,
    DyeColor color,
    boolean flicker,
    boolean trail,
    ItemFirework.FireworkExplosion.ExplosionType explosionType
  ) {
    spawn(position, color, flicker, trail, explosionType, 1, 20);
  }

  public static void spawn(
    Position position,
    DyeColor color,
    boolean flicker,
    boolean trail,
    ItemFirework.FireworkExplosion.ExplosionType explosionType,
    int flight,
    int lifetime
  ) {
    Level level = position.getLevel();

    if (!Server.getInstance().isLevelLoaded(level.getName())) {
      return;
    }

    CompoundTag tag = new CompoundTag();

    CompoundTag explosion = new CompoundTag()
      .putByteArray("FireworkColor", new byte[] { (byte) color.getDyeData() })
      .putByteArray("FireworkFade", new byte[] {})
      .putBoolean("FireworkFlicker", flicker)
      .putBoolean("FireworkTrail", trail)
      .putByte("FireworkType", explosionType.ordinal());

    tag.putCompound(
      "Fireworks",
      new CompoundTag("Fireworks")
        .putList(new ListTag<CompoundTag>("Explosions").add(explosion))
        .putByte("Flight", flight)
    );

    ItemFirework item = new ItemFirework();
    item.setNamedTag(tag);

    CustomItemFirework entity = new CustomItemFirework(
      position.getChunk(),
      new CompoundTag()
        .putList(
          new ListTag<DoubleTag>("Pos")
            .add(new DoubleTag("", position.x + 0.5))
            .add(new DoubleTag("", position.y + 0.5))
            .add(new DoubleTag("", position.z + 0.5))
        )
        .putList(
          new ListTag<>("Motion")
            .add(new DoubleTag("", 0))
            .add(new DoubleTag("", 0))
            .add(new DoubleTag("", 0))
        )
        .putList(
          new ListTag<FloatTag>("Rotation")
            .add(new FloatTag("", 0))
            .add(new FloatTag("", 0))
        )
        .putCompound("FireworkItem", NBTIO.putItemHelper(item)),
      lifetime
    );
    entity.spawnToAll();
  }
}
