package net.josscoder.gameapi.user.storage;

import com.google.common.base.MoreObjects;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LocalStorage {

  private final Map<String, Object> storage = new ConcurrentHashMap<>();

  public Map<String, Object> getAll() {
    return storage;
  }

  @SuppressWarnings("unchecked")
  public <T> T get(String key) {
    return (T) storage.get(key);
  }

  public Boolean getBoolean(String key) {
    return MoreObjects.firstNonNull(get(key), false);
  }

  public String getString(String key) {
    return get(key);
  }

  public Integer getInteger(String key) {
    return MoreObjects.firstNonNull(get(key), 0);
  }

  public Long getLong(String key) {
    return MoreObjects.firstNonNull(get(key), 0L);
  }

  public Float getFloat(String key) {
    return MoreObjects.firstNonNull(get(key), 0f);
  }

  public void set(String key, Object value) {
    storage.put(key, value);
  }

  public boolean exists(String key) {
    return get(key) != null;
  }

  public void remove(String key) {
    storage.remove(key);
  }
}
