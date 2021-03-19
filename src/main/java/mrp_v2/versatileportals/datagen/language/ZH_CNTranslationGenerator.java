package mrp_v2.versatileportals.datagen.language;

import mrp_v2.mrplibrary.datagen.providers.LanguageProvider;
import mrp_v2.versatileportals.client.gui.screen.PortalControllerScreen;
import mrp_v2.versatileportals.item.ExistingWorldControlItem;
import mrp_v2.versatileportals.util.ObjectHolder;
import mrp_v2.versatileportals.world.EventHandler;
import net.minecraft.data.DataGenerator;

public class ZH_CNTranslationGenerator extends LanguageProvider
{
    public ZH_CNTranslationGenerator(DataGenerator gen, String modid)
    {
        super(gen, modid, "zh_cn");
    }

    @Override protected void addTranslations()
    {
        add(ObjectHolder.PORTAL_BLOCK.get(), "传送门");
        add(EventHandler.noPortalController.getKey(), "找不到此门的传送门控制器");
        add(ObjectHolder.PORTAL_CONTROLLER_BLOCK.get(), "传送门控制器");
        add(PortalControllerScreen.colorBLabel.getKey(), "传送门色彩 B：");
        add(PortalControllerScreen.colorGLabel.getKey(), "传送门色彩 G：");
        add(PortalControllerScreen.colorRLabel.getKey(), "传送门色彩 R：");
        add(PortalControllerScreen.controlItemLabel.getKey(), "维度标识器放置位置");
        add(EventHandler.noControlItem.getKey(), "没有维度标识器，传送门无法工作");
        add(EventHandler.teleported.getKey(), "传送成功");
        add(EventHandler.teleportingInFunction.apply(new Object[0]).getKey(), "正在传送%s……");
        add(ObjectHolder.PORTAL_FRAME_BLOCK.get(), "传送门框架");
        add(ObjectHolder.EMPTY_EXISTING_WORLD_TELEPORT_ITEM.get(), "空白的维度标识器");
        add(ObjectHolder.EXISTING_WORLD_TELEPORT_ITEM.get(), "维度标识器");
        add(ExistingWorldControlItem.noTeleportSelf.getKey(), "标识控制器必须标识与现有维度不同的维度世界");
        add(ExistingWorldControlItem.worldDoesNotExist.getKey(), "我们失去了与标识控制器匹配的维度");
        add(ObjectHolder.PORTAL_LIGHTER_ITEM.get(), "维度打火石");
        add(ObjectHolder.MAIN_ITEM_GROUP, "通用传送门");
    }
}
