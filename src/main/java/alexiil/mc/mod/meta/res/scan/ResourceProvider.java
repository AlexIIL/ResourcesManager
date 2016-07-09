package alexiil.mc.mod.meta.res.scan;

import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.client.resources.ResourcePackRepository.Entry;
import net.minecraft.util.ResourceLocation;

public class ResourceProvider {
    public final String name;
    private final ResourcePackRepository.Entry entry;
    private final IResourcePack pack;

    public ResourceProvider(IResourcePack pack) {
        this.name = "Minecraft";
        this.entry = null;
        this.pack = pack;
    }

    public ResourceProvider(Entry entry) {
        this.name = entry.getResourcePackName();
        this.entry = entry;
        this.pack = entry.getResourcePack();
    }

    public boolean resourceExists(ResourceLocation location) {
        return pack.resourceExists(location);
    }
}
