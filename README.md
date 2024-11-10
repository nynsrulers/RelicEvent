# Relic Event!
The Relic Event on the Not-yet-named Server!

## Introduction
The Relic Event is a simple event plugin built for Minecraft servers.
It is very vanilla-friendly, though it only works on SpigotMC servers (and forks).
This plugin was originally built in only eleven hours (7 PM to 6 AM, nearly non-stop coding).

## Setup
This simple guide will help anyone set up the plugin.
1. Download the latest plugin release and upload it to your server's `plugins` folder, then restart your server.
    - Note that the plugin will only run if your server is using the SpigotMC software or forks of it (such as Paper).
    - Also, make sure to install the [NBTAPI](https://modrinth.com/plugin/nbtapi) plugin, as it is a required dependency.
2. In your server's permission system, grant the `relicevent.admin` permission to server admins.
    - Alternatively, grant operator status (`/op`) to those admins.
    - This is required to use the `/relicmgr` command or place relics.
3. When logged into the server, use the `/relicmgr giveblock` command from an admin account.
    - We suggest using creative mode from here on, as in survival this block is consumed when a relic is placed.
4. Travel to many areas around the server, and place relics using the Relic Block.
5. Place a gold block at your server's spawn, and get its coordinates. Put these in the configuration file.
    - A command is coming soon to do this!
    - Players will have to bring their relics here to redeem them.
6. (Optional) Go to a Discord server and [create a Webhook](https://support.discord.com/hc/en-us/articles/228383668-Intro-to-Webhooks). Then, put the 
Webhook URL in the plugin configuration.
   - This will send a message to the Discord server whenever a relic is claimed.
   - Leaving this set to the default (CHANGEME) will disable the feature.
7. Reload the plugin with the `/relicmgr reload` command.
   - If you enabled a Discord webhook (in Step 6), you can now test it with the `/relicmgr testdiscord` command.
8. Run the `/relicmgr status on` command, announce the event to your server, and watch the hunt begin!

## Features (detailed list)
- User-friendly command (`/relic`, used to check stats)
- Simple leaderboard available to all (`/relic lb`)
- Easy-to-use administration command (`/relicmgr`)
    - Note that, at present, this command's responses are very cute :3
- Event can be disabled, preventing collection and redemption of relics.
    - This is best used during setup, but also allows breaks in the event.
- Simple, readable data storage (no database required!)
- Easy-to-use configuration file
- Relic holding limits (players can only redeem one relic at a time) (toggleable with `EnforceHoldingLimit` in `config.yml`)
- Relic trading prevention (relics can only be redeemed by the player who collected them) (toggleable with `EnforceNoTrading` in `config.yml`)
- Discord Webhook integration (sends a message when a relic is claimed)
- As well as many more!

## Planned Features
All of these are coming soon!
- Relic removal (currently the only way to remove a relic is to claim it)
- Command to pick a redemption spot (right now the coordinates have to be set manually)
- Command to randomly place relics around the server (will not work in the nether and end due to limitations)
- Particle effects around the redemption spot
- Auto-spawning relic defenders (armored zombies and skeletons that will fight anyone trying to claim a relic)
- Claim delays (people will have to wait a certain amount of time near the relic to claim it)
- Customizable messages (edit a `lang.yml` file to change message text)

## Development
This guide is for developers looking to modify/build the plugin.
### Building
1. Download the source code from this repository.
2. Make sure you have Java 21 installed on your system.
3. Open a terminal and navigate to the plugin's directory.
4. Run `./gradlew build`. (If on Windows, use `gradlew.bat build` instead)
5. Find the finished plugin in `build/libs` and upload it to your server's `plugins` folder.
6. Restart your server, and follow the steps from the setup guide.
### Contributing
1. Fork the repository.
2. Follow the "Building" guide right above.
3. Make any changes you are interested in adding.
4. Submit a pull request, making sure to be specific about changes.
5. The maintainer (me!) will review and merge your pull request. Thanks!