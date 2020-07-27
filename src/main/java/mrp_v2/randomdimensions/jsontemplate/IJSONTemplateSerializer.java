package mrp_v2.randomdimensions.jsontemplate;

import com.google.gson.JsonObject;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

public interface IJSONTemplateSerializer extends IForgeRegistryEntry<IJSONTemplateSerializer> {

	public JSONTemplate read(ResourceLocation templateID, JsonObject json);

	public JSONTemplate read(ResourceLocation templateID, PacketBuffer buffer);

	public void write(PacketBuffer buffer, JSONTemplate template);
}
