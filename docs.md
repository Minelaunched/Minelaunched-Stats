<div align="center">
  <h1>📊 MinelaunchedStats - La Documentation Complète et Exhaustive</h1>
  <p><b>Le Guide Ultime : Administrateurs & Développeurs</b></p>
  <p><i>Compatible de la version 1.8.8 jusqu'aux dernières versions (Spigot, Paper, Purpur, Pufferfish, Folia)</i></p>
  <p><i>Version de la documentation : 2.0 (Architecture Zero-Touch)</i></p>
</div>

---

# 📚 Table des Matières

## PARTIE I : GUIDE UTILISATEUR (ADMINISTRATEURS)
1. **[Introduction et Philosophie](#1-introduction-et-philosophie)**
   - Qu'est-ce que MinelaunchedStats ?
   - Pourquoi le JSON est supérieur aux bases de données
   - Performances et Impact Serveur
2. **[Installation et Prérequis](#2-installation-et-prérequis)**
   - Systèmes supportés
   - Guide d'installation pas-à-pas
3. **[Analyse Profonde du `config.yml`](#3-analyse-profonde-du-configyml)**
   - Le serveur Web embarqué
   - Le pont Redis Pub/Sub
   - L'Auto-Updater intelligent
   - Le système de Configuration Magique (Auto-Generated Hooks)
4. **[Dictionnaire des Données Natives Exportées](#4-dictionnaire-des-données-natives-exportées)**
   - 🖥️ Système (RAM, CPU, Threads, Garbage Collectors)
   - 🌍 Serveur (TPS, Joueurs max, Version, Propriétés)
   - 🗺️ Mondes (Entités, Météo, Chunks, Graines)
   - 👥 Joueurs (Inventaires complets, Ping, Statistiques de jeu)
   - 🔌 Plugins (Liste, Auteurs, Versions)
5. **[Les 100+ Intégrations Tierces (Hooks)](#5-les-100-intégrations-tierces-hooks)**
   - Économie & Monnaies
   - Rôles, Staff & Modération
   - Survie, RPG & Skyblock
   - Factions & Systèmes de Claims
   - Cosmétiques & Divers
6. **[Tutoriels d'Exploitation (Code)](#6-tutoriels-dexploitation-code)**
   - Exemple 1 : Créer un Dashboard Web en HTML/JS
   - Exemple 2 : Créer un Bot Discord en Python
   - Exemple 3 : Écouter le serveur via Redis avec Node.js

## PARTIE II : GUIDE DÉVELOPPEUR (CONTRIBUTEURS)
7. **[Architecture du Code (Clean Architecture)](#7-architecture-du-code-clean-architecture)**
   - La Philosophie "Plug & Play"
   - Structure des packages Java
8. **[Le Système Révolutionnaire "Zero-Touch Hook"](#8-le-système-révolutionnaire-zero-touch-hook)**
   - Pourquoi cette architecture ?
   - Le cycle de vie de la configuration (Comment les valeurs s'auto-injectent)
9. **[Tutoriel : Créer un Hook Parfait en 3 Minutes](#9-tutoriel--créer-un-hook-parfait-en-3-minutes)**
   - Étape 1 : Création de la classe
   - Étape 2 : L'Art de la Reflection (Règle d'Or)
   - Étape 3 : Enregistrement dans le `HookManager`
10. **[Plongée dans les Collecteurs Natifs](#10-plongée-dans-les-collecteurs-natifs)**
11. **[Compilation et Contribution](#11-compilation-et-contribution)**
12. **[FAQ & Résolution des problèmes](#12-faq--résolution-des-problèmes)**

---

# PARTIE I : GUIDE UTILISATEUR (ADMINISTRATEURS)

## 1. Introduction et Philosophie

### Qu'est-ce que MinelaunchedStats ?
**MinelaunchedStats** est le plugin ultime de télémétrie pour Minecraft. Son objectif est de scanner l'intégralité de la mémoire de votre serveur (de la version de Java utilisée jusqu'à la durabilité des bottes en cuir portées par un joueur) et de convertir toutes ces informations en un immense objet **JSON**.

### Pourquoi le JSON est supérieur aux bases de données SQL
La majorité des plugins de statistiques utilisent MySQL. C'est une erreur architecturale lourde pour la télémétrie en temps réel :
1. **Lags (Chutes de TPS) :** L'écriture constante dans une base de données MySQL bloque très souvent le thread principal du serveur si elle est mal gérée.
2. **Schémas Rigides :** Si un joueur a 36 objets dans son inventaire, comment modélisez-vous cela dans une table SQL proprement ? Cela nécessite des dizaines de tables croisées.
3. **Poids :** Les bases de données grossissent indéfiniment.

MinelaunchedStats adopte l'approche **Stateless (Sans état)**. Il ne sauvegarde rien sur le disque. Lorsqu'une requête est faite au plugin, il lit la RAM instantanément et génère du texte (JSON). C'est des milliers de fois plus rapide, cela ne pèse rien sur le disque dur, et c'est universellement lisible par n'importe quel langage de programmation moderne.

### Performances et Impact Serveur
Le plugin a été écrit avec une obsession pour les performances :
- L'assemblage du JSON complet pour un serveur de 100 joueurs prend en moyenne **moins de 2 millisecondes**.
- L'utilisation de threads asynchrones via NanoHTTPD garantit que les requêtes Web n'affectent **JAMAIS** les TPS du serveur.

---

## 2. Installation et Prérequis

### Systèmes Supportés
- **Versions Minecraft :** 1.8.8 jusqu'à 1.20.4 et au-delà.
- **Logiciels Serveurs :** Spigot, Paper, Purpur, Pufferfish, Folia, et tous les forks basés sur l'API Bukkit.
- **Java :** Java 8, 11, 17, ou 21.

### Guide d'installation pas-à-pas
1. Téléchargez la dernière version compilée de `MinelaunchedStats.jar`.
2. Placez le fichier dans le dossier `plugins/` de votre serveur Minecraft.
3. Démarrez (ou redémarrez) votre serveur.
4. Dans la console, vous verrez `[MinelaunchedStats] MinelaunchedStats enabled and listening on port 8080!`.
5. Ouvrez un navigateur web et tapez `http://ip_de_votre_serveur:8080/` pour visualiser vos données instantanément !

**Commandes utiles :**
- `/mlstats reload` : Recharge le fichier `config.yml`, redémarre le serveur Web interne et relance les connexions Redis. Ne nécessite pas de redémarrer le serveur Minecraft entier. (Nécessite la permission `mlstats.admin`).

---

## 3. Analyse Profonde du `config.yml`

Le fichier de configuration est le cœur du plugin. Il a été conçu pour être le plus minimaliste possible.

### Le Serveur Web Embarqué (Mode PULL)
Le plugin contient son propre serveur Web (basé sur NanoHTTPD). Pas besoin d'Apache ou Nginx.
```yaml
web:
  port: 8080
  endpoint: "/"
  pretty_print: false
  cors_origin: "*"
```
- `port`: Le port sur lequel le serveur écoute (ouvrez ce port sur votre machine/hébergeur).
- `pretty_print`: Si mis sur `true`, le JSON sera formaté avec des espaces et des retours à la ligne (plus facile à lire à l'œil nu, mais plus lourd à télécharger). Recommandé de laisser sur `false` en production.
- `cors_origin`: `*` autorise n'importe quel site web (comme un Dashboard local) à faire des requêtes AJAX vers ce serveur.

### Le Pont Redis Pub/Sub (Mode PUSH)
Si vous possédez un réseau BungeeCord avec des dizaines de serveurs, vous ne voulez pas faire de requêtes HTTP à chaque serveur. Vous voulez qu'ils "crient" leurs données dans un haut-parleur central. Ce haut-parleur, c'est Redis.
```yaml
redis:
  enabled: false
  host: "127.0.0.1"
  port: 6379
  password: ""
  channel: "minelaunched_stats"
  update_interval_seconds: 5
```
Si activé, le plugin se connectera à Redis et publiera le JSON complet sur le salon `minelaunched_stats` toutes les `update_interval_seconds` secondes. Un seul script Node.js centralisé pourra alors écouter ce salon et recevoir les données de tous vos serveurs en temps réel !

### L'Auto-Updater Intelligent
```yaml
auto_updater:
  enabled: true
  github_repo: "Minelaunched/Minelaunched-Stats"
  smart_restart: true
```
Le plugin est capable de se mettre à jour tout seul. À chaque démarrage, il interroge l'API GitHub de votre repository. Si une nouvelle release (`.jar`) est détectée, il la télécharge dans le dossier `plugins/update/`.
Si `smart_restart: true` est activé, le plugin ordonnera un redémarrage complet du serveur (via la commande Bukkit `/restart`) **uniquement lorsqu'il y aura 0 joueur en ligne** (ex: la nuit).

### La Configuration Magique (Auto-Generated Hooks)
L'ancien `config.yml` faisait plus de 300 lignes car il listait tous les plugins supportés manuellement.
Avec l'architecture **Zero-Touch**, c'est de l'histoire ancienne !

Par défaut, le fichier de configuration ne contient AUCUN hook. Mais au démarrage, le plugin scanne votre serveur. S'il détecte que vous avez installé `Vault` et `ViaVersion`, il **écrira lui-même** les lignes suivantes à la fin de votre fichier `config.yml` :
```yaml
hooks:
  vault:
    enabled: true
    export_server_economy: true
    export_player_balance: true
  viaversion:
    enabled: true
    export_protocol_version: true
```
Cela signifie que la configuration s'adapte automatiquement à votre serveur. De plus, **si vous désinstallez un plugin**, MinelaunchedStats s'en rendra compte et supprimera lui-même les anciennes configurations de votre `config.yml` pour le garder propre !
Vous pouvez désactiver l'exportation d'une donnée spécifique en changeant `true` en `false`. Si vous mettez `export_player_balance: false`, l'argent des joueurs ne sera plus exporté, réduisant ainsi la taille du JSON et le temps de calcul.

---

## 4. Dictionnaire des Données Natives Exportées

Cette section détaille tout ce que le plugin exporte nativement, sans nécessiter d'autres plugins. Ces paramètres sont gérés dans la section `stats:` du `config.yml`.

### 🖥️ Système (`system`)
La télémétrie matérielle et logicielle de la machine hôte :
- `os_name`, `os_version`, `os_arch` : Informations sur le système d'exploitation (Linux, Windows).
- `cores` : Le nombre de cœurs logiques du processeur.
- `cpu_load` et `process_cpu_load` : L'utilisation globale du CPU et l'utilisation spécifique du serveur Minecraft (en pourcentage).
- `java_version`, `java_vendor` : La version de la JVM.
- `ram_max_mb`, `ram_total_mb`, `ram_free_mb`, `ram_used_mb` : Monitoring précis de la RAM (Heap).
- `disk_total_mb`, `disk_usable_mb` : L'espace de stockage restant sur la machine.
- `thread_count`, `peak_thread_count` : L'état du multi-threading.
- `garbage_collectors` : Le nombre et le temps passé par chaque ramasse-miettes (G1GC, ZGC, Shenandoah) pour détecter les micro-lags.

### 🌍 Serveur (`server`)
L'état global de l'instance Bukkit :
- `tps` : Les *Ticks Per Second* exacts. Un indicateur critique de lag (doit être proche de 20.0).
- `online_mode`, `is_hardcore`, `whitelist_enabled` : Les paramètres du `server.properties`.
- `banned_ips_count`, `banned_names_count` : Le nombre d'infractions enregistrées.
- `version`, `bukkit_version` : La version exacte du logiciel (ex: Paper 1.20.4).
- `animal_spawn_limit`, `monster_spawn_limit` : Les limites d'apparition configurées.

### 🗺️ Mondes (`worlds`)
Un tableau JSON contenant les statistiques de **chaque monde chargé** (`world`, `world_nether`, `world_the_end`, mondes customs) :
- `loaded_chunks` : Le nombre de chunks gardés en mémoire.
- `entities`, `living_entities`, `players` : Le nombre exact d'entités présentes dans ce monde.
- `time`, `full_time` : L'heure ingame.
- `has_storm`, `is_thundering` : L'état de la météo.
- `seed` : La graine de génération.
- `difficulty` : Paisible, Facile, Normal, Difficile.

### 👥 Joueurs (`players`)
Un tableau JSON extrêmement massif contenant les informations de **chaque joueur connecté en direct** :
- `ping` : La latence du joueur avec le serveur.
- `health`, `food`, `saturation` : L'état vital du joueur.
- `location` : Les coordonnées exactes X, Y, Z, et le nom du monde.
- `is_flying`, `is_sneaking`, `gamemode` : L'état de mouvement.
- `first_played_ms`, `last_played_ms` : Les timestamps de connexion.
- **`inventory` et `armor` :** Une extraction complète de l'inventaire du joueur ! Chaque objet contient son ID, sa quantité, et sa durabilité. Parfait pour faire un système d'inventaire Web (Web-Inventory).
- **`game_statistics` :** Tous les "Succès/Statistiques" de Minecraft vanilla (nombre de blocs de terre posés, nombre de zombies tués, nombre de fois où le joueur a sauté).

### 🔌 Plugins (`plugins`)
Une liste de tous les plugins installés et activés sur le serveur, avec leur version, auteur, et description. Idéal pour monitorer les versions à travers un grand réseau.

---

## 5. Les 100+ Intégrations Tierces (Hooks)

La force monumentale de MinelaunchedStats réside dans son interopérabilité avec presque tous les plugins majeurs de l'écosystème Minecraft.

Si l'un des plugins suivants est détecté sur votre serveur, ses données seront automatiquement imbriquées dans le JSON natif !

### 💰 Économie & Monnaies
- **Vault :** Exporte le solde bancaire des joueurs (`vault_balance`) et calcule la masse monétaire totale du serveur (Inflation, `vault_total_economy`).
- **PlayerPoints :** Exporte le solde de monnaie premium (Gemmes/Points).
- **TokenManager / GemsEconomy / BeastTokens :** Support complet de toutes les monnaies alternatives courantes.
- **JobsReborn :** Exporte la liste des métiers actifs du joueur et leur niveau exact.
- **BountyHunters :** Exporte la valeur de la prime active sur la tête d'un joueur.

### 🛡️ Rôles, Staff & Modération
- **LuckPerms :** Exporte le Grade Primaire (`primary_group`), le préfixe et le suffixe du joueur. Idéal pour afficher le grade sur le site Web !
- **EssentialsX / CMI :** Exporte l'état en direct du joueur (`is_afk`, `is_vanished`, `is_god_mode`, `is_muted`, `nickname`).
- **SuperVanish / PremiumVanish :** Détecte si un administrateur est invisible, pour pouvoir par exemple le cacher sur la "LiveMap" du site web !
- **LiteBans / AdvancedBan :** Exporte le statut de bannissement (`is_banned`).
- **DiscordSRV :** Exporte l'identifiant Discord lié au joueur (`discord_id`).
- **Citizens :** Permet de savoir si une entité "Joueur" est en réalité un faux joueur (NPC). Permet d'ignorer les bots dans les statistiques !

### ⚔️ Survie, RPG & Skyblock
- **mcMMO :** Exporte le niveau de puissance total du joueur (`power_level`).
- **AuraSkills / AureliumSkills :** Exporte le mana en direct (`mana`) et le niveau RPG global.
- **Slimefun :** Exporte le nombre de recherches scientifiques débloquées.
- **MyPet :** Exporte le nom du familier actif.
- **Vampire :** Exporte l'état d'infection (`is_vampire`).
- **Quests :** Exporte le nombre de quêtes complétées.
- **SuperiorSkyblock2 / BentoBox / IridiumSkyblock :** Exporte le niveau et le nom de l'île Skyblock du joueur !

### 🏰 Factions & Systèmes de Claims
- **Towny :** Exporte la ville (`town`), la nation (`nation`) et le titre (`title`) du joueur.
- **FactionsUUID :** Exporte le nom de la faction et le rôle du joueur (Leader, Membre).
- **Lands :** Exporte le nombre de territoires possédés.
- **GriefPrevention / GriefDefender :** Exporte les blocs de protection disponibles (`claim_blocks`, `bonus_blocks`).
- **SimpleClans :** Exporte le nom du clan et son tag.

### 🛑 Anti-Cheats & PvP
- **Vulcan / Spartan / GrimAC / Matrix :** Exporte le nombre d'alertes ou de violations (`violations`). Permet aux admins de créer un Dashboard Discord d'alerte !
- **CombatLogX / PvPManager :** Exporte un booléen `is_in_combat` si le joueur est actuellement en plein combat PvP.

### 🎨 Cosmétiques, Clients & Divers
- **PlaceholderAPI :** Exécute tous les placeholders que vous définissez dans la config (ex: `%server_uptime%`) et exporte le résultat textuel !
- **ViaVersion :** Exporte la version exacte du protocole client (ex: Joueur sur 1.18.2).
- **Geyser / Floodgate :** Exporte si le joueur vient du jeu Bedrock (téléphone, console) et donne même son OS (`device_os`: iOS, Android, Xbox).
- **DeluxeTags / AlonsoTags :** Exporte le Tag chat cosmétique actif.
- **SkinsRestorer :** Exporte le nom du skin que le joueur a volé/appliqué.
- **CrazyCrates / ExcellentCrates :** Exporte le nombre de clés virtuelles possédées.
- **MarriageMaster :** Exporte le nom du mari/de la femme du joueur.
- **VotingPlugin :** Exporte le nombre total de votes effectués par le joueur.

---

## 6. Tutoriels d'Exploitation (Code)

Maintenant que le plugin tourne, comment utiliser cette montagne de données JSON ? Voici 3 exemples pratiques.

### Exemple 1 : Dashboard Web (HTML / Vanilla JavaScript)
Créez un fichier `index.html` sur votre PC. Remplacez l'IP par celle de votre serveur.

```html
<!DOCTYPE html>
<html>
<head>
    <title>Mon Serveur Live</title>
</head>
<body>
    <h1>Statistiques en direct</h1>
    <p>Joueurs connectés : <b id="onlineCount">Chargement...</b> / <b id="maxPlayers">?</b></p>
    <p>TPS : <b id="tps">Chargement...</b></p>

    <script>
        async function fetchStats() {
            try {
                // Remplacez 127.0.0.1 par l'IP publique de votre serveur
                const response = await fetch('http://127.0.0.1:8080/'); 
                const data = await response.json();
                
                document.getElementById('onlineCount').innerText = data.players.online_count;
                document.getElementById('maxPlayers').innerText = data.server.max_players;
                document.getElementById('tps').innerText = data.server.tps;
            } catch (error) {
                console.error("Serveur éteint ou inaccessible");
            }
        }
        
        // Mettre à jour toutes les 5 secondes
        setInterval(fetchStats, 5000);
        fetchStats();
    </script>
</body>
</html>
```

### Exemple 2 : Bot Discord en Python (Requêtes PULL)
Ce script simple nécessite la librairie `requests`.

```python
import requests

def print_server_status():
    try:
        url = "http://127.0.0.1:8080/"
        response = requests.get(url)
        data = response.json()
        
        print(f"--- ETAT DU SERVEUR ---")
        print(f"TPS: {data['server']['tps']}")
        print(f"RAM Utilisée: {data['system']['ram_used_mb']} MB")
        
        print("--- JOUEURS EN LIGNE ---")
        for player in data['players']['list']:
            name = player['name']
            ping = player['ping']
            money = player.get('vault_balance', 0)
            
            print(f"- {name} (Ping: {ping}ms) | Argent: {money}$")
            
    except Exception as e:
        print("Impossible de se connecter au serveur MinelaunchedStats.")

print_server_status()
```

### Exemple 3 : Écouteur Redis en Node.js (Mode PUSH)
Si Redis est activé dans le `config.yml`, le serveur publiera le JSON sans que vous ayez à le demander !

```javascript
const redis = require('redis');

// Remplacez par les infos de votre base de données Redis
const subscriber = redis.createClient({ host: '127.0.0.1', port: 6379 });

subscriber.on('message', (channel, message) => {
    if (channel === 'minelaunched_stats') {
        const data = JSON.parse(message);
        console.log(`[NOUVELLE DONNÉE] TPS du serveur : ${data.server.tps}`);
        
        // Vous pouvez sauvegarder ce JSON dans une base MongoDB, 
        // l'envoyer par websocket à un client React, etc.
    }
});

subscriber.subscribe('minelaunched_stats');
console.log("En attente des données des serveurs Minecraft...");
```

---

# PARTIE II : GUIDE DÉVELOPPEUR (CONTRIBUTEURS)

MinelaunchedStats est construit selon les principes rigoureux de l'ingénierie logicielle (SOLID, Clean Architecture).

## 7. Architecture du Code (Clean Architecture)

L'arborescence globale du code :
- **`com.minelaunched.stats`** : Le point d'entrée central (`MinelaunchedStatsPlugin.java`). Il gère l'initialisation de la config et le démarrage asynchrone des services.
- **`com.minelaunched.stats.collectors`** : Gère l'extraction native des données brutes de l'API Bukkit (`PlayerCollector.java`, `ServerCollector.java`). C'est le moteur de recherche.
- **`com.minelaunched.stats.network`** : Les protocoles de sortie de l'information (`WebServer.java` via NanoHTTPD et `RedisManager.java` via Jedis).
- **`com.minelaunched.stats.utils`** : Les outils indépendants (comme le `TpsTracker` asynchrone et l'`AutoUpdater`).
- **`com.minelaunched.stats.hooks`** : L'écosystème d'intégration des plugins tiers. C'est la zone la plus fascinante de l'architecture.

## 8. Le Système Révolutionnaire "Zero-Touch Hook"

L'architecture traditionnelle (dite du plat de spaghettis) consiste à créer un énorme fichier avec 50 conditions `if (pluginX_actif) { ... } else if (pluginY_actif) { ... }`.
C'est inmaintenable, propice aux erreurs de fusion Git, et horrible à configurer.

**MinelaunchedStats utilise l'architecture "Zero-Touch".**
Dans ce modèle :
1. Chaque intégration est **isolée** dans sa propre classe (ex: `VaultHook.java`).
2. Chaque classe hérite du contrat `MinelaunchedHook`.
3. Le développeur ne touche **JAMAIS** au fichier `config.yml`. Le Hook possède la responsabilité de déclarer *lui-même* les clés de configuration dont il a besoin !

### Le Cycle de Vie de la Configuration
Voici ce qu'il se passe lors du `onEnable()` :
1. Le plugin copie le `config.yml` minimaliste (sans les hooks) présent dans le `.jar`.
2. Le `HookManager` instancie les 100+ hooks en mémoire.
3. Le `HookManager` appelle la méthode native `registerDefaults(config)` de chaque hook. Le Hook insère ses chemins YAML directement dans la mémoire de Bukkit (via `config.addDefault()`).
4. Le plugin exécute `config.options().copyDefaults(true)` et `saveConfig()`. Bukkit va fusionner les valeurs injectées en RAM avec celles du fichier physique !
5. Résultat : La configuration s'auto-génère proprement en bas du fichier, sans intervention humaine.

---

## 9. Tutoriel : Créer un Hook Parfait en 3 Minutes

Vous avez créé un plugin qui s'appelle `SuperCustomPlugin` et vous voulez que ses statistiques soient exportées dans le JSON universel ? Voici la marche à suivre.

### Étape 1 : Créer la classe du Hook
Dans `src/main/java/com/minelaunched/stats/hooks/`, créez `SuperCustomHook.java`.

```java
package com.minelaunched.stats.hooks;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class SuperCustomHook extends MinelaunchedHook {

    private boolean initialized = false;

    /**
     * 1. Définir le nom "racine" pour la config.
     * Génèrera -> hooks.supercustomplugin.enabled: true
     */
    @Override
    public String getPluginName() {
        return "supercustomplugin";
    }

    /**
     * 2. Définir les sous-propriétés que l'admin peut activer/désactiver.
     * Génèrera -> hooks.supercustomplugin.export_niveau_magie: true
     */
    @Override
    public List<String> getExportKeys() {
        return Arrays.asList("niveau_magie");
    }

    /**
     * 3. Initialiser le hook (Vérifier si le plugin est là)
     */
    @Override
    public void init() {
        if (Bukkit.getPluginManager().getPlugin("SuperCustomPlugin") != null) {
            // (Insérer ici la Reflection si nécessaire)
            initialized = true;
            Bukkit.getLogger().info("[MinelaunchedStats] Hooks: SuperCustomPlugin détecté !");
        }
    }

    /**
     * 4. Statut d'activation
     */
    @Override
    public boolean isEnabled() {
        return initialized;
    }

    /**
     * 5. Injecter la donnée par joueur
     */
    @Override
    public void appendPlayerStats(JsonObject playerData, Player p, FileConfiguration config) {
        // Obtenir la donnée (ex: un niveau de 42)
        int niveau = 42;
        
        // Ajouter la propriété au JSON du joueur !
        playerData.addProperty("supercustom_niveau", niveau);
        
        // Note : Vous n'avez pas besoin de vérifier le boolean "export_niveau_magie".
        // Le système `config` est passé si vous avez besoin de validations ultra-précises, 
        // mais le fait que l'admin active/désactive le hook en entier est déjà 
        // géré par la classe parente HookManager !
    }
}
```

### Étape 2 : L'Art de la Reflection (Règle d'Or)
Dans l'exemple ci-dessus, j'ai mis `int niveau = 42`.
Mais si vous devez utiliser l'API du plugin tiers, **N'UTILISEZ JAMAIS D'IMPORT DIRECT**.

```java
// ❌ CRASH GARANTI (Si le plugin n'est pas installé sur le serveur, Java panique avant même de lire le "if")
import fr.monplugin.SuperAPI;
int niveau = SuperAPI.getNiveau(p);

// ✅ SAFE REFLECTION (Java lit la classe en temps réel de manière prudente)
Class<?> apiClass = Class.forName("fr.monplugin.SuperAPI");
Method getMethod = apiClass.getMethod("getNiveau", Player.class);
int niveau = (int) getMethod.invoke(null, p);
```
Mettez en cache vos objets `Method` et `Class<?>` dans la fonction `init()` pour que l'appel `invoke()` dans `appendPlayerStats` prenne moins d'une nanoseconde !

### Étape 3 : Enregistrement dans le `HookManager`
Dernière étape. Ouvrez le fichier `HookManager.java`. Allez dans la méthode `initAll()` et ajoutez votre nouvelle classe :

```java
public static void initAll() {
    register(new VaultHook());
    register(new PapiHook());
    // ...
    register(new SuperCustomHook()); // C'est fait !
    
    for (MinelaunchedHook hook : registeredHooks) {
        hook.init();
    }
}
```
Compilez avec Maven, et votre hook est universellement prêt !

---

## 10. Plongée dans les Collecteurs Natifs

Si vous voulez modifier les données natives (ex: ajouter le niveau d'XP de la main gauche), vous devez modifier le package `collectors`.
Le point d'entrée est `StatsCollector.java` : il crée l'objet JSON racine `{}` et appelle les collecteurs enfants.

- **`PlayerCollector.java` :** Le plus massif. Il boucle sur `Bukkit.getOnlinePlayers()`. Modifiez ce fichier pour rajouter des attributs propres aux joueurs.
- **`WorldCollector.java` :** Récupère les données climatiques et d'entités des mondes.
- **`SystemCollector.java` :** Utilise `java.lang.management` (JMX) pour analyser l'OS et la JVM de l'hôte.

La méthode utilitaire `CollectorUtils.addSafe()` est utilisée massivement pour éviter qu'une méthode qui a changé entre la version 1.8.8 et 1.20 (comme `player.getLocale()`) ne fasse crasher toute la récolte de statistiques en cas de `NoSuchMethodError`.

---

## 11. Compilation et Contribution

Le projet se gère via **Apache Maven**.

1. Clonez le dépôt : `git clone https://github.com/Minelaunched/Minelaunched-Stats.git`
2. Compilez le projet : `mvn clean package`
3. Le fichier final se trouvera dans `target/MinelaunchedStats-x.x.x-shaded.jar`.

Notez que le plugin utilise le plugin Maven **maven-shade-plugin** pour embarquer les bibliothèques suivantes directement dans le `.jar` :
- `Gson` (Manipulation JSON)
- `Jedis` et `commons-pool2` (Client Redis)
- L'API NanoHTTPD (Serveur Web) est incluse nativement dans le code source via une classe utilitaire.

---

## 12. FAQ & Résolution des problèmes

**Q : Mon serveur web indique "Address already in use" dans la console.**
R : Le port `8080` est déjà utilisé par un autre logiciel sur votre machine. Changez le port dans le `config.yml`.

**Q : Mon JSON affiche des erreurs de caractères accentués (Ã©) !**
R : NanoHTTPD gère l'UTF-8 de manière stricte. Assurez-vous que le navigateur avec lequel vous lisez le JSON traite l'encodage comme UTF-8.

**Q : L'Auto-Updater crashe en téléchargeant la Release.**
R : Vérifiez que le repository GitHub est bien public ou que la configuration `github_repo` est correcte. Notez que GitHub limite parfois les requêtes anonymes (Rate Limit).

---

<div align="center">
  <p>Créé avec ❤️ par la communauté Minelaunched.</p>
</div>
