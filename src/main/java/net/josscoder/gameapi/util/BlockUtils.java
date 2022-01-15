package net.josscoder.gameapi.util;

import cn.nukkit.block.Block;
import cn.nukkit.level.Position;
import java.util.ArrayList;
import java.util.List;

public class BlockUtils {

  public static List<Block> getNearbyBlocks(Position position, int radius) {
    List<Block> blocks = new ArrayList<>();

    for (
      int x = (int) (position.getX() - radius);
      x <= position.getX() + radius;
      x++
    ) {
      for (
        int y = (int) (position.getY() - radius);
        y <= position.getY() + radius;
        y++
      ) {
        for (
          int z = (int) (position.getZ() - radius);
          z <= position.getZ() + radius;
          z++
        ) {
          Block block = position.getLevel().getBlock(x, y, z);
          if (block != null) {
            blocks.add(block);
          }
        }
      }
    }

    return blocks;
  }
}
