package mrp_v2.versatileportals.common.capabilities;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;

public interface IPortalDataCapability
{
    Codec<IPortalDataCapability> CODEC = RecordCodecBuilder.create((builder) -> builder
            .group(Codec.INT.fieldOf("RemainingPortalCooldown")
                            .forGetter(IPortalDataCapability::getRemainingPortalCooldown),
                    Codec.INT.fieldOf("InPortalTime").forGetter(IPortalDataCapability::getInPortalTime),
                    Codec.BOOL.fieldOf("IsInPortal").forGetter(IPortalDataCapability::getInPortal),
                    BlockPos.CODEC.fieldOf("PortalPos").forGetter(IPortalDataCapability::getPortalPos))
            .apply(builder, PortalDataHandler::new));
    void decrementRemainingPortalCooldown();
    int getRemainingPortalCooldown();
    void setRemainingPortalCooldown(int remainingPortalCooldown);
    int getInPortalTime();
    void setInPortalTime(int inPortalTime);
    int incrementInPortalTime();
    boolean getInPortal();
    void setInPortal(boolean isInPortal);
    void setPortal(BlockPos pos);
    BlockPos getPortalPos();
    void setPortalPos(BlockPos pos);
}
