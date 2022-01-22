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

package net.josscoder.gameapi.map;

import cn.nukkit.Player;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.item.ItemFirework;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.DyeColor;
import cn.nukkit.utils.TextFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.josscoder.gameapi.Game;
import net.josscoder.gameapi.util.Firework;
import net.josscoder.gameapi.util.SkinUtils;
import org.citizen.attributes.EmoteType;
import org.citizen.attributes.InvokeAttribute;
import org.citizen.entity.Citizen;

@Getter
@Setter
public class WaitingRoomMap extends Map<Game> {

  protected Vector3 exitEntitySpawn;

  protected Vector3 pedestalCenterSpawn;

  protected Vector3 pedestalOneSpawn;

  protected Vector3 pedestalTwoSpawn;

  protected Vector3 pedestalThreeSpawn;

  private Citizen exitEntity;

  protected String pedestalOneTag = "&e1st place";

  protected String pedestalTwoTag = "&72nd place";

  protected String pedestalThreeTag = "&63rd place";

  protected Skin exitEntitySkin = null;

  protected List<Citizen> entities = new ArrayList<>();

  private static final SplittableRandom random = new SplittableRandom();

  public WaitingRoomMap(Game game, String name, Vector3 safeSpawn) {
    this(game, name, safeSpawn, null);
  }

  public WaitingRoomMap(
    Game game,
    String name,
    Vector3 safeSpawn,
    Vector3 exitEntitySpawn
  ) {
    super(game, name, safeSpawn);
    this.exitEntitySpawn = exitEntitySpawn;

    generateExitEntity();
  }

  private void generateExitEntity() {
    if (exitEntitySpawn == null) {
      return;
    }

    prepare();

    exitEntity = new Citizen();
    exitEntity.setPosition(Position.fromObject(exitEntitySpawn, toLevel()));
    exitEntity.setScale(1.3f);
    exitEntity.lookAt(safeSpawn);
    exitEntity.setSkin(
      exitEntitySkin == null ? SkinUtils.getRandom() : exitEntitySkin
    );
    exitEntity.getTagEditor().putLine(TextFormat.YELLOW + "Back to Hub");
    exitEntity.setInvokeAttribute(
      new InvokeAttribute(exitEntity) {
        @Override
        public void invoke(@NonNull Player player) {
          game.sendToHub(player);
        }
      }
    );

    game.getCitizenLibrary().getCitizenFactory().add(exitEntity);
  }

  private Citizen generatePedestalEntity(
    @NonNull Player pedestalPlayer,
    int position
  ) {
    Citizen citizen = new Citizen();
    citizen.setPosition(
      Position.fromObject(
        position == 1
          ? pedestalOneSpawn
          : position == 2
            ? pedestalTwoSpawn
            : position == 3 ? pedestalThreeSpawn : pedestalOneSpawn,
        toLevel()
      )
    );
    citizen.setScale(1.4f);
    citizen.lookAt(pedestalCenterSpawn);
    citizen.setSkin(pedestalPlayer.getSkin());
    citizen
      .getTagEditor()
      .putLine(
        TextFormat.colorize(
          position == 1
            ? pedestalOneTag
            : position == 2
              ? pedestalTwoTag
              : position == 3 ? pedestalThreeTag : ""
        )
      )
      .putLine(TextFormat.colorize("&f&l") + pedestalPlayer.getName());
    citizen.executeEmote(
      EmoteType.values()[random.nextInt(EmoteType.values().length)].getId(),
      true,
      5
    );

    return citizen;
  }

  public void generatePedestalEntities(
    java.util.Map<Player, Integer> pedestalPlayers
  ) {
    if (pedestalPlayers.isEmpty()) {
      return;
    }

    for (java.util.Map.Entry<Player, Integer> set : pedestalPlayers.entrySet()) {
      Player pedestalPlayer = set.getKey();

      if (pedestalPlayer == null) {
        continue;
      }

      Citizen citizen = generatePedestalEntity(pedestalPlayer, set.getValue());
      game.getCitizenLibrary().getCitizenFactory().add(citizen);
      entities.add(citizen);
    }
  }

  private void spawnFirework(Vector3 vector3) {
    prepare();

    Position position = Position.fromObject(vector3, toLevel());

    Firework.spawn(
      position,
      DyeColor.YELLOW,
      false,
      false,
      ItemFirework.FireworkExplosion.ExplosionType.SMALL_BALL
    );

    Firework.spawn(
      position,
      DyeColor.ORANGE,
      true,
      true,
      ItemFirework.FireworkExplosion.ExplosionType.SMALL_BALL
    );
  }

  public void spawnFireworks(int count) {
    game.schedule(
      () -> {
        if (count == 1) {
          spawnFirework(pedestalOneSpawn);
        } else if (count == 2) {
          spawnFirework(pedestalTwoSpawn);
        } else if (count > 2) {
          spawnFirework(pedestalThreeSpawn);
        }
      },
      20 * 3
    );
  }

  public void teleportToPedestalCenter(Player player) {
    if (pedestalCenterSpawn == null) {
      return;
    }

    prepare();

    if (toLevel() == null) {
      return;
    }

    player.teleport(
      Position.fromObject(pedestalCenterSpawn.add(0, 1), toLevel())
    );

    game.schedule(() -> game.showGameResume(player), 20 * 2);
  }
}
