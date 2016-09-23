package hellfirepvp.astralsorcery.client.util;

import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.common.constellation.CelestialHandler;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TexturePreloader
 * Created by HellFirePvP
 * Date: 21.09.2016 / 15:44
 */
public class TexturePreloader {

    public static void preloadTextures() {
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guiJResBG")         .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guiCloud1")         .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guiConPaper")       .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guiJBlank")         .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guiJSpace")         .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guiJBookmark")      .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guiAltar1")         .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "lightbeam")      .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "burst2")         .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "ceffect1")       .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "ceffect2")       .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "ceffect3")       .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "starlight_store").allocateGlId();
        MoonPhaseRenderHelper.getMoonPhaseTexture(CelestialHandler.MoonPhase.NEW); //Loads all phase textures
    }

}