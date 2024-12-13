package com.synthwave.timetracker;

import javax.swing.*;
import java.awt.*;

public class ProgressCircle extends JComponent {
  private float progress = 0.0f; // 0.0 to 1.0 representing 0% to 100%
  // Pink gradient colors
  private final Color startColor = new Color(255, 182, 193); // LightPink
  private final Color endColor = new Color(255, 105, 180);   // HotPink

  public ProgressCircle() {
    setPreferredSize(new Dimension(60, 60));
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
    // Enable anti-aliasing
    Graphics2D g2d = (Graphics2D) g.create();
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    int size = Math.min(getWidth(), getHeight());
    int x = (getWidth() - size) / 2;
    int y = (getHeight() - size) / 2;

    // Draw background circle
    g2d.setColor(getBackground());
    g2d.fillOval(x, y, size, size);

    // Draw progress arc using a gradient
    int arcAngle = (int) (360 * progress);
    if (arcAngle > 0) {
      GradientPaint gradient = new GradientPaint(
          x, y, startColor,
          x + size, y, endColor
      );
      g2d.setPaint(gradient);
      g2d.fillArc(x, y, size, size, 90, -arcAngle);
    }

    // Optional: draw a thin outline
    g2d.setColor(Color.DARK_GRAY);
    g2d.setStroke(new BasicStroke(1f));
    g2d.drawOval(x, y, size, size);

    g2d.dispose();
  }
}
