package com.artillexstudios.axtrade.utils;

import com.artillexstudios.axtrade.AxTrade;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.ZonedDateTime;

public class HistoryUtils {

    public static void writeToHistory(@NotNull String txt) {
        final ZonedDateTime zdt = ZonedDateTime.now();
        final File subdir = new File(AxTrade.getInstance().getDataFolder().getPath() + System.getProperty("file.separator") + "history");
        subdir.mkdirs();
        final File file = new File(subdir.getPath() + "/" + zdt.getYear() + "-" + twoDigit(zdt.getMonthValue()) + "-" + twoDigit(zdt.getDayOfMonth()) + ".log");

        try (FileWriter fr = new FileWriter(file.getAbsoluteFile(), true)) {
            try (PrintWriter pr = new PrintWriter(fr)) {
                pr.print("[" + twoDigit(zdt.getHour()) + ":" + twoDigit(zdt.getMinute()) + ":" + twoDigit(zdt.getSecond()) + "] ");
                pr.println(txt);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static @NotNull String twoDigit(int i) {
        return i > 9 ? Integer.toString(i) : "0" + i;
    }
}
