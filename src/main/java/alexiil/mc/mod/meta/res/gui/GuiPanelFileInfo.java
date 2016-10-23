package alexiil.mc.mod.meta.res.gui;

import java.awt.Desktop;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import alexiil.mc.mod.meta.res.scan.ResFile;
import alexiil.mc.mod.meta.res.scan.ResourceProvider;

public class GuiPanelFileInfo extends GuiPanel {
    public final ResFile file;

    private final SmallButton edit, delete;

    private final SmallButton[] view, copy, overwrite;

    public GuiPanelFileInfo(ResFile openFile) {
        this.file = openFile;

        edit = new SmallButton("Edit", () -> {
            Path path = file.editFile.toPath();
            try {
                try {
                    Desktop.getDesktop().browse(path.toUri());
                } catch (IOException io) {
                    io.printStackTrace();
                    Desktop.getDesktop().browse(path.getParent().toUri());
                }
            } catch (IOException io) {
                io.printStackTrace();
            }
        });
        delete = new SmallButton("Delete", () -> {

            // Instead of *actually* deleting it we will just rename it to a hidden file
            // We do overwrite the hidden file if one already exists though

            Path deletedTemp = file.editFolder.toPath().resolve("." + file.editFile.getName() + ".deleted");
            try {
                Files.move(file.editFile.toPath(), deletedTemp, StandardCopyOption.REPLACE_EXISTING);
                Files.setAttribute(deletedTemp, "dos:hidden", Boolean.TRUE);
            } catch (IOException io) {
                io.printStackTrace();
            }
        });

        view = new SmallButton[file.providers.length];
        copy = new SmallButton[file.providers.length];
        overwrite = new SmallButton[file.providers.length];
        for (int i = 0; i < file.providers.length; i++) {
            final ResourceProvider provider = file.providers[i];
            view[i] = new SmallButton("View", () -> {
                try (InputStream is = provider.pack.getInputStream(file.location)) {
                    Path path = Files.createTempFile("read_only_", "_" + file.name);
                    Files.copy(is, path, StandardCopyOption.REPLACE_EXISTING);
                    Desktop.getDesktop().browse(path.toUri());
                } catch (IOException io) {
                    io.printStackTrace();
                }
            });

            Runnable overwriteEditing = () -> {
                try (InputStream is = provider.pack.getInputStream(file.location)) {
                    file.editFolder.mkdirs();
                    Path path = file.editFile.toPath();
                    Files.copy(is, path, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException io) {
                    io.printStackTrace();
                }
            };
            copy[i] = new SmallButton("Copy to editing", overwriteEditing);
            overwrite[i] = new SmallButton("Replace editing", overwriteEditing);
        }
    }

    @Override
    public int getWidth() {
        return 400;
    }

    private class SmallButton {
        private final String text;
        private final int strWidth;
        private final Runnable onClick;

        public SmallButton(String text, Runnable onClick) {
            this.text = text;
            this.onClick = onClick;
            strWidth = Minecraft.getMinecraft().fontRendererObj.getStringWidth(text);
        }

        private int draw(int mouseX, int mouseY, int x, int y) {
            FontRenderer font = Minecraft.getMinecraft().fontRendererObj;
            font.drawString(text, x + 2, y, 0xFF_BB_BB_BB);
            if (mouseX > x && mouseX < x + strWidth && mouseY > y - 2 && mouseY < y + 12) {
                GuiUtil.drawHoverBox(x - 1, y - 2, strWidth + 4, 12);
            }
            return strWidth + 10;
        }

        private int onClick(int mouseX, int mouseY, int x, int y) {
            if (mouseX > x && mouseX < x + strWidth && mouseY > y - 2 && mouseY < y + 12) {
                onClick.run();
            }
            return strWidth + 10;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        FontRenderer font = Minecraft.getMinecraft().fontRendererObj;

        boolean hasEdit = file.editFile.exists();

        font.drawString(file.location.toString(), 0, 0, 0xFF_DD_DD_DD);
        int y = 20;

        font.drawString("Resource Packs:", 10, y, 0xFF_FF_FF_FF);
        y += 10;
        for (int i = 0; i < file.providers.length; i++) {
            font.drawString(file.providers[i].name, 20, y, 0xFF_99_99_99);

            int dx = 28;
            dx += view[i].draw(mouseX, mouseY, dx, y + 10);
            SmallButton btn = hasEdit ? overwrite[i] : copy[i];
            dx += btn.draw(mouseX, mouseY, dx, y + 10);
            y += 30;
        }

        if (hasEdit) {
            font.drawString("Editing:", 10, y, 0xFF_FF_FF_FF);
            int dx = 28;
            dx += edit.draw(mouseX, mouseY, dx, y + 10);
            dx += delete.draw(mouseX, mouseY, dx, y + 10);
            y += 30;
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        boolean hasEdit = file.editFile.exists();

        int y = 20;

        y += 10;
        for (int i = 0; i < file.providers.length; i++) {

            int dx = 28;
            dx += view[i].onClick(mouseX, mouseY, dx, y + 10);
            SmallButton btn = hasEdit ? overwrite[i] : copy[i];
            dx += btn.onClick(mouseX, mouseY, dx, y + 10);
            y += 30;
        }

        if (hasEdit) {
            int dx = 28;
            dx += edit.onClick(mouseX, mouseY, dx, y + 10);
            dx += delete.onClick(mouseX, mouseY, dx, y + 10);
            y += 30;
        }
    }
}
