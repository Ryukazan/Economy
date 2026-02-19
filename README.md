# Economy

A comprehensive and robust economy system for Hytale servers. This mod provides essential economy features including player balances, administration commands, and an easy-to-use API for other developers.

## Features
- **Player Wallet System**: Persistent balance tracking for players.
- **Admin Commands**: Easily manage player balances (give, take, set).
- **Console Support**: Execute admin commands directly from the server console.
- **Pay Command**: Allow players to transfer funds to each other safely.
- **API**: Simple `MoneyComponent` for integration with other mods.

## Commands

### Player Commands
- `/bal` or `/money` or `/wallet`: Check your current balance.
- `/pay <player> <amount>`: Send money to another player.

### Admin Commands (Requires Creative Mode or Permissions)
- `/ecoadmin give <player> <amount>`: Give money to a player.
- `/ecoadmin take <player> <amount>`: Remove money from a player.
- `/ecoadmin set <player> <amount>`: Set a player's balance.

## Installation
1. Stop your Hytale server.
2. Download the latest release `Economy-1.2.0.jar`.
3. Place the JAR file into your server's `mods` directory (create it if it doesn't exist).
4. Start the server. The mod should load automatically.

## Developer API (Maven)

To use the Economy mod API in your own project, add the following to your `build.gradle`:

```gradle
repositories {
    maven {
        name = 'repsy'
        url = uri("https://repo.repsy.io/yigit-guven/economy")
    }
}

dependencies {
    compileOnly 'com.yigitguven:economy:1.2.0'
}
```

## Troubleshooting
- **Player not found**: Ensure the player is online.
- **Console issues**: Admin commands are fully supported from the console in v1.2.0.

## Links
- [Source Code](https://github.com/yigit-guven/Economy)
- [Issues](https://github.com/yigit-guven/Economy/issues)
- [Wiki](https://github.com/yigit-guven/Economy/wiki)
- [CurseForge](https://www.curseforge.com/hytale/mods/economy)
- [Author Website](https://yigitguven.net/)

## License
Licensed under LGPL-3.0.
