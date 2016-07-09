package alexiil.mc.mod.meta.res.scan;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.Item;

public class AllItemModels {
    public static final Map<Item, ItemModels> items = new HashMap<>();

    public static class ItemModels {
        public final Item item;

        public ItemModels(Item item) {
            this.item = item;
        }
    }
}
