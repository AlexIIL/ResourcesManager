package alexiil.mc.mod.meta.res.gui._new_windows;

import net.minecraft.client.gui.FontRenderer;

import alexiil.mc.mod.meta.res.gui.GuiUtilRM;
import alexiil.mc.mod.meta.res.scan.ResFile;

public class PanelFileInfo extends PanelWindow {
    public final ResFile file;

    public PanelFileInfo(GuiMultiWindow gui, ResFile file) {
        super(gui);
        this.file = file;
        this.title = file.name;
    }

    @Override
    public void draw(float partialTicks) {
        GuiUtilRM.drawRect(insideWindow, 0xFF_00_00_00);
        FontRenderer font = gui.mc.fontRendererObj;
        boolean hasEdit = file.editFile.exists();
        int _x = insideWindow.getX();
        int _y = insideWindow.getY() + 10;
        font.drawString("Resource Packs:", _x + 10, _y, 0xFF_FF_FF_FF);
        _y += 10;
        for (int i = 0; i < file.providers.length; i++) {
            font.drawString(file.providers[i].name, _x + 20, _y, 0xFF_99_99_99);
            //
            // int dx = 28;
            // dx += view[i].draw(mouseX, mouseY, dx, y + 10);
            // SmallButton btn = hasEdit ? overwrite[i] : copy[i];
            // dx += btn.draw(mouseX, mouseY, dx, y + 10);
            _y += 30;
        }

        if (hasEdit) {
            font.drawString("Editing:", _x + 10, _y, 0xFF_FF_FF_FF);
            // int dx = 28;
            // dx += edit.draw(mouseX, mouseY, dx, y + 10);
            // dx += delete.draw(mouseX, mouseY, dx, y + 10);
            _y += 30;
        }
    }
}
