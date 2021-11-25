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

package net.josscoder.gameapi.customitem;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.TextFormat;
import java.util.*;
import lombok.Getter;
import net.josscoder.gameapi.customitem.factory.CustomItemFactory;
import net.josscoder.gameapi.customitem.handler.EntityHandler;
import net.josscoder.gameapi.customitem.handler.Handler;
import net.josscoder.gameapi.user.User;

@Getter
public class CustomItem {

  private final String uuid;
  private Item item;
  private String customName;
  private boolean transferable;
  private final List<Enchantment> enchantments;
  private final LinkedList<String> lore;
  private ArrayList<String> commands;
  private int damage = 0;
  private int count = 1;
  private Handler interactHandler;
  private EntityHandler entityDamageHandler;
  private EntityHandler entityInteractHandler;
  private String sound;

  public CustomItem(String customName) {
    this(Item.get(Item.AIR), customName);
  }

  public CustomItem(Item item, String customName) {
    this(item, customName, null);
  }

  public CustomItem(Item item, String customName, Handler interactHandler) {
    this(item, customName, interactHandler, null);
  }

  public CustomItem(
    Item item,
    String customName,
    Handler interactHandler,
    EntityHandler entityDamageHandler
  ) {
    this(item, customName, interactHandler, entityDamageHandler, null);
  }

  public CustomItem(
    Item item,
    String customName,
    Handler interactHandler,
    EntityHandler entityDamageHandler,
    EntityHandler entityInteractHandler
  ) {
    this(
      item,
      customName,
      true,
      interactHandler,
      entityDamageHandler,
      entityInteractHandler,
      new ArrayList<>(),
      new LinkedList<>(),
      new ArrayList<>(),
      null
    );
  }

  public CustomItem(
    Item item,
    String customName,
    boolean transferable,
    Handler interactHandler,
    EntityHandler entityDamageHandler,
    EntityHandler entityInteractHandler,
    ArrayList<Enchantment> enchantments,
    LinkedList<String> lore,
    ArrayList<String> commands,
    String sound
  ) {
    this.uuid = UUID.randomUUID().toString();
    this.item = item;
    this.customName = TextFormat.colorize(customName);
    this.transferable = transferable;
    this.interactHandler = interactHandler;
    this.entityDamageHandler = entityDamageHandler;
    this.entityInteractHandler = entityInteractHandler;
    this.enchantments = enchantments;
    this.lore = lore;
    this.commands = commands;
    this.sound = sound;

    CustomItemFactory.storeItem(this);
  }

  public CustomItem setItem(Item item) {
    this.item = item;

    return this;
  }

  public CustomItem setCustomName(String customName) {
    this.customName = customName;

    return this;
  }

  public CustomItem setTransferable(boolean transferable) {
    this.transferable = transferable;

    return this;
  }

  public CustomItem setDamage(int damage) {
    this.damage = damage;

    return this;
  }

  public CustomItem setCount(int count) {
    this.count = count;

    return this;
  }

  public void clearLore() {
    lore.clear();
  }

  public CustomItem addLore(String line) {
    lore.add(line);

    return this;
  }

  public CustomItem addLore(String... lines) {
    Arrays.stream(lines).forEach(this::addLore);

    return this;
  }

  public CustomItem setInteractHandler(Handler interactHandler) {
    this.interactHandler = interactHandler;

    return this;
  }

  public CustomItem setEntityDamageHandler(EntityHandler entityDamageHandler) {
    this.entityDamageHandler = entityDamageHandler;

    return this;
  }

  public CustomItem setEntityHandler(EntityHandler entityDamageHandler) {
    setEntityInteractHandler(entityDamageHandler);
    setEntityDamageHandler(entityDamageHandler);

    return this;
  }

  public CustomItem setEntityInteractHandler(
    EntityHandler entityInteractHandler
  ) {
    this.entityInteractHandler = entityInteractHandler;

    return this;
  }

  public void handle(User user, Player player) {
    for (String command : commands) {
      if (command == null) {
        continue;
      }

      Server.getInstance().dispatchCommand(player, command);
    }

    if (interactHandler != null) {
      interactHandler.handle(user, player);
    }
  }

  public void handleEntityDamage(
    User user,
    Player player,
    Entity to,
    DataPacketReceiveEvent event
  ) {
    if (entityDamageHandler != null) {
      entityDamageHandler.handle(event, user, player, to);
    }
  }

  public void handleEntityInteract(
    User user,
    Player player,
    Entity to,
    DataPacketReceiveEvent event
  ) {
    if (entityInteractHandler != null) entityInteractHandler.handle(
      event,
      user,
      player,
      to
    );
  }

  public void addEnchantment(Enchantment... enchantment) {
    enchantments.addAll(Arrays.asList(enchantment));
  }

  public void addCommands(String... command) {
    commands.addAll(Arrays.asList(command));
  }

  public CustomItem setCommands(ArrayList<String> commands) {
    this.commands = commands;

    return this;
  }

  public CustomItem setSound(String sound) {
    this.sound = sound;

    return this;
  }

  public Item build() {
    item.setDamage(damage);
    item.setCount(count);

    enchantments.forEach(item::addEnchantment);

    CompoundTag nbt = new CompoundTag().putString("customItem", uuid);

    item.setLore(lore.toArray(new String[0]));

    return item.setCustomName(customName).setCustomBlockData(nbt);
  }
}
