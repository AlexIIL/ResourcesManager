package alexiil.mc.mod.meta.res.scan;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.client.resources.ResourcePackRepository.Entry;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import alexiil.mc.mod.meta.res.scan.AllBlockModels.BlockModels;
import alexiil.mc.mod.meta.res.scan.AllItemModels.ItemModels;

public enum ResourceScanner {
    INSTANCE;

    public final NavigableMap<ResourceLocation, List<ResourceProvider>> resources = new TreeMap<>((a, b) -> {
        int diff = a.getResourceDomain().compareTo(b.getResourceDomain());
        if (diff != 0) return diff;
        return a.getResourcePath().compareTo(b.getResourcePath());
    });

    public final List<ResourceProvider> providers = new ArrayList<>();

    public void scan() {
        resources.clear();
        providers.clear();

        ResourcePackRepository repo = Minecraft.getMinecraft().getResourcePackRepository();
        providers.add(new ResourceProvider(Minecraft.getMinecraft().mcDefaultResourcePack));
        for (Entry entry : repo.getRepositoryEntries()) {
            providers.add(new ResourceProvider(entry));
        }

        System.out.println("Scanning...");

        scanTextures();
        scanBlockModels();
        scanItemModels();
    }

    private void scanTextures() {
        TextureMap textures = Minecraft.getMinecraft().getTextureMapBlocks();
        // TODO!
    }

    public void scanLocation(ResourceLocation location) {
        if (resources.containsKey(location)) {
            return;
        }
        List<ResourceProvider> ps = new ArrayList<>();
        System.out.println("  - " + location);
        for (ResourceProvider p : providers) {
            if (p.resourceExists(location)) {
                ps.add(p);
                System.out.println("    - " + p.name);
            }
        }
        resources.put(location, ps);
    }

    private static void scanBlockModels() {
        for (Block b : Block.REGISTRY) {
            AllBlockModels.blocks.put(b, new BlockModels(b));
        }
    }

    private static void scanItemModels() {
        for (Item i : Item.REGISTRY) {
            AllItemModels.items.put(i, new ItemModels(i));
        }
    }
}
