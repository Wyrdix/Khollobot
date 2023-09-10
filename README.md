# Khollobot
## Presentation

Initially, this bot discord was designed as an assistant for the HX1 (MPI) class of the Faidherbe preparatory school in Lille (France) (class of 2022/2023).
For the time being, no modifications have been made to make this bot compatible with multiple servers and/or multiple classes. This may change in the future.

## Build and run
To compile this project, use `gradle clean shadowJar`.
Be aware that to compile and run this project successfully, a `login.json` file must be placed in `src/main/resources/login.json`.

The command will produce a runnable jar.

## Login
For the moment, the structure of the login.json file is as follows: ``json
{
  "discord_token": "[your bot token]",
  "mail_username": "[the email address you wish to forward to discord]",
  "mail_password": "[the password of the email address]".
}
```
You'll need to fill at least the discord bot token, and set the mail plugin to disabled if you want to run the bot
