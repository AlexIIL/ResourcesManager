package alexiil.mc.mod.meta.res.scan;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.minecraft.client.resources.*;
import net.minecraft.util.ResourceLocation;

public class ResourceProvider {
    private static final Field abstractResourcePack__resourcePackFile;
    private static final Field defaultResourcePack__resourceIndex;
    private static final Field resourceIndex__resourceMap;

    private static final String[] STRIP_STARTS = { "FMLFileResourcePack:", "FMLFolderResourcePack:" };

    static {
        abstractResourcePack__resourcePackFile = AbstractResourcePack.class.getDeclaredFields()[1];
        abstractResourcePack__resourcePackFile.setAccessible(true);

        defaultResourcePack__resourceIndex = DefaultResourcePack.class.getDeclaredFields()[1];
        defaultResourcePack__resourceIndex.setAccessible(true);

        resourceIndex__resourceMap = ResourceIndex.class.getDeclaredFields()[1];
        resourceIndex__resourceMap.setAccessible(true);
    }

    public final String name;
    public final IResourcePack pack;

    public ResourceProvider(IResourcePack pack) {
        this.name = makeName(pack.getPackName());
        this.pack = pack;
    }

    private static String makeName(String name) {
        if ("Default".equals(name)) {
            return "Minecraft";
        }
        for (String start : STRIP_STARTS) {
            if (name.startsWith(start)) {
                return name.substring(start.length());
            }
        }
        return name;
    }

    public boolean resourceExists(ResourceLocation location) {
        return pack.resourceExists(location);
    }

    @Override
    public String toString() {
        return name;
    }

    public List<ResourceLocation> scanAllResourceLocations() {
        return scanPack(name, pack);
    }

    private static List<ResourceLocation> scanPack(String name, IResourcePack pack) {
        System.out.print("Scanning " + name + "\n");
        File file = null;
        List<ResourceLocation> locations = new ArrayList<>();
        if (pack instanceof AbstractResourcePack) {
            try {
                file = (File) abstractResourcePack__resourcePackFile.get(pack);
            } catch (IllegalAccessException e) {
                throw new Error(e);
            }
        } else if (pack instanceof DefaultResourcePack) {
            try {
                ResourceIndex index = (ResourceIndex) defaultResourcePack__resourceIndex.get(pack);
                Map<String, File> map = (Map<String, File>) resourceIndex__resourceMap.get(index);
                for (String s : map.keySet()) {
                    locations.add(new ResourceLocation(s));
                }
                Collections.sort(locations, (a, b) -> a.toString().compareTo(b.toString()));

                CodeSource src = DefaultResourcePack.class.getProtectionDomain().getCodeSource();
                if (src != null) {
                    file = new File(src.getLocation().getPath().split("!")[0].substring("file:".length()));
                }
            } catch (IllegalAccessException e) {
                throw new Error(e);
            }
        } else if (pack instanceof LegacyV2Adapter) {
            try {
                Field f = LegacyV2Adapter.class.getDeclaredFields()[0];
                f.setAccessible(true);
                return scanPack(name, (IResourcePack) f.get(pack));
            } catch (IllegalAccessException e) {
                throw new Error(e);
            }
        } else {
            System.out.print("Unknown IResourcePack " + pack.getClass() + "\n");
        }

        if (file != null) {
            if (file.isDirectory()) {
                try {
                    Files.walkFileTree(file.toPath(), new AddingFileVisitor(locations, file.toPath()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (file.isFile()) {
                try (ZipFile zf = new ZipFile(file)) {
                    Collections.addAll(locations, zf.stream().map(ResourceProvider::extractResourceLocation).filter(rl -> rl != null).toArray((l) -> new ResourceLocation[l]));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            System.out.print("  Didn't find a file!\n");
        }
        return locations;
    }

    private static ResourceLocation extractResourceLocation(ZipEntry ze) {
        String zeName = ze.getName();
        if (ze.isDirectory()) {
            return null;
        }
        return extractResourceLocation(zeName);
    }

    private static ResourceLocation extractResourceLocation(String zeName) {
        if (!zeName.startsWith("assets")) {
            return null;
        }
        String domain = zeName.substring("assets/".length());
        domain = domain.substring(0, domain.indexOf('/'));
        String rest = zeName.substring("assets/".length() + domain.length() + 1);
        if (rest.length() == 0) {
            return null;
        }
        return new ResourceLocation(domain, rest);
    }

    private static class AddingFileVisitor implements FileVisitor<Path> {
        private final List<ResourceLocation> locations;
        private final Path base;

        public AddingFileVisitor(List<ResourceLocation> locations, Path base) {
            this.locations = locations;
            this.base = base;
        }

        private String extractLoc(Path path) {
            return base.relativize(path).toString();
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            String loc = extractLoc(dir);
            if (loc.startsWith("assets") || loc.length() == 0) {
                return FileVisitResult.CONTINUE;
            }
            return FileVisitResult.SKIP_SUBTREE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            String ext = extractLoc(file);
            ResourceLocation loc = extractResourceLocation(ext);
            if (loc != null) {
                locations.add(loc);
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            return FileVisitResult.CONTINUE;
        }
    }
}
