Zubat bot
------------
The Zubat bot is a multi-purpose, "modular" multi-protocol chat bot with a long list of features. 
It has been a fun playground for experimenting with both different  protocols and services.
Notable inclusions are: 

- Cross protocol chat between IRC, Mumble and Google Talk
- Everything is multithreaded, and each server connection manages multiple channels.
- Automatic metadata video information from the following providers:
-- Youtube
-- Twitch
-- Vimeo
-- SVT Play
- Automatic web shopping info from provided links (like price, sales price ( % off), location, and time left). About 23 domains supported with some crafty jsoup code.
- web titles for posted links (with included metadata), also contains short versions of links if the link is too long. 
- Automatic reminders/messages triggered on either a user joining or a time and date.
- A bunch of other "random" functions like providing name suggestions based on pokemon/soda names, getting random website links when you're bored etc.

All configuration options are loaded on a per-module basis, just add json files for irc servers/channels, or modify existing ones for plugins etc.
The project is a MAVEN project configured to release a single self-containing executable JAR (with configs outside the JAR), all dependencies
are from either Apache maven repository or Atlassian maven repository, So bulding it should be a breeze under most IDEs.

Notice: This is a project for fun, use whatever parts you find useful for your own projects. It is currently also in the works of being replaced by a simpler version for IRC use only.