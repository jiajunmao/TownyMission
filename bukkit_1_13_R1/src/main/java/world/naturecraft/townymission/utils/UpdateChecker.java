package world.naturecraft.townymission.utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.jar.Pack200;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateChecker {

    private JavaPlugin plugin;
    private int resourceId;

    public UpdateChecker(JavaPlugin plugin, int resourceId) {
        this.plugin = plugin;
        this.resourceId = resourceId;
    }

    public void getVersion(final Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            try (InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + this.resourceId).openStream();
                 Scanner scanner = new Scanner(inputStream)) {
                if (scanner.hasNext()) {
                    consumer.accept(scanner.next());
                }
            } catch (IOException exception) {
                this.plugin.getLogger().info("Cannot look for updates: " + exception.getMessage());
            }
        });
    }

    public static boolean isGreater(String ver1, String ver2) {
        List<String> ver1Tags = new LinkedList<>();
        List<String> ver2Tags = new LinkedList<>();

        int shortest = 0;
        if (ver1.length() != ver2.length()) {
            shortest = Math.min(ver1.length(), ver2.length());
        } else {
            shortest = ver1.length();
        }

        for (int i = 0; i < shortest; i++) {
            if (ver1.charAt(i) != '.') {
                ver1Tags.add("" + ver1.charAt(i));
                ver2Tags.add("" + ver2.charAt(i));
            }
        }

        for(int i = 0; i < 3; i++) {
            if (Integer.parseInt(ver1Tags.get(i).replace(".", "")) > Integer.parseInt(ver2Tags.get(i).replace(".", ""))) {
                return true;
            }
        }

        if (ver1Tags.size() > ver2Tags.size()) {
            return true;
        }

        return false;
    }
}