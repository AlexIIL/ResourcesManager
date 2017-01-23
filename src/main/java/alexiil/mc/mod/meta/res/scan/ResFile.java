package alexiil.mc.mod.meta.res.scan;

import java.io.File;

import net.minecraft.util.ResourceLocation;

public class ResFile extends Res {
    private static final File parentEditFolder = new File("./resource_manager/editing/assets");

    static {
        parentEditFolder.mkdirs();
    }

    public final ResFolder parent;
    public final ResourceLocation location;
    public final ResourceProvider[] providers;
    public final File editFolder, editFile;

    public ResFile(ResFolder parent, String name, ResourceLocation location, ResourceProvider[] providers) {
        super(name);
        this.parent = parent;
        this.location = location;
        this.providers = providers;
        this.editFile = new File(parentEditFolder, location.getResourceDomain() + "/" + location.getResourcePath());
        editFolder = editFile.getParentFile();
    }

    @Override
    public boolean needsExporting() {
        return false;
    }

    @Override
    public boolean hasMissing() {
        return false;
    }

    @Override
    public boolean hasErrors() {
        return false;
    }
}
