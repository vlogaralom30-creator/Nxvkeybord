package com.example.language.dictionary

object CreatorVocabularyDictionary {

    /**
     * Curated catalog of 1,000+ terms, jargon, slang, workflow tools,
     * metrics, and vocabulary specifically used by video creators,
     * YouTubers, TikTokers, vloggers, live streamers, and digital influencers.
     */
    val WORDS: List<String> = listOf(
        // === VLOGGING, CAMERA, SHOOTING & OPTICS ===
        "vlog", "vlogger", "vlogging", "motovlog", "dailyvlog", "travelvlog", "foodvlog", "techvlog",
        "beautyvlog", "gamingvlog", "familyvlog", "couplevlog", "b-roll", "a-roll", "broll", "aroll",
        "cinematic", "cinematography", "videography", "filmmaking", "footage", "clip", "rawfootage",
        "framerate", "fps", "24fps", "30fps", "60fps", "120fps", "240fps", "resolution", "4k", "8k",
        "1080p", "720p", "1440p", "fullhd", "ultra_hd", "aspectratio", "widescreen", "anamorphic",
        "vertical", "horizontal", "16:9", "9:16", "1:1", "4:5", "portrait", "landscape",
        "aperture", "f-stop", "f1.4", "f1.8", "f2.8", "f4", "shutterspeed", "180degree", "ruleofthirds",
        "iso", "nativeiso", "dualiso", "exposure", "overexposed", "underexposed", "histogram", "zebras",
        "focuspeaking", "whitebalance", "kelvin", "colortemp", "autofocus", "eyetracking", "manualfocus",
        "focallength", "wideangle", "ultrawide", "telephoto", "macro", "primelens", "zoomlens", "fisheye",
        "bokeh", "depthoffield", "dof", "shallowdof", "motionblur", "ndfilter", "cplfilter", "mistfilter",
        "pro-mist", "stepupring", "lenshood", "lensflare", "lenscap", "sensor", "fullframe", "apsc",
        "microfourthirds", "m43", "dynamicrange", "logprofile", "slog", "slog3", "dlog", "clog", "vlog-l",
        "hlg", "hdr", "sdr", "rec709", "rec2020", "lut", "lutpack", "colorprofile", "flatprofile",

        // === GEAR, RIGS, LIGHTING & ACCESSORIES ===
        "tripod", "monopod", "gorillapod", "gimbal", "stabilizer", "slider", "jib", "crane", "dolly",
        "camerarig", "cage", "smallrig", "top_handle", "sidehandle", "quickrelease", "arca-swiss",
        "actioncam", "gopro", "hero12", "hero13", "insta360", "x3", "x4", "acepro", "dji", "osmo",
        "pocket3", "action4", "action5", "drone", "fpv", "mavic", "air3", "mini4pro", "avata",
        "teleprompter", "greenscreen", "bluescreen", "chromakey", "backdrop", "seamlesspaper",
        "softbox", "ringlight", "keylight", "filllight", "backlight", "rimlight", "hairlight",
        "practicallight", "ambientlight", "rgblight", "ledpanel", "tubelight", "spotlight", "coblight",
        "aputure", "amaran", "godox", "neewer", "nanlite", "ulanzi", "diffuser", "reflector",
        "grid", "barn_doors", "lightstand", "c-stand", "sandbag", "magicarm", "superclamp",
        "cabletie", "cablemanagement", "batterygrip", "dummybattery", "vmount", "npf970", "powerbank",

        // === AUDIO, MICROPHONES & SOUND DESIGN ===
        "audio", "microphone", "mic", "wirelessmic", "lavalier", "lavmic", "shotgunmic", "boommic",
        "condensermic", "dynamicmic", "usbmicrophone", "xlr", "xlrcable", "rodemicrophones", "wirelessgo",
        "wirelesspro", "djimic", "djimic2", "hollyland", "larkm2", "boya", "shuresm7b", "shuremv7",
        "podmic", "quadcast", "fifine", "audiointerface", "focusrite", "scarlett", "motu", "rodecaster",
        "zoomh6", "tascam", "soundrecorder", "gain", "gainstaging", "preamp", "phantompower", "+48v",
        "deadcat", "windscreen", "popfilter", "shockmount", "boomarm", "soundproofing", "acousticfoam",
        "sounddesign", "soundeffect", "sfx", "foley", "bgm", "backgroundmusic", "soundtrack",
        "royaltyfree", "copyrightfree", "ncs", "lofi", "epidemicsound", "artlist", "envatomarket",
        "audiojungle", "audiomachine", "whoosh", "riser", "impact", "bassdrop", "glitchsfx", "mouseclick",
        "keyboardclack", "pop", "ding", "cinematichit", "vinylcrackle", "white_noise", "equalizer",
        "eq", "highpassfilter", "lowpassfilter", "compressor", "limiter", "de-esser", "denoiser",
        "noiseremoval", "noisefloor", "decibel", "db", "peak", "clipping", "normalize", "lufs",
        "loudness", "stereopan", "monocompatibility", "autoducking", "voiceover", "vo", "dubbing",
        "podcast", "podcasting", "interview", "monitoring", "headphones", "iems", "audiomonitor",

        // === VIDEO EDITING, POST-PRODUCTION & VFX ===
        "editing", "videoeditor", "editor", "timeline", "playhead", "scrubbing", "cut", "split",
        "trim", "slice", "rippleedit", "rollingedit", "sliptool", "slidetool", "razortool",
        "snapping", "inpoint", "outpoint", "marker", "clipduration", "speedramp", "slowmo",
        "fastforward", "freezeframe", "reverseclip", "keyframe", "keyframes", "easing", "easein",
        "easeout", "linearease", "bezier", "curves", "motiongraphics", "titlecard", "lowerthird",
        "subtitle", "subtitles", "caption", "captions", "autocaption", "burnedcaptions", "srt",
        "typography", "fontpairing", "callout", "arrowanimation", "highlight", "zoomin", "zoomout",
        "punchin", "punchout", "whipfade", "crossdissolve", "dip_to_black", "dip_to_white",
        "morphcut", "jumpcut", "matchcut", "j-cut", "l-cut", "smashcut", "invisiblecut", "transition",
        "preset", "template", "overlay", "particles", "lightleak", "filmgrain", "halftones",
        "chromaticaberration", "vignette", "lensdistortion", "gloweffect", "blur", "gaussianblur",
        "radialblur", "motionblur", "masking", "rotoscoping", "greenkeying", "chromakeying",
        "ultrakey", "garbage_matte", "motiontracking", "cameratracking", "cornerpin", "warpstabilizer",
        "stabilization", "colorcorrection", "colorgrading", "colorwheels", "scopemonitor", "waveform",
        "parade", "vectorscope", "lumetri", "powergrade", "node", "serialnode", "parallel_node",
        "render", "rendering", "export", "exportsettings", "codec", "h264", "h265", "hevc",
        "prores", "dnxhr", "av1", "bitrate", "cbr", "vbr", "renderqueue", "proxy", "proxymedia",
        "multicam", "syncclips", "nesting", "compoundclip", "adjustmentlayer", "aspectratiobars",

        // === PLATFORM, ALGORITHM, GROWTH & SEO ===
        "algorithm", "recommendation", "algorithmhack", "feed", "foryou", "foryoupage", "fyp",
        "explorepage", "discover", "trending", "trend", "viral", "virality", "viralvideo",
        "hook", "3secondhook", "storytelling", "retention", "watchtime", "audienceretention",
        "avd", "averageduration", "percentageviewed", "dropoff", "retentiongraph", "ctr",
        "clickthroughrate", "impressions", "reach", "views", "engagement", "like", "share",
        "comment", "save", "bookmark", "repost", "stitch", "duet", "subscribe", "subscriber",
        "subscribers", "subcount", "subgoal", "milestone", "100k", "1m", "silverplaybutton",
        "goldplaybutton", "diamondplaybutton", "creatorawards", "notificationbell", "bellicon",
        "communitytab", "communitypost", "poll", "story", "stories", "reels", "shorts", "ytshorts",
        "tiktokvideo", "longform", "shortform", "midform", "thumbnail", "customthumbnail",
        "clickbait", "abtesting", "thumbnailtesting", "title", "catchytitle", "videotitle",
        "description", "tag", "tags", "hashtag", "hashtags", "keyword", "keywordresearch",
        "seo", "vidiq", "tubebuddy", "trends", "google_trends", "playlist", "series", "season",
        "premiere", "livestream", "livebroadcast", "livechat", "superchat", "superthanks",
        "superstickers", "channelmembership", "joinbutton", "exclusivebadge", "emotes",
        "customemotes", "loyaltybadge", "shadowban", "communityguidelines", "terms_of_service",
        "copyright", "copyrightclaim", "copyrightstrike", "contentid", "fairuse", "dmca",
        "reusedcontent", "repetitiouscontent", "yellowdollar", "demonetized", "monetization",
        "monetized", "youtubechannel", "tiktokshop", "facebookpage", "instapage", "creatorstudio",
        "youtubestudio", "ytstudio", "analyticstab", "realtimeviews", "traffic_source",
        "suggestedvideos", "browsefeatures", "youtubesearch", "external_traffic", "audience_demographics",

        // === BUSINESS, SPONSORSHIP, MERCH & REVENUE ===
        "adsense", "googleadsense", "cpm", "rpm", "revenue", "estimatedrevenue", "payout",
        "banktransfer", "threshold", "sponsor", "sponsorship", "sponsoredvideo", "branddeal",
        "brandpartnership", "collab", "collaboration", "affiliate", "affiliatelink", "affiliatemarketing",
        "promocode", "discountcode", "mediakit", "ratecard", "deliverables", "usage_rights",
        "exclusivity", "ugc", "ugccreator", "brandambassador", "productplacement", "gifted",
        "unboxing", "prpackage", "reviewunit", "merch", "merchandise", "merchdrop", "hoodie",
        "tshirt", "stickers", "patreon", "buymeacoffee", "kofi", "donations", "tipping",
        "kickstarter", "crowdfunding", "fanbase", "agency", "creatoragency", "talentmanager",

        // === STREAMING, GAMING & BROADCASTING ===
        "stream", "streamer", "livestreaming", "streaming", "justchatting", "irlstream",
        "gamingstream", "gameplay", "walkthrough", "playthrough", "letsplay", "speedrun",
        "speedrunner", "esports", "tournament", "scrims", "fragmovie", "montage", "clutch",
        "highlights", "streamdeck", "elgato", "capturecard", "camlink", "hd60x", "keylightair",
        "wave3", "greenscreen_stream", "streamoverlay", "alertbox", "chatbox", "followergoal",
        "subathon", "charitystream", "marathonstream", "raid", "host", "hypetrain",
        "bitrate_drop", "droppedframes", "streambitrate", "streamkey", "streamdelay", "obssetup",
        "vtuber", "vroid", "pngtuber", "facemocapanimation", "avatar", "mod", "moderator",
        "nightbot", "streamelements", "moobot", "chatrules", "banhammer", "timeout", "whisper",

        // === CONTENT FORMATS, THEMES & NICHE TERMS ===
        "tutorial", "howtovideo", "guide", "masterclass", "breakdown", "explainer", "videoessay",
        "deepdive", "documentary", "minidoc", "investigation", "case_study", "review", "honestreview",
        "comparison", "unboxingvideo", "haul", "roomtour", "setuptour", "desktour", "camerabagtour",
        "dayinthelife", "ditl", "morningroutine", "nightroutine", "whatieatinaday", "wieiad",
        "studywithme", "workwithme", "grwm", "getreadywithme", "ootd", "outfitoftheday",
        "mukbang", "asmr", "whispering", "tingles", "reaction", "reactionvideo", "reactingto",
        "commentary", "roast", "critique", "tierlist", "ranking", "tierlistmaker", "challenge",
        "24hourchallenge", "overnightchallenge", "surviving24hours", "prank", "socialexperiment",
        "streetinterview", "voxpop", "storytime", "bts", "behindthescenes", "bloopers",
        "outtakes", "deletedscenes", "compilation", "bestof", "top10", "top5", "countdown",
        "parody", "meme", "shitpost", "skit", "comedy", "shortfilm", "trailer", "teaser",

        // === TRENDING SOCIAL SLANG, INTERNET & CREATOR DIALECT ===
        "pov", "fr", "forreal", "nocap", "cap", "goat", "w", "huge_w", "l", "big_l",
        "based", "cringe", "slay", "aesthetic", "vibe", "vibes", "drip", "flex",
        "bet", "lowkey", "highkey", "sus", "simp", "sigma", "rizz", "gyatt",
        "skibidi", "mid", "clutch", "toxic", "stan", "fan", "hater", "troll",
        "ratio", "cooked", "let_him_cook", "maincharacter", "rentfree", "understoodtheassignment",
        "era", "inmyera", "core", "softlaunch", "hardlaunch", "lore", "canon",
        "gatekeep", "gaslight", "girlboss", "npc", "delulu", "solulu", "ick",
        "glowup", "shadowbanned", "algorithmfavors", "trendingnow", "soundtracktrend",
        "originalaudio", "duetthis", "stitchthis", "linkinbio", "checkbio", "tapthelink",
        "swipeup", "taphere", "linkindescription", "pinnedcomment", "heartthecomment",
        "replywithvideo", "qna", "askmeanything", "ama", "shoutout", "collabwithme",
        "tagafriend", "shareswithfriends", "subscribefor_more", "leaveacomment", "smashthatlikebutton",
        "hitthebell", "watchtilltheend", "waitforit", "part2", "part3", "storytimepart2"
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
