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

package net.josscoder.gameapi.util.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.NBTEntityData;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.EntityEventPacket;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.commons.math3.util.FastMath;

public class CustomItemFirework extends Entity {

  public static final int NETWORK_ID = 72;
  private int lifetime;
  private Item firework;

  public CustomItemFirework(FullChunk chunk, CompoundTag nbt, int lifetime) {
    super(chunk, nbt);
    this.lifetime = lifetime;
  }

  public void initEntity() {
    ThreadLocalRandom rand = ThreadLocalRandom.current();

    if (lifetime == 0) {
      lifetime = 20 + rand.nextInt(6) + rand.nextInt(7);
    }

    motionX = rand.nextGaussian() * 0.001D;
    motionZ = rand.nextGaussian() * 0.001D;
    motionY = 0.05D;

    if (namedTag.contains("FireworkItem")) {
      firework = NBTIO.getItemHelper(namedTag.getCompound("FireworkItem"));
      setDataProperty(new NBTEntityData(16, firework));
    }
  }

  public int getNetworkId() {
    return NETWORK_ID;
  }

  public boolean onUpdate(int currentTick) {
    if (closed) {
      return false;
    } else {
      int tickDiff = currentTick - lastUpdate;
      if (tickDiff <= 0 && !justCreated) {
        return true;
      } else {
        lastUpdate = currentTick;
        if (timing != null) {
          timing.startTiming();
        }

        boolean hasUpdate = entityBaseTick(tickDiff);
        if (isAlive()) {
          motionX *= 1.15D;
          motionZ *= 1.15D;
          motionY += 0.04D;
          move(motionX, motionY, motionZ);
          updateMovement();
          float f = (float) Math.sqrt(motionX * motionX + motionZ * motionZ);
          yaw = (float) (FastMath.atan2(motionX, motionZ) * 57.29577951308232D);
          pitch = (float) (FastMath.atan2(motionY, f) * 57.29577951308232D);
          if (age == 0) {
            getLevel().addLevelSoundEvent(this, 56);
          }

          if (age >= lifetime) {
            EntityEventPacket pk = new EntityEventPacket();
            pk.event = 25;
            pk.eid = getId();
            level.addChunkPacket(getFloorX() >> 4, getFloorZ() >> 4, pk);
            level.addLevelSoundEvent(this, 58, -1, 72);
            kill();
            hasUpdate = true;
          }
        }

        if (timing != null) {
          timing.stopTiming();
        }

        return (
          hasUpdate ||
          !onGround ||
          Math.abs(motionX) > 1.0E-5D ||
          Math.abs(motionY) > 1.0E-5D ||
          Math.abs(motionZ) > 1.0E-5D
        );
      }
    }
  }

  public boolean attack(EntityDamageEvent source) {
    return (
      (
        source.getCause() == EntityDamageEvent.DamageCause.VOID ||
        source.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK ||
        source.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION ||
        source.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION
      ) &&
      super.attack(source)
    );
  }

  public void setFirework(Item item) {
    firework = item;
    setDataProperty(new NBTEntityData(16, firework));
  }

  public float getWidth() {
    return 0.25F;
  }

  public float getHeight() {
    return 0.25F;
  }
}
