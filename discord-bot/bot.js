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

const HELP_MESSAGE =
    `RunTracker Bot Commands\n` +
    `!help - Show this help message\n` +
    `!register <username> - Create and link a new RunTracker account\n` +
    `!createuser <username> - Same as !register\n` +
    `!link <userId> - Link Discord to an existing RunTracker user\n` +
    `!logrun <distanceKm> <durationMinutes> - Log a run, e.g. !logrun 5 30 or !logrun 5km 30min\n` +
    `!stats - Show your running stats\n` +
    `!unlink - Unlink your Discord account\n` +
    `!ping - Check if the bot is responding`;


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

    if (msg === "!help") {
        message.reply(HELP_MESSAGE);
    }

    if (msg.startsWith("!logrun")) {
        await logRun(message);
    }

    if (msg.startsWith("!register")) {
        await createUserFromDiscord(message, "!register");
    }

    if (msg.startsWith("!createuser")) {
        await createUserFromDiscord(message, "!createuser");
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

            const latestWeek = stats.weeklyStats?.[0];
            const latestMonth = stats.monthlyStats?.[0];
            const latestYear = stats.yearlyStats?.[0];

            message.reply(
                `Stats\n` +
                `Total Runs: ${stats.totalRuns}\n` +
                `Total Distance: ${stats.totalDistance} km\n` +
                `Total Duration: ${stats.totalDurationMinutes} min\n` +
                `Avg Distance: ${stats.averageDistance} km\n` +
                `Avg Pace: ${stats.averagePace} km/hr\n` +
                `Longest Run: ${stats.longestRun?.distanceKM || 0} km\n` +
                `Fastest Run: ${stats.fastestRun?.distanceKM || 0} km in ${stats.fastestRun?.durationMinutes || 0} min\n` +
                `This Week (${latestWeek?.period || "n/a"}): ${latestWeek?.totalDistance || 0} km\n` +
                `This Month (${latestMonth?.period || "n/a"}): ${latestMonth?.totalDistance || 0} km\n` +
                `This Year (${latestYear?.period || "n/a"}): ${latestYear?.totalDistance || 0} km`
            );


        } catch (error) {
            const apiMessage = error?.response?.data?.message;
            message.reply(`Could not fetch stats. ${apiMessage || "Try !register first."}`.trim());
        }
    }

    if (msg === "!unlink") {
        await unlinkUser(message);
    }

});

async function createUserFromDiscord(message, command) {
    try {
        const username = message.content.slice(command.length).trim();

        if (!username) {
            message.reply(`Usage: \`${command} <username>\``);
            return;
        }

        const res = await axios.post(
            `${process.env.API_URL}/bot/create-user`,
            { username },
            {
                headers: {
                    "X-DISCORD-ID": message.author.id,
                    "X-BOT-KEY": process.env.BOT_API_KEY
                }
            }
        );

        message.reply(
            `✅ Created and linked user: ${res.data.username} (account id: ${res.data.user_id}). ` +
            `Save this id so you can link other Discord accounts with \`!link ${res.data.user_id}\`.`
        );
    } catch (error) {
        const apiMessage = error?.response?.data?.message;
        message.reply(`Could not create user. ${apiMessage || ""}`.trim());
    }
}

async function unlinkUser(message) {
    try {
        await axios.delete(
            `${process.env.API_URL}/bot/unlink`,
            {
                headers: {
                    "X-DISCORD-ID": message.author.id,
                    "X-BOT-KEY": process.env.BOT_API_KEY
                }
            }
        );

        message.reply("✅ Your Discord account has been unlinked.");
    } catch (error) {
        const apiMessage = error?.response?.data?.message;
        message.reply(`⚠️ Could not unlink account. ${apiMessage || ""}`.trim());
    }
}

async function logRun(message) {
    try {
        const match = message.content
            .trim()
            .match(/^!logrun\s+(\d+(?:\.\d+)?)\s*(?:km)?\s+(\d+)\s*(?:min|mins|minutes)?$/i);

        if (!match) {
            message.reply("Usage: `!logrun <distanceKm> <durationMinutes>` Example: `!logrun 5 30`");
            return;
        }

        const distanceKM = Number(match[1]);
        const durationMinutes = Number(match[2]);

        if (distanceKM <= 0 || durationMinutes <= 0) {
            message.reply("Distance and duration must both be greater than 0.");
            return;
        }

        const res = await axios.post(
            `${process.env.API_URL}/bot/runs`,
            { distanceKM, durationMinutes },
            {
                headers: {
                    "X-DISCORD-ID": message.author.id,
                    "X-BOT-KEY": process.env.BOT_API_KEY
                }
            }
        );

        const run = res.data;
        message.reply(
            `✅ Run logged: ${run.distanceKM} km in ${run.durationMinutes} min` +
            `${run.date ? ` on ${run.date}` : ""}`
        );
    } catch (error) {
        const apiMessage = error?.response?.data?.message;
        message.reply(`Could not log run. ${apiMessage || "Try !register or !link first."}`.trim());
    }
}



// Login
client.login(process.env.DISCORD_TOKEN);