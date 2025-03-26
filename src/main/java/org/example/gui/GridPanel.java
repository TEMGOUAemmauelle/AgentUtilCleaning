package org.example.gui;

import org.example.agent.EnvironementModel;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class GridPanel extends JPanel {
    private final EnvironementModel env;
    private final JPanel[][] cells;

    public GridPanel(EnvironementModel env) {
        this.env = env;
        setLayout(new GridLayout(env.getGrid().length, env.getGrid()[0].length, 3, 3));
        setBackground(new Color(220, 230, 240));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        cells = new JPanel[env.getGrid().length][env.getGrid()[0].length];
        initializeCells();
    }

    public EnvironementModel getEnv() {
        return env;
    }

    private void initializeCells() {
        for (int y = 0; y < env.getGrid()[0].length; y++) {
            for (int x = 0; x < env.getGrid().length; x++) {
                cells[x][y] = new JPanel(new GridBagLayout()) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        Graphics2D g2 = (Graphics2D) g;
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(getBackground());
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    }
                };

                cells[x][y].setOpaque(false);
                cells[x][y].setBorder(BorderFactory.createEmptyBorder());

                JLabel label = new JLabel("", SwingConstants.CENTER);
                label.setFont(new Font("Segoe UI", Font.BOLD, 16));
                cells[x][y].add(label);

                cells[x][y].setPreferredSize(new Dimension(60, 60));
                add(cells[x][y]);
            }
        }
    }

    public void updateCell(int x, int y, boolean isAgent, boolean isDirty) {
        SwingUtilities.invokeLater(() -> {
            JPanel cell = cells[x][y];
            JLabel label = (JLabel) cell.getComponent(0);

            if (isAgent) {
                cell.setBackground(new Color(70, 130, 220));

                label.setForeground(Color.WHITE);
            } else if (isDirty) {
                cell.setBackground(new Color(80, 80, 80));

                label.setForeground(new Color(220, 220, 220));
            } else {
                cell.setBackground(new Color(245, 245, 245));
                label.setText("");
            }

            // Animation de mise à jour
            cell.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200, 100), 1));
            Timer timer = new Timer(300, e -> {
                cell.setBorder(BorderFactory.createEmptyBorder());
            });
            timer.setRepeats(false);
            timer.start();
        });
    }
}