/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.particles;

import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EntityShrinkingParticleFX extends EntitySimpleParticleFX {

    private final float originalScale;

    public EntityShrinkingParticleFX(World world, double x, double y, double z, double velX, double velY, double velZ, float scale) {
        super(world, x, y, z, velX, velY, velZ, scale);
        this.originalScale = this.particleScale;
    }

    @Override
    public void renderParticle(VertexBuffer worldRendererIn, Entity entityIn, float par2, float par3, float par4, float par5, float par6, float par7) {
        float age = ((float) this.particleAge + par2) / (float) this.particleMaxAge * 32.0F;
        
        age = MathHelper.clamp_float(age, 0.0F, 1.0F);

        this.particleScale = this.originalScale * age;
        super.renderParticle(worldRendererIn, entityIn, par2, par3, par4, par5, par6, par7);
    }

}
