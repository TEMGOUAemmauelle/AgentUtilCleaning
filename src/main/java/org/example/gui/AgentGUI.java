package org.example.gui;

import org.example.agent.EnvironementModel;
import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.Queue;

public class AgentGUI extends JFrame {
    private final GridPanel gridPanel;
    private final JLabel energyLabel;
    private final JTextArea logArea;
    private final Queue<String> logQueue;
    private static final int MAX_LOG_LINES = 10;

    public AgentGUI(EnvironementModel env) {
        setTitle("Simulation Agent Nettoyeur");
        setSize(700, 800); // Hauteur augmentée pour les logs en bas
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        logQueue = new LinkedList<>();

        // Panel principal avec BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Panel pour la grille (centre)
        gridPanel = new GridPanel(env);
        mainPanel.add(gridPanel, BorderLayout.CENTER);

        // Panel combiné pour info et logs (sud)
        JPanel southPanel = new JPanel(new BorderLayout());

        // Panel d'information (nord dans southPanel)
        JPanel infoPanel = new JPanel();
        energyLabel = new JLabel("Énergie: 100");
        energyLabel.setFont(new Font("Arial", Font.BOLD, 16));
        infoPanel.add(energyLabel);
        southPanel.add(infoPanel, BorderLayout.NORTH);

        // Panel des logs (centre dans southPanel)
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(BorderFactory.createTitledBorder("Journal des actions"));

        logArea = new JTextArea(5, 40);
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(logArea);
        logPanel.add(scrollPane, BorderLayout.CENTER);

        southPanel.add(logPanel, BorderLayout.CENTER);
        mainPanel.add(southPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    public void updateDisplay(int agentX, int agentY, int energy) {
        // Mettre à jour la grille
        for (int y = 0; y < gridPanel.getEnv().getGrid()[0].length; y++) {
            for (int x = 0; x < gridPanel.getEnv().getGrid().length; x++) {
                boolean isAgent = (x == agentX && y == agentY);
                boolean isDirty = gridPanel.getEnv().isDirty(x, y);
                gridPanel.updateCell(x, y, isAgent, isDirty);
            }
        }
        energyLabel.setText("Énergie: " + energy);
    }

    public void addLog(String message) {
        SwingUtilities.invokeLater(() -> {
            logQueue.add(message);
            if (logQueue.size() > MAX_LOG_LINES) {
                logQueue.poll();
            }

            StringBuilder sb = new StringBuilder();
            for (String log : logQueue) {
                sb.append(log).append("\n");
            }
            logArea.setText(sb.toString());
            logArea.setCaretPosition(logArea.getDocument().getLength());


        });
    }
    public void refreshLog() {
        SwingUtilities.invokeLater(() -> {
            StringBuilder sb = new StringBuilder();
            for (String log : logQueue) {
                sb.append(log).append("\n");
            }
            logArea.setText(sb.toString());
            logArea.setText("mes log");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }



}