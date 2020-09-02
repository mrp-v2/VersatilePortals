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

    @SubscribeEvent public static void onLoad(final ModConfig.Loading configEvent)
    {
        LogManager.getLogger()
                .debug("Loaded " + RandomDimensions.DISPLAY_NAME + " config file {}",
                        configEvent.getConfig().getFileName());
    }

    @SubscribeEvent public static void onFileChange(final ModConfig.Reloading configEvent)
    {
        LogManager.getLogger().debug(RandomDimensions.DISPLAY_NAME + " config just got changed on the file system!");
    }

    public static class Server
    {
        public final ForgeConfigSpec.BooleanValue harderCrafting;

        Server(final ForgeConfigSpec.Builder builder)
        {
            builder.comment(" Server configuration settings.").push("server");
            harderCrafting = builder.comment(
                    " Whether to increase the difficulty of recipes by increasing their costs." +
                            " For example, replacing redstone dust with redstone blocks.")
                    .translation(TRANSLATION_KEY + ".harder_crafting")
                    .worldRestart()
                    .define("harder_crafting", false);
            builder.pop();
        }
    }
}
