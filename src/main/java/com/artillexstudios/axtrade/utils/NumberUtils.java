package com.artillexstudios.axtrade.utils;

import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import static com.artillexstudios.axtrade.AxTrade.CONFIG;

public class NumberUtils {
    private static NumberFormat formatter = new DecimalFormat(CONFIG.getString("number-formatting.formatted", "#,###.##"));
    private static NumberFormat shortFormat = new DecimalFormat(CONFIG.getString("number-formatting.formatted", "#,###.##"));

    public static void reload() {
        final String[] lang = CONFIG.getString("number-formatting.short", "en_US").split("_");
        shortFormat = DecimalFormat.getCompactNumberInstance(new Locale(lang[0], lang[1]), NumberFormat.Style.SHORT);

        int mode = CONFIG.getInt("number-formatting.mode", 0);
        switch (mode) {
            case 0:
                formatter = new DecimalFormat(CONFIG.getString("number-formatting.formatted", "#,###.##"));
                break;
            case 1:
                formatter = shortFormat;
                break;
            case 2:
                formatter = null;
                break;
        }
    }

    public static String formatNumber(double number) {
        return formatter == null ? "" + number : formatter.format(number);
    }

    @Nullable
    public static Double parseNumber(String number) {
        try {
            return shortFormat.parse(number.toUpperCase()).doubleValue();
        } catch (ParseException ex) {
            return null;
        }
    }
}
