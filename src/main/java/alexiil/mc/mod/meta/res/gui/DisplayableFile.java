package alexiil.mc.mod.meta.res.gui;

import alexiil.mc.mod.meta.res.scan.ResFile;

public class DisplayableFile extends DisplayableFilePart {
    public final ResFile file;

    public DisplayableFile(ResFile file) {
        super(file);
        this.file = file;
    }

    @Override
    public void render(GuiPanelFolders panel, int x, int y, float partialTicks) {

        renderOverlays(panel, x, y);
    }
}
