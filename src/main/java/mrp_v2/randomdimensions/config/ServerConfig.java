package mrp_v2.randomdimensions.config;

import mrp_v2.randomdimensions.RandomDimensions;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;

public class ServerConfig
{
    public static final ForgeConfigSpec SERVER_SPEC;
    public static final Server SERVER;
    
    private static final String TRANSLATION_KEY = RandomDimensions.ID + ".config";

    static
    {
        final Pair<Server, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Server::new);
        SERVER_SPEC = specPair.getRight();
        SERVER = specPair.getLeft();
        
    }

    public static class Server
    {
        Server(final ForgeConfigSpec.Builder builder)
        {
            builder.comment("Server configuration settings.").push("server");
            builder.pop();
        }
    }

    @SubscribeEvent
    public static void onLoad(final ModConfig.Loading configEvent) {
        LogManager.getLogger().debug("Loaded " + RandomDimensions.DISPLAY_NAME + " config file {}",
                configEvent.getConfig().getFileName());
    }

    @SubscribeEvent
    public static void onFileChange(final ModConfig.Reloading configEvent) {
        LogManager.getLogger()
                .debug(RandomDimensions.DISPLAY_NAME + " config just got changed on the file system!");
    }
}
