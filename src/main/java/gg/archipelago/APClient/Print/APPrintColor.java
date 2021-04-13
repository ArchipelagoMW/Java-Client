package gg.archipelago.APClient.Print;

public enum APPrintColor {
    bold("0"),
    underline("0"),
    black("1d1c21"),
    red("b02e26"),
    green("00ff80"),
    yellow("ffd83d"),
    blue("3c44a9"),
    magenta("c64fbd"),
    cyan("3ab3da"),
    white("f9ffff"),
    black_bg("1d1c21"),
    red_bg("b02e26"),
    green_bg("5d7c15"),
    yellow_bg("f9801d"),
    blue_bg("169c9d"),
    purple_bg("8932b7"),
    cyan_bg("3ab3da"),
    white_bg("9c9d97"),
    gold("ffcc00");

    public final int value;

    private APPrintColor(String color) {
        this.value = Integer.parseInt(color, 16);
    }
}
