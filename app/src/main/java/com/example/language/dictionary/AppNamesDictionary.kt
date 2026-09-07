package com.example.language.dictionary

object AppNamesDictionary {

    /**
     * Curated catalog of 1,000+ popular mobile applications, web platforms,
     * social media, streaming services, creator tools, games, utilities,
     * and tech applications.
     */
    val WORDS: List<String> = listOf(
        // === POPULAR SOCIAL MEDIA & MESSAGING (User Highlights) ===
        "youtube", "tiktok", "facebook", "instagram", "whatsapp", "messenger",
        "telegram", "snapchat", "twitter", "x", "threads", "reddit",
        "pinterest", "discord", "wechat", "twitch", "kick", "bluesky",
        "mastodon", "tumblr", "line", "viber", "skype", "imo", "vk",
        "likee", "bigolive", "tango", "kuaishou", "dailymotion", "rumble",
        "vimeo", "clubhouse", "bereal", "lemon8", "triller", "tinder",
        "bumble", "hinge", "badoo", "quora", "medium", "signal", "element",
        "session", "threema", "wire", "matrix", "mewe", "gab", "parler",
        "truthsocial", "gettr", "rumble", "trovo", "afreecatv", "showroom",
        "mirrativ", "chzzk", "bilibili", "douyin", "xiaohongshu", "weibo",
        "qq", "kakaotalk", "zalo", "hike", "nimbuzz", "fring", "meetme",
        "tagged", "skout", "twoo", "yubo", "hi5", "myspace",

        // === VIDEO, AUDIO & PHOTO CREATOR / EDITING APPS ===
        "capcut", "premiere", "premierepro", "aftereffects", "davinciresolve", "davinci",
        "photoshop", "lightroom", "canva", "vneditor", "vn", "filmora",
        "inshot", "kinemaster", "alightmotion", "picsart", "snapseed", "vsco",
        "meitu", "procreate", "blender", "finalcut", "finalcutpro", "audacity",
        "flstudio", "ableton", "logicpro", "obs", "obsstudio", "streamlabs",
        "prismlive", "bandicam", "camtasia", "remini", "photoroom", "pixlr",
        "retrica", "vita", "vivavideo", "powerdirector", "videoshow", "nodevideo",
        "lumafusion", "cutter", "clipchamp", "descript", "opusclip", "vizard",
        "captions", "autocap", "blink", "veed", "kapwing", "invideo",
        "movavi", "shotcut", "kdenlive", "openshot", "hitfilm", "magix",
        "pinnacle", "cyberlink", "coreldraw", "illustrator", "indesign", "acrobat",
        "lightworks", "vegaspro", "avid", "garageband", "cubase", "reaper",
        "studioone", "protools", "bitwig", "soundforge", "waveform", "tracktion",
        "facetune", "airbrush", "beautyplus", "b612", "snow", "soda",
        "foodie", "youcam", "photogrid", "layout", "unfold", "mojo",
        "storyart", "storychic", "over", "sparkpost", "fotor", "befunky",
        "polarr", "darkroom", "vsco", "rni", "huji", "dazzcam",
        "nomo", "dispo", "goproquik", "quik", "djifly", "djiapp",
        "djipocket", "insta360app", "lumixsync", "canonconnect", "sonycreatorsapp", "fujifilmxapp",
        "nikonsnapbridge", "blackmagiccamera", "filmicpro", "mcpro24fps", "procamera", "halide",

        // === AI, LLM & DEVELOPER CREATOR TOOLS ===
        "chatgpt", "gemini", "claude", "copilot", "midjourney", "stablediffusion",
        "perplexity", "deepseek", "cursor", "github", "gitlab", "bitbucket",
        "vscode", "androidstudio", "intellij", "pycharm", "webstorm", "clion",
        "termux", "figma", "framer", "sketch", "postman", "insomnia",
        "replit", "huggingface", "runway", "runwayml", "elevenlabs", "suno",
        "udio", "kling", "sora", "pika", "lumaai", "haiper",
        "leonardoai", "firefly", "recraft", "magnific", "topaz", "upscayl",
        "v0", "bolt", "lovable", "windsurf", "aider", "openwebui",
        "ollama", "lmstudio", "jan", "anythingllm", "flowise", "langchain",
        "langflow", "crewai", "autogen", "dify", "n8n", "zapier",
        "make", "makecom", "bard", "bingai", "dalle", "geminipro",
        "geminiultra", "geminiflash", "chatgptplus", "gpt4", "gpt4o", "gpt35",
        "claude3", "claude35", "sonnet", "haiku", "opus", "deepseekv3",
        "deepseekr1", "whisper", "groq", "togetherai", "replicate", "falai",

        // === STREAMING, MUSIC & ENTERTAINMENT ===
        "spotify", "netflix", "primevideo", "disneyplus", "hotstar", "hbomax",
        "max", "hulu", "appletv", "youtubemusic", "soundcloud", "shazam",
        "deezer", "tidal", "audiomack", "pandora", "iheartradio", "tunein",
        "mixcloud", "bandcamp", "jiocinema", "zee5", "sonyliv", "voot",
        "hoichoi", "chorki", "toffee", "bingebd", "bioscope", "rabbitholebd",
        "bongobd", "crunchyroll", "funimation", "hidive", "iqiyi", "wetv",
        "viki", "kodi", "plex", "jellyfin", "stremio", "popcorntime",
        "peacock", "paramountplus", "discoveryplus", "fubotv", "slingtv", "directv",
        "espn", "dazn", "eurosport", "cricbuzz", "espncricinfo", "crex",
        "livecric", "darazlive", "streamio", "kinopoisk", "megogo", "ivi",
        "wink", "okko", "moretv", "premier", "amediateka", "rutube",

        // === FINTECH, BANKING, WALLETS & CRYPTO ===
        "bkash", "nagad", "rocket", "upay", "cellfin", "nexuspay",
        "islamibank", "citytouch", "bracbank", "eblbl", "mtb", "scb",
        "hsbc", "dhakabank", "primebank", "ucb", "trustbank", "bankasia",
        "paypal", "wise", "payoneer", "revolut", "cashapp", "venmo",
        "zelle", "googlepay", "gpay", "applepay", "paytm", "phonepe",
        "googlewallet", "samsungpay", "binance", "coinbase", "bybit", "okx",
        "kucoin", "kraken", "trustwallet", "metamask", "phantomwallet", "exodus",
        "ledger", "trezor", "uniswap", "pancakeswap", "opensea", "blur",
        "stripe", "square", "skrill", "neteller", "perfectmoney", "webmoney",
        "payeer", "alipay", "wechatpay", "grabpay", "shopeepay", "truemoney",
        "toss", "kakaopay", "linepay", "mercadopago", "nubank", "picpay",

        // === E-COMMERCE, DELIVERY & RIDE-SHARING ===
        "amazon", "daraz", "aliexpress", "ebay", "shopee", "lazada",
        "shein", "temu", "flipkart", "walmart", "target", "bestbuy",
        "etsy", "shopify", "woocommerce", "bikroy", "rokomari", "chaldal",
        "pickaboo", "shwapno", "meenaclick", "aarong", "othoba", "priyoshop",
        "foodpanda", "pathao", "uber", "ubereats", "careem", "indrive",
        "grab", "deliveroo", "doordash", "grubhub", "instacart", "zomato",
        "swiggy", "blinkit", "zepto", "bigbasket", "talabat", "hungerstation",
        "jahez", "noon", "souq", "jumia", "kilimall", "takealot",
        "mercadolibre", "magazineluiza", "americanas", "olx", "craigslist", "carousell",
        "avito", "wildberries", "ozon", "yandexgo", "yandexmarket", "deliverystar",
        "lyft", "bolt", "freeflow", "ola", "rapido", "didi", "gojek", "maxim",

        // === PRODUCTIVITY, OFFICE, CLOUD & UTILITY ===
        "googledrive", "gdrive", "gmail", "googledocs", "googlesheets", "googleslides",
        "googlekeep", "googlemeet", "googlecalendar", "googlemaps", "googlephotos", "googletranslate",
        "googleearth", "googlelens", "googleclassroom", "googleforms", "googleplay", "playstore",
        "appstore", "microsoft365", "office365", "word", "excel", "powerpoint",
        "outlook", "onedrive", "teams", "onenote", "todo", "copilot365",
        "notion", "trello", "asana", "slack", "zoom", "evernote",
        "todoist", "obsidian", "dropbox", "mega", "terabox", "box",
        "pcloud", "icloud", "airdrop", "quickshare", "samsungsmartswitch", "miui",
        "airtable", "clickup", "monday", "jira", "confluence", "basecamp",
        "coda", "craft", "bear", "goodnotes", "notability", "collanote",
        "drawio", "lucidchart", "miro", "mural", "whimsical", "excalidraw",
        "bitwarden", "lastpass", "1password", "dashlane", "nordpass", "authenticator",
        "googleauthenticator", "microsoftauthenticator", "authy", "duo", "yubikey", "protonmail",
        "tutanota", "zoho", "zohomail", "yandexmail", "mailru", "fastmail",

        // === BROWSERS, UTILITIES, SYSTEM & MEDIA PLAYERS ===
        "chrome", "googlechrome", "firefox", "opera", "operagx", "operamini",
        "brave", "edge", "microsoftedge", "ucbrowser", "safari", "torbrowser",
        "duckduckgo", "vivaldi", "samsunginternet", "kiwibrowser", "puffin", "dolphin",
        "vlc", "vlcmediaplayer", "mxplayer", "kmplayer", "potplayer", "bsplayer",
        "gompalyer", "poweramp", "musicolet", "aimp", "pulsar", "blackplayer",
        "shareit", "xender", "zapya", "sendanywhere", "snapdrop", "feem",
        "truecaller", "camscanner", "adobescan", "adobereader", "wpsoffice", "polarisoffic",
        "speedtest", "fastcom", "wifianalyzer", "rar", "zarchiver", "7zip",
        "winrar", "esfileexplorer", "solidexplorer", "cxfileexplorer", "filesbygoogle", "totalcommander",
        "gboard", "swiftkey", "fleksy", "ridmik", "avro", "bijoy",
        "novalauncher", "lawnchair", "niagaralauncher", "smartlauncher", "microsoftlauncher", "actionlauncher",
        "accubattery", "greenify", "sdmaid", "cpu-z", "aida64", "geekbench",
        "antutu", "3dmark", "termux", "androix", "parallelsspace", "dualspace",
        "shizuku", "magisk", "kernelsu", "lsposed", "luckyypatcher", "apkpure",
        "apkmirror", "fdroid", "aurorastore", "uptodown", "aptoide", "happymod",
        "nordvpn", "expressvpn", "surfshark", "cyberghost", "protonvpn", "windscribe",
        "warp", "1111", "tunnelbear", "hotspotshield", "supervpn", "turbo_vpn",
        "turbovpn", "psiphon", "v2ray", "v2rayng", "shadowsocks", "openvpn",
        "wireguard", "clash", "clashforandroid", "nekobox", "singbox", "karing",

        // === POPULAR MOBILE & PC GAMES ===
        "freefire", "freefiremax", "pubg", "pubgmobile", "bgmi", "callofduty",
        "codmobile", "warzone", "roblox", "minecraft", "clashofclans", "coc",
        "clashroyale", "subwaysurfers", "templerun", "candycrush", "ludoking", "ludo",
        "mobilelegends", "mlbb", "genshinimpact", "genshin", "honkaistarrail", "zenlesszonezero",
        "honkaithethird", "honorofkings", "wildrift", "brawlstars", "amongus", "asphalt9",
        "asphalt8", "needforspeed", "realracing", "easportsfc", "fifamobile", "efootball",
        "pesmobile", "carrompool", "8ballpool", "chess", "chesscom", "lichess",
        "shadowfight", "shadowfight2", "shadowfight3", "shadowfight4", "mortalcombat", "streetfighter",
        "pokemonunite", "pokemongo", "stumbleguys", "fallguys", "fortnite", "apexlegends",
        "valorant", "counterstrike", "csgo", "cs2", "dota2", "leagueoflegends",
        "rocketleague", "overwatch", "destiny2", "gta5", "gtasanandreas", "gtavicecity",
        "cyberpunk", "witcher3", "reddead", "rdr2", "eldenring", "darksouls",
        "sekiro", "bloodborne", "godofwar", "spiderman", "batmanarkham", "assassinscreed",
        "farcry", "callofdutywarzone", "battlefield", "tomb_raider", "uncharted", "lastofus",
        "residentevil", "silenthill", "fnaf", "terraria", "dontstarve", "stardewvalley",
        "deadcells", "hollowknight", "cuphead", "undertale", "deltarune", "geometrydash",
        "vector", "angrybirds", "fruitninja", "doodlejump", "flappybird", "plantsvszombies",
        "cuttherope", "hillclimbracing", "trafficrider", "trafficracer", "drdriving", "extremeclimbing",
        "slitherio", "wormszone", "agar", "diepio", "paperio", "survivor",

        // === EDUCATION, TRAVEL, HEALTH & LIFESTYLE ===
        "duolingo", "khanacademy", "coursera", "udemy", "edx", "quizlet",
        "photomath", "solvelit", "brainly", "socratic", "symbolab", "desmos",
        "wolframalpha", "ancestry", "babbel", "memrise", "busuu", "lingodeer",
        "rosettastone", "ted", "tedtalks", "blinkist", "headway", "audiobooks",
        "audible", "kindle", "googleplaybooks", "wattpad", "webtoon", "tapas",
        "manga", "mangaplus", "tachiyomi", "mihon", "shonenjump", "crunchyrollmanga",
        "strava", "myfitnesspal", "nikerunclub", "niketrainingclub", "fitbit", "garminconnect",
        "applefitness", "samsunghealth", "googlefit", "flo", "clue", "headspace",
        "calm", "wakingup", "insighttimer", "medito", "sleepcycle", "yazio",
        "loseit", "lifesum", "waterreminder", "habitica", "fabulous", "forest",
        "booking", "bookingcom", "agoda", "airbnb", "tripadvisor", "expedia",
        "kayak", "skyscanner", "trivago", "hooper", "flightradar24", "marinetraffic",
        "googletranslate", "deepl", "reverso", "linguee", "papago", "itranslate",
        "shojobd", "bdtickets", "shohoz", "chalo", "jatri", "ticketplus",

        // === CREATOR PLATFORMS, HOSTING & WEB TOOLS ===
        "wordpress", "wix", "squarespace", "webflow", "ghost", "blogger",
        "medium", "substack", "beehiiv", "convertkit", "mailchimp", "brevo",
        "hubspot", "salesforce", "zendesk", "intercom", "crisp", "tawkto",
        "gumroad", "kofi", "buymeacoffee", "patreon", "subscribestar", "fanbase",
        "onlyfans", "fansly", "passionate", "teachable", "thinkific", "kajabi",
        "podia", "skool", "mighty", "circle", "discourse", "vbulletin",
        "cloudflare", "aws", "gcp", "azure", "digitalocean", "linode",
        "vultr", "hetzner", "vercel", "netlify", "render", "railway",
        "flyio", "heroku", "supabase", "firebase", "mongodb", "planetscale",
        "neon", "upstash", "resend", "postmark", "sendgrid", "twilio"
    )

    private val wordsByPrefix: Map<Char, List<String>> by lazy {
        WORDS.groupBy { it.first().lowercaseChar() }
    }

    fun getSuggestions(prefix: String, maxCount: Int = 5): List<String> {
        val query = prefix.trim().lowercase()
        if (query.isEmpty()) return emptyList()
        val firstChar = query.first()
        val candidateList = wordsByPrefix[firstChar] ?: WORDS

        val exactMatches = candidateList.filter { it.startsWith(query) && it != query }
        val results = exactMatches.take(maxCount)
        if (prefix.isNotEmpty() && prefix.first().isUpperCase()) {
            return results.map { it.replaceFirstChar { c -> c.uppercase() } }
        }
        return results
    }
}
