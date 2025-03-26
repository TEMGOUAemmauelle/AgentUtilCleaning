package org.example.agent;

import jade.core.behaviours.CyclicBehaviour;
import java.util.*;

public class AdvancedCleaningBehaviour extends CyclicBehaviour {
    public enum Action {
        CLEAN, MOVE_UP, MOVE_DOWN, MOVE_LEFT, MOVE_RIGHT
    }

    // Constantes ajustées
    private static final int BASE_CLEAN_PRIORITY = 20;
    private static final int DIRTY_ADJACENT_BONUS = 5;
    private static final int UNVISITED_BONUS = 8;
    private static final int CONSECUTIVE_PENALTY = 10;

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
            agent.logAction("Mission accomplie!");
            agent.updateLogDisplay();
            agent.doDelete();
            return true;
        }
        return false;
    }

    private Action selectOptimalAction() {
        Map<Action, Integer> scores = new EnumMap<>(Action.class);

        // Évaluation initiale
        for (Action action : Action.values()) {
            scores.put(action, evaluateAction(action));
        }

        // Application des pénalités
        applyPenalties(scores);

        // Sélection avec randomisation pour éviter les boucles
        return selectActionWithRandomization(scores);
    }

    private Action selectActionWithRandomization(Map<Action, Integer> scores) {
        // Convertir en liste triée
        List<Map.Entry<Action, Integer>> sortedActions = new ArrayList<>(scores.entrySet());
        sortedActions.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        // Prendre une action aléatoire parmi les 2 meilleures
        int topActions = Math.min(2, sortedActions.size());
        int selection = random.nextInt(topActions);

        return sortedActions.get(selection).getKey();
    }

    private int evaluateAction(Action action) {
        int x = agent.getPosX();
        int y = agent.getPosY();

        switch (action) {
            case CLEAN:
                if (agent.getEnvironment().isDirty(x, y)) {
                    int score = BASE_CLEAN_PRIORITY;
                    if (!agent.hasVisited(x, y)) score += UNVISITED_BONUS;
                    return score;
                }
                return Integer.MIN_VALUE;

            case MOVE_UP: return evaluateMove(x, y-1);
            case MOVE_DOWN: return evaluateMove(x, y+1);
            case MOVE_LEFT: return evaluateMove(x-1, y);
            case MOVE_RIGHT: return evaluateMove(x+1, y);
            default: return Integer.MIN_VALUE;
        }
    }

    private int evaluateMove(int newX, int newY) {
        if (!agent.isValidPosition(newX, newY)) {
            return Integer.MIN_VALUE;
        }

        int score = 0;
        EnvironementModel env = agent.getEnvironment();

        // Bonus pour case non visitée
        if (!agent.hasVisited(newX, newY)) {
            score += UNVISITED_BONUS;
        }

        // Bonus pour saleté sur la case cible
        if (env.isDirty(newX, newY)) {
            score += BASE_CLEAN_PRIORITY - 5;
        }

        // Détection des saletés adjacentes (3x3 autour de la cible)
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
            scores.put(lastAction, scores.get(lastAction) - penalty);
        }
    }

    private void executeAction(Action action) {
        switch (action) {
            case CLEAN:
                if (agent.cleanCurrentCell()) {
                    agent.logAction("Nettoyage en (" + agent.getPosX() + "," + agent.getPosY() + ")");
                    agent.markVisited(agent.getPosX(), agent.getPosY());
                } else {
                    agent.logAction("Aucune saleté en (" + agent.getPosX() + "," + agent.getPosY() + ")");
                }
                break;

            case MOVE_UP:
                if (agent.moveTo(agent.getPosX(), agent.getPosY() - 1)) {
                    logDeplacement("haut");
                }
                break;
            case MOVE_DOWN:
                if (agent.moveTo(agent.getPosX(), agent.getPosY() + 1)) {
                    logDeplacement("bas");
                }
                break;
            case MOVE_LEFT:
                if (agent.moveTo(agent.getPosX() - 1, agent.getPosY())) {
                    logDeplacement("gauche");
                }
                break;
            case MOVE_RIGHT:
                if (agent.moveTo(agent.getPosX() + 1, agent.getPosY())) {
                    logDeplacement("droite");
                }
                break;
        }

        agent.consumeEnergy(1);
        agent.updateLogDisplay(); // Mise à jour de l'affichage des logs
    }

    private void logDeplacement(String direction) {
        String log = "Déplacement vers " + direction +
                " -> (" + agent.getPosX() + "," + agent.getPosY() + ")";
        agent.logAction(log);
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