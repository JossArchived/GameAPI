package jossc.game.phase.lobby;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockBurnEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.entity.CreatureSpawnEvent;
import cn.nukkit.event.entity.EntityDamageByBlockEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.player.PlayerDropItemEvent;
import cn.nukkit.event.player.PlayerFoodLevelChangeEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.plugin.PluginBase;
import java.time.Duration;

import cn.nukkit.utils.TextFormat;
import jossc.game.phase.GamePhase;

public abstract class LobbyPhase extends GamePhase {

  public LobbyPhase(PluginBase plugin) {
    super(plugin);
  }

  public LobbyPhase(PluginBase plugin, Duration duration) {
    super(plugin, duration);
  }

  @Override
  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
    super.onJoin(event);

    Player player = event.getPlayer();

    String playerName = TextFormat.GRAY + player.getName();

    player.setDisplayName(playerName);
    player.setNameTag(playerName);
  }

  @EventHandler (priority = EventPriority.HIGHEST)
  public void onBlockPlace(BlockPlaceEvent event)
  {
    event.setCancelled();
  }

  @EventHandler (priority = EventPriority.HIGHEST)
  public void onBlockBreak(BlockBreakEvent event)
  {
    event.setCancelled();
  }

  @EventHandler (priority = EventPriority.HIGHEST)
  public void onBlockBurn(BlockBurnEvent event)
  {
    event.setCancelled();
  }

  @EventHandler (priority = EventPriority.HIGHEST)
  public void onEntityDamageByBlock(EntityDamageByBlockEvent event)
  {
    event.setCancelled();
  }

  @EventHandler (priority = EventPriority.HIGHEST)
  public void onItemDrop(PlayerDropItemEvent event)
  {
    event.setCancelled();
  }

  @EventHandler (priority = EventPriority.HIGHEST)
  public void onDamage(EntityDamageEvent event)
  {
    event.setCancelled();
  }

  @EventHandler (priority = EventPriority.HIGHEST)
  public void onEntitySpawn(CreatureSpawnEvent event)
  {
    event.setCancelled();
  }

  @EventHandler (priority = EventPriority.HIGHEST)
  public void onFoodLevelChange(PlayerFoodLevelChangeEvent event)
  {
    event.setCancelled();
  }
}
