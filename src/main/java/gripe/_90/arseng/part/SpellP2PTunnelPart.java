package gripe._90.arseng.part;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.hollingsworth.arsnouveau.api.event.SpellProjectileHitEvent;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.entity.EntityProjectileSpell;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAccelerate;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDecelerate;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.BlockHitResult;

import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.blockentity.networking.CableBusBlockEntity;
import appeng.items.parts.PartModels;
import appeng.parts.p2p.P2PModels;
import appeng.parts.p2p.P2PTunnelPart;

import gripe._90.arseng.definition.ArsEngCore;

public class SpellP2PTunnelPart extends P2PTunnelPart<SpellP2PTunnelPart> {
    public static final P2PModels MODELS = new P2PModels(ArsEngCore.makeId("part/spell_p2p_tunnel"));

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

    public static void onSpellHit(SpellProjectileHitEvent event) {
        if (event.getHitResult() instanceof BlockHitResult blockHit) {
            var be = event.getProjectile().level.getBlockEntity(blockHit.getBlockPos());

            if (be instanceof CableBusBlockEntity cableBus) {
                var part = cableBus.getPart(blockHit.getDirection());

                if (part instanceof SpellP2PTunnelPart spellP2P) {
                    spellP2P.redirectSpell(event.getProjectile());
                    event.setCanceled(true);
                }
            }
        }
    }

    private void redirectSpell(EntityProjectileSpell spell) {
        if (!this.equals(getInput())) return;

        var outputs = new ArrayList<>(getOutputs());
        if (outputs.isEmpty()) return;

        Collections.shuffle(outputs);
        var selected = outputs.get(0);

        var dir = selected.getSide();
        var pos = selected.getHost().getBlockEntity().getBlockPos();
        var x = pos.getX() + 0.5;
        var y = pos.getY() + 0.5;
        var z = pos.getZ() + 0.5;

        spell.setPos(x, y, z);
        spell.prismRedirect++;

        if (spell.spellResolver == null) {
            spell.remove(Entity.RemovalReason.DISCARDED);
            return;
        }

        var acceleration = spell.spellResolver.spell.getBuffsAtIndex(0, null, AugmentAccelerate.INSTANCE);
        var deceleration = spell.spellResolver.spell.getBuffsAtIndex(0, null, AugmentDecelerate.INSTANCE);
        var velocity = Math.max(0.1, 0.5 + 0.1 * Math.min(2, acceleration - deceleration * 0.5));

        spell.shoot(dir.getStepX(), dir.getStepY(), dir.getStepZ(), (float) velocity, 0);
        BlockUtil.updateObservers(getLevel(), new BlockPos(x, y, z));
    }
}
