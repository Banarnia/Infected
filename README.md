# Infected
Spigot-Plugin that adds an Infected game mode.
Tested and create for Spigot 1.19.2.

This plugin adds a command that **infects** a certain player. This player will get **configurable potion effects** and will stay infected for a certain time. Within this time he can infect other player within a given range of blocks around him.
After the infection has been cured, by expiring or drinking milk, he will be protected from further infection for a certain time.

# Note
This plugin was created on request with given requirements. If you find any bugs or want to request a feature feel free to create an issue.

# Commands & Permissions
Admin commands require permission: ``infected.admin``
```
/infected infect <Player> - Infects the player.
/infected cure <Player> - Cures the player from the infection.
/infected reload - Reloads the configuration.
```

# Configuration
Two files for configuration will be created:
```
config.yml - Adds configuration options.
messages.yml - Config file for all messages.
```

## config.yml
```
infection-time-seconds - Specify how long a player gets infected.
effects - Configure the effects a player should get.
infection-radius - Range to infect other players.
protection-time-seconds - Amount of seconds a player will stay protected after he cured.
infection-check-time-ticks - Delay, in ticks, between every scan around infected players.
infection-while-in-air - Enable/Disable infection if a player is not on ground.
glow-enabled - Make infected players glow.
```
