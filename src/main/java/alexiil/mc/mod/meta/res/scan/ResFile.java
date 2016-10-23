package alexiil.mc.mod.meta.res.scan;

import java.io.File;

import net.minecraft.util.ResourceLocation;

public class ResFile {
    private static final File parentEditFolder = new File("./resource_manager/editing/assets");

    static {
        parentEditFolder.mkdirs();
    }

    public final ResFolder parent;
    public final String name;
    public final ResourceLocation location;
    public final ResourceProvider[] providers;
    public final File editFolder, editFile;

    public ResFile(ResFolder parent, String name, ResourceLocation location, ResourceProvider[] providers) {
        this.parent = parent;
        this.name = name;
        this.location = location;
        this.providers = providers;
        this.editFile = new File(parentEditFolder, location.getResourceDomain() + "/" + location.getResourcePath());
        editFolder = editFile.getParentFile();
    }
}
