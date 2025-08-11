package io.github.archipelagomw.Print;


import java.awt.*;

public enum APPrintColor {
    none,
    bold,
    underline,
    black(Color.black),
    red(Color.red),
    green(Color.green),
    yellow(Color.yellow),
    blue(Color.blue),
    magenta(Color.magenta),
    cyan(Color.cyan),
    white(Color.white),
    black_bg(Color.black),
    red_bg(Color.red),
    green_bg(Color.green),
    yellow_bg(Color.yellow),
    blue_bg(Color.blue),
    purple_bg(Color.decode("#800080")),
    cyan_bg(Color.cyan),
    white_bg(Color.white),
    magenta_bg(Color.magenta),
    gold(Color.decode("#FFD700"));

    public final Color color;

    APPrintColor() {
        this.color = Color.white;
    }

    APPrintColor(Color color) {
        this.color = color;
    }
}
