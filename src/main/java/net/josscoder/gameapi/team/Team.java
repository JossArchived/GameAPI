package net.josscoder.gameapi.team;

import cn.nukkit.Player;
import cn.nukkit.utils.DyeColor;
import cn.nukkit.utils.TextFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class Team implements Cloneable {

  protected final String id;
  protected final String color;
  protected final DyeColor dyeColor;
  protected List<Player> members = Collections.synchronizedList(
    new ArrayList<>()
  );

  public boolean isMember(Player player) {
    return members.contains(player);
  }

  public String getNameWithColor() {
    return getColor() + id;
  }

  public String getColor() {
    return TextFormat.colorize(color);
  }

  public void clearMembers() {
    members.clear();
  }

  public void addMember(Player member) {
    members.add(member);
  }

  public void removeMember(Player member) {
    members.remove(member);
  }

  public int countMembers() {
    return members.size();
  }

  @Override
  public Team clone() throws CloneNotSupportedException {
    return (Team) super.clone();
  }
}
