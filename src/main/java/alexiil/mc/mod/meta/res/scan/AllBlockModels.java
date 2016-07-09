package alexiil.mc.mod.meta.res.scan;

import java.util.NavigableMap;
import java.util.TreeMap;

import net.minecraft.block.Block;

public enum AllBlockModels {
    INSTANCE;

    public static final NavigableMap<Block, SingleStateModels> blocks = new TreeMap<>(INSTANCE::compare);

    private int compare(Block a, Block b) {
        return a.getRegistryName().toString().compareTo(b.getRegistryName().toString());
    }

    public static class SingleStateModels {
        public final Block block;

        public SingleStateModels(Block block) {
            this.block = block;
        }
    }
}
