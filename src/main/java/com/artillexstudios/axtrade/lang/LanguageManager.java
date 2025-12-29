package com.artillexstudios.axtrade.lang;

import com.artillexstudios.axapi.config.Config;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axapi.utils.Version;
import com.artillexstudios.axtrade.AxTrade;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import static com.artillexstudios.axtrade.AxTrade.CONFIG;

public class LanguageManager {
    private static Config translations;
    private static final Gson gson = new GsonBuilder().create();

    public static void reload() {
        final String lang = CONFIG.getString("language", "en_US").toLowerCase();
        final File file = new File(AxTrade.getInstance().getDataFolder(), "lib/translations/" + lang + ".yml");
        boolean exists = file.exists();
        translations = new Config(file);
        if (exists && !translations.getBackingDocument().isEmpty(true)) return;

        final List<String> versions = Version.getServerVersion().getVersions();
        final String version = versions.get(versions.size() - 1);
        final String url = "https://api.github.com/repos/InventivetalentDev/minecraft-assets/contents/assets/minecraft/lang/" + lang + ".json?ref=" + version;
        Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#00FF00╠ &#AAFFAADownloading &f" + lang + " &#AAFFAAlanguage files.."));

        final HttpClient client = HttpClient.newHttpClient();
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        try {
            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            final JsonObject object = gson.fromJson(response.body(), JsonObject.class);

            final String base64Content = object.get("content").getAsString().replace("\n", "");
            var gsonObject = gson.fromJson(new String(Base64.getDecoder().decode(base64Content)), JsonObject.class);

            for (Map.Entry<String, JsonElement> e : gsonObject.entrySet()) {
                if (e.getKey().startsWith("item.minecraft.")) {
                    final String name = e.getKey().replace("item.minecraft.", "");
                    if (name.contains(".")) continue;
                    translations.set("material." + name, e.getValue().getAsString());
                }
                if (e.getKey().startsWith("block.minecraft.")) {
                    final String name = e.getKey().replace("block.minecraft.", "");
                    if (name.contains(".")) continue;
                    translations.set("material." + name, e.getValue().getAsString());
                }
            }
            translations.save();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        client.close();
    }

    public static String getTranslated(@NotNull Material material) {
        return translations.getString("material." + material.name().toLowerCase(), material.name().toLowerCase().replace("_", " "));
    }
}
