[![AOCBOt](https://socialify.git.ci/dynmie/AOCBOt/image?description=1&descriptionEditable=A%20random%20discord%20bot%20to%20keep%20track%20of%20AOC%20activities.&forks=1&issues=1&language=1&name=1&owner=1&pulls=1&stargazers=1&theme=Light)](https://github.com/dynmie/AOCBot)
<div align="center"><img alt="GitHub last commit" src="https://img.shields.io/github/last-commit/dynmie/AOCBot?style=for-the-badge"><img alt="GitHub Workflow Status" src="https://img.shields.io/github/actions/workflow/status/dynmie/AOCBot/gradle.yml?branch=master&logo=github&style=for-the-badge"></div>

### Notable features
- Member management system
- Volunteer hours tracking system for members
- Competitive volunteer hours leaderboard system
- Member striking system
- First join information panel

### What doesn't work
- Member caching with Redis

# Getting started
## Running the bot
### Prerequisites
- [Java 17 JRE](https://adoptium.net/temurin/releases/?version=17)
- [MongoDB](https://www.mongodb.com/try/download/community)

### Setup
1. Run the bot once to generate the configuration files. Make sure your current Java version is at least 17.
2. Open the configuration file and set the following properties:
    1. Set `TOKEN` to your bot's token.
    2. Set `GUILD` to your discord guild.
    3. Set `MONGO_URI` to your MongoDB URI.
    4. Set `AOC_APPLICATION_LINK` to your own application link.
3. You're done!

### Starting the bot
```bash
java -jar bot.jar
```

## Building
### Prerequisites
- [Java 17 JDK](https://adoptium.net/temurin/releases/?version=17)
- [Git](https://git-scm.com/downloads)

### Cloning the GitHub repository
```bash
git clone https://github.com/dynmie/AOCBot.git
```
### Compiling
Windows:
```cmd
.\gradlew jar
```

GNU Linux:
```bash
chmod +x ./gradlew
./gradlew jar
```

You can find the output jar at `./build/libs`.