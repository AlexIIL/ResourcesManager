package alexiil.mc.mod.meta.res.gui;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

import org.lwjgl.input.Mouse;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

import alexiil.mc.mod.meta.res.scan.ResourceProvider;
import alexiil.mc.mod.meta.res.scan.ResourceScanner;

public class GuiResourcesMain extends GuiScreen {
    private static int levels = 1;
    private final List<GuiSelectable<GuiString>> shownFolders = new ArrayList<>();
    private final ResFolder rootFolder = new ResFolder();

    public static class ResFolder {
        public final ResFolder parent;
        public final GuiString guiElement;
        public final Map<String, ResFolder> folders = new TreeMap<>();
        public final Map<String, ResFile> files = new TreeMap<>();
        public final List<GuiString> guiElements = new ArrayList<>();

        public ResFolder() {
            parent = null;
            guiElement = new GuiString("", "");
        }

        public ResFolder(ResFolder parent, String name) {
            this.parent = parent;
            guiElement = new GuiString(name + "/", parent.guiElement.id + name + "/");
        }

        public void genGuiElements(String parent) {
            guiElements.clear();

            for (ResFolder f : folders.values()) {
                guiElements.add(f.guiElement);
                f.genGuiElements(guiElement.id);
            }
            for (ResFile file : files.values()) {
                guiElements.add(new GuiString(file.name + "  " + Arrays.toString(file.providers), parent + file.name));
            }
        }

        public void reset() {
            folders.clear();
            files.clear();
            guiElements.clear();
        }

        public List<GuiString> getChildFolder(String id) {
            ResFolder current = this;
            String[] split = id.split("/");
            for (int i = 0; i < split.length; i++) {
                current = current.folders.get(split[i]);
                if (current == null) {
                    List<GuiString> list = new ArrayList<>();
                    list.add(new GuiString("null path!"));
                    return list;
                }
            }
            return current.guiElements;
        }
    }

    public static class ResFile {
        public final ResFolder parent;
        public final String name;
        public final ResourceLocation location;
        public final ResourceProvider[] providers;

        public ResFile(ResFolder parent, String name, ResourceLocation location, ResourceProvider[] providers) {
            this.parent = parent;
            this.name = name;
            this.location = location;
            this.providers = providers;
        }
    }

    public void addFile(ResourceLocation location, ResourceProvider[] providers) {
        String[] loc = toPath(location);
        ResFolder current = rootFolder;
        for (int i = 0; i + 1 < loc.length; i++) {
            String s = loc[i];
            if (!current.folders.containsKey(s)) {
                current.folders.put(s, new ResFolder(current, s));
            }
            current = current.folders.get(s);
            levels = Math.max(levels, i);
        }
        String file = loc[loc.length - 1];
        current.files.put(file, new ResFile(current, file, location, providers));
    }

    public GuiResourcesMain() {
        initDomains();
    }

    private void initDomains() {
        shownFolders.clear();
        rootFolder.reset();

        ResourceScanner.INSTANCE.scan();
        shownFolders.add(new GuiSelectable<>(this, 30, 60, this::onSelectFolderOrFile));

        for (Entry<ResourceLocation, List<ResourceProvider>> entry : ResourceScanner.INSTANCE.resources.entrySet()) {
            ResourceLocation loc = entry.getKey();
            addFile(loc, entry.getValue().toArray(new ResourceProvider[entry.getValue().size()]));
        }
        rootFolder.genGuiElements("");
        shownFolders.get(0).setList(rootFolder.guiElements);

        for (int i = 1; i <= levels; i++) {
            shownFolders.add(new GuiSelectable<>(this, 30, 30 + i * 60, this::onSelectFolderOrFile));
        }
    }

    private static String[] toPath(ResourceLocation loc) {
        String full = loc.getResourceDomain() + "/" + loc.getResourcePath();
        return full.split("/");
    }

    private void onSelectFolderOrFile(GuiSelectable<GuiString> selector, GuiString selected) {
        if (selected == null) {
            return;
        }
        if (selected.text.endsWith("/")) {
            // Its a folder
            int totalWidth = 30;
            for (GuiSelectable<?> select : shownFolders) {
                select.x = totalWidth;
                totalWidth += select.width + 30;
            }
            int index = shownFolders.indexOf(selector);
            GuiSelectable<GuiString> childGuiElement = shownFolders.get(index + 1);
            List<GuiString> children = rootFolder.getChildFolder(selected.id);
            childGuiElement.setList(children);
            for (int i = index + 2; i < shownFolders.size(); i++) {
                shownFolders.get(i).setList(null);
            }
        } else {
            // Its a file
        }
    }

    @Override
    public void initGui() {

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
        drawBackground(0);
        super.drawScreen(mouseX, mouseY, partialTicks);

        for (GuiSelectable<GuiString> shown : shownFolders) {
            shown.draw(mouseX, mouseY);
            if (!shown.isSelected()) {
                break;
            }
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
        super.handleMouseInput();
        float scroll = Mouse.getEventDWheel();
        scroll /= 4.0F;

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
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        for (GuiSelectable<GuiString> shown : shownFolders) {
            tryMouseClick(mouseX, shown);
            if (!shown.isSelected()) {
                break;
            }
        }
    }

    private void tryMouseClick(int mouseX, GuiSelectable<GuiString> selected) {
        if (mouseX > selected.x && mouseX <= selected.x + selected.width) {
            selected.onClick();
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        for (GuiSelectable<GuiString> shown : shownFolders) {
            tryMouseRelease(mouseX, shown);
            if (!shown.isSelected()) {
                break;
            }
        }
    }

    private void tryMouseRelease(int mouseX, GuiSelectable<GuiString> selected) {
        if (mouseX > selected.x && mouseX <= selected.x + selected.width) {
            selected.onRelease();
        }
    }
}
