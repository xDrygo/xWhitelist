# xWhitelist

xWhitelist the best hybrid whitelist plugin ever made, it have a local whitelist system, MySQL support, and a staff only whitelist system.

## Features
- A local whitelist system managed by a config file.
- A MySQL database whitelist mode.
- Multi line kick messages.
- Maintenance Whitelist system.
- Main configuration file. *(Includes: Enable/disable MySQL mode, MySQL database credentials, enable/disable whitelist)*
- Whitelist file *(Includes: Configuration for the local whitelist)*
- Configuration file for Maintenance Whitelist. *(Includes: Enable/Disable configuration & list of players from the whitelist)*
- Messages configuration file. *(Includes: Prefix, command messages, kick messages, help command, etc.)
- Permissions and commands for administration.

## Commands
List of all commands from the plugin. (All the commands can be used from the console.)

**Plugin** commands are the general commands that no manage the plugin, not a whitelist.  
**Regular Whitelist** commands manage the regular MySQL whitelist.  
**Maintenance Whitelist** commands manage the staff whitelist.

### Plugin
- **/xwhitelist help** » Displays the list of commands in-game. (with Regular Whitelist and Maintenance Whitelist commands.)
- **/xwhitelist reload** » Reloads all the plugin configuration. (Database, Messages, Whitelist, Maintenance Whitelist etc.)
- **/xwhitelist info** » Displays information about the plugin. (If extensions are enabled, versions, changelogs, etc.)

### Regular Whitelist
- **/xwhitelist enable** » Enables the whitelist.
- **/xwhitelist disable** » Disables the whitelist.
- **/xwhitelist add [player]** » Add the *[player]* to the whitelist.
- **/xwhitelist remove [player]** » Removes the *[player]* from the whitelist.
- **/xwhitelist list** » Displays the list of players in the whitelist.
- **/xwhitelist cleanup** » Removes all the players from the whitelist.

### Maintenance Whitelist
- **/xwhitelist maintenance enable** » Enables the maintenance whitelist.
- **/xwhitelist maintenance disable** » Disables the maintenance whitelist.
- **/xwhitelist maintenance add [player]** » Add the *[player]* to the maintenance whitelist.
- **/xwhitelist maintenance remove [player]** » Removes the *[player]* from the maintenance whitelist.
- **/xwhitelist maintenance list** » Displays the list of players in the maintenance whitelist.
- **/xwhitelist maintenance cleanup** » Removes all the players from the maintenance whitelist.

## Permissions
### Plugin
- **xwhitelist.plugin.help** » Permission to use `/xwhitelist help`
- **xwhitelist.plugin.reload** » Permission to use `/xwhitelist reload`
- **xwhitelist.plugin.info** » Permission to use `/xwhitelist info`
### Regular Whitelist
- **xwhitelist.whitelist.enable** » Permission to use `/xwhitelist enable`
- **xwhitelist.whitelist.disable** » Permission to use `/xwhitelist disable`
- **xwhitelist.whitelist.add** » Permission to use  `/xwhitelist add <player>`
- **xwhitelist.whitelist.remove** » Permission to use  `/xwhitelist remove <player>`
- **xwhitelist.whitelist.list** » Permission to use  `/xwhitelist list`
- **xwhitelist.whitelist.cleanup** » Permission to use `/xwhitelist cleanup`
### Maintenance Whitelist
- **xwhitelist.maintenance.enable** » Permission to use `/mwhitelist enable`
- **xwhitelist.maintenance.disable** » Permission to use `/mwhitelist disable`
- **xwhitelist.maintenance.add** » Permission to use  `/mwhitelist add <player>`
- **xwhitelist.maintenance.remove** » Permission to use  `/mwhitelist remove <player>`
- **xwhitelist.maintenance.list** » Permission to use  `/mwhitelist list`
- **xwhitelist.maintenance.cleanup** » Permission to use `/mwhitelist cleanup`
### All permissions
- **xwhitelist.admin** » Permission for all. _(* permission)_

## Placeholders
*(These placeholders needs PlaceholderAPI to work.)*
- **%xwhitelist_mysql_enabled%** » Returns "true" if the MySQL mode is enabled, otherwise "false".
- **%xwhitelist_whitelist_enabled%** » Returns "true" if the whitelist is enabled, otherwise "false".
- **%xwhitelist_whitelist_iswhitelisted%** » Returns "true" if the player is whitelisted, otherwise "false".
- **%xwhitelist_whitelist_playerswhitelisted%** » Returns the number of players in the whitelist.`
- **%xwhitelist_maintenance_enabled%** » Returns "true" if the maintenance whitelist is enabled, otherwise "false".`
- **%xwhitelist_maintenance_iswhitelisted%** » Returns "true" if the player is whitelisted in the maintenance whitelist, otherwise "false"..
- **%xwhitelist_maintenance_playerswhitelisted%** » Returns the number of players in the maintenance whitelist.`
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
#   |__/  \__/|__/     \__/|__/  |__/|__/   \___/   \_______/|__/|__/|_______/    \___/  by Drygo

#        Github page: https://github.com/xDrygo/xWhitelist - X: https://x.com/eldrygo

# Welcome to the configuration file of the plugin xWhitelist.

# If is enabled the whitelist (true/false), it can be changed in-game with command /xwhitelist enable and /xwhitelist disable.
enabled: false

# MySQL database connection. You can modify the connection to the database here. (The database table that will be used is: whitelist)
mysql:
  enable: false # Use the database connection instead offline mode (true / false)
  host: "localhost" # Example: 111.111.111.111 (WITHOUT PORT)
  port: 3306
  database: "whitelist_database" # Database name
  user: "root" # Database username
  password: "password" #Database password.

# NO DOT MODIFY
plugin:
  first_run: true
```
### [whitelist.yml](https://github.com/xDrygo/xWhitelist/blob/master/src/main/resources/whitelist.yml)
```

#     /$$      /$$ /$$       /$$   /$$               /$$ /$$             /$$           /$$$$$$$$ /$$ /$$
#    | $$  /$ | $$| $$      |__/  | $$              | $$|__/            | $$          | $$_____/|__/| $$
#    | $$ /$$$| $$| $$$$$$$  /$$ /$$$$$$    /$$$$$$ | $$ /$$  /$$$$$$$ /$$$$$$        | $$       /$$| $$  /$$$$$$
#    | $$/$$ $$ $$| $$__  $$| $$|_  $$_/   /$$__  $$| $$| $$ /$$_____/|_  $$_/        | $$$$$   | $$| $$ /$$__  $$
#    | $$$$_  $$$$| $$  \ $$| $$  | $$    | $$$$$$$$| $$| $$|  $$$$$$   | $$          | $$__/   | $$| $$| $$$$$$$$
#    | $$$/ \  $$$| $$  | $$| $$  | $$ /$$| $$_____/| $$| $$ \____  $$  | $$ /$$      | $$      | $$| $$| $$_____/
#    | $$/   \  $$| $$  | $$| $$  |  $$$$/|  $$$$$$$| $$| $$ /$$$$$$$/  |  $$$$/      | $$      | $$| $$|  $$$$$$$
#    |__/     \__/|__/  |__/|__/   \___/   \_______/|__/|__/|_______/    \___/        |__/      |__/|__/ \_______/  by Drygo

#                                            Offline mode whitelist file.

# Welcome to the offline whitelist file. Here you can add, remove or modify the player of the offline mode whitelist.

# Add the players to the whitelist like this example:
# whitelist:
#   - xDrygo
#   - player1
#   - player2
#   - player3

whitelist: []

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
### [messages.yml](https://github.com/xDrygo/xWhitelist/blob/master/src/main/resources/messages.yml)
```

#     /$$      /$$
#    | $$$    /$$$
#    | $$$$  /$$$$  /$$$$$$   /$$$$$$$ /$$$$$$$  /$$$$$$   /$$$$$$   /$$$$$$   /$$$$$$$
#    | $$ $$/$$ $$ /$$__  $$ /$$_____//$$_____/ |____  $$ /$$__  $$ /$$__  $$ /$$_____/
#    | $$  $$$| $$| $$$$$$$$|  $$$$$$|  $$$$$$   /$$$$$$$| $$  \ $$| $$$$$$$$|  $$$$$$
#    | $$\  $ | $$| $$_____/ \____  $$\____  $$ /$$__  $$| $$  | $$| $$_____/ \____  $$
#    | $$ \/  | $$|  $$$$$$$ /$$$$$$$//$$$$$$$/|  $$$$$$$|  $$$$$$$|  $$$$$$$ /$$$$$$$/
#    |__/     |__/ \_______/|_______/|_______/  \_______/ \____  $$ \_______/|_______/  by Drygo
#                                                         /$$  \ $$
#             xWhitelist plugin's message config.        |  $$$$$$/
#                                                         \______/

# Welcome to the messages configuration file. Here you can change every in-game message of the plugin, prefix and kick messages.

# Changes the prefix of the plugin, you can call it typing %prefix% in the messages.
prefix:
  default: "&8[#ff0000&lx&r&lWhitelist&8]"
  maintenance: "&8[#ff50af&lM&r&lWhitelist&8]"

# The messages that displays when a player tries to login in the server and is not on the whitelist.
kick_messages:
  whitelist:
    - "&8[#ff0000&lx&r&lWhitelist&8]"
    - ""
    - "#FF0000🚫 You are not in the server whitelist."
    - ""
    - "&8If you think this is an mistake,"
    - "&8contact us on discord.myserver.net"
  maintenance:
    - "&8[#ff50af&lM&r&lWhitelist&8]"
    - ""
    - "#FF0000🚫 You are not in the maintenance whitelist."
    - ""
    - "&8If you think this is an mistake,"
    - "&8contact us on discord.myserver.net"

# The messages that the command sender recieve after using a command.
commands:
  plugin:
    unknown_command: "&8[#ff0000&lx&r&lWhitelist&8] #FF0000🚫 Unknown command. &7Use &f/xwhitelist help &7to see the list of commands."
    reload_success: "&8[#ff0000&lx&r&lWhitelist&8] #a0ff72✔ Plugin reloaded."
  whitelist:
    enable:
      success: "&8[#ff0000&lx&r&lWhitelist&8] #a0ff72✔ The whitelist has been enabled."
      already: "&8[#ff0000&lx&r&lWhitelist&8] #fff18d📰 The whitelist is already enabled."
    disable:
      success: "&8[#ff0000&lx&r&lWhitelist&8] #ff7272❌ The whitelist has been disabled."
      already: "&8[#ff0000&lx&r&lWhitelist&8] #fff18d📰 The whitelist is already disabled."
    add:
      success: "&8[#ff0000&lx&r&lWhitelist&8] #a0ff72✔ Added player %player% to the whitelist."
      already: "&8[#ff0000&lx&r&lWhitelist&8] #FF0000🚫 The player %player% is already in the whitelist."
      usage: "&8[#ff0000&lx&r&lWhitelist&8] #FF0000🚫 Incorrect usage. &7Use &f/xwhitelist add <player> &7to add a player to the whitelist."
    remove:
      success: "&8[#ff0000&lx&r&lWhitelist&8] #ff7272❌ Removed player %player% from the whitelist."
      already: "&8[#ff0000&lx&r&lWhitelist&8] #FF0000🚫 Can't find player %player% in the whitelist."
      usage: "&8[#ff0000&lx&r&lWhitelist&8] #FF0000🚫 Incorrect usage. &7Use &f/xwhitelist remove <player> &7to remove a player from the whitelist."
    list:
      header: "&8[#ff0000&lx&r&lWhitelist&8] #fff18d📰 List of player in the whitelist:"
      row: "&8- #ffffff%player%"
      empty: "&8[#ff0000&lx&r&lWhitelist&8] #FF0000🚫 The whitelist is empty."
    cleanup:
      success: "&8[#ff0000&lx&r&lWhitelist&8] #a0ff72✔ The whitelist has been cleared."
  maintenance:
    enable:
      success: "&8[#ff50af&lM&r&lWhitelist&8] #a0ff72✔ The maintenance whitelist has been enabled."
      already: "&8[#ff50af&lM&r&lWhitelist&8] #fff18d📰 The maintenance whitelist is already enabled."
    disable:
      success: "&8[#ff50af&lM&r&lWhitelist&8] #ff7272❌ The maintenance whitelist has been disabled."
      already: "&8[#ff50af&lM&r&lWhitelist&8] #fff18d📰 The maintenance whitelist is already disabled."
    add:
      success: "&8[#ff50af&lM&r&lWhitelist&8] #a0ff72✔ Added player %player% to the MWhitelist."
      already: "&8[#ff50af&lM&r&lWhitelist&8] #FF0000🚫 The player %player% is already in MWhitelist."
      usage: "&8[#ff50af&lM&r&lWhitelist&8] #FF0000🚫 Incorrect usage. &7Use &f/mwhitelist add <player> &7to add a player to the MWhitelist."
    remove:
      success: "&8[#ff50af&lM&r&lWhitelist&8] #ff7272❌ Removed player %player% from the MWhitelist."
      already: "&8[#ff50af&lM&r&lWhitelist&8] #FF0000🚫 Can't find player %player% in the MWhitelist."
      usage: "&8[#ff50af&lM&r&lWhitelist&8] #FF0000🚫 Incorrect usage. &7Use &f/mwhitelist remove <player> &7to remove a player from the MWhitelist."
    list:
      header: "&8[#ff50af&lM&r&lWhitelist&8] #fff18d📰 List of player in the MWhitelist:"
      row: "&8- #ffffff%player%"
      empty: "&8[#ff50af&lM&r&lWhitelist&8] #FF0000🚫 The MWhitelist is empty."
    cleanup:
      success: "&8[#ff50af&lM&r&lWhitelist&8] #a0ff72✔ The whitelist has been cleared."

# Help command message. (You can get it when using /xwhitelist help)
help_message:
  - "#666666+==================================================+"
  - " "
  - "                            #ff0000&lX&r&lWhitelist &8- &r&fHelp"
  - " "
  - "#fff18d&l                    ᴘʟᴜɢɪɴ ᴄᴏᴍᴍᴀɴᴅꜱ"
  - "&f  /xᴡʜɪᴛᴇʟɪꜱᴛ ʜᴇʟᴘ #707070- #ccccccShows this help message"
  - "&f  /xᴡʜɪᴛᴇʟɪꜱᴛ ʀᴇʟᴏᴀᴅ #707070- #ccccccReloads the plugin configuration"
  - " "
  - "                 #fff18d&lᴡʜɪᴛᴇʟɪꜱᴛ ᴄᴏᴍᴍᴀɴᴅꜱ"
  - "&f  /xᴡʜɪᴛᴇʟɪꜱᴛ ᴇɴᴀʙʟᴇ #707070- #ccccccEnables the whitelist"
  - "&f  /xᴡʜɪᴛᴇʟɪꜱᴛ ᴅɪꜱᴀʙʟᴇ #707070- #ccccccDisables the whitelist"
  - "&f  /xᴡʜɪᴛᴇʟɪꜱᴛ ᴀᴅᴅ #707070- #ccccccAdd a player to the whitelist"
  - "&f  /xᴡʜɪᴛᴇʟɪꜱᴛ ʀᴇᴍᴏᴠᴇ #707070- #ccccccRemove a player from the whitelist"
  - "&f  /xᴡʜɪᴛᴇʟɪꜱᴛ ʟɪꜱᴛ #707070- #ccccccDisplays the players in the whitelist"
  - "&f  /xᴡʜɪᴛᴇʟɪꜱᴛ ᴄʟᴇᴀɴᴜᴘ #707070- #ccccccRemoves all players from the whitelist"
  - " "
  - "          #fff18d&lᴍᴀɪɴᴛᴇɴᴀɴᴄᴇ ᴡʜɪᴛᴇʟɪꜱᴛ ᴄᴏᴍᴍᴀɴᴅꜱ"
  - "&f  /ᴍᴡʜɪᴛᴇʟɪꜱᴛ ᴇɴᴀʙʟᴇ #707070- #ccccccEnables the maintenance whitelist"
  - "&f  /ᴍᴡʜɪᴛᴇʟɪꜱᴛ ᴅɪꜱᴀʙʟᴇ #707070- #ccccccDisables the maintenance whitelist"
  - "&f  /ᴍᴡʜɪᴛᴇʟɪꜱᴛ ᴀᴅᴅ #707070- #ccccccAdd a player to the maintenance whitelist"
  - "&f  /ᴍᴡʜɪᴛᴇʟɪꜱᴛ ʀᴇᴍᴏᴠᴇ #707070- #ccccccRemove a player from the maintenance whitelist"
  - "&f  /ᴍᴡʜɪᴛᴇʟɪꜱᴛ ʟɪꜱᴛ #707070- #ccccccDisplays the players in the maintenance whitelist"
  - "&f  /ᴍᴡʜɪᴛᴇʟɪꜱᴛ ᴄʟᴇᴀɴᴜᴘ #707070- #ccccccRemoves all players from the maintenance whitelist"
  - " "
  - "#666666+==================================================+"

# Command error messages.
error:
  no_permission: "&8[#ff0000&lx&r&lWhitelist&8] #FF0000🚫 You have no permission to use this command."
  database_exception: "&8[#ff0000&lx&r&lWhitelist&8] #FF0000🚫 Database connection error."

# This is the end of the file.
```
## FAQ
- **In which versions does xWhitelist works? »** *The plugin work in **Spigot & Paper 1.21.4** and **newer**, also work in **1.20.1***
- **xWhitelist works as a authentication plugin? »** *The plugin **only verifies the nickname** so if you want to use it in a no-premium server, you will need another plugin por authenticate the players.*
- **You need to use the whitelist file to make xWhitelist work? »** *The plugin is on offline mode by default (uses the local whitelist system), but you can enable the MySQL system in the config.*
- **xWhitelist works in Networks? »** *The plugin was not tested in Proxys so we don't know if it works, but we are working in a network supported version.*
- **What is the Maintenance Whitelist? »** *The maintenance whitelist is a system designed for staff members, when the __MWhitelist__ is __enabled__, only the player who is in the maintenance whitelist can connect to the server.*
## Support
If you have any questions, suggestions, or problems, you can contact me on my X: [@eldrygo](https://x.com/eldrygo) or my Discord (@xdrygo).
