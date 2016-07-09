package alexiil.mc.mod.meta.res.scan;

import java.util.NavigableMap;
import java.util.TreeMap;

import net.minecraft.item.Item;

public enum AllItemModels {
    INSTANCE;

    public static final NavigableMap<Item, ItemModels> items = new TreeMap<>(INSTANCE::compare);

    private int compare(Item a, Item b) {
        return a.getRegistryName().toString().compareTo(b.getRegistryName().toString());
    }

    public static class ItemModels {
        public final Item item;

        public ItemModels(Item item) {
            this.item = item;
        }
    }
}
