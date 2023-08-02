package mrp_v2.versatileportals.tileentity.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mrp_v2.versatileportals.tileentity.PortalControllerTileEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;

public class PortalControllerData
{
    private static final String NULL_STRING = "null";
    public static final Codec<PortalControllerData> CODEC = RecordCodecBuilder.create(
            (builder) -> builder.group(Codec.INT.fieldOf("PortalColor").forGetter(PortalControllerData::getPortalColor),
                    Codec.STRING.fieldOf("CustomName").forGetter(PortalControllerData::getCustomNameString),
                            CompoundTag.CODEC.fieldOf("Inventory").forGetter(PortalControllerData::getInventoryData))
                    .apply(builder, PortalControllerData::new));
    private final CompoundTag inventoryData;
    @Nullable
    private final Component customName;
    private final int portalColor;

    public PortalControllerData(PortalControllerTileEntity portalController)
    {
        this(portalController.getPortalColor(), portalController.getCustomName(),
                portalController.getInventory().serializeNBT());
    }

    private PortalControllerData(int portalColor, @Nullable Component customName, CompoundTag inventoryData)
    {
        this.portalColor = portalColor;
        this.customName = customName;
        this.inventoryData = inventoryData;
    }

    private PortalControllerData(int portalColor, String customName, CompoundTag inventoryData)
    {
        this(portalColor, makeITextComponent(customName), inventoryData);
    }

    @Nullable
    private static Component makeITextComponent(String str)
    {
        return str.equals(NULL_STRING) ? null : Component.Serializer.fromJson(str);
    }

    public CompoundTag getInventoryData()
    {
        return this.inventoryData;
    }

    @Nullable
    public Component getCustomName()
    {
        return this.customName;
    }

    private String getCustomNameString()
    {
        return this.customName == null ? NULL_STRING : Component.Serializer.toJson(this.customName);
    }

    public int getPortalColor()
    {
        return this.portalColor;
    }
}
