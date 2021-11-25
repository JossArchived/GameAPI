package net.josscoder.gameapi.util;

import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ItemUtils {

  public static List<Item> stringListToItemList(List<String> list) {
    List<Item> itemList = new ArrayList<>();

    for (String i : list) itemList.add(stringToItem(i));

    return itemList;
  }

  public static Item stringToItem(String string) {
    if (string == null || string.length() < 2) return null;

    String[] data = string.split(":");

    Item item = Item.get(
      Integer.parseInt(data[0]),
      Integer.parseInt(data[1]),
      Integer.parseInt(data[2])
    );

    if (data.length < 4) return item;

    for (int i = 3; i < data.length; i++) {
      String[] enchantData = data[i].split(";");

      Enchantment enchantment = Enchantment.getEnchantment(
        Integer.parseInt(enchantData[0])
      );

      if (enchantData.length > 1) enchantment.setLevel(
        Integer.parseInt(enchantData[1])
      );

      item.addEnchantment(enchantment);
    }

    return item;
  }

  public static String itemToString(Item item) {
    if (item == null) return null;

    StringBuilder stringBuilder = new StringBuilder(
      item.getId() + ":" + item.getDamage() + ":" + item.getCount()
    );

    for (Enchantment enchantment : item.getEnchantments()) {
      stringBuilder
        .append(":")
        .append(enchantment.getId())
        .append(";")
        .append(enchantment.getLevel());
    }

    return stringBuilder.toString();
  }

  @SuppressWarnings("unchecked")
  public static List<Item> listToItems(List<Map<String, Object>> list) {
    List<Item> items = new ArrayList<>();

    for (Map<String, Object> map : list) {
      Integer id = (Integer) map.get("id");
      Integer meta = (Integer) map.get("meta");
      Integer count = (Integer) map.get("count");

      Item item = Item.get(
        id != null ? id : Item.AIR,
        meta != null ? meta : 0,
        count != null ? count : 1
      );

      List<? extends Map<String, Integer>> enchantments = (List<? extends Map<String, Integer>>) map.get(
        "enchantments"
      );

      if (enchantments != null) {
        for (Map<String, Integer> data : enchantments) {
          Integer level = data.get("level");

          item.addEnchantment(
            Enchantment.get(data.get("id")).setLevel(level != null ? level : 0)
          );
        }
      }

      items.add(item);
    }

    return items;
  }

  public static List<Map<String, Object>> itemsToList(List<Item> list) {
    List<Map<String, Object>> items = new ArrayList<>();

    for (Item item : list) {
      Map<String, Object> itemMap = new LinkedHashMap<>();

      itemMap.put("id", item.getId());
      itemMap.put("meta", item.getDamage());
      itemMap.put("count", item.getCount());

      List<Map<String, Integer>> enchantments = new ArrayList<>();

      for (Enchantment enchantment : item.getEnchantments()) {
        enchantments.add(
          new LinkedHashMap<String, Integer>() {
            {
              put("id", enchantment.getId());
              put("level", enchantment.getLevel());
            }
          }
        );
      }

      itemMap.put("enchantments", enchantments);
      items.add(itemMap);
    }

    return items;
  }
}
