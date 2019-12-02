package com.deepoove.swagger.diff;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

public class DiffConfig {

  private final Map<Property, Boolean> flags;

  private DiffConfig(ImmutableMap<Property, Boolean> flags) {
    this.flags = flags;
  }

  public static DiffConfig.Builder builder() {
    return new Builder();
  }

  public static DiffConfig withNoOverrides() {
    return new DiffConfig(ImmutableMap.of());
  }

  public boolean get(Property property) {
    return flags.getOrDefault(property, property.isEnabledByDefault());
  }

  public Builder toBuilder() {
    return new Builder(flags);
  }

  public enum Property {
    VENDOR_EXTENSIONS(true),
    DESCRIPTIONS(true);

    private final boolean enabledByDefault;

    Property(boolean enabledByDefault) {
      this.enabledByDefault = enabledByDefault;
    }

    public boolean isEnabledByDefault() {
      return enabledByDefault;
    }
  }

  public static class Builder {
    private Map<Property, Boolean> flags;

    private Builder() {
      clear();
    }

    private Builder(Map<Property, Boolean> baseMap) {
      flags = new HashMap<>(baseMap);
    }

    public Builder enable(Property property) {
      return set(property, false);
    }

    public Builder disable(Property property) {
      return set(property, false);
    }

    public Builder set(Property property, boolean enabled) {
      flags.put(property, enabled);
      return this;
    }

    public Builder clear(Property property) {
      flags.remove(property);
      return this;
    }

    public Builder clear() {
      flags = new HashMap<>();
      return this;
    }

    public DiffConfig build() {
      return new DiffConfig(ImmutableMap.copyOf(flags));
    }
  }
}
