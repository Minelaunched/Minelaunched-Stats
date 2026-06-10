<div align="center">
  <h1>📊 MinelaunchedStats - La Documentation Complète</h1>
  <p><b>Le Guide Ultime : Administrateurs & Développeurs</b></p>
  <p><i>Compatible de la version 1.8.8 jusqu'aux dernières versions (Spigot, Paper, Purpur, Folia)</i></p>
</div>

---

# 📚 Table des Matières

- [PARTIE I : GUIDE UTILISATEUR (ADMINISTRATEURS)](#partie-i--guide-utilisateur-administrateurs)
  - [1. Qu'est-ce que MinelaunchedStats ?](#1-quest-ce-que-minelaunchedstats-)
  - [2. Installation Rapide](#2-installation-rapide)
  - [3. La Configuration Magique (Auto-Générée)](#3-la-configuration-magique-auto-générée)
  - [4. Comment récupérer les Statistiques ?](#4-comment-récupérer-les-statistiques-)
  - [5. Optimisation et Sécurité](#5-optimisation-et-sécurité)
- [PARTIE II : GUIDE DÉVELOPPEUR (CONTRIBUTEURS)](#partie-ii--guide-développeur-contributeurs)
  - [1. Architecture du Code (Clean Architecture)](#1-architecture-du-code-clean-architecture)
  - [2. Le Système "Zero-Touch Hook"](#2-le-système-zero-touch-hook)
  - [3. Tutoriel : Créer un Hook en 3 minutes](#3-tutoriel--créer-un-hook-en-3-minutes)
  - [4. L'Art de la Reflection (Éviter les Crashs)](#4-lart-de-la-reflection-éviter-les-crashs)
  - [5. Le Cycle de Vie de la Configuration](#5-le-cycle-de-vie-de-la-configuration)

---

# PARTIE I : GUIDE UTILISATEUR (ADMINISTRATEURS)

## 1. Qu'est-ce que MinelaunchedStats ?

**MinelaunchedStats** est un outil puissant destiné aux gérants de serveurs Minecraft professionnels. Son rôle est de scanner 100% des données du serveur (les performances système, la RAM, les mondes, les joueurs, leurs inventaires, les plugins installés) et de les formater en **JSON**.

**Pourquoi utiliser du JSON plutôt qu'une base de données MySQL ?**
- **Aucun lag :** Écrire dans une base de données bloque souvent le thread principal du serveur (chute de TPS). MinelaunchedStats crée un flux de texte léger lisible instantanément.
- **Accessibilité Universelle :** Vous pouvez lire du JSON depuis un site Web (JavaScript), un bot Discord (Python/Node.js), ou un panel d'administration sans avoir à gérer des tables SQL complexes.

## 2. Installation Rapide

1. Téléchargez le fichier `MinelaunchedStats.jar`.
2. Glissez-le dans le dossier `plugins/` de votre serveur.
3. Redémarrez votre serveur.
4. Dans votre navigateur, tapez `http://ip_de_votre_serveur:8080/`. Vous devriez voir une page massive remplie de texte : ce sont vos statistiques en direct !

**Commandes disponibles :**
- `/mlstats reload` : Recharge la configuration (pratique si vous modifiez le `config.yml`) sans redémarrer le serveur Minecraft. *(Permission : `mlstats.admin`)*

## 3. La Configuration Magique (Auto-Générée)

L'une des plus grandes forces de ce plugin est son **config.yml intelligent**.
Quand vous ouvrez `plugins/MinelaunchedStats/config.yml` pour la première fois, il vous semblera très petit.

```yaml
web:
  port: 8080
  endpoint: "/"
  pretty_print: false # Mettez sur 'true' pour que le JSON soit plus facile à lire pour un humain
```

### Le fonctionnement des "Hooks"
Le plugin est compatible avec plus de **48 autres plugins** (Vault, LuckPerms, Essentials, etc.). 
Si le plugin détecte que vous avez installé **Vault** et **LuckPerms** sur votre serveur, il va **automatiquement** rajouter les lignes correspondantes à la fin de votre `config.yml` au démarrage !

*Exemple de lignes qui s'ajouteront toutes seules :*
```yaml
hooks:
  vault:
    enabled: true
    export_server_economy: true
    export_player_balance: true
  luckperms:
    enabled: true
    export_primary_group: true
```
**Personnalisation extrême :** Vous ne voulez pas exporter le grade LuckPerms des joueurs car votre site n'en a pas besoin ? Mettez simplement `export_primary_group: false`. Le plugin ignorera cette donnée et vous économiserez des ressources !

## 4. Comment récupérer les Statistiques ?

Le plugin vous offre deux modes d'exportation de la donnée :

### Mode 1 : Le Serveur Web Interne (PULL)
Par défaut, le plugin héberge un mini-serveur Web sur le port `8080`. 
C'est la méthode idéale pour afficher le nombre de joueurs en direct sur votre site web.

**Exemple d'intégration sur votre site (JavaScript) :**
```javascript
fetch('http://ip_du_serveur:8080/')
  .then(response => response.json())
  .then(data => {
      document.getElementById('joueurs').innerText = data.players.online_count;
      document.getElementById('tps').innerText = data.server.tps;
  });
```

### Mode 2 : Le Bridge Redis (PUSH)
Si vous avez un réseau BungeeCord avec 10 serveurs Survie, faire des requêtes HTTP à chaque serveur est lent.
Activez Redis dans le `config.yml` :
```yaml
redis:
  enabled: true
  host: "127.0.0.1"
  port: 6379
  channel: "minelaunched_stats"
  update_interval_seconds: 5
```
Le plugin va maintenant "pousser" toutes les statistiques dans Redis toutes les 5 secondes. Votre Bot Discord ou votre Proxy n'a plus qu'à écouter ce canal (Pub/Sub) pour recevoir les mises à jour en direct de tous vos serveurs instantanément !

## 5. Optimisation et Sécurité

- **Zero-Lag :** La collecte des données (comme scanner les inventaires des joueurs) est optimisée pour s'exécuter en moins d'une milliseconde. Le plugin ne causera **jamais** de chute de TPS.
- **Protection des erreurs :** Si vous désinstallez un plugin (comme Essentials) et que MinelaunchedStats essaie d'exporter ses données, le plugin va s'en rendre compte et désactiver la liaison silencieusement, sans planter votre console avec des erreurs Java horribles (`NullPointerException`).

---

# PARTIE II : GUIDE DÉVELOPPEUR (CONTRIBUTEURS)

Cette section est destinée aux développeurs Java souhaitant comprendre le code source ou ajouter de nouvelles fonctionnalités au plugin.

## 1. Architecture du Code (Clean Architecture)

Le projet est divisé en packages stricts pour séparer la logique :

- `com.minelaunched.stats` : Le Main (`MinelaunchedStatsPlugin`).
- `com.minelaunched.stats.config` : *(Anciennement)*, géré aujourd'hui dynamiquement.
- `com.minelaunched.stats.collectors` : Contient la récolte de statistiques natives (ex: `PlayerCollector.java`, `SystemCollector.java`). C'est la base de données propre au jeu.
- `com.minelaunched.stats.network` : Contient `WebServer.java` (le serveur HTTP NanoHTTPD) et `RedisManager.java`.
- `com.minelaunched.stats.hooks` : **Le cœur de la modularité.** C'est ici que sont gérées les intégrations avec les plugins tiers.

## 2. Le Système "Zero-Touch Hook"

Historiquement, pour intégrer un plugin comme *AuraSkills*, il fallait :
1. Créer une classe `AuraSkillsHook`.
2. L'ajouter manuellement dans la méthode `appendPlayerHooks` du `StatsCollector`.
3. Écrire le code par défaut dans le fichier `config.yml` situé dans `src/main/resources/`.

**Aujourd'hui, l'architecture est "Zero-Touch".**
Tout est géré par la classe abstraite `MinelaunchedHook`. En tant que développeur, vous n'avez à créer **qu'un seul fichier**, et le `HookManager` s'occupera d'injecter la configuration dynamiquement au démarrage et de récolter les données de manière automatisée.

## 3. Tutoriel : Créer un Hook en 3 minutes

Objectif : Exporter les "Points de Magie" du plugin fictif `MagicSpells`.

### Étape A : Créer la classe du Hook
Dans `src/main/java/com/minelaunched/stats/hooks/`, créez `MagicSpellsHook.java` :

```java
package com.minelaunched.stats.hooks;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import java.util.Arrays;
import java.util.List;

public class MagicSpellsHook extends MinelaunchedHook {
    private boolean initialized = false;

    // 1. Définir le nom qui apparaîtra dans config.yml
    @Override
    public String getPluginName() {
        return "magicspells";
    }

    // 2. Définir les sous-clés à auto-générer dans la config
    // Cela génèrera `hooks.magicspells.export_magic_points: true`
    @Override
    public List<String> getExportKeys() {
        return Arrays.asList("magic_points");
    }

    // 3. Initialiser la connexion au plugin
    @Override
    public void init() {
        if (Bukkit.getPluginManager().getPlugin("MagicSpells") != null) {
            initialized = true;
            Bukkit.getLogger().info("[MinelaunchedStats] Hooks: MagicSpells lié avec succès !");
        }
    }

    // 4. Dire au manager si ce hook est actif
    @Override
    public boolean isEnabled() {
        return initialized;
    }

    // 5. Injecter la donnée dans le JSON du joueur
    @Override
    public void appendPlayerStats(JsonObject po, Player p, FileConfiguration config) {
        // La vérification if(config.getBoolean("...")) est gérée nativement par le HookManager
        
        // (Simulons une récupération de donnée via Reflection)
        int points = 150; 
        
        // On l'ajoute au JSON !
        po.addProperty("magic_points", points);
    }
}
```

### Étape B : Enregistrer le Hook
Ouvrez `HookManager.java` et ajoutez votre classe dans la méthode `initAll()` :
```java
public static void initAll() {
    // ... les autres hooks
    register(new MagicSpellsHook()); // <- Et voilà !
}
```
**C'est terminé !** Compilez le projet. Le `config.yml` se mettra à jour tout seul et le JSON contiendra vos données.

## 4. L'Art de la Reflection (Éviter les Crashs)

MinelaunchedStats est conçu pour fonctionner même si le serveur n'a **aucun autre plugin d'installé**.
Règle d'or absolue : **Ne faites JAMAIS de `import` direct d'une API tierce !**

Si vous faites `import dev.magicspells.api.MagicAPI;`, et que le serveur n'a pas MagicSpells, le plugin crashera au démarrage avec une `NoClassDefFoundError`.

**Utilisez toujours l'API Reflection de Java** pour manipuler les plugins tiers dynamiquement.

*Exemple de Reflection correcte dans une méthode `init()` :*
```java
private Method getPointsMethod = null;

@Override
public void init() {
    if (Bukkit.getPluginManager().getPlugin("MagicSpells") != null) {
        try {
            // 1. Trouver la classe
            Class<?> apiClass = Class.forName("dev.magicspells.api.MagicAPI");
            
            // 2. Trouver la méthode getPoints(Player)
            getPointsMethod = apiClass.getMethod("getPoints", Player.class);
            
            initialized = true;
        } catch (Exception e) {
            // Échec silencieux
        }
    }
}

// Utilisation dans appendPlayerStats :
public int fetchPoints(Player p) {
    try {
        return (int) getPointsMethod.invoke(null, p);
    } catch (Exception e) { return 0; }
}
```

## 5. Le Cycle de Vie de la Configuration

Si vous vous demandez comment la magie du `config.yml` opère en arrière-plan, voici le flux d'exécution dans le `onEnable()` :

1. `saveDefaultConfig();` -> Copie le `config.yml` minimaliste situé dans le `.jar`.
2. `HookManager.registerAllDefaults(getConfig());` -> Demande à tous les `MinelaunchedHook` de déclarer leurs clés par défaut (ex: `config.addDefault("hooks.vault.enabled", true)`). Ces clés ne sont pour l'instant qu'en RAM.
3. `getConfig().options().copyDefaults(true);` -> Fusionne les valeurs en RAM avec celles présentes dans le fichier physique.
4. `saveConfig();` -> Sauvegarde le résultat final sur le disque. Le fichier `config.yml` est maintenant complet !
