/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.config;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.data.config.entry.ConfigEntry;
import hellfirepvp.astralsorcery.common.network.packet.server.PktSyncConfig;
import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: Config
 * Created by HellFirePvP
 * Date: 07.05.2016 / 01:14
 */
public class Config {

    //TODO remember to do a configurable itemSword-classname blacklist for sharpening.

    private static Configuration latestConfig;
    public static List<PktSyncConfig.SyncTuple> savedSyncTuples = new LinkedList<>();

    //public static boolean stopOnIllegalState = true;
    public static boolean spawnRockCrystalOres = true;
    public static boolean respectIdealDistances = true;
    public static int crystalDensity = 15;
    public static int aquamarineAmount = 64;
    public static int marbleAmount = 4, marbleVeinSize = 20;
    public static int constellationPaperRarity = 10, constellationPaperQuality = 2;

    public static boolean clientPreloadTextures = true;
    public static boolean giveJournalFirst = true;
    public static boolean doesMobSpawnDenyDenyEverything = false;

    @Sync public static boolean craftingLiqCrystalGrowth = true;
    @Sync public static boolean craftingLiqCelestialCrystalForm = true;

    public static boolean enableRetroGen = false;

    //Also has a squared field to provide slightly faster rendering.
    public static int maxEffectRenderDistance = 64, maxEffectRenderDistanceSq;

    public static int particleAmount = 2;

    public static int ambientFlareChance = 20;
    public static boolean flareKillsBats = true;

    public static boolean shouldChargedToolsRevert = false;
    public static int revertStart = 40;
    public static int revertChance = 80;

    @Sync public static double swordSharpMultiplier = 0.1;

    @Sync public static float illuminationWandUseCost = 0.5F;
    @Sync public static float architectWandUseCost = 0.07F;
    @Sync public static float exchangeWandUseCost = 0.08F;

    @Sync public static int dimensionIdSkyRift = -81;

    public static Integer[] constellationSkyDimWhitelist = new Integer[0];
    public static List<Integer> weakSkyRendersWhitelist = Lists.newArrayList();
    public static List<String> modidOreGenBlacklist = Lists.newArrayList();
    public static List<Integer> worldGenDimWhitelist = Lists.newArrayList();
    public static boolean performNetworkIntegrityCheck = false;

    private static List<ConfigEntry> dynamicConfigEntries = new LinkedList<>();

    private Config() {}

    public static void load(File file) {
        latestConfig = new Configuration(file);
        latestConfig.load();
        loadData();
        latestConfig.save();
    }

    public static void addDynamicEntry(ConfigEntry entry) {
        if(latestConfig != null) {
            throw new IllegalStateException("Too late to add dynamic configuration entries");
        }
        dynamicConfigEntries.add(entry);
    }

    public static void rebuildClientConfig() {
        try {
            for (PktSyncConfig.SyncTuple tuple : savedSyncTuples) {
                Field field = Config.class.getField(tuple.key);
                field.set(null, tuple.value);
            }
            savedSyncTuples.clear();
        } catch (Throwable exc) {
            AstralSorcery.log.error("Failed to reapply saved client config!");
            throw new RuntimeException(exc);
        }
    }

    private static void loadData() {
        giveJournalFirst = latestConfig.getBoolean("giveJournalAtFirstJoin", "general", true, "If set to 'true', the player will receive an AstralSorcery Journal if he joins the server for the first time.");
        doesMobSpawnDenyDenyEverything = latestConfig.getBoolean("doesMobSpawnDenyAllTypes", "general", false, "If set to 'true' anything that prevents mobspawning by this mod, will also prevent EVERY natural mobspawning of any mobtype. When set to 'false' it'll only stop monsters from spawning.");
        swordSharpMultiplier = latestConfig.getFloat("swordSharpenedMultiplier", "general", 0.1F, 0.0F, 10000.0F, "Defines how much the 'sharpened' modifier increases the damage of the sword if applied. Config value is in percent.");
        String[] dimWhitelist = latestConfig.getStringList("skySupportedDimensions", "general", new String[] { "0" }, "Whitelist of dimension ID's that will have special sky rendering");
        String[] weakSkyRenders = latestConfig.getStringList("weakSkyRenders", "general", new String[] {}, "IF a dimensionId is listed in 'skySupportedDimensions' you can add it here to keep its sky render, but AS will try to render only constellations on top of its existing sky render.");
        dimensionIdSkyRift = latestConfig.getInt("dimensionIdSkyRift", "general", -81, Integer.MIN_VALUE, Integer.MAX_VALUE, "DimensionId for SkyRift");
        String[] oreModidBlacklist = latestConfig.getStringList("oreGenBlacklist", "general", new String[] { "techreborn" }, "List any number of modid's here and the aevitas perk & mineralis ritual will not spawn ores that originate from any of the mods listed here.");
        modidOreGenBlacklist = Lists.newArrayList(oreModidBlacklist);

        ambientFlareChance = latestConfig.getInt("EntityFlare.ambientspawn", "entities", 20, 0, 200_000, "Defines how common ***ambient*** flares are. the lower the more common. 0 = ambient ones don't appear/disabled.");
        flareKillsBats = latestConfig.getBoolean("EntityFlare.killbats", "entities", true, "If this is set to true, occasionally, a spawned flare will (attempt to) kill bats close to it.");

        illuminationWandUseCost = latestConfig.getFloat("wandCost_illumination", "tools", 0.5F, 0.0F, 1.0F, "Sets the alignment charge cost for one usage of the illumination wand");
        architectWandUseCost = latestConfig.getFloat("wandCost_architect", "tools", 0.03F, 0.0F, 1.0F, "Sets the alignment charge cost for one usage of the architect wand");
        exchangeWandUseCost = latestConfig.getFloat("wandCost_exchange", "tools", 0.002F, 0.0F, 1.0F, "Sets the alignment charge cost for one usage of the exchange wand");

        shouldChargedToolsRevert = latestConfig.getBoolean("chargedCrystalToolsRevert", "tools", shouldChargedToolsRevert, "If this is set to true, charged crystals tools can revert back to their inert state.");
        revertStart = latestConfig.getInt("chargedCrystalToolsRevertStart", "tools", revertStart, 0, Integer.MAX_VALUE - 1, "Defines the minimum uses a user at least gets before it's trying to revert to an inert crystal tool.");
        revertChance = latestConfig.getInt("chargedCrystalToolsRevertChance", "tools", revertChance, 1, Integer.MAX_VALUE, "After 'chargedCrystalToolsRevertStart' uses, it will random.nextInt(chance) == 0 try and see if the tool gets reverted to its inert crystal tool.");

        craftingLiqCrystalGrowth = latestConfig.getBoolean("liquidStarlightCrystalGrowth", "crafting", true, "Set this to false to disable Rock/Celestial Crystal growing in liquid starlight.");
        craftingLiqCelestialCrystalForm = latestConfig.getBoolean("liquidStarlightCelestialCrystalCluster", "crafting", true, "Set this to false to disable crystal + stardust -> Celestial Crystal cluster forming");

        latestConfig.addCustomCategoryComment("lightnetwork", "Maintenance options for the Starlight network. Use the integrity check when you did a bigger rollback or MC-Edited stuff out of the world. Note that it will only affect worlds that get loaded. So if you edited out something on, for example, dimension -76, be sure to go into that dimension with the maintenance options enabled to properly perform maintenance there.");
        performNetworkIntegrityCheck = latestConfig.getBoolean("performNetworkIntegrityCheck", "lightnetwork", false, "NOTE: ONLY run this once and set it to false again afterwards, nothing will be gained by setting this to true permanently, just longer loading times. When set to true and the server started, this will perform an integrity check over all nodes of the starlight network whenever a world gets loaded, removing invalid ones in the process. This might, depending on network sizes, take a while. It'll leave a message in the console when it's done. After this check has been run, you might need to tear down and rebuild your starlight network in case something doesn't work anymore.");

        maxEffectRenderDistance = latestConfig.getInt("maxEffectRenderDistance", "rendering", 64, 1, 512, "Defines how close to the position of a particle/floating texture you have to be in order for it to render.");
        maxEffectRenderDistanceSq = maxEffectRenderDistance * maxEffectRenderDistance;
        clientPreloadTextures = latestConfig.getBoolean("preloadTextures", "rendering", true, "If set to 'true' the mod will preload most of the bigger textures during postInit. This provides a more fluent gameplay experience (as it doesn't need to load the textures when they're first needed), but increases loadtime.");
        particleAmount = latestConfig.getInt("particleAmount", "rendering", 2, 0, 2, "Sets the amount of particles/effects: 0 = minimal (only necessary particles will appear), 1 = lowered (most unnecessary particles will be filtered), 2 = all particles are visible");

        spawnRockCrystalOres = latestConfig.getBoolean("rockCrystalsEnabled", "worldgen", true, "Set this to false to disable rock crystal oregen entirely.");
        crystalDensity = latestConfig.getInt("crystalDensity", "worldgen", 15, 0, 40, "Defines how frequently rock-crystals will spawn underground. The lower the number, the more frequent crystals will spawn. (onWorldGen: random.nextInt(crystalDensity) == 0 -> gen 1 ore in that chunk)");
        marbleAmount = latestConfig.getInt("generateMarbleAmount", "worldgen", 4, 0, 32, "Defines how many marble veins are generated per chunk. 0 = disabled");
        marbleVeinSize = latestConfig.getInt("generateMarbleVeinSize", "worldgen", 20, 1, 32, "Defines how big generated marble veins are.");
        aquamarineAmount = latestConfig.getInt("generateAquamarineAmount", "worldgen", 64, 0, 2048, "Defines how many aquamarine ores it'll attempt to generate in per chunk. 0 = disabled");
        constellationPaperRarity = latestConfig.getInt("constellationPaperRarity", "worldgen", 10, 1, 128, "Defines the rarity of the constellation paper item in loot chests.");
        constellationPaperQuality = latestConfig.getInt("constellationPaperQuality", "worldgen", 2, 1, 128, "Defines the quality of the constellation paper item in loot chests.");
        respectIdealDistances = latestConfig.getBoolean("respectIdealStructureDistances", "worldgen", respectIdealDistances, "If this is set to true, the world generator will try and spawn structures more evenly distributed by their 'ideal' distance set in their config entries. WARNING: might add additional worldgen time.");
        String[] dimGenWhitelist = latestConfig.getStringList("worldGenWhitelist", "worldgen", new String[] { "0" }, "the Astral Sorcery-specific worldgen will only run in Dimension ID's listed here.");

        enableRetroGen = latestConfig.getBoolean("enableRetroGen", "retrogen", false, "WARNING: Setting this to true, will check on every chunk load if the chunk has been generated depending on the current AstralSorcery version. If the chunk was then generated with an older version, the mod will try and do the worldgen that's needed from the last recorded version to the current version. DO NOT ENABLE THIS FEATURE UNLESS SPECIFICALLY REQUIRED. It might/will slow down chunk loading.");

        fillWhitelistIDs(dimWhitelist);
        fillWeakSkyRenders(weakSkyRenders);
        fillDimGenWhitelist(dimGenWhitelist);

        for (ConfigEntry ce : dynamicConfigEntries) {
            ce.loadFromConfig(latestConfig);
        }
    }

    private static void fillDimGenWhitelist(String[] dimGenWhitelist) {
        List<Integer> out = new ArrayList<>();
        for (String s : dimGenWhitelist) {
            if(s.isEmpty()) continue;
            try {
                out.add(Integer.parseInt(s));
            } catch (NumberFormatException exc) {
                AstralSorcery.log.warn("[AstralSorcery] Error while reading config entry 'worldGenWhitelist': " + s + " is not a number!");
            }
        }
        worldGenDimWhitelist = new ArrayList<>(out.size());
        worldGenDimWhitelist.addAll(out);
        Collections.sort(worldGenDimWhitelist);
    }

    private static void fillWeakSkyRenders(String[] weakSkyRenders) {
        List<Integer> out = new ArrayList<>();
        for (String s : weakSkyRenders) {
            if(s.isEmpty()) continue;
            try {
                out.add(Integer.parseInt(s));
            } catch (NumberFormatException exc) {
                AstralSorcery.log.warn("[AstralSorcery] Error while reading config entry 'weakSkyRenders': " + s + " is not a number!");
            }
        }
        weakSkyRendersWhitelist = new ArrayList<>(out.size());
        weakSkyRendersWhitelist.addAll(out);
        Collections.sort(weakSkyRendersWhitelist);
    }

    private static void fillWhitelistIDs(String[] dimWhitelist) {
        List<Integer> out = new ArrayList<>();
        for (String s : dimWhitelist) {
            if(s.isEmpty()) continue;
            try {
                out.add(Integer.parseInt(s));
            } catch (NumberFormatException exc) {
                AstralSorcery.log.warn("[AstralSorcery] Error while reading config entry 'skySupportedDimensions': " + s + " is not a number!");
            }
        }
        constellationSkyDimWhitelist = new Integer[out.size()];
        for (int i = 0; i < out.size(); i++) {
            constellationSkyDimWhitelist[i] = out.get(i);
        }
        Arrays.sort(constellationSkyDimWhitelist);
    }

}
