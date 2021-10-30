package jossc.game.listener;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerLoginEvent;

public class MainEventListener implements Listener {

  @EventHandler
  public void onLogin(PlayerLoginEvent event) {}

  @EventHandler
  public void onJoin(PlayerJoinEvent event) {}
}
