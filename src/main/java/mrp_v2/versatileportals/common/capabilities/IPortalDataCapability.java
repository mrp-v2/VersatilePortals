package mrp_v2.versatileportals.common.capabilities;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public interface IPortalDataCapability
{
    Codec<IPortalDataCapability> CODEC = RecordCodecBuilder.create((builder) -> builder.group(
            Codec.INT.fieldOf("RemainingPortalCooldown").forGetter(IPortalDataCapability::getRemainingPortalCooldown),
            Codec.INT.fieldOf("InPortalTime").forGetter(IPortalDataCapability::getInPortalTime))
            .apply(builder, PortalDataHandler::new));
    void decrementRemainingPortalCooldown();
    int getRemainingPortalCooldown();
    void setRemainingPortalCooldown(int remainingPortalCooldown);
    int getInPortalTime();
    void setInPortalTime(int inPortalTime);
    int incrementInPortalTime();
}
