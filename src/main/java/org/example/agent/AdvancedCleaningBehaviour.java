package org.example.agent;

import jade.core.behaviours.CyclicBehaviour;
import java.util.*;

public class AdvancedCleaningBehaviour extends CyclicBehaviour {
    public enum Action {
        CLEAN, MOVE_UP, MOVE_DOWN, MOVE_LEFT, MOVE_RIGHT
    }

    // Constantes de priorisation
    private static final int CLEAN_PRIORITY = 100;  // Nettoyage toujours prioritaire
    private static final int BASE_MOVE_PRIORITY = 10;
    private static final int DIRTY_ADJACENT_BONUS = 5;
    private static final int UNVISITED_BONUS = 8;
    private static final int CONSECUTIVE_PENALTY = 50;

    private final CleaningAgent agent;
    private Action lastAction = null;
    private int consecutiveSameAction = 0;
    private Random random = new Random();

    public AdvancedCleaningBehaviour(CleaningAgent agent) {
        super(agent);
        this.agent = agent;
    }

    @Override
    public void action() {
        if (shouldTerminate()) return;

        Action action = selectOptimalAction();
        executeAction(action);
        updateActionHistory(action);

        agent.refreshGUI();
        pause(200);
    }

    private boolean shouldTerminate() {
        if (agent.getEnergy() <= 0) {
            agent.logAction("Arrêt - énergie épuisée");
            agent.updateLogDisplay();
            agent.doDelete();
            return true;
        }
        if (agent.getEnvironment().isClean()) {
            agent.logAction("Mission accomplie !");
            agent.updateLogDisplay();
            agent.doDelete();
            return true;
        }
        return false;
    }

    private Action selectOptimalAction() {
        int x = agent.getPosX();
        int y = agent.getPosY();

        // Priorité absolue : Si la case actuelle est sale, on nettoie
        if (agent.getEnvironment().isDirty(x, y)) {
            return Action.CLEAN;
        }

        // Évaluation des mouvements possibles
        Map<Action, Integer> scores = new EnumMap<>(Action.class);
        for (Action action : new Action[]{Action.MOVE_UP, Action.MOVE_DOWN, Action.MOVE_LEFT, Action.MOVE_RIGHT}) {
            scores.put(action, evaluateMove(action));
        }

        // Application des pénalités pour éviter les répétitions excessives
        applyPenalties(scores);

        // Sélection de l’action optimale avec une légère randomisation
        return selectBestMove(scores);
    }

    private Action selectBestMove(Map<Action, Integer> scores) {
        List<Map.Entry<Action, Integer>> sortedActions = new ArrayList<>(scores.entrySet());
        sortedActions.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        // On privilégie toujours la meilleure action
        return sortedActions.get(0).getKey();
    }

    private int evaluateMove(Action action) {
        int newX = agent.getPosX();
        int newY = agent.getPosY();

        switch (action) {
            case MOVE_UP: newY--; break;
            case MOVE_DOWN: newY++; break;
            case MOVE_LEFT: newX--; break;
            case MOVE_RIGHT: newX++; break;
            default: return Integer.MIN_VALUE;
        }

        if (!agent.isValidPosition(newX, newY)) {
            return Integer.MIN_VALUE;
        }

        int score = BASE_MOVE_PRIORITY;
        EnvironementModel env = agent.getEnvironment();

        // Bonus si la case est sale
        if (env.isDirty(newX, newY)) {
            score += CLEAN_PRIORITY;
        }

        // Bonus si la case n’a jamais été visitée
        if (!agent.hasVisited(newX, newY)) {
            score += UNVISITED_BONUS;
        }

        // Bonus pour les cases adjacentes sales
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;
                int adjX = newX + dx;
                int adjY = newY + dy;
                if (agent.isValidPosition(adjX, adjY) && env.isDirty(adjX, adjY)) {
                    score += DIRTY_ADJACENT_BONUS;
                }
            }
        }

        return score;
    }

    private void applyPenalties(Map<Action, Integer> scores) {
        if (lastAction != null) {
            int penalty = consecutiveSameAction * CONSECUTIVE_PENALTY;
            scores.put(lastAction, scores.getOrDefault(lastAction, 0) - penalty);
        }
    }

    private void executeAction(Action action) {
        int x = agent.getPosX();
        int y = agent.getPosY();

        switch (action) {
            case CLEAN:
                if (agent.cleanCurrentCell()) {
                    agent.logAction("Nettoyage en (" + x + "," + y + ")");
                    agent.markVisited(x, y);
                }
                break;

            case MOVE_UP:
                if (agent.moveTo(x, y - 1)) logDeplacement("haut");
                break;
            case MOVE_DOWN:
                if (agent.moveTo(x, y + 1)) logDeplacement("bas");
                break;
            case MOVE_LEFT:
                if (agent.moveTo(x - 1, y)) logDeplacement("gauche");
                break;
            case MOVE_RIGHT:
                if (agent.moveTo(x + 1, y)) logDeplacement("droite");
                break;
        }

        agent.consumeEnergy(1);
        agent.updateLogDisplay();
    }

    private void logDeplacement(String direction) {
        agent.logAction("Déplacement vers " + direction + " -> (" + agent.getPosX() + "," + agent.getPosY() + ")");
    }


    private void updateActionHistory(Action action) {
        if (action == lastAction) {
            consecutiveSameAction++;
        } else {
            consecutiveSameAction = 0;
            lastAction = action;
        }
    }

    private void pause(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
