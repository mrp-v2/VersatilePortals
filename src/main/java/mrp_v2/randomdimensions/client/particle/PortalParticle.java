package mrp_v2.randomdimensions.client.particle;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PortalParticle extends net.minecraft.client.particle.PortalParticle {

	protected PortalParticle(ClientWorld p_i232417_1_, double p_i232417_2_, double p_i232417_4_, double p_i232417_6_,
			double p_i232417_8_, double p_i232417_10_, double p_i232417_12_) {
		super(p_i232417_1_, p_i232417_2_, p_i232417_4_, p_i232417_6_, p_i232417_8_, p_i232417_10_, p_i232417_12_);
		float f = this.rand.nextFloat();
		this.particleRed = 0.0F;
		this.particleGreen = f;
		this.particleBlue = 0.0F;
	}

	@OnlyIn(Dist.CLIENT)
	public static class Factory implements IParticleFactory<BasicParticleType> {
		private final IAnimatedSprite spriteSet;

		public Factory(IAnimatedSprite spriteSet) {
			this.spriteSet = spriteSet;
		}

		public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z,
				double xSpeed, double ySpeed, double zSpeed) {
			PortalParticle portalparticle = new PortalParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
			portalparticle.selectSpriteRandomly(this.spriteSet);
			return portalparticle;
		}
	}
}
