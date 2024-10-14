package gripe._90.arseng.integration.tooltip;

import appeng.api.integrations.igtooltip.BaseClassRegistration;
import appeng.api.integrations.igtooltip.TooltipProvider;

import gripe._90.arseng.block.MESourceJarBlock;
import gripe._90.arseng.block.entity.MESourceJarBlockEntity;

@SuppressWarnings("UnstableApiUsage")
public class ArsEngTooltipProvider implements TooltipProvider {
    @Override
    public void registerBlockEntityBaseClasses(BaseClassRegistration registration) {
        registration.addBaseBlockEntity(MESourceJarBlockEntity.class, MESourceJarBlock.class);
    }
}
