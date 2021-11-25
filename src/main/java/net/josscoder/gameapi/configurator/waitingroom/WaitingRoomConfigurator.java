package net.josscoder.gameapi.configurator.waitingroom;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.item.Item;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.ConfigSection;
import cn.nukkit.utils.LevelException;
import lombok.Getter;
import lombok.Setter;
import net.josscoder.gameapi.Game;
import net.josscoder.gameapi.configurator.Configurator;
import net.josscoder.gameapi.customitem.CustomItem;
import net.josscoder.gameapi.user.User;
import net.josscoder.gameapi.util.VectorUtils;

@Getter
@Setter
public class WaitingRoomConfigurator extends Configurator {

  private WaitingRoomStep step = null;
  private Vector3 exitEntitySpawn;
  private Vector3 pedestalCenterSpawn;
  private Vector3 pedestalOneSpawn;
  private Vector3 pedestalTwoSpawn;
  private Vector3 pedestalThreeSpawn;

  private CustomItem exitItem;

  public WaitingRoomConfigurator(Game game, Player player, String mapName) {
    super(game, player, mapName);
  }

  @Override
  public void initItems() {
    exitItem = new CustomItem(Item.get(Item.EMERALD), "&r&bExit &7[Use]");
    exitItem
      .setTransferable(false)
      .setInteractHandler(
        (user, p) -> {
          if (!isConfigurator(p)) {
            return;
          }

          teleportToDefaultLevel();
          this.player = null;
        }
      );
  }

  @Override
  public void run() {
    try {
      if (!game.getServer().loadLevel(mapName)) {
        error("That map does not exist!");

        return;
      }
    } catch (LevelException e) {
      error("Error loading map " + mapName + ": " + e.getMessage());

      return;
    }

    if (toLevel() != null) {
      info("You have entered creation mode for the waiting room!");

      game.schedule(
        () -> {
          if (player == null || userFactory == null) {
            return;
          }

          alert("Register the Safe Spawn!");
          step = WaitingRoomStep.SAFE_SPAWN;

          player.setGamemode(1);
          player.teleport(toLevel().getSafeSpawn());

          User user = toUser();

          if (user != null) {
            user.clearAllInventory();
            user.getInventory().setItem(8, exitItem.build());
            user.updateInventory();
          }
        },
        20 * 2
      );

      return;
    }

    error(
      "Failed to get the map. Please check the maps from the folders or look at the console and try again!"
    );
  }

  @Override
  public boolean isCompleted() {
    return (
      exitEntitySpawn != null &&
      pedestalCenterSpawn != null &&
      pedestalOneSpawn != null &&
      pedestalTwoSpawn != null &&
      pedestalThreeSpawn != null
    );
  }

  @Override
  public void complete() {
    super.complete();

    step = WaitingRoomStep.COMPLETE;

    game.schedule(
      () ->
        new Thread(
          () -> {
            game.storeMapBackup(mapName);

            ConfigSection mapSection = new ConfigSection();
            mapSection.set("name", mapName.replace(" ", "_"));
            mapSection.set("safeSpawn", VectorUtils.vectorToString(safeSpawn));
            mapSection.set(
              "exitEntitySpawn",
              VectorUtils.vectorToString(exitEntitySpawn)
            );
            mapSection.set(
              "pedestalCenterSpawn",
              VectorUtils.vectorToString(pedestalCenterSpawn)
            );
            mapSection.set(
              "pedestalOneSpawn",
              VectorUtils.vectorToString(pedestalOneSpawn)
            );
            mapSection.set(
              "pedestalTwoSpawn",
              VectorUtils.vectorToString(pedestalTwoSpawn)
            );
            mapSection.set(
              "pedestalThreeSpawn",
              VectorUtils.vectorToString(pedestalThreeSpawn)
            );

            game.getConfig().set("waitingRoom", mapSection);
            game.getConfig().save();

            info("You have finished setting up the waiting room");
            player = null;
          }
        )
          .start(),
      20 * 2
    );
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onInteract(PlayerInteractEvent event) {
    Player eventPlayer = event.getPlayer();

    if (!isConfigurator(eventPlayer)) {
      return;
    }

    if (step == null) {
      return;
    }

    Block block = event.getBlock().clone();

    if (block.getId() == Block.AIR) {
      return;
    }

    block.add(0.5, 1, 0.5);

    switch (step) {
      case SAFE_SPAWN:
        safeSpawn = block;
        info("You have registered the Safe Spawn!");
        step = WaitingRoomStep.EXIT_ENTITY_SPAWN;
        alert("Now register the Entity Exit Spawn!");
        break;
      case EXIT_ENTITY_SPAWN:
        exitEntitySpawn = block;
        info("You have registered the Entity Exit Spawn!");
        step = WaitingRoomStep.PEDESTAL_CENTER_SPAWN;
        alert("Now register the Pedestal Center Spawn!");

        if (isCompleted()) {
          complete();
        }
        break;
      case PEDESTAL_CENTER_SPAWN:
        pedestalCenterSpawn = block;
        info("You have registered the Pedestal Center!");
        step = WaitingRoomStep.PEDESTAL_ONE_SPAWN;
        alert("Now register the Pedestal One Spawn!");

        if (isCompleted()) {
          complete();
        }
        break;
      case PEDESTAL_ONE_SPAWN:
        pedestalOneSpawn = block;
        info("You have registered the Pedestal One Spawn!");
        step = WaitingRoomStep.PEDESTAL_TWO_SPAWN;
        alert("Now register the Pedestal Two Spawn!");

        if (isCompleted()) {
          complete();
        }
        break;
      case PEDESTAL_TWO_SPAWN:
        pedestalTwoSpawn = block;
        info("You have registered the Pedestal Two Spawn!");
        step = WaitingRoomStep.PEDESTAL_THREE_SPAWN;
        alert("Now register the Pedestal Three Spawn!");

        if (isCompleted()) {
          complete();
        }
        break;
      case PEDESTAL_THREE_SPAWN:
        pedestalThreeSpawn = block;
        info("You have registered the Pedestal Three Spawn!");
        step = WaitingRoomStep.COMPLETE;

        if (isCompleted()) {
          complete();
        }
        break;
    }
  }
}
