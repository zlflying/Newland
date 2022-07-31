package com.example.newland.utils;

public enum MultiPage {

    首页(0),
    ADAM4150(1),
    ADAM4017(2),
    ZigBee(3),
    RFID(4),
    LedScreen(5),
    Camera(6),
    SerialPort(7);

    private final int position;

    MultiPage(int pos) {
        position = pos;
    }

    public static MultiPage getPage(int position) {
        return MultiPage.values()[position];
    }

    public static int size() {
        return MultiPage.values().length;
    }

    public static String[] getPageNames() {
        MultiPage[] pages = MultiPage.values();
        String[] pageNames = new String[pages.length];
        for (int i = 0; i < pages.length; i++) {
            pageNames[i] = pages[i].name();
        }
        return pageNames;
    }

    public int getPosition() {
        return position;
    }

}
