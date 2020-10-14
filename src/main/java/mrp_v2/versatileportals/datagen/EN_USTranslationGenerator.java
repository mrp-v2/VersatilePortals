package mrp_v2.versatileportals.datagen;

import mrp_v2.mrp_v2datagenlibrary.datagen.TranslationGenerator;
import mrp_v2.versatileportals.VersatilePortals;
import mrp_v2.versatileportals.item.BasicSingleItem;
import mrp_v2.versatileportals.util.ObjectHolder;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.HashMap;

public class EN_USTranslationGenerator extends TranslationGenerator
{
    private static final HashMap<String, String> translations = new HashMap<>();

    public EN_USTranslationGenerator(DataGenerator gen, String modid)
    {
        super(gen, modid, "en_us");
    }

    public static TranslationTextComponent makeTextTranslation(String key, String name)
    {
        return new TranslationTextComponent(makeStringTranslation(key, name));
    }

    public static String makeStringTranslation(String key, String name)
    {
        translations.put(key, name);
        return key;
    }

    @Override protected void addTranslations()
    {
        add(ObjectHolder.PORTAL_BLOCK, "Portal");
        add(ObjectHolder.PORTAL_FRAME_BLOCK, "Portal Frame");
        add(ObjectHolder.PORTAL_CONTROLLER_BLOCK, "Portal Controller");
        add(ObjectHolder.EMPTY_EXISTING_WORLD_TELEPORT_ITEM, "Empty Existing World Control");
        add(ObjectHolder.PORTAL_LIGHTER_ITEM, "Portal Lighter");
        add(BasicSingleItem.MAIN_ITEM_GROUP, VersatilePortals.DISPLAY_NAME);
        translations.forEach(this::add);
    }
}
