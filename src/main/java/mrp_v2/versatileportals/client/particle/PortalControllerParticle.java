package mrp_v2.versatileportals.client.particle;

import mrp_v2.versatileportals.particles.PortalControllerParticleData;
import mrp_v2.versatileportals.util.Util;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PortalControllerParticle extends TextureSheetParticle
{
    private final double originX;
    private final double originY;
    private final double originZ;

    protected PortalControllerParticle(ClientLevel clientWorld, double x, double y, double z, double motionX,
            double motionY, double motionZ, int color)
    {
        super(clientWorld, x, y, z);
        this.xd = motionX;
        this.yd = motionY;
        this.zd = motionZ;
        this.originX = this.x;
        this.originY = this.y;
        this.originZ = this.z;
        this.quadSize = 0.05f;
        this.rCol = Util.fGetColorR(color);
        this.gCol = Util.fGetColorG(color);
        this.bCol = Util.fGetColorB(color);
        this.lifetime = 40;
    }

    @Override public float getQuadSize(float scaleFactor)
    {
        return this.quadSize;
    }

    @Override public void tick()
    {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime)
        {
            this.remove();
        } else
        {
            double agePercent = (double) this.age / this.lifetime;
            this.x = this.originX + this.xd * agePercent;
            this.y = this.originY + this.yd * agePercent;
            this.z = this.originZ + this.zd * agePercent;
        }
    }

    @Override
    public ParticleRenderType getRenderType()
    {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override protected int getLightColor(float partialTick)
    {
        int superBrightness = super.getLightColor(partialTick);
        int sBLeft16And255 = superBrightness >> 16 & 255;
        sBLeft16And255 += 15;
        if (sBLeft16And255 > 240)
        {
            sBLeft16And255 = 240;
        }
        return superBrightness & 255 | sBLeft16And255 << 16;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements ParticleProvider<PortalControllerParticleData>
    {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet spriteSet)
        {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(PortalControllerParticleData particleData, ClientLevel worldIn, double x,
                double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            PortalControllerParticle portalControllerParticle =
                    new PortalControllerParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, particleData.getColor());
            portalControllerParticle.pickSprite(this.spriteSet);
            return portalControllerParticle;
        }
    }
}
