package com.synthwave.timetracker;

import javax.swing.*;
import java.awt.*;

public class SessionProgressBar extends JComponent {
  private float progress = 0.0f;
  private final Color barColor = Color.GREEN; // Base stable color
  private final int preferredWidth = 200;
  private final int preferredHeight = 20;

  public SessionProgressBar() {
    setPreferredSize(new Dimension(preferredWidth, preferredHeight));
  }

  /**
   * Set the current progress of the bar.
   * @param p a value between 0.0 and 1.0 representing percentage of progress
   */
  public void setProgress(float p) {
    this.progress = Math.max(0.0f, Math.min(1.0f, p));
    repaint();
  }

  public float getProgress() {
    return progress;
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2d = (Graphics2D) g.create();
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    int width = getWidth();
    int height = getHeight();

    // Draw background
    g2d.setColor(getBackground());
    g2d.fillRect(0, 0, width, height);

    // Draw progress portion
    int filledWidth = (int) (width * progress);
    if (filledWidth > 0) {
      // Create a gradient from a lighter green to a darker green
      Color startColor = barColor.brighter();
      Color endColor = barColor.darker();
      GradientPaint gradient = new GradientPaint(
          0, 0, startColor,
          filledWidth, 0, endColor
      );
      g2d.setPaint(gradient);
      g2d.fillRect(0, 0, filledWidth, height);
    }

    // Draw border
    g2d.setColor(Color.DARK_GRAY);
    g2d.drawRect(0, 0, width - 1, height - 1);

    g2d.dispose();
  }
}