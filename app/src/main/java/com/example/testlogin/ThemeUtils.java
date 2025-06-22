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
     */
    public static void applyThemeFromPreferences(Context context) {
        boolean isDarkMode = getDarkModePreference(context);
        setAppThemeMode(isDarkMode);
    }

    /**
     * Cambia entre modo claro/oscuro y guarda la preferencia
     */
    public static void toggleTheme(Context context, boolean enableDarkMode) {
        saveDarkModePreference(context, enableDarkMode);
        setAppThemeMode(enableDarkMode);
    }

    /**
     * Devuelve si el modo oscuro está activado o no
     */
    public static boolean isDarkModeEnabled(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(DARK_MODE_KEY, false);
    }

    // Establece el modo de tema de la aplicación
    private static void setAppThemeMode(boolean isDarkMode) {
        int mode = isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
        AppCompatDelegate.setDefaultNightMode(mode);
    }

    // Guarda la preferencia del modo oscuro
    private static void saveDarkModePreference(Context context, boolean value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean(DARK_MODE_KEY, value);
        editor.apply();
    }

    // Obtiene la preferencia actual del modo oscuro
    static boolean getDarkModePreference(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(DARK_MODE_KEY, false);
    }
}
