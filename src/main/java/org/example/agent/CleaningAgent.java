package org.example.agent;

import jade.core.Agent;
import org.example.gui.AgentGUI;
import javax.swing.SwingUtilities;

public class CleaningAgent extends Agent {
    private EnvironementModel environment;
    private int posX, posY;
    private int energy = 100;
    private transient AgentGUI gui;
    private boolean[][] visitedCells;

    @Override
    protected void setup() {
        Object[] args = getArguments();
        if (args != null && args.length >= 3) {
            try {
                environment = (EnvironementModel) args[0];
                posX = (int) args[1];
                posY = (int) args[2];

                visitedCells = new boolean[environment.getGrid().length][environment.getGrid()[0].length];
                markVisited(posX, posY);

                // Initialisation de l'interface graphique
                initGUI();

                addBehaviour(new AdvancedCleaningBehaviour(this));

            } catch (ClassCastException | ArrayIndexOutOfBoundsException e) {
                System.err.println("Erreur d'initialisation: " + e.getMessage());
                doDelete();
            }
        } else {
            System.err.println("Arguments manquants pour l'agent");
            doDelete();
        }
    }

    private void initGUI() {
        SwingUtilities.invokeLater(() -> {
            gui = new AgentGUI(environment);
            gui.updateDisplay(posX, posY, energy);
            logStatus();
        });
    }

    // --- Méthodes améliorées ---
    public boolean moveTo(int x, int y) {
        if (isValidPosition(x, y)) {
            setPosition(x, y);
            markVisited(x, y);
            return true;
        }
        return false;
    }

    private void log(String message) {
        System.out.println(message); // Console
        if (gui != null) {
            gui.addLog(message); // Interface graphique
        }
    }

    public boolean cleanCurrentCell() {
        if (environment.isDirty(posX, posY)) {
            environment.cleanCell(posX, posY);
            logAction("Nettoyage en (" + posX + "," + posY + ")");
            return true;
        }
        return false;
    }

    // --- Méthodes utilitaires ---
    public boolean isValidPosition(int x, int y) {
        return x >= 0 && y >= 0 && x < environment.getGrid().length && y < environment.getGrid()[0].length;
    }

    public void logStatus() {
        System.out.printf("[Agent %s] Pos:(%d,%d) Energie:%d Sales:%d%n",
                getLocalName(), posX, posY, energy, environment.countDirtyCells());
    }

    public void logAction(String action) {
        System.out.printf("[Agent %s] %s%n", getLocalName(), action);
    }

    // --- Getters/Setters ---
    public EnvironementModel getEnvironment() {
        return environment;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public int getEnergy() {
        return energy;
    }

    public void setPosition(int x, int y) {
        this.posX = x;
        this.posY = y;
        refreshGUI();
    }

    public void consumeEnergy(int amount) {
        this.energy = Math.max(0, this.energy - amount);
        refreshGUI();
    }

    public void refreshGUI() {
        if (gui != null) {
            gui.updateDisplay(posX, posY, energy);
        }
    }

    public void updateLogDisplay() {
        if (gui != null) {
            gui.refreshLog();
        }
    }

    public void markVisited(int x, int y) {
        if (isValidPosition(x, y)) {
            visitedCells[x][y] = true;
        }
    }

    public boolean hasVisited(int x, int y) {
        return isValidPosition(x, y) && visitedCells[x][y];
    }
}
