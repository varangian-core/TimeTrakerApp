package com.synthwave.timetracker;

import javax.swing.*;
import java.awt.*;

public class ProgressCircle extends JComponent {
  private float progress = 0.0f; // 0.0 to 1.0 representing 0% to 100%

  public ProgressCircle() {
    // Distinct colors to confirm it’s working:
    setForeground(Color.GREEN);      // Arc color
    setBackground(Color.LIGHT_GRAY); // Background circle color
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

    // Draw the progress arc from 0 degrees (3 o'clock) counterclockwise
    g2d.setColor(getForeground());
    int arcAngle = (int) (360 * progress);
    g2d.fillArc(x, y, size, size, 0, arcAngle);

    // Optional: draw a thin gray outline to see the circle clearly
    g2d.setColor(Color.DARK_GRAY);
    g2d.setStroke(new BasicStroke(1f));
    g2d.drawOval(x, y, size, size);

    g2d.dispose();
  }

  // Optional test harness
  public static void main(String[] args) {
    JFrame frame = new JFrame("ProgressCircle Test");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    ProgressCircle circle = new ProgressCircle();
    frame.add(circle);
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);

    // Simulate progress increasing from 0% to 100% repeatedly
    new Timer(100, e -> {
      float newProgress = circle.progress + 0.01f;
      if (newProgress > 1.0f) newProgress = 0.0f;
      circle.setProgress(newProgress);
    }).start();
  }
}