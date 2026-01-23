
package io.github.feather_browser.feather.ui

fun isDarkMode(): Boolean {
    return try {
        val process = ProcessBuilder(
            "reg",
            "query",
            "HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Themes\\Personalize",
            "/v",
            "AppsUseLightTheme"
        ).start()

        val output = process.inputStream.bufferedReader().readText()
        output.contains("REG_DWORD") && output.trim().endsWith("0x0")
    } catch (e: Exception) {
        false
    }
}