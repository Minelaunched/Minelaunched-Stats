<div align="center">
  <h1>📊 MinelaunchedStats - Documentation Officielle</h1>
  <p><b>L'Extracteur de Statistiques Ultime pour Minecraft (1.8.8 - 1.20+)</b></p>
</div>

---

## 🌟 1. Introduction

**MinelaunchedStats** est un plugin Bukkit/Spigot/Paper ultra-optimisé conçu pour extraire absolument TOUTES les données de votre serveur Minecraft en temps réel, sans aucune base de données (MySQL/MongoDB).

Le plugin expose ces données via un **Serveur Web JSON embarqué** ou les diffuse via **Redis Pub/Sub**. Il est idéal pour créer des dashboards web, des bots Discord, ou monitorer l'économie d'un réseau BungeeCord.

---

## ⚡ 2. Fonctionnalités Clés

- **Plug & Play :** Glissez le `.jar`, redémarrez, et naviguez sur `http://votre-ip:8080/`. Le JSON est généré instantanément.
- **Zero-Touch Hooks :** Intègre nativement 48+ plugins (Vault, LuckPerms, Essentials...). La configuration s'auto-génère !
- **Ultra-Léger :** Ne cause **aucun lag** (TPS drop). Les appels API utilisent de la Reflection asynchrone et sécurisée.
- **Auto-Update OTA :** Se met à jour tout seul depuis GitHub avec un redémarrage intelligent quand le serveur est vide.

---

## 🚀 3. Installation & Configuration

1. Placez `MinelaunchedStats.jar` dans votre dossier `plugins/`.
2. Démarrez le serveur.
3. Ouvrez `plugins/MinelaunchedStats/config.yml`.

### Comprendre le `config.yml` "Auto-Généré"
Le fichier de configuration est conçu pour être minimaliste. Par défaut, il ne contient que les réglages de base (Port Web, Redis, Stats natives).
Cependant, **grâce à l'architecture Zero-Touch**, le plugin détectera automatiquement les autres plugins installés sur votre serveur (ex: Vault) et **rajoutera les options correspondantes tout seul** en bas du fichier !

```yaml
web:
  port: 8080
  endpoint: "/"
  pretty_print: false # Mettez sur 'true' pour un JSON lisible par l'humain
  cors_origin: "*"

redis:
  enabled: false
  host: "127.0.0.1"
  port: 6379
  channel: "minelaunched_stats"

auto_updater:
  enabled: true
  smart_restart: true # Redémarre à 4h du matin si 0 joueur

# ... (Les hooks comme Vault, LuckPerms s'ajouteront tout seuls ici) ...
```

---

## 📡 4. Réseaux & Exportation

Vous avez deux moyens d'exploiter les statistiques :

### A. Le Serveur Web Interne (Mode PULL)
Le plugin lance un micro-serveur HTTP sur le port `8080` (modifiable). 
Vous pouvez faire une requête `GET http://ip:8080/` en JavaScript depuis votre site web :
```javascript
fetch('http://monserveur.fr:8080/')
  .then(res => res.json())
  .then(data => console.log("Joueurs en ligne :", data.players.online_count));
```

### B. Redis Pub/Sub (Mode PUSH)
Si activé, le plugin pousse le JSON complet dans le channel Redis `minelaunched_stats` toutes les `update_interval_seconds`. Idéal pour synchroniser 50 serveurs survie vers un seul bot Discord !

---

## 🛠️ 5. Guide Développeur (Créer un Hook)

MinelaunchedStats possède l'architecture la plus propre possible. Pour ajouter la compatibilité avec un nouveau plugin, **vous n'avez pas besoin de toucher au fichier de configuration ni aux collecteurs de stats.**

Vous avez juste à créer **1 Fichier** !

### Étape 1 : Créer la classe
Allez dans `com.minelaunched.stats.hooks` et créez une classe qui étend `MinelaunchedHook` :

```java
public class MonPluginHook extends MinelaunchedHook {
    private boolean enabled = false;

    @Override
    public String getPluginName() {
        return "monplugin"; // Nom dans le config.yml
    }

    @Override
    public List<String> getExportKeys() {
        return Arrays.asList("niveau_magie", "points_vip"); // Auto-génère les clés config !
    }

    @Override
    public void init() {
        if (Bukkit.getPluginManager().getPlugin("MonPlugin") != null) {
            enabled = true; // Activer le hook via Reflection
        }
    }

    @Override
    public boolean isEnabled() { return enabled; }

    @Override
    public void appendPlayerStats(JsonObject playerData, Player p, FileConfiguration config) {
        // Ajout des stats dans le JSON
        playerData.addProperty("niveau_magie", 99);
    }
}
```

### Étape 2 : L'enregistrer
Ouvrez `HookManager.java` et ajoutez votre hook à la liste :
```java
public static void initAll() {
    register(new MonPluginHook());
    // Le plugin fera le reste !
}
```

---

## 🔌 6. Plugins Supportés (48+)

MinelaunchedStats extrait nativement les données de tous ces plugins (si installés) :

- **Économie :** Vault, PlayerPoints, TokenManager, GemsEconomy, BeastTokens, JobsReborn.
- **Rôles & Staff :** LuckPerms, EssentialsX, CMI, SuperVanish, LiteBans, AdvancedBan.
- **Survie & RPG :** mcMMO, AuraSkills, AureliumSkills, Slimefun, MyPet, Quests, Vampire.
- **Factions & Claims :** Towny, Lands, FactionsUUID, GriefPrevention, SimpleClans, GriefDefender.
- **Skyblock :** SuperiorSkyblock2, BentoBox, IridiumSkyblock.
- **Anti-Cheats :** Vulcan, Spartan, GrimAC, Matrix.
- **Cosmétiques :** DeluxeTags, AlonsoTags, SkinsRestorer, CrazyCrates, ExcellentCrates.
- **Divers :** PlaceholderAPI, ViaVersion, Floodgate, Geyser, DiscordSRV, CombatLogX, PvPManager, Citizens, VotingPlugin, BountyHunters, MarriageMaster.

Chaque intégration est sécurisée : si le plugin ciblé crashe ou n'est pas à jour, MinelaunchedStats l'ignore gracieusement.

---

## ⚖️ 7. Commandes & Permissions

- `/mlstats reload` : Recharge la configuration `config.yml` et relance le Serveur Web et la connexion Redis à chaud (sans reboot le serveur).
- **Permission :** `mlstats.admin`
