# 🤖 Agent Nettoyeur Intelligent - Simulation IA

![Java](https://img.shields.io/badge/Java-17%2B-blue)
![JADE](https://img.shields.io/badge/Framework-JADE-green)
![Swing](https://img.shields.io/badge/GUI-Java%20Swing-orange)

Un système multi-agent simulant un robot nettoyeur intelligent évoluant dans un environnement dynamique.

## 🎯 Fonctionnalités

- **Algorithme intelligent** : Décision basée sur :
    - Priorisation des cases sales
    - Bonus pour cases non visitées
    - Détection des saletés adjacentes
- **Interface graphique** :
    - Visualisation temps réel
    - Journal des actions (logs)
    - Affichage de l'énergie
- **Paramètres configurables** :
    - Taille de la grille
    - Niveau d'énergie initial
    - Fréquence d'apparition de saletés

## 🚀 Installation

1. **Prérequis** :
    - JDK 17+
    - Maven 3.8+
    - Librairie JADE

2. **Cloner le dépôt** :
   ```bash
   git clone https://github.com/votre-utilisateur/agent-nettoyeur.git
   cd agent-nettoyeur
   ```

3. **Compiler et exécuter** :
   ```bash
   mvn clean install
   mvn exec:java -Dexec.mainClass="org.example.Main"
   ```

## 🖥️ Interface

![Screenshot](docs/screenshot.png) *(Remplacez par votre propre capture d'écran)*

- **Légende** :
    - 🤖 : Agent nettoyeur
    - 💩 : Case sale
    - 🔋 : Niveau d'énergie
    - 📜 : Journal des actions

## 🧠 Algorithme

```java
public Action selectOptimalAction() {
    // 1. Évaluation des actions possibles
    // 2. Bonus pour cases non visitées
    // 3. Pénalité pour répétition
    // 4. Sélection intelligente
}
```

## 📂 Structure du projet

```
src/
├── main/
│   ├── java/
│   │   ├── agent/          # Classes des agents
│   │   ├── gui/            # Interface graphique
│   │   └── environment/    # Modélisation environnement
│   └── resources/
├── test/                   # Tests unitaires
docs/                       # Documentation
pom.xml                     # Configuration Maven
```

## 📝 Licence

Ce projet est sous licence MIT - voir le fichier [LICENSE](LICENSE).

## 👨‍💻 Auteur

Votre Nom - [@votre-compte](https://github.com/votre-compte) - votre.email@example.com

*"Un environnement propre est un environnement heureux!"* 🌱