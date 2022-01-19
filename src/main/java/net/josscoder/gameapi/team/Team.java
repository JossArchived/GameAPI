package net.josscoder.gameapi.team;

import cn.nukkit.Player;
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
public abstract class Team implements Cloneable {

  protected final String id;
  protected final String color;
  protected int slot = 0;
  protected List<Player> members = Collections.synchronizedList(
    new ArrayList<>()
  );

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

  public boolean hasSlot() {
    return slot != 0;
  }

  @Override
  public Team clone() throws CloneNotSupportedException {
    return (Team) super.clone();
  }
}
