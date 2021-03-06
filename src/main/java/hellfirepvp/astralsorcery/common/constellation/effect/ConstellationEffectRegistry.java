/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.effect;

import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.constellation.effect.aoe.*;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.event.APIRegistryEvent;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ConstellationEffectRegistry
 * Created by HellFirePvP
 * Date: 01.10.2016 / 15:49
 */
public class ConstellationEffectRegistry {

    private static Map<IWeakConstellation, ConstellationEffectProvider> providerMap = new HashMap<>();
    private static Map<IWeakConstellation, ConstellationEffect> singleRenderInstances = new HashMap<>();

    public static void init() {
        register(Constellations.aevitas,    CEffectAevitas::new);
        register(Constellations.discidia,   CEffectDiscidia::new);
        register(Constellations.armara,     CEffectArmara::new);
        register(Constellations.vicio,      CEffectVicio::new);

        register(Constellations.mineralis,  CEffectMineralis::new);
        register(Constellations.lucerna,    CEffectLucerna::new);
        register(Constellations.bootes,     CEffectBootes::new);
        register(Constellations.horologium, CEffectHorologium::new);
        register(Constellations.octans,     CEffectOctans::new);
        register(Constellations.fornax,     CEffectFornax::new);

        MinecraftForge.EVENT_BUS.post(new APIRegistryEvent.ConstellationEffectRegister());
    }

    public static void addDynamicConfigEntries() {
        Config.addDynamicEntry(new CEffectAevitas());
        Config.addDynamicEntry(new CEffectDiscidia());
        Config.addDynamicEntry(new CEffectArmara());
        Config.addDynamicEntry(new CEffectVicio());

        Config.addDynamicEntry(new CEffectHorologium());
        Config.addDynamicEntry(new CEffectMineralis());
        Config.addDynamicEntry(new CEffectLucerna());
        Config.addDynamicEntry(new CEffectBootes());
        Config.addDynamicEntry(new CEffectOctans());
        Config.addDynamicEntry(new CEffectFornax());
    }

    private static void register(IWeakConstellation c, ConstellationEffectProvider provider) {
        providerMap.put(c, provider);
        singleRenderInstances.put(c, provider.provideEffectInstance());
    }

    public static void registerFromAPI(IWeakConstellation c, Function<Void, ConstellationEffect> providerFunc) {
        providerMap.put(c, new APIConstellationProvider(providerFunc));
        singleRenderInstances.put(c, providerFunc.apply(null));
    }

    @Nullable
    public static ConstellationEffect clientRenderInstance(IWeakConstellation c) {
        return singleRenderInstances.get(c);
    }

    @Nullable
    public static ConstellationEffect getEffect(IWeakConstellation c) {
        ConstellationEffectProvider p = providerMap.get(c);
        if(p != null) {
            return p.provideEffectInstance();
        }
        return null;
    }

    public static interface ConstellationEffectProvider {

        public ConstellationEffect provideEffectInstance();

    }

    public static class APIConstellationProvider implements ConstellationEffectProvider {

        private final Function<Void, ConstellationEffect> providerFunc;

        public APIConstellationProvider(Function<Void, ConstellationEffect> providerFunc) {
            this.providerFunc = providerFunc;
        }

        @Override
        public ConstellationEffect provideEffectInstance() {
            return providerFunc.apply(null);
        }
    }

}
