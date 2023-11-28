package gripe._90.arseng.part;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.hollingsworth.arsnouveau.api.event.SpellProjectileHitEvent;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.advancement.ANCriteriaTriggers;
import com.hollingsworth.arsnouveau.common.entity.EntityProjectileSpell;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAccelerate;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDecelerate;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.BlockHitResult;

import appeng.api.parts.IPartHost;
import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.items.parts.PartModels;
import appeng.parts.p2p.P2PModels;
import appeng.parts.p2p.P2PTunnelPart;

import gripe._90.arseng.ArsEnergistique;

public class SpellP2PTunnelPart extends P2PTunnelPart<SpellP2PTunnelPart> {
    public static final P2PModels MODELS = new P2PModels(ArsEnergistique.makeId("part/spell_p2p_tunnel"));

    public SpellP2PTunnelPart(IPartItem<?> partItem) {
        super(partItem);
    }

    @Override
    protected float getPowerDrainPerTick() {
        return 0.5F;
    }

    @SuppressWarnings("unused")
    @PartModels
    public static List<IPartModel> getModels() {
        return MODELS.getModels();
    }

    @Override
    public IPartModel getStaticModels() {
        return MODELS.getModel(isPowered(), isActive());
    }

    @SuppressWarnings("resource")
    public static void onSpellHit(SpellProjectileHitEvent event) {
        if (event.getHitResult() instanceof BlockHitResult blockHit) {
            var be = event.getProjectile().level().getBlockEntity(blockHit.getBlockPos());

            if (be instanceof IPartHost partHost) {
                var part = partHost.getPart(blockHit.getDirection());

                if (part instanceof SpellP2PTunnelPart spellP2P && spellP2P.equals(spellP2P.getInput())) {
                    var outputs = new ArrayList<>(spellP2P.getOutputs());

                    if (!outputs.isEmpty()) {
                        Collections.shuffle(outputs);
                        spellP2P.redirectSpell(event.getProjectile(), outputs.get(0));
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

    /**
     * See {@link com.hollingsworth.arsnouveau.common.block.SpellPrismBlock#onHit}
     */
    private void redirectSpell(EntityProjectileSpell spell, SpellP2PTunnelPart output) {
        var dir = output.getSide();
        var pos = output.getHost().getBlockEntity().getBlockPos();

        spell.setPos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        spell.prismRedirect++;

        if (spell.prismRedirect >= 3 && getLevel() instanceof ServerLevel level) {
            ANCriteriaTriggers.rewardNearbyPlayers(ANCriteriaTriggers.PRISMATIC, level, pos, 10);
        }

        if (spell.spellResolver == null) {
            spell.remove(Entity.RemovalReason.DISCARDED);
            return;
        }

        var acceleration = spell.spellResolver.spell.getBuffsAtIndex(0, null, AugmentAccelerate.INSTANCE);
        var deceleration = spell.spellResolver.spell.getBuffsAtIndex(0, null, AugmentDecelerate.INSTANCE);
        var velocity = Math.max(0.1, 0.5 + 0.1 * Math.min(2, acceleration - deceleration * 0.5));

        spell.shoot(dir.getStepX(), dir.getStepY(), dir.getStepZ(), (float) velocity, 0);
        BlockUtil.updateObservers(getLevel(), pos);
    }
}
