package com.synthwave.timetracker;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class SessionInfoPanel extends JPanel implements ThemedComponent {
  private JLabel sessionInfoLabel;
  private RoundedProgressCircle progressCircle;
  private String sessionName;
  private int totalTime;
  private int remainingTime;

  public SessionInfoPanel(String sessionName, int totalTime) {
    this.sessionName = sessionName;
    this.totalTime = totalTime;
    this.remainingTime = totalTime;

    setOpaque(false);
    setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));

    sessionInfoLabel = new JLabel();
    sessionInfoLabel.setFont(new Font("Serif", Font.BOLD, 14));

    progressCircle = new RoundedProgressCircle();
    progressCircle.setPreferredSize(new Dimension(20, 20));

    add(sessionInfoLabel);
    add(progressCircle);

    ThemeManager.register(this);
    applyTheme(ThemeManager.getTheme());

    updateDisplay();
  }

  // Update remaining time and refresh display
  public void setRemainingTime(int remainingTime) {
    this.remainingTime = Math.max(0, Math.min(totalTime, remainingTime));
    updateDisplay();
  }

  private void updateDisplay() {
    // Format: "SessionName [current/total]"
    int elapsed = totalTime - remainingTime;
    String timeText = String.format("%s [%d/%d]", sessionName, elapsed, totalTime);
    sessionInfoLabel.setText(timeText);

    // Calculate progress as a fraction of total time
    float progress = (float) elapsed / (float) totalTime;
    progressCircle.setProgress(progress);

    repaint();
  }

  @Override
  public void applyTheme(Theme theme) {
    Color background;
    Color foreground;
    Color borderColor; // color for borders if any components use RoundedBorder

    switch (theme) {
      case LIGHT:
        background = Color.WHITE;
        foreground = Color.BLACK;
        borderColor = foreground;
        break;
      case DARK:
        background = new Color(45,45,45);
        foreground = Color.WHITE;
        borderColor = foreground;
        break;
      case SYNTHWAVE:
        background = new Color(40,0,40);
        foreground = Color.MAGENTA;
        borderColor = new Color(255, 105, 180); // Pink for borders/circle
        break;
      default:
        background = Color.WHITE;
        foreground = Color.BLACK;
        borderColor = foreground;
    }

    setBackground(background);
    sessionInfoLabel.setForeground(foreground);

    // Update the progress circle to match the theme
    progressCircle.setForeground(borderColor);
    progressCircle.setBackground(background);

    repaint();
  }

  // Inner class for the progress circle
  class RoundedProgressCircle extends JComponent {
    private float progress = 0.0f; // between 0.0 (0%) and 1.0 (100%)

    public void setProgress(float progress) {
      this.progress = Math.max(0.0f, Math.min(1.0f, progress));
      repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2d = (Graphics2D) g.create();
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

      int size = Math.min(getWidth(), getHeight());
      int x = (getWidth() - size) / 2;
      int y = (getHeight() - size) / 2;

      // Draw background circle
      g2d.setColor(getBackground());
      g2d.fillOval(x, y, size, size);

      // Draw progress arc
      g2d.setColor(getForeground());
      int arcAngle = (int) (360 * progress);
      g2d.fillArc(x, y, size, size, 90, -arcAngle);

      g2d.dispose();
    }
  }
}