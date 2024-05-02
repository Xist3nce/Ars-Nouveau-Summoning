package com.sleepyentity.summoning.glyphs;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.items.curios.SummoningFocus;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.sleepyentity.summoning.SleepySummoningMain;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import com.hollingsworth.arsnouveau.common.entity.SummonSkeleton;
import com.hollingsworth.arsnouveau.common.potions.ModPotions;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;


import javax.annotation.Nonnull;

import java.util.Set;

public class EffectSummonImp extends AbstractEffect {

    public static EffectSummonImp INSTANCE = new EffectSummonImp(SleepySummoningMain.prefix("summon_imp"), "summon_imp");

    public EffectSummonImp(ResourceLocation tag, String description) {
        super(tag, description);
    }

    @Override
    public int getDefaultManaCost() {
        return 20;
    }

    @Override
    public void onResolve(HitResult rayTraceResult, Level world, @Nonnull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        super.onResolve(rayTraceResult, world, shooter, spellStats, spellContext, resolver);
        if (!canSummon(shooter))
            return;

        Vec3 vector3d = safelyGetHitPos(rayTraceResult);
        int ticks = (int) (20 * (100 + 1 * spellStats.getDurationMultiplier()));
        BlockPos pos = new BlockPos(vector3d);
        if (ticks <= 0) return;
        int count = 1 + spellStats.getBuffCount(AugmentSplit.INSTANCE);
        for (int i = 0; i < count; ++i) {
            BlockPos blockpos = pos.offset(-2 + shooter.getRandom().nextInt(5), 2, -2 + shooter.getRandom().nextInt(5));

            ItemStack weapon = Items.IRON_SWORD.getDefaultInstance();
            if ((spellStats.hasBuff(AugmentPierce.INSTANCE))) {
                weapon = Items.BOW.getDefaultInstance();
                if (spellStats.getAmpMultiplier() > 0)
                    weapon.enchant(Enchantments.POWER_ARROWS, Math.max(4, (int) spellStats.getAmpMultiplier()) - 1);
            } else {
                if (spellStats.getAmpMultiplier() >= 3) {
                    weapon = Items.NETHERITE_AXE.getDefaultInstance();
                } else if (spellStats.getAmpMultiplier() > 2) {
                    weapon = Items.NETHERITE_SWORD.getDefaultInstance();
                } else if (spellStats.getAmpMultiplier() > 1) {
                    weapon = Items.DIAMOND_SWORD.getDefaultInstance();
                }
            }
            SummonSkeleton undeadentity = new SummonSkeleton(world, shooter, weapon);
            undeadentity.moveTo(blockpos, 0.0F, 0.0F);
            undeadentity.finalizeSpawn((ServerLevelAccessor) world, world.getCurrentDifficultyAt(blockpos), MobSpawnType.MOB_SUMMONED, null, null);
            undeadentity.setOwner(shooter);
            undeadentity.setLimitedLife(ticks);
            //Add potion effect to the summoned entity
            undeadentity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, ticks, 1));
            summonLivingEntity(rayTraceResult, world, shooter, spellStats, spellContext, resolver, undeadentity);
        }

        shooter.addEffect(new MobEffectInstance(ModPotions.SUMMONING_SICKNESS_EFFECT.get(), ticks));
        //Take health from the caster with the damage source being themselves
        shooter.hurt(DamageSource.MAGIC, (shooter.getHealth()/2)+1);
        System.out.println("Demon Summoned at power level " + spellStats.getAmpMultiplier());
    }


    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentExtendTime.INSTANCE, AugmentDurationDown.INSTANCE, AugmentAmplify.INSTANCE, AugmentSplit.INSTANCE, AugmentPierce.INSTANCE);
    }
    @Override
    public SpellTier defaultTier() {
        return SpellTier.TWO;
    }
    @Override
    public String getBookDescription() {
        return "Summons a number of demonic allies that will attack nearby hostile enemies. These demons will last a short time until they begin to take damage, but time may be extended with the " +
                "Extend Time augment.  Additionally, the demon summoned changes based on augments, use Amplify to summon stronger demons, or Pierce to give it a bow.  Adding Split after the effect will add to the number of summoned demons.";
    }
    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.CONJURATION);
    }

}