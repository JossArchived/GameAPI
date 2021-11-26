package test.phase;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.player.PlayerDeathEvent;
import cn.nukkit.item.Item;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import net.josscoder.gameapi.Game;
import net.josscoder.gameapi.phase.GamePhase;
import net.josscoder.gameapi.phase.base.EndGamePhase;
import net.josscoder.gameapi.user.User;
import net.josscoder.gameapi.util.TimeUtils;

public class FightPhase extends GamePhase {

  public FightPhase(Game game, Duration duration) {
    super(game, duration);
  }

  @Override
  protected void onStart() {
    broadcastMessage("&l&6»&r &fThe game has started, good luck!");
  }

  @Override
  public void onUpdate() {
    if (countNeutralPlayers() <= 1) {
      Map<Player, Integer> pedestalWinners = new HashMap<>();

      if (countNeutralPlayers() == 1) {
        Player winner = getNeutralPlayers().get(0);

        if (winner != null) {
          pedestalWinners.put(winner, 1);
          broadcastMessage(
            "&l&b»&r &7" + winner.getName() + " is the winner!",
            onlinePlayer -> getOnlinePlayers().contains(onlinePlayer)
          );
        }
      } else {
        broadcastMessage(
          "&l&c»&r &cThere are no winners!",
          onlinePlayer -> getOnlinePlayers().contains(onlinePlayer)
        );
      }

      game
        .getPhaseSeries()
        .addNext(
          new EndGamePhase(game, Duration.ofSeconds(20), (pedestalWinners))
        );

      end();

      return;
    }

    broadcastActionBar(
      "&b&lGame ends in &r&b" +
      TimeUtils.timeToString((int) getRemainingDuration().getSeconds())
    );
  }

  @Override
  protected void onEnd() {}

  @EventHandler(priority = EventPriority.NORMAL)
  public void onDeath(PlayerDeathEvent event) {
    event.setDeathMessage("");
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onEntityDamage(EntityDamageEvent event) {
    if (event.isCancelled()) {
      return;
    }

    Entity entity = event.getEntity();

    if (!(entity instanceof Player)) {
      return;
    }

    Player player = (Player) entity;

    User user = userFactory.get(player);

    if (user == null) {
      return;
    }

    if (event.getFinalDamage() < entity.getHealth()) {
      return;
    }

    event.setCancelled();

    for (Item drop : player.getDrops()) {
      entity.getLevel().dropItem(entity, drop);
    }

    player.teleport(
      game.getGameMapManager().getMapWinner().getSafeSpawn().add(0, 1)
    );
    user.convertSpectator(true);

    String message = "";

    switch (event.getCause()) {
      case ENTITY_ATTACK:
        if (event instanceof EntityDamageByEntityEvent) {
          Entity damager = ((EntityDamageByEntityEvent) event).getDamager();

          if (damager instanceof Player) {
            message = "was killed by " + damager.getName();
          }
        }
        break;
      case PROJECTILE:
        message = "was shot by projectile";
        break;
      case FALL:
        message = "has hit on the ground";
        break;
      case VOID:
        message = "fell into the void";
        break;
      case ENTITY_EXPLOSION:
      case BLOCK_EXPLOSION:
        message = "has exploded";
        break;
      case LAVA:
        message = "did not survive in the lava";
        break;
      case FIRE:
      case FIRE_TICK:
        message = "did not survive the fire";
        break;
      case MAGIC:
        message = "died by a witchcraft";
        break;
      case DROWNING:
        message = "has drowned";
        break;
      case SUFFOCATION:
        message = "died suffocated somewhere";
        break;
      case SUICIDE:
        message = "couldn't take it anymore and he committed suicide";
        break;
      case HUNGER:
        message = "had no food";
        break;
      case LIGHTNING:
        message = "was struck by lightning";
        break;
      default:
        message = "has died";
        break;
    }

    broadcastMessage(
      "&l&c»&r &8" + player.getName() + " &7" + message + "&f!",
      onlinePlayer -> getOnlinePlayers().contains(onlinePlayer)
    );
    broadcastSound(
      "mob.guardian.death",
      onlinePlayer -> getOnlinePlayers().contains(onlinePlayer)
    );
  }
}
