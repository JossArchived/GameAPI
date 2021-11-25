package net.josscoder.gameapi.configurator;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.TextFormat;
import net.josscoder.gameapi.Game;
import net.josscoder.gameapi.listener.GameListener;
import net.josscoder.gameapi.user.User;
import net.josscoder.gameapi.util.PacketUtils;

public abstract class Configurator
  extends GameListener
  implements IConfigurator {

  protected Player player;
  protected final String mapName;
  protected Vector3 safeSpawn;

  public Configurator(Game game, Player player, String mapName) {
    super(game);
    this.player = player;
    this.mapName = mapName;

    game.registerListener(this);

    initItems();
  }

  public Level toLevel() {
    return game.getServer().getLevelByName(mapName);
  }

  public User toUser() {
    return userFactory.get(player);
  }

  public void info(String message) {
    if (player == null) {
      return;
    }

    player.sendMessage(TextFormat.colorize("&l&a»&r &a") + message);
    PacketUtils.playSoundDataPacket(player, "random.levelup", 1, 1);
  }

  public void alert(String message) {
    if (player == null) {
      return;
    }

    player.sendMessage(TextFormat.colorize("&l&e»&r &e") + message);
    PacketUtils.playSoundDataPacket(player, "beacon.power", 1, 1);
  }

  public void error(String message) {
    if (player == null) {
      return;
    }

    player.sendMessage(TextFormat.colorize("&l&c»&r &c") + message);
    PacketUtils.playSoundDataPacket(player, "note.bass", 1, 1);
  }

  public boolean isConfigurator(Player who) {
    return player != null && who.getName().equals(player.getName());
  }

  public void handleComplete(Player who) {
    if (!isConfigurator(who)) {
      return;
    }

    if (isCompleted()) {
      complete();

      return;
    }

    error("You did not complete the steps. You have left creation mode!");

    this.player = null;
  }

  public void handleCancel(Cancellable cancellable, Player player) {
    if (!isConfigurator(player)) {
      return;
    }

    cancellable.setCancelled();
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onQuit(PlayerQuitEvent event) {
    handleComplete(event.getPlayer());
    teleportToDefaultLevel();
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onEntityDamage(EntityDamageEvent event) {
    Entity entity = event.getEntity();

    if (!(entity instanceof Player)) {
      return;
    }

    handleCancel(event, (Player) entity);
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onBlockPlace(BlockPlaceEvent event) {
    handleCancel(event, event.getPlayer());
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onBlockBreak(BlockBreakEvent event) {
    handleCancel(event, event.getPlayer());
  }

  public void teleportToDefaultLevel() {
    if (player == null) {
      return;
    }

    player.teleport(
      game.getServer().getDefaultLevel().getSafeSpawn().add(0, 1)
    );

    User user = toUser();

    if (user != null) {
      user.clearAllInventory();
      user.updateInventory();
    }
  }

  @Override
  public void complete() {
    teleportToDefaultLevel();
  }
}
