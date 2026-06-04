require("dotenv").config();
const { Client, GatewayIntentBits } = require("discord.js");
const axios = require("axios");

const PREFIX = "!";
const REQUIRED_ENV = ["DISCORD_TOKEN", "API_URL", "BOT_API_KEY"];
const missingEnv = REQUIRED_ENV.filter((name) => !process.env[name]);

if (missingEnv.length > 0) {
    throw new Error(`Missing required environment variables: ${missingEnv.join(", ")}`);
}

const api = axios.create({
    baseURL: process.env.API_URL,
    timeout: 10000
});

const client = new Client({
    intents: [
        GatewayIntentBits.Guilds,
        GatewayIntentBits.GuildMessages,
        GatewayIntentBits.MessageContent
    ]
});

const HELP_MESSAGE =
    `RunTracker Bot Commands\n` +
    `${PREFIX}help - Show this help message\n` +
    `${PREFIX}register <username> - Create and link a new RunTracker account\n` +
    `${PREFIX}createuser <username> - Same as ${PREFIX}register\n` +
    `${PREFIX}link <userId> - Link Discord to an existing RunTracker user\n` +
    `${PREFIX}logrun <distanceKm> <durationMinutes> - Log a run, e.g. ${PREFIX}logrun 5 30 or ${PREFIX}logrun 5km 30min\n` +
    `${PREFIX}stats - Show your running stats\n` +
    `${PREFIX}unlink - Unlink your Discord account\n` +
    `${PREFIX}ping - Check if the bot is responding`;

client.once("ready", () => {
    console.log(`Logged in as ${client.user.tag}`);
});

client.on("messageCreate", async (message) => {
    if (message.author.bot || !message.content.startsWith(PREFIX)) return;

    const [command] = message.content.trim().toLowerCase().split(/\s+/);

    if (command === `${PREFIX}ping`) {
        await message.reply("Pong!");
    } else if (command === `${PREFIX}help`) {
        await message.reply(HELP_MESSAGE);
    } else if (command === `${PREFIX}logrun`) {
        await logRun(message);
    } else if (command === `${PREFIX}register`) {
        await createUserFromDiscord(message, `${PREFIX}register`);
    } else if (command === `${PREFIX}createuser`) {
        await createUserFromDiscord(message, `${PREFIX}createuser`);
    } else if (command === `${PREFIX}link`) {
        await linkUser(message);
    } else if (command === `${PREFIX}stats`) {
        await showStats(message);
    } else if (command === `${PREFIX}unlink`) {
        await unlinkUser(message);
    }
});

function botHeaders(message) {
    return {
        "X-DISCORD-ID": message.author.id,
        "X-BOT-KEY": process.env.BOT_API_KEY
    };
}

function apiErrorMessage(error) {
    return error?.response?.data?.message || "";
}

async function createUserFromDiscord(message, command) {
    try {
        const username = message.content.slice(command.length).trim();

        if (!username) {
            await message.reply(`Usage: \`${command} <username>\``);
            return;
        }

        const res = await api.post(
            "/bot/create-user",
            { username },
            { headers: botHeaders(message) }
        );

        await message.reply(
            `✅ Created and linked user: ${res.data.username} (account id: ${res.data.user_id}). ` +
            `Save this id so you can link other Discord accounts with \`${PREFIX}link ${res.data.user_id}\`.`
        );
    } catch (error) {
        await message.reply(`Could not create user. ${apiErrorMessage(error)}`.trim());
    }
}

async function linkUser(message) {
    try {
        const userId = Number(message.content.replace(`${PREFIX}link`, "").trim());
        if (!Number.isInteger(userId) || userId <= 0) {
            await message.reply(`Usage: \`${PREFIX}link <userId>\``);
            return;
        }

        const res = await api.post(
            "/bot/link-existing",
            { userId },
            { headers: botHeaders(message) }
        );

        await message.reply(`Linked to existing user: ${res.data.username} (id: ${res.data.user_id})`);
    } catch (error) {
        await message.reply(`Could not link account. ${apiErrorMessage(error)}`.trim());
    }
}

async function showStats(message) {
    try {
        const res = await api.get(
            "/bot/stats",
            { headers: botHeaders(message) }
        );

        const stats = res.data;
        const latestWeek = stats.weeklyStats?.[0];
        const latestMonth = stats.monthlyStats?.[0];
        const latestYear = stats.yearlyStats?.[0];

        await message.reply(
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
        await message.reply(`Could not fetch stats. ${apiErrorMessage(error) || "Try !register first."}`.trim());
    }
}

async function unlinkUser(message) {
    try {
        await api.delete(
            "/bot/unlink",
            { headers: botHeaders(message) }
        );

        await message.reply("✅ Your Discord account has been unlinked.");
    } catch (error) {
        await message.reply(`⚠️ Could not unlink account. ${apiErrorMessage(error)}`.trim());
    }
}

async function logRun(message) {
    try {
        const match = message.content
            .trim()
            .match(/^!logrun\s+(\d+(?:\.\d+)?)\s*(?:km)?\s+(\d+)\s*(?:min|mins|minutes)?$/i);

        if (!match) {
            await message.reply(`Usage: \`${PREFIX}logrun <distanceKm> <durationMinutes>\` Example: \`${PREFIX}logrun 5 30\``);
            return;
        }

        const distanceKM = Number(match[1]);
        const durationMinutes = Number(match[2]);

        if (distanceKM <= 0 || durationMinutes <= 0) {
            await message.reply("Distance and duration must both be greater than 0.");
            return;
        }

        const res = await api.post(
            "/bot/runs",
            { distanceKM, durationMinutes },
            { headers: botHeaders(message) }
        );

        const run = res.data;
        await message.reply(
            `✅ Run logged: ${run.distanceKM} km in ${run.durationMinutes} min` +
            `${run.date ? ` on ${run.date}` : ""}`
        );
    } catch (error) {
        await message.reply(`Could not log run. ${apiErrorMessage(error) || "Try !register or !link first."}`.trim());
    }
}

client.login(process.env.DISCORD_TOKEN);
