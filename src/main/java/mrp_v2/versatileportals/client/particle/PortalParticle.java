package mrp_v2.versatileportals.client.particle;

import mrp_v2.versatileportals.particles.PortalParticleData;
import mrp_v2.versatileportals.util.Util;
import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT) public class PortalParticle extends net.minecraft.client.particle.PortalParticle
{
    protected PortalParticle(ClientWorld clientWorld, double x, double y, double z, double xSpeed, double ySpeed,
            double zSpeed, int color)
    {
        super(clientWorld, x, y, z, xSpeed, ySpeed, zSpeed);
        this.rCol = Util.fGetColorR(color);
        this.gCol = Util.fGetColorG(color);
        this.bCol = Util.fGetColorB(color);
    }

    @Override public void tick()
    {
        super.tick();
        this.y -= 1 - (float) this.age / this.lifetime;
    }

    @OnlyIn(Dist.CLIENT) public static class Factory implements IParticleFactory<PortalParticleData>
    {
        private final IAnimatedSprite spriteSet;

        public Factory(IAnimatedSprite spriteSet)
        {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(PortalParticleData particleData, ClientWorld worldIn, double x, double y,
                double z, double xSpeed, double ySpeed, double zSpeed)
        {
            PortalParticle portalParticle =
                    new PortalParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, particleData.getColor());
            portalParticle.pickSprite(this.spriteSet);
            return portalParticle;
        }
    }
}
