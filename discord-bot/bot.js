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

        message.reply(`Run logged: ${run}`);
    }

    if (msg.startsWith("!register")) {
        try {
            const username = message.content.replace("!register", "").trim();

            if (!username) {
                message.reply("Usage: `!register <username>`");
                return;
            }

            const res = await axios.post(
                `${process.env.API_URL}/bot/register`,
                { username },
                {
                    headers: {
                        "X-DISCORD-ID": message.author.id,
                        "X-BOT-KEY": process.env.BOT_API_KEY
                    }
                }
            );

            message.reply(`✅ Registered and linked to user: ${res.data.username}`);
        } catch (error) {
            const apiMessage = error?.response?.data?.message;
            message.reply(`Could not register. ${apiMessage || ""}`.trim());
        }
    }

    if (msg.startsWith("!link")) {
        try {
            const userId = Number(message.content.replace("!link", "").trim());
            if (!userId) {
                message.reply("Usage: `!link <userId>`");
                return;
            }

            const res = await axios.post(
                `${process.env.API_URL}/bot/link-existing`,
                { userId },
                {
                    headers: {
                        "X-DISCORD-ID": message.author.id,
                        "X-BOT-KEY": process.env.BOT_API_KEY
                    }
                }
            );

            message.reply(`Linked to existing user: ${res.data.username} (id: ${res.data.user_id})`);
        } catch (error) {
            const apiMessage = error?.response?.data?.message;
            message.reply(`Could not link account. ${apiMessage || ""}`.trim());
        }
    }

    if (msg === "!stats") {

        try {
            const res = await axios.get(
                `${process.env.API_URL}/bot/stats`,
                {
                    headers: {
                        "X-DISCORD-ID": message.author.id,
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
            const apiMessage = error?.response?.data?.message;
            message.reply(`Could not fetch stats. ${apiMessage || "Try !register first."}`.trim());
        }
    }

});

// Login
client.login(process.env.DISCORD_TOKEN);