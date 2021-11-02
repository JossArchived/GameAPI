package jossc.game.utils;

import cn.nukkit.Server;
import cn.nukkit.entity.item.EntityFirework;
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

public class Firework {

  private static final SplittableRandom random = new SplittableRandom();

  public static void spawn(Position position) {
    Level level = position.getLevel();

    if (!Server.getInstance().isLevelLoaded(level.getFolderName())) {
      return;
    }

    ItemFirework item = new ItemFirework();
    CompoundTag tag = new CompoundTag();
    CompoundTag ex = new CompoundTag()
      .putByteArray(
        "FireworkColor",
        new byte[] {
          (byte) DyeColor
            .values()[random.nextInt(
              ItemFirework.FireworkExplosion.ExplosionType.values().length
            )].getDyeData()
        }
      )
      .putByteArray("FireworkFade", new byte[] {})
      .putBoolean("FireworkFlicker", random.nextBoolean())
      .putBoolean("FireworkTrail", random.nextBoolean())
      .putByte(
        "FireworkType",
        ItemFirework.FireworkExplosion.ExplosionType
          .values()[random.nextInt(
            ItemFirework.FireworkExplosion.ExplosionType.values().length
          )].ordinal()
      );
    tag.putCompound(
      "Fireworks",
      new CompoundTag("Fireworks")
        .putList(new ListTag<CompoundTag>("Explosions").add(ex))
        .putByte("Flight", 1)
    );
    item.setNamedTag(tag);
    CompoundTag nbt = new CompoundTag()
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
      .putCompound("FireworkItem", NBTIO.putItemHelper(item));
    EntityFirework entity = new EntityFirework(
      level.getChunk((int) position.x >> 4, (int) position.z >> 4),
      nbt
    );
    entity.spawnToAll();
  }
}
