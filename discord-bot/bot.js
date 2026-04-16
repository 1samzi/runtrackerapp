require("dotenv").config();
const { Client, GatewayIntentBits } = require("discord.js");
//Request library
const axios = require("axios");



// Create client
const client = new Client({
    intents: [
        GatewayIntentBits.Guilds,
        GatewayIntentBits.GuildMessages,
        GatewayIntentBits.MessageContent
    ]
});

// When bot is ready
client.once("ready", () => {
    console.log(`Logged in as ${client.user.tag}`);
});

// Listen for messages
client.on("messageCreate", async (message) => {

    if (message.author.bot) return;

    const msg = message.content.toLowerCase();

    if (msg === "!ping") {
        message.reply("Pong!");
    }

    if (msg.startsWith("!logrun")) {
        // Example: !logrun 5km
        const run = msg.replace("!logrun", "").trim();

        message.reply(`🏃 Run logged: ${run}`);
    }

    if (msg === "!stats") {

        try {

            const userId = 1;

            const res = await axios.get(
                `${process.env.API_URL}/users/${userId}/stats`,
                {
                    headers: {
                        "X-BOT-KEY": process.env.BOT_API_KEY
                    }
                }
            );

            const stats = res.data;

            message.reply(
                `Stats\n` +
                `Total Runs: ${stats.totalRuns}\n` +
                `Total Distance: ${stats.totalDistance} km\n` +
                `Avg Pace: ${stats.averagePace} km/hr`
            );

        } catch (error) {
            console.error(error);
            message.reply("⚠️ Could not fetch stats.");
        }
    }


});

// Login
client.login(process.env.DISCORD_TOKEN);