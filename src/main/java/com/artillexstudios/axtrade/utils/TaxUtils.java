package com.artillexstudios.axtrade.utils;

import com.artillexstudios.axtrade.hooks.currency.CurrencyHook;

public class TaxUtils {

    public static double getTotalAfterTax(double original, CurrencyHook currencyHook) {
        Number tax = getTaxPercent(currencyHook);
        double taxMulti = getMultiplierFormat(tax.doubleValue());
        if (taxMulti == 1D) return original;
        return original * taxMulti;
    }

    public static double getTotalTax(double original, CurrencyHook currencyHook) {
        Number tax = getTaxPercent(currencyHook);
        double taxMulti = getMultiplierFormat(tax.doubleValue());
        if (taxMulti == 1D) return 0;
        return original - original * taxMulti;
    }

    public static Number getTaxPercent(CurrencyHook currencyHook) {
        Number value = (Number) currencyHook.getSettings().getOrDefault("tax", 0);
        return Math.min(value.doubleValue(), 99D); // at most 99% tax is possible
    }

    public static double getMultiplierFormat(double original) {
        return (100D - original) / 100D;
    }
}
