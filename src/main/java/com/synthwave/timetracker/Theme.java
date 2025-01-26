package com.synthwave.timetracker;

import java.awt.*;

public enum Theme {
  LIGHT(Color.WHITE, Color.BLACK, Color.LIGHT_GRAY, Color.BLACK),
  DARK(new Color(45, 45, 45), Color.WHITE, new Color(60, 60, 60), Color.WHITE),
  SYNTHWAVE(new Color(40, 0, 40), Color.MAGENTA, new Color(20, 0, 20), Color.MAGENTA);

  public final Color background;
  public final Color foreground;
  public final Color componentBackground;
  public final Color timerForeground; // New color for timer label

  Theme(Color background, Color foreground, Color componentBackground, Color timerForeground) {
    this.background = background;
    this.foreground = foreground;
    this.componentBackground = componentBackground;
    this.timerForeground = timerForeground;
  }
}