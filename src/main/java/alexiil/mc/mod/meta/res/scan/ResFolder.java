package alexiil.mc.mod.meta.res.scan;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.minecraft.util.ResourceLocation;

import alexiil.mc.mod.meta.res.gui.GuiString;

public class ResFolder {
    public final ResFolder parent;
    public final GuiString guiElement;
    public final Map<String, ResFolder> folders = new TreeMap<>();
    public final Map<String, ResFile> files = new TreeMap<>();
    public final List<GuiString> guiElements = new ArrayList<>();
    public int depth = 0;

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
            guiElements.add(new GuiString(file.name, guiElement.id + file.name));
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

    public ResFile getFile(String id) {
        ResFolder lookup = this;
        if (id.contains("/")) {
            String[] split = id.split("/");
            for (int i = 0; i < split.length - 1; i++) {
                lookup = lookup.folders.get(split[i]);
            }
            id = split[split.length - 1];
        }
        return lookup.files.get(id);
    }

    public void addFile(ResourceLocation location, ResourceProvider[] providers) {
        String[] loc = toPath(location);
        ResFolder current = this;
        for (int i = 0; i + 1 < loc.length; i++) {
            String s = loc[i];
            if (!current.folders.containsKey(s)) {
                current.folders.put(s, new ResFolder(current, s));
            }
            current = current.folders.get(s);
        }
        String file = loc[loc.length - 1];
        current.files.put(file, new ResFile(current, file, location, providers));
        for (int i = 1; current != null; i++, current = current.parent) {
            current.depth = Math.max(current.depth, i);
        }
    }

    private static String[] toPath(ResourceLocation loc) {
        String full = loc.getResourceDomain() + "/" + loc.getResourcePath();
        return full.split("/");
    }
}
