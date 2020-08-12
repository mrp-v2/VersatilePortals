package mrp_v2.randomdimensions.client.particle;

import mrp_v2.randomdimensions.particles.PortalControllerParticleData;
import mrp_v2.randomdimensions.util.Util;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PortalControllerParticle extends SpriteTexturedParticle
{
    protected PortalControllerParticle(ClientWorld clientWorld, double x, double y, double z, double motionX,
            double motionY, double motionZ, int color)
    {
        super(clientWorld, x, y, z);
        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;
        this.posX = x;
        this.posY = y;
        this.posZ = z;
        this.particleScale = 0.51f;
        this.particleRed = Util.fGetColorR(color);
        this.particleGreen = Util.fGetColorG(color);
        this.particleBlue = Util.fGetColorB(color);
        this.maxAge = 40;
    }

    @Override public float getScale(float scaleFactor)
    {
        float scale = (this.maxAge / 2.0F + scaleFactor) / this.maxAge;
        scale = 1.0F - scale;
        scale = scale * scale;
        scale = 1.0F - scale;
        return this.particleScale * scale;
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
            this.posX = this.posX + this.motionX;
            this.posY = this.posY + this.motionY;
            this.posZ = this.posZ + this.motionZ;
        }
    }

    @Override public IParticleRenderType getRenderType()
    {
        return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override public void move(double x, double y, double z)
    {
        this.setBoundingBox(this.getBoundingBox().offset(x, y, z));
        this.resetPositionToBB();
    }

    @Override protected int getBrightnessForRender(float partialTick)
    {
        int superBrightness = super.getBrightnessForRender(partialTick);
        float agePercent = (float) this.age / (float) this.maxAge;
        agePercent = agePercent * agePercent;
        agePercent = agePercent * agePercent;
        int j = superBrightness & 255;
        int k = superBrightness >> 16 & 255;
        k = k + (int) (agePercent * 15.0F * 16.0F);
        if (k > 240)
        {
            k = 240;
        }
        return j | k << 16;
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
            PortalParticle portalParticle =
                    new PortalParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, particleData.getColor());
            portalParticle.selectSpriteRandomly(this.spriteSet);
            return portalParticle;
        }
    }
}
