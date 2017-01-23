package alexiil.mc.mod.meta.res.gui._new_windows;

import alexiil.mc.mod.meta.res.scan.ResourceScanner;

public class GuiResources extends GuiMultiWindow {
    public GuiResources() {
        super(PanelExplorer::new);
        ResourceScanner.INSTANCE.scan();

        PanelExplorer explorer = (PanelExplorer) rootPanel;
        explorer.setFolder(ResourceScanner.INSTANCE.rootFolder);

        PanelExplorer exp2 = new PanelExplorer(this);
        exp2.x = 40;
        exp2.y = 40;
        exp2.width = 260;
        exp2.height = 140;
        exp2.setFolder(ResourceScanner.INSTANCE.rootFolder.folders.get("minecraft"));
        openWindows.add(exp2);

        PanelExplorer exp3 = new PanelExplorer(this);
        exp3.x = 340;
        exp3.y = 140;
        exp3.width = 260;
        exp3.height = 140;
        exp3.setFolder(ResourceScanner.INSTANCE.rootFolder.folders.get("buildcraftlib"));
        openWindows.add(exp3);
    }
}
