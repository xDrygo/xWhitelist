# xWhitelist

xWhitelist is a plugin that adds a whitelist system based on a MySQL database, adds commands to manage it and other features.

## Features
- A MySQL based whitelist system.
- Maintenance Whitelist system.
- Main configuration file. *(Includes: MySQL database credentials, Message configuration)*
- Configuration file for Maintenance Whitelist. *(Includes: Whitelist, Message configuration, Enable/Disable configuration)*
- Permissions and commands for administration.

## Commands
List of all commands from the plugin. (All the commands can be  used from the console.)  

**Plugin** commands are the general commands that no manage the plugin, not a whitelist.  
**Regular Whitelist** commands manage the regular MySQL whitelist.  
**Maintenance Whitelist** commands manage the staff whitelist.

### Plugin
- **/xwhitelist help** » Displays the list of commands in-game. (with Regular Whitelist and Maintenance Whitelist commands.)
- **/xwhitelist reload** » Reloads all the plugin configuration. (Database, Messages, if the Maintenance Whitelist is enabled, etc.)
- **/xwhitelist info** » Displays information about the plugin. (If extensions are enabled, versions, changelogs, etc.)

### Regular Whitelist
- **/xwhitelist add [player]** » Add the *[player]* to the whitelist.
- **/xwhitelist remove [player]** » Removes the *[player]* from the whitelist.
- **/xwhitelist list** » Displays the list of players in the whitelist.
- **/xwhitelist cleanup** » Removes all the players from the whitelist.

### Maintenance Whitelist
- **/mwhitelist enable** » Enables the maintenance whitelist.
- **/mwhitelist disable** » Disables the maintenance whitelist.

## Permissions
### Plugin
- **xwhitelist.plugin.help** » Permission to use `/xwhitelist help`
- **xwhitelist.plugin.reload** » Permission to use `/xwhitelist reload`
- **xwhitelist.plugin.info** » Permission to use `/xwhitelist info`
### Regular Whitelist
- **xwhitelist.whitelist.add** » Permission to use  `/xwhitelist add <player>`
- **xwhitelist.whitelist.remove** » Permission to use  `/xwhitelist remove <player>`
- **xwhitelist.whitelist.list** » Permission to use  `/xwhitelist list`
- **xwhitelist.whitelist.cleanup** » Permission to use `/xwhitelist cleanup`
### Maintenance Whitelist
- **xwhitelist.maintenance.enable** » Permission to use `/mwhitelist enable`
- **xwhitelist.maintenance.disable** » Permission to use `/mwhitelist disable`
### All permissions
- **xwhitelist.admin** » Permission for all. _(* permission)_

## Configuration Files
### [config.yml](https://github.com/xDrygo/xWhitelist/blob/master/src/main/resources/config.yml)
```

#              /$$      /$$ /$$       /$$   /$$               /$$ /$$             /$$
#             | $$  /$ | $$| $$      |__/  | $$              | $$|__/            | $$
#    /$$   /$$| $$ /$$$| $$| $$$$$$$  /$$ /$$$$$$    /$$$$$$ | $$ /$$  /$$$$$$$ /$$$$$$
#   |  $$ /$$/| $$/$$ $$ $$| $$__  $$| $$|_  $$_/   /$$__  $$| $$| $$ /$$_____/|_  $$_/
#    \  $$$$/ | $$$$_  $$$$| $$  \ $$| $$  | $$    | $$$$$$$$| $$| $$|  $$$$$$   | $$
#     >$$  $$ | $$$/ \  $$$| $$  | $$| $$  | $$ /$$| $$_____/| $$| $$ \____  $$  | $$ /$$
#    /$$/\  $$| $$/   \  $$| $$  | $$| $$  |  $$$$/|  $$$$$$$| $$| $$ /$$$$$$$/  |  $$$$/
#    |__/  \__/|__/     \__/|__/  |__/|__/   \___/   \_______/|__/|__/|_______/    \___/  by Drygo

        #Github page: https://github.com/xDrygo/xWhitelist - X: https://x.com/eldrygo

# Welcome to the configuration file of the plugin XWhitelist.

# MySQL database connection. You can modify the connection to the database here.
mysql:
  host: "localhost"
  port: 3306
  database: "whitelist_database"
  user: "root"
  password: "password"

# Plugin messages. You can modify the messages of the plugin here.
messages:
  player_not_whitelisted: "&8[#ff0000&lX&r&lWhitelist&8] #FF0000🚫 You are not in the whitelist."
  player_added: "&8[#ff0000&lX&r&lWhitelist&8] #a0ff72✔ Added player %player% to the whitelist."
  player_removed: "&8[#ff0000&lX&r&lWhitelist&8] #ff7272❌ Removed player %player% from the whitelist."
  whitelist_header: "&8[#ff0000&lX&r&lWhitelist&8] #fff18d📰 List of player in the whitelist:"
  whitelist_cleaned: "&8[#ff0000&lX&r&lWhitelist&8] #a0ff72✔ The whitelist has been cleared."
  config_reloaded: "&8[#ff0000&lX&r&lWhitelist&8] #a0ff72✔ Plugin reloaded."
  already_whitelisted: "&8[#ff0000&lX&r&lWhitelist&8] #FF0000🚫 The player %player% is already in the whitelist."
  player_argument_needed: "&8[#ff0000&lX&r&lWhitelist&8] #FF0000🚫 You need to specify a player in the command."
  no_additional_arguments: "&8[#ff0000&lX&r&lWhitelist&8] #FF0000🚫 No argument needed in this command."
  unknown_command: "&8[#ff0000&lX&r&lWhitelist&8] #FF0000🚫 Unknown command. &7Use &f/xwhitelist help &7to see the command list."
  no_permission: "&8[#ff0000&lX&r&lWhitelist&8] #FF0000🚫 You have no permission to use this command."
  error_database: "&8[#ff0000&lX&r&lWhitelist&8] #FF0000🚫 Database connection error."

# NO DOT MODIFY
plugin:
  first_run: true
```
### [maintenance_whitelist.yml](https://github.com/xDrygo/xWhitelist/blob/master/src/main/resources/maintenance_whitelist.yml)
```
#                  /$$      /$$ /$$       /$$   /$$               /$$ /$$             /$$    
#                 | $$  /$ | $$| $$      |__/  | $$              | $$|__/            | $$    
#    /$$$$$$/$$$$ | $$ /$$$| $$| $$$$$$$  /$$ /$$$$$$    /$$$$$$ | $$ /$$  /$$$$$$$ /$$$$$$  
#   | $$_  $$_  $$| $$/$$ $$ $$| $$__  $$| $$|_  $$_/   /$$__  $$| $$| $$ /$$_____/|_  $$_/  
#   | $$ \ $$ \ $$| $$$$_  $$$$| $$  \ $$| $$  | $$    | $$$$$$$$| $$| $$|  $$$$$$   | $$    
#   | $$ | $$ | $$| $$$/ \  $$$| $$  | $$| $$  | $$ /$$| $$_____/| $$| $$ \____  $$  | $$ /$$
#   | $$ | $$ | $$| $$/   \  $$| $$  | $$| $$  |  $$$$/|  $$$$$$$| $$| $$ /$$$$$$$/  |  $$$$/
#   |__/ |__/ |__/|__/     \__/|__/  |__/|__/   \___/   \_______/|__/|__/|_______/    \___/   by Drygo
#                                 Maintenance Whitelist Feature.

# Welcome to the maintenance whitelist configuration file. Here you can configure the whitelist status and plugin messages, as well as the player list.

# Status of the maintenance whitelist. (true to activate it, false to deactivate it)
enabled: false

# Maintenance whitelist messages, with the kick message for players trying to join who are not on the whitelist. (player_not_whitelisted)
messages:
  enable: "&8[#ff50af&lM&r&lWhitelist&8] #a0ff72✔ The maintenance whitelist has been enabled."
  disable: "&8[#ff50af&lM&r&lWhitelist&8] #ff7272❌ The maintenance whitelist has been disabled."
  already_enabled: "&8[#ff50af&lM&r&lWhitelist&8] #fff18d📰 The maintenance whitelist is already enabled."
  already_disabled: "&8[#ff50af&lM&r&lWhitelist&8] #fff18d📰 The maintenance whitelist is already disabled."
  player_not_whitelisted: "&8[#ff50af&lM&r&lWhitelist&8] #FF0000🚫 You are not on the maintenance whitelist."
  no_permission: "&8[#ff50af&lM&r&lWhitelist&8] #FF0000🚫 You don't have permissions."
  unknown_command: "&8[#ff0000&lX&r&lWhitelist&8] #FF0000🚫 Unknown command. &7Use &f/xwhitelist help &7to see the list of commands."
  no_additional_arguments: "&8[#ff50af&lM&r&lWhitelist&8] #FF0000🚫 No arguments are needed in this command."

# List of players on the maintenance whitelist.
whitelist:
  - player1
  - player2
  - player3
```
## Requirements
- The plugin work in **Spigot & Paper 1.21.4** and **newer**, but I am working for make it work in **1.20.1**
- You need to enter the credentials of a **working MySQL database** in the config to make the plugin **work**.
- The plugin **only verifies the nickname** so if you want to use it in a no-premium server, you will need another plugin por authenticate the players.
## Support
If you have any questions, suggestions, or problems, you can contact me on my X: [@eldrygo](https://x.com/eldrygo) or my Discord (@xdrygo).
