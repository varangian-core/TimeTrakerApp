package com.synthwave.timetracker;

import java.util.ArrayList;
import java.util.List;

public class ThemeManager {

  private static Theme currentTheme = Theme.LIGHT;
  private static final List<ThemedComponent> listeners = new ArrayList<>();

  public static void setTheme(Theme theme) {
    currentTheme = theme;
    notifyListeners();
  }

  public static Theme getTheme() {
    return currentTheme;
  }

  public static void register(ThemedComponent component) {
    if (!listeners.contains(component)) {
      listeners.add(component);
    }
  }

  private static void notifyListeners() {
    for (ThemedComponent c : listeners) {
      c.applyTheme(currentTheme);
    }
  }
}
