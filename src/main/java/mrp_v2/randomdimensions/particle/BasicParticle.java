package mrp_v2.randomdimensions.particle;

import mrp_v2.randomdimensions.RandomDimensions;
import net.minecraft.particles.BasicParticleType;

public class BasicParticle extends BasicParticleType {

	public BasicParticle(String id, boolean alwaysShow) {
		super(alwaysShow);
		this.setRegistryName(RandomDimensions.ID, id);
	}
}
