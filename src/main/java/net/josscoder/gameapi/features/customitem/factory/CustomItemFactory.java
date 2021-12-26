package net.josscoder.gameapi.features.customitem.factory;

import java.util.HashMap;
import java.util.Map;
import net.josscoder.gameapi.features.customitem.CustomItem;

public class CustomItemFactory {

  protected static Map<String, CustomItem> storage = new HashMap<>();

  public static CustomItem get(String name) {
    return storage.get(name);
  }

  public static void storeItem(CustomItem item) {
    storage.put(item.getUuid(), item);
  }
}
