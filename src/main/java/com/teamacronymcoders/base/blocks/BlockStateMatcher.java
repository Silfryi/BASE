package com.teamacronymcoders.base.blocks;

import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.IProperty;

import java.util.List;
import java.util.Map;

public class BlockStateMatcher {
    private final Block block;
    private final Map<IProperty<?>, List<?>> allowedValues;

    public BlockStateMatcher(Block block, Map<IProperty<?>, List<?>> allowedValues) {
        this.block = block;
        this.allowedValues = allowedValues;
    }

    public BlockStateMatcher(Block block) {
        this(block, Maps.newHashMap());
    }

    public boolean matches(BlockState blockState) {
        if (block == blockState.getBlock()) {
            for (IProperty<?> property: blockState.getProperties()) {
                List<?> allowedValue = allowedValues.get(property);
                if (allowedValue != null) {
                    if (!allowedValue.contains(blockState.get(property))) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }
}
