package alexiil.mc.mod.meta.res.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import alexiil.mc.mod.meta.res.scan.ResFolder;
import alexiil.mc.mod.meta.res.scan.ResourceScanner;

public class GuiPanelFolderStructure extends GuiPanel {
    public final GuiResourcesMain gui;

    private final List<GuiSelectable<GuiString>> shownFolders = new ArrayList<>();
    private final ResFolder rootFolder = ResourceScanner.INSTANCE.rootFolder;

    public GuiPanelFolderStructure(GuiResourcesMain gui) {
        this.gui = gui;
    }

    @Override
    public int getWidth() {
        int width = 30;
        for (GuiSelectable<GuiString> shown : shownFolders) {
            width += shown.width + 30;
            if (!shown.isSelected()) {
                break;
            }
        }
        return width;
    }

    @Override
    public void onOpen() {
        shownFolders.clear();

        rootFolder.genGuiElements("");

        shownFolders.add(new GuiSelectable<>(0, 0, this::onSelectFolderOrFile));
        shownFolders.get(0).setList(rootFolder.guiElements);

        for (int i = 1; i <= rootFolder.depth; i++) {
            shownFolders.add(new GuiSelectable<>(0, 0, this::onSelectFolderOrFile));
        }
    }

    private void onSelectFolderOrFile(GuiSelectable<GuiString> selector, GuiString selected) {
        List<GuiString> children = null;
        if (selected != null) {
            if (selected.text.endsWith("/")) {
                // Its a folder
                int totalWidth = 30;
                for (GuiSelectable<?> select : shownFolders) {
                    select.x = totalWidth;
                    totalWidth += select.width + 30;
                }
                children = rootFolder.getChildFolder(selected.id);
                gui.changeOpenFileDlg(null);
            } else {
                // Its a file
                gui.changeOpenFileDlg(rootFolder.getFile(selected.id));
            }
        } else {
            gui.changeOpenFileDlg(null);
        }
        int index = shownFolders.indexOf(selector);
        if (index + 1 < shownFolders.size()) {
            GuiSelectable<GuiString> childGuiElement = shownFolders.get(index + 1);
            childGuiElement.setList(children);
            for (int i = index + 2; i < shownFolders.size(); i++) {
                shownFolders.get(i).setList(null);
            }
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        for (GuiSelectable<GuiString> shown : shownFolders) {
            shown.tick();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        for (GuiSelectable<GuiString> shown : shownFolders) {
            shown.drawHeight = drawArea.height;
            shown.draw(mouseX, mouseY);
            if (!shown.isSelected()) {
                break;
            }
        }
    }

    @Override
    public void mouseScroll(int mouseX, int mouseY, float scroll) {
        super.mouseScroll(mouseX, mouseY, scroll);
        for (GuiSelectable<GuiString> shown : shownFolders) {
            tryScroll(mouseX, scroll, shown);
            if (!shown.isSelected()) {
                break;
            }
        }
    }

    private static void tryScroll(int mouseX, float scroll, GuiSelectable<GuiString> selected) {
        if (mouseX > selected.x && mouseX <= selected.x + selected.width) {
            selected.onScroll(scroll);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        for (GuiSelectable<GuiString> shown : shownFolders) {
            tryMouseClick(mouseX, shown);
            if (!shown.isSelected()) {
                break;
            }
        }
    }

    private static void tryMouseClick(int mouseX, GuiSelectable<GuiString> selected) {
        if (mouseX > selected.x && mouseX <= selected.x + selected.width) {
            selected.onClick();
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        for (GuiSelectable<GuiString> shown : shownFolders) {
            tryMouseRelease(mouseX, shown);
            if (!shown.isSelected()) {
                break;
            }
        }
    }

    private static void tryMouseRelease(int mouseX, GuiSelectable<GuiString> selected) {
        if (mouseX > selected.x && mouseX <= selected.x + selected.width) {
            selected.onRelease();
        }
    }
}
