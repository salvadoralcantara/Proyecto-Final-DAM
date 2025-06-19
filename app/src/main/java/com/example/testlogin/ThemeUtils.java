package com.example.testlogin;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;

public final class ThemeUtils {
    private static final String PREFERENCES_NAME = "theme_preferences";
    private static final String DARK_MODE_KEY = "is_dark_mode_enabled";

    // Constructor privado para prevenir instanciación
    private ThemeUtils() {}

    /**
     * Aplica el tema guardado en las preferencias
     * @param context Contexto de la aplicación
     */
    public static void applyThemeFromPreferences(Context context) {
        boolean isDarkMode = getDarkModePreference(context);
        setAppThemeMode(isDarkMode);
    }

    /**
     * Cambia entre modo claro/oscuro y guarda la preferencia
     * @param context Contexto de la aplicación
     * @param enableDarkMode True para activar modo oscuro, false para modo claro
     */
    public static void toggleTheme(Context context, boolean enableDarkMode) {
        saveDarkModePreference(context, enableDarkMode);
        setAppThemeMode(enableDarkMode);
    }

    private static void setAppThemeMode(boolean isDarkMode) {
        int mode = isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
        AppCompatDelegate.setDefaultNightMode(mode);
    }

    private static boolean getDarkModePreference(Context context) {
        SharedPreferences prefs = getPreferences(context);
        return prefs.getBoolean(DARK_MODE_KEY, false);
    }

    private static void saveDarkModePreference(Context context, boolean value) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(DARK_MODE_KEY, value);
        editor.apply();
    }

    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }
}