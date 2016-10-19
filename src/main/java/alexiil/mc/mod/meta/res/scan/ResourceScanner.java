package alexiil.mc.mod.meta.res.scan;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.client.resources.ResourcePackRepository.Entry;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import alexiil.mc.mod.meta.res.scan.AllBlockModels.SingleStateModels;
import alexiil.mc.mod.meta.res.scan.AllItemModels.ItemModels;

public enum ResourceScanner {
    INSTANCE;

    public final List<ResourceProvider> providers = new ArrayList<>();

    public final SortedMap<ResourceLocation, List<ResourceProvider>> resources = new TreeMap<>((a, b) -> a.toString().compareTo(b.toString()));

    public void scan() {
        resources.clear();
        providers.clear();

        ResourcePackRepository repo = Minecraft.getMinecraft().getResourcePackRepository();
        for (Entry entry : repo.getRepositoryEntries()) {
            providers.add(new ResourceProvider(entry));
        }
        List<IResourcePack> packs = getOnlyList();
        for (IResourcePack pack : packs) {
            providers.add(new ResourceProvider(pack));
        }

        System.out.print("Scanning...\n");

        for (ResourceProvider provider : providers) {
            for (ResourceLocation loc : provider.scanAllResourceLocations()) {
                List<ResourceProvider> list = resources.get(loc);
                if (list == null) {
                    resources.put(loc, list = new ArrayList<>());
                }
                list.add(provider);
            }
        }

        scanTextures();
        scanBlockModels();
        scanItemModels();

        System.out.print("Finished scanning!\n");
    }

    private static List<IResourcePack> getOnlyList() {
        for (Field f : Minecraft.class.getDeclaredFields()) {
            f.setAccessible(true);
            if (f.getType() == List.class && !Modifier.isStatic(f.getModifiers())) {
                try {
                    return (List<IResourcePack>) f.get(Minecraft.getMinecraft());
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    throw new Error(e);
                }
            }
        }
        throw new Error("Didn't find it!");
    }

    private static void scanTextures() {
        // TextureMap textures = Minecraft.getMinecraft().getTextureMapBlocks();
        // TODO?
    }

    private static void scanBlockModels() {
        for (Block b : Block.REGISTRY) {
            AllBlockModels.blocks.put(b, new SingleStateModels(b));
        }
        System.out.print("  Scanned " + AllBlockModels.blocks.size() + " blocks!\n");
    }

    private static void scanItemModels() {
        for (Item i : Item.REGISTRY) {
            AllItemModels.items.put(i, new ItemModels(i));
        }
        System.out.print("  Scanned " + AllItemModels.items.size() + " items!\n");
    }
}
