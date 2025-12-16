# Pac-Man Design Patterns Project

## Description A comprehensive Pac-Man game implementation demonstrating Object-Oriented Design Patterns. developed for the Design Patterns course.

## Membres du Groupe
- Roua Essid

## Technologies Utilisées
- Langage : Java 17
- Framework GUI : Swing (Java)
- Logging : Logback / SLF4J
- Build : Maven

## Design Patterns Implémentés
1. **State Pattern** : Gestion des états du jeu (Menu, Jeu) et des états des personnages (Idle, Running).
2. **Decorator Pattern** : Gestion des bonus (Bouclier) via `ShieldDecorator`.
3. **Composite Pattern** : Gestion de la hiérarchie des objets du jeu (Niveaux, Entités) via `Level` et `GameComponent`.
4. **Factory Pattern** : Création centralisée des entités du jeu (Fantômes, Murs, Pacman) via `EntityFactory`.

## Installation

### Prérequis
- JDK 17 ou supérieur
- Maven 3.6+

### Étapes
1. Compiler le projet :
   ```bash
   mvn clean package
   ```
2. Exécuter le jeu :
   ```bash
   java -jar target/pacman-design-patterns-1.0-SNAPSHOT.jar
   ```

## Utilisation
- **Flèches directionnelles** : Déplacement du Pac-Man.
- **Barre d'espace** : (Dans le menu) Démarrer le jeu.
- Le fichier de log `pacman.log` sera généré à la racine pour tracer les événements.
