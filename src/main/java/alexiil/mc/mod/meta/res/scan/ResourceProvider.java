package alexiil.mc.mod.meta.res.scan;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.resources.AbstractResourcePack;
import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.client.resources.ResourcePackRepository.Entry;
import net.minecraft.util.ResourceLocation;

public class ResourceProvider {
    private static final Field abstractResourcePack__resourcePackFile;

    static {
        abstractResourcePack__resourcePackFile = AbstractResourcePack.class.getDeclaredFields()[1];
        abstractResourcePack__resourcePackFile.setAccessible(true);
    }

    public final String name;
    private final ResourcePackRepository.Entry entry;
    private final IResourcePack pack;

    public ResourceProvider(IResourcePack pack) {
        this.name = pack.getPackName();
        this.entry = null;
        this.pack = pack;
    }

    public ResourceProvider(Entry entry) {
        this.name = entry.getResourcePackName();
        this.entry = entry;
        this.pack = entry.getResourcePack();
    }

    public boolean resourceExists(ResourceLocation location) {
        return pack.resourceExists(location);
    }

    @Override
    public String toString() {
        return name;
    }

    public List<ResourceLocation> scanAllResourceLocations() {
        System.out.print("Scanning " + name + "\n");
        File file = null;
        if (pack instanceof AbstractResourcePack) {
            try {
                file = (File) abstractResourcePack__resourcePackFile.get(pack);
            } catch (IllegalAccessException e) {
                throw new Error(e);
            }
        } else if (pack instanceof DefaultResourcePack) {

        }

        if (file != null) {
            List<ResourceLocation> locations = new ArrayList<>();
            if (file.isDirectory()) {
                try {
                    Files.walkFileTree(file.toPath(), new AddingFileVisitor(locations, file.toPath()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (file.isFile()) {
                try (ZipFile zf = new ZipFile(file)) {
                    Collections.addAll(locations, zf.stream().map(this::extractResourceLocation).filter(rl -> rl != null).toArray((l) -> new ResourceLocation[l]));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return locations;
        } else {
            return ImmutableList.of();
        }
    }

    private ResourceLocation extractResourceLocation(ZipEntry ze) {
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
