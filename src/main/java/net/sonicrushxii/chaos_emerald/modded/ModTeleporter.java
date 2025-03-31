package net.sonicrushxii.chaos_emerald.modded;

import net.minecraft.core.BlockPos;
import net.minecraftforge.common.util.ITeleporter;

public class ModTeleporter implements ITeleporter {
    public static BlockPos thisPos = BlockPos.ZERO;
    public static boolean insideDimension = true;

    public ModTeleporter(BlockPos pos, boolean insideDim) {
        thisPos = pos;
        insideDimension = insideDim;
    }
}