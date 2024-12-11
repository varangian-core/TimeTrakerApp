package com.synthwave.timetracker;

import javax.swing.*;
import java.awt.*;

public class ProgressCircle extends JComponent {
  private float progress = 0.0f; // 0.0 to 1.0 representing 0% to 100%

  public ProgressCircle() {
    setPreferredSize(new Dimension(100, 100));
  }

  /**
   * Set the current progress of the circle.
   * @param progress a value between 0.0 and 1.0
   */
  public void setProgress(float progress) {
    this.progress = Math.max(0.0f, Math.min(1.0f, progress));
    repaint();
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    // Enable anti-aliasing for smoother edges
    Graphics2D g2d = (Graphics2D) g.create();
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);

    int size = Math.min(getWidth(), getHeight());
    int x = (getWidth() - size) / 2;
    int y = (getHeight() - size) / 2;

    // Draw the background circle
    g2d.setColor(getBackground());
    g2d.fillOval(x, y, size, size);

    // Draw the progress arc
    g2d.setColor(getForeground());
    int arcAngle = (int) (360 * progress);
    g2d.fillArc(x, y, size, size, 90, -arcAngle);
    // Starting from 90 degrees for a top start, and negative angle to fill clockwise

    g2d.dispose();
  }
}