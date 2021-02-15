package mrp_v2.versatileportals.datagen;

import mrp_v2.mrplibrary.datagen.providers.LanguageProvider;
import mrp_v2.versatileportals.VersatilePortals;
import mrp_v2.versatileportals.client.gui.screen.PortalControllerScreen;
import mrp_v2.versatileportals.util.ObjectHolder;
import net.minecraft.data.DataGenerator;

public class EN_USTranslationGenerator extends LanguageProvider
{
    public EN_USTranslationGenerator(DataGenerator gen, String modid)
    {
        super(gen, modid, "en_us");
    }

    @Override protected void addTranslations()
    {
        add(ObjectHolder.PORTAL_BLOCK.get(), "Portal");
        add(ObjectHolder.PORTAL_FRAME_BLOCK.get(), "Portal Frame");
        add(ObjectHolder.PORTAL_CONTROLLER_BLOCK.get(), "Portal Controller");
        add(ObjectHolder.EMPTY_EXISTING_WORLD_TELEPORT_ITEM.get(), "Empty Existing World Control");
        add(ObjectHolder.EXISTING_WORLD_TELEPORT_ITEM.get(), "Existing World Control");
        add(ObjectHolder.PORTAL_LIGHTER_ITEM.get(), "Portal Lighter");
        add(ObjectHolder.MAIN_ITEM_GROUP, VersatilePortals.DISPLAY_NAME);
        PortalControllerScreen.staticInit();
        super.addTranslations();
    }
}
