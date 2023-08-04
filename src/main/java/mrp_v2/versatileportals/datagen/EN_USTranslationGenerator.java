package mrp_v2.versatileportals.datagen;

import mrp_v2.mrplibrary.datagen.providers.LanguageProvider;
import mrp_v2.versatileportals.VersatilePortals;
import mrp_v2.versatileportals.client.gui.screen.PortalControllerScreen;
import mrp_v2.versatileportals.item.ExistingWorldControlItem;
import mrp_v2.versatileportals.util.ObjectHolder;
import mrp_v2.versatileportals.world.EventHandler;
import net.minecraft.data.DataGenerator;

public class EN_USTranslationGenerator extends LanguageProvider {
    public EN_USTranslationGenerator(DataGenerator gen, String modid) {
        super(gen, modid, "en_us");
    }

    @Override
    protected void addTranslations() {
        add(ObjectHolder.PORTAL_BLOCK.get(), "Portal");
        add(EventHandler.noPortalController.getKey(), "A Portal Controller could not be found for this portal");
        add(ObjectHolder.PORTAL_CONTROLLER_BLOCK.get(), "Portal Controller");
        add(PortalControllerScreen.colorBLabel.getKey(), "B: ");
        add(PortalControllerScreen.colorGLabel.getKey(), "G: ");
        add(PortalControllerScreen.colorRLabel.getKey(), "R: ");
        add(PortalControllerScreen.controlItemLabel.getKey(), "Control Item");
        add(PortalControllerScreen.matchControlItemLabel.getKey(), "Set Color from Item");
        add(EventHandler.noControlItem.getKey(), "There is no control item or it is invalid");
        add(EventHandler.teleported.getKey(), "Teleported");
        add(EventHandler.teleportingInFunction.apply(new Object[0]).getKey(), "Teleporting in %s...");
        add(ObjectHolder.PORTAL_FRAME_BLOCK.get(), "Portal Frame");
        add(ObjectHolder.EMPTY_EXISTING_WORLD_TELEPORT_ITEM.get(), "Empty Existing World Control");
        add(ObjectHolder.EXISTING_WORLD_TELEPORT_ITEM.get(), "Existing World Control");
        add(ExistingWorldControlItem.noTeleportSelf.getKey(), "The control item must be for a different dimension");
        add(ExistingWorldControlItem.worldDoesNotExist.getKey(), "There is no world matching the control item");
        add(ObjectHolder.PORTAL_LIGHTER_ITEM.get(), "Portal Lighter");
        add(ExistingWorldControlItem.DISPLAY_NAME.getKey(), "Control Item Editor");
        add(ObjectHolder.MAIN_ITEM_GROUP, VersatilePortals.DISPLAY_NAME);
    }
}
