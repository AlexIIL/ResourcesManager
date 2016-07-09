package alexiil.mc.mod.meta.res.scan;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;

public class AllBlockModels {
    public static final Map<Block, BlockModels> blocks = new HashMap<>();

    public static class BlockModels {
        public final Block block;

        public BlockModels(Block block) {
            this.block = block;
        }
    }
}
