package mrp_v2.versatileportals.client.particle;

import mrp_v2.versatileportals.particles.PortalControllerParticleData;
import mrp_v2.versatileportals.util.Util;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PortalControllerParticle extends SpriteTexturedParticle
{
    private final double originX;
    private final double originY;
    private final double originZ;

    protected PortalControllerParticle(ClientWorld clientWorld, double x, double y, double z, double motionX,
            double motionY, double motionZ, int color)
    {
        super(clientWorld, x, y, z);
        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;
        this.originX = this.posX;
        this.originY = this.posY;
        this.originZ = this.posZ;
        this.particleScale = 0.05f;
        this.particleRed = Util.fGetColorR(color);
        this.particleGreen = Util.fGetColorG(color);
        this.particleBlue = Util.fGetColorB(color);
        this.maxAge = 40;
    }

    @Override public float getScale(float scaleFactor)
    {
        return this.particleScale;
    }

    @Override public void tick()
    {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (this.age++ >= this.maxAge)
        {
            this.setExpired();
        } else
        {
            double agePercent = (double) this.age / this.maxAge;
            this.posX = this.originX + this.motionX * agePercent;
            this.posY = this.originY + this.motionY * agePercent;
            this.posZ = this.originZ + this.motionZ * agePercent;
        }
    }

    @Override public IParticleRenderType getRenderType()
    {
        return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override protected int getBrightnessForRender(float partialTick)
    {
        int superBrightness = super.getBrightnessForRender(partialTick);
        int sBLeft16And255 = superBrightness >> 16 & 255;
        sBLeft16And255 += 15;
        if (sBLeft16And255 > 240)
        {
            sBLeft16And255 = 240;
        }
        return superBrightness & 255 | sBLeft16And255 << 16;
    }

    @OnlyIn(Dist.CLIENT) public static class Factory implements IParticleFactory<PortalControllerParticleData>
    {
        private final IAnimatedSprite spriteSet;

        public Factory(IAnimatedSprite spriteSet)
        {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle makeParticle(PortalControllerParticleData particleData, ClientWorld worldIn, double x, double y,
                double z, double xSpeed, double ySpeed, double zSpeed)
        {
            PortalControllerParticle portalControllerParticle =
                    new PortalControllerParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, particleData.getColor());
            portalControllerParticle.selectSpriteRandomly(this.spriteSet);
            return portalControllerParticle;
        }
    }
}
