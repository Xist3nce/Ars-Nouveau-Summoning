package com.sleepyentity.summoning.glyphs;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.sleepyentity.summoning.SleepySummoningMain;
import net.minecraft.resources.ResourceLocation;
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
        return 10;
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
            summonLivingEntity(rayTraceResult, world, shooter, spellStats, spellContext, resolver, undeadentity);
        }
        shooter.addEffect(new MobEffectInstance(ModPotions.SUMMONING_SICKNESS_EFFECT.get(), ticks));
        System.out.println("Imp Summoned!");
    }


    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentExtendTime.INSTANCE, AugmentDurationDown.INSTANCE, AugmentAmplify.INSTANCE, AugmentSplit.INSTANCE, AugmentPierce.INSTANCE);
    }
    @Override
    public SpellTier defaultTier() {
        return SpellTier.THREE;
    }
    @Override
    public String getBookDescription() {
        return "Summons a number of Imp allies that will attack nearby hostile enemies. These Imps will last a short time until they begin to take damage, but time may be extended with the " +
                "Extend Time augment.  Additionally, their summoned weapons are changed using augments, use Amplify to give it a better sword, or Pierce to give it a bow.  Adding Split after the effect will add to the number of summoned skeletons.";
    }
    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.CONJURATION);
    }

}