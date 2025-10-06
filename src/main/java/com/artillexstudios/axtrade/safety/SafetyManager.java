package com.artillexstudios.axtrade.safety;

import com.artillexstudios.axapi.executor.ExceptionReportingScheduledThreadPool;
import com.artillexstudios.axapi.utils.Version;
import com.artillexstudios.axapi.utils.http.Requests;
import com.artillexstudios.axtrade.AxTrade;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.artillexstudios.axtrade.AxTrade.CONFIG;

public enum SafetyManager {
    TRADING,
    CURRENCY_SELECTOR;

    private static final JavaPlugin instance = AxTrade.getInstance();
    private static final Gson gson = new GsonBuilder().create();
    private static ScheduledExecutorService service = null;
    private static Notifier notifier = null;
    private static boolean safe = true;
    private boolean enabled = true;

    public void set(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean get() {
        return enabled;
    }

    public static boolean isSafe() {
        return safe;
    }

    private static void check() {
        String str = "https://api.artillex-studios.com/safety/?plugin=%s&version=%s&mc=%s".formatted(instance.getName(), instance.getDescription().getVersion(), Version.getProtocolVersion());
        String body;
        try {
            body = Requests.get(str, Map.of()).body();
        } catch (Exception ignored) {
            return;
        }

        JsonArray disabled = gson.fromJson(body, JsonArray.class);
        for (SafetyManager value : SafetyManager.values()) {
            value.set(true);
        }
        safe = true;
        for (JsonElement jsonElement : disabled) {
            try {
                SafetyManager.valueOf(jsonElement.getAsString()).set(false);
            } catch (Exception ignored) {}
            safe = false;
        }
        if (!safe) notifier.sendAlert();
    }

    public static void start() {
        if (!CONFIG.getBoolean("enable-safety", true)) return;
        if (service != null) return;
        notifier = new Notifier();
        service = new ExceptionReportingScheduledThreadPool(0, Thread.ofVirtual().factory());
        service.scheduleAtFixedRate(SafetyManager::check, 0, 3, TimeUnit.MINUTES);
    }

    public static void stop() {
        if (service == null) return;
        service.shutdownNow();
    }
}
