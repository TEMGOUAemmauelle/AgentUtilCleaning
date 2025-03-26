package org.example.agent;

import jade.core.behaviours.CyclicBehaviour;
import java.util.Random;

public class CleaningBehaviour extends CyclicBehaviour {
    private final CleaningAgent agent;
    private final Random random = new Random();

    public CleaningBehaviour(CleaningAgent agent) {
        super(agent);
        this.agent = agent;
    }

    @Override
    public void action() {
        // Vérifier les conditions d'arrêt
        if (agent.getEnergy() <= 0 || agent.getEnvironment().isClean()) {
            System.out.println("Arrêt - Énergie: " + agent.getEnergy() + " | Environnement propre: " + agent.getEnvironment().isClean());
            agent.doDelete();
            return;
        }

        // Décision de mouvement
        int action = random.nextInt(6);
        executeAction(action);

        // Mise à jour de l'interface
        agent.consumeEnergy(1);
        agent.refreshGUI();

        // Pause pour visualisation
        pause(500);
    }

    private void executeAction(int action) {
        switch (action) {
            case 0:
                avancerHaut();
                break;
            case 1:
                avancerBas();
                break;
            case 2:
                avancerGauche();
                break;
            case 3:
                avancerDroite();
                break;
            case 4:
                nettoyer();
                break;
            case 5:
                rester();
                break;
            default:
                rester();
        }
    }

    private void avancerHaut() {
        int newY = agent.getPosY() - 1;
        if (newY >= 0) {
            agent.setPosition(agent.getPosX(), newY);
            logDeplacement("haut");
        }
    }

    private void avancerBas() {
        int newY = agent.getPosY() + 1;
        if (newY < agent.getEnvironment().getGrid()[0].length) {
            agent.setPosition(agent.getPosX(), newY);
            logDeplacement("bas");
        }
    }

    private void avancerGauche() {
        int newX = agent.getPosX() - 1;
        if (newX >= 0) {
            agent.setPosition(newX, agent.getPosY());
            logDeplacement("gauche");
        }
    }

    private void avancerDroite() {
        int newX = agent.getPosX() + 1;
        if (newX < agent.getEnvironment().getGrid().length) {
            agent.setPosition(newX, agent.getPosY());
            logDeplacement("droite");
        }
    }

    private void nettoyer() {
        EnvironementModel env = agent.getEnvironment();
        int x = agent.getPosX();
        int y = agent.getPosY();

        if (env.isDirty(x, y)) {
            env.cleanCell(x, y);
            System.out.println("Nettoyage effectué en (" + x + "," + y + ")");
        } else {
            System.out.println("Aucune saleté à nettoyer en (" + x + "," + y + ")");
        }
    }

    private void rester() {
        System.out.println("Agent en attente - Position: (" +
                agent.getPosX() + "," + agent.getPosY() + ")");
    }

    private void logDeplacement(String direction) {
        System.out.println("Déplacement vers " + direction +
                " - Nouvelle position: (" +
                agent.getPosX() + "," + agent.getPosY() + ")");
    }

    private void pause(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            System.err.println("Interruption pendant la pause: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}