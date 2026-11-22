package com.greatsage.client;

import net.minecraftforge.fml.loading.FMLPaths;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/** Favoritos persistentes (client). Um id por linha em config/greatsage_favorites.txt */
public final class FavoritesStore {
    private static final Set<String> FAVS = new LinkedHashSet<>();
    private static boolean loaded = false;
    private static Path file() { return FMLPaths.CONFIGDIR.get().resolve("greatsage_favorites.txt"); }

    private static void load() {
        if (loaded) return;
        loaded = true;
        try { if (Files.exists(file())) FAVS.addAll(Files.readAllLines(file())); } catch (IOException ignored) {}
    }
    public static boolean isFav(String id) { load(); return FAVS.contains(id); }
    public static void toggle(String id) {
        load();
        if (!FAVS.remove(id)) FAVS.add(id);
        try { Files.write(file(), FAVS); } catch (IOException ignored) {}
    }
}
