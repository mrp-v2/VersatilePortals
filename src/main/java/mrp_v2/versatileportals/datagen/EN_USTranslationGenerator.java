package mrp_v2.versatileportals.datagen;

import mrp_v2.mrplibrary.datagen.providers.LanguageProvider;
import mrp_v2.versatileportals.VersatilePortals;
import mrp_v2.versatileportals.client.gui.screen.PortalControllerScreen;
import mrp_v2.versatileportals.item.ExistingWorldControlItem;
import mrp_v2.versatileportals.util.ObjectHolder;
import mrp_v2.versatileportals.world.EventHandler;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;

public class EN_USTranslationGenerator extends LanguageProvider {

    public static final Component VERSATILE_PORTAL_CREATIVE_TAB_LABEL = MutableComponent.create(new TranslatableContents(VersatilePortals.ID + ".creativeTab", null, new Object[0]));

    public EN_USTranslationGenerator(PackOutput output, String modid) {
        super(output, modid, "en_us");
    }

    @Override
    protected void addTranslations() {
        add(ObjectHolder.PORTAL_BLOCK.get(), "Portal");
        add(EventHandler.noPortalController, "A Portal Controller could not be found for this portal");
        add(ObjectHolder.PORTAL_CONTROLLER_BLOCK.get(), "Portal Controller");
        add(PortalControllerScreen.colorBLabel, "B: ");
        add(PortalControllerScreen.colorGLabel, "G: ");
        add(PortalControllerScreen.colorRLabel, "R: ");
        add(PortalControllerScreen.controlItemLabel, "Control Item");
        add(PortalControllerScreen.matchControlItemLabel, "Set Color from Item");
        add(EventHandler.noControlItemContent, "There is no control item or it is invalid");
        add(EventHandler.teleportedContent, "Teleported");
        add(EventHandler.teleportingInFunction.apply(new Object[0]), "Teleporting in %s...");
        add(ObjectHolder.PORTAL_FRAME_BLOCK.get(), "Portal Frame");
        add(ObjectHolder.EMPTY_EXISTING_WORLD_TELEPORT_ITEM.get(), "Empty Existing World Control");
        add(ObjectHolder.EXISTING_WORLD_TELEPORT_ITEM.get(), "Existing World Control");
        add(ExistingWorldControlItem.noTeleportSelf, "The control item must be for a different dimension");
        add(ExistingWorldControlItem.worldDoesNotExist, "There is no world matching the control item");
        add(ObjectHolder.PORTAL_LIGHTER_ITEM.get(), "Portal Lighter");
        add(ExistingWorldControlItem.DISPLAY_NAME, "Control Item Editor");
        add(VERSATILE_PORTAL_CREATIVE_TAB_LABEL, VersatilePortals.DISPLAY_NAME);
    }
}
