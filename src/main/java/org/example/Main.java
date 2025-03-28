package org.example;

import org.example.agent.CleaningAgent;
import org.example.agent.EnvironementModel;  // Orthographe avec un seul 'n'
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

import java.util.Random;

public class Main {
    public static void main(String[] args) {
        // 1. Initialisation de l'environnement
        EnvironementModel env = initializeEnvironment(10, 10, 20);

        // 2. Configuration de la plateforme JADE
        Runtime runtime = Runtime.instance();
        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.GUI, "true");

        // 3. Création du conteneur principal
        AgentContainer mainContainer = runtime.createMainContainer(profile);

        // 4. Lancement des agents
        launchAgents(mainContainer, env);
    }

    private static EnvironementModel initializeEnvironment(int width, int height, int dirtyCells) {
        EnvironementModel env = new EnvironementModel(width, height);
        Random random = new Random();

        for (int i = 0; i < dirtyCells; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            env.setDirty(x, y);
        }
        return env;
    }

    private static void launchAgents(AgentContainer container, EnvironementModel env) {
        try {
            // Agent 1 - Position initiale (0,0)
            AgentController agent1 = container.createNewAgent(
                    "Cleaner1",
                    CleaningAgent.class.getName(),
                    new Object[]{env, 0, 0}
            );
            agent1.start();
           // agent2 a la position
            AgentController agent2 = container.createNewAgent("Cleaner2",
                    CleaningAgent.class.getName(),
                    new Object[]{env,0,9}
            );

            agent2.start();



        } catch (StaleProxyException e) {
            System.err.println("Erreur lors du lancement des agents:");
            e.printStackTrace();
        }
    }
}