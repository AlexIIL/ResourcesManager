package alexiil.mc.mod.meta.res.gui._new_windows;

import alexiil.mc.mod.meta.res.scan.ResourceScanner;

public class GuiResources extends GuiMultiWindow {
    public GuiResources() {
        super(PanelExplorer::new);
        ResourceScanner.INSTANCE.scan();

        PanelExplorer explorer = (PanelExplorer) rootPanel;
        explorer.setFolder(ResourceScanner.INSTANCE.rootFolder);
    }
}
