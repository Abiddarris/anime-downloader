package com.aabid.animedownloader.utils;

public class ByteFormat {

    public static String formatBytes(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        }
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        char pre = "KMGTPE".charAt(exp - 1);
        return String.format("%.2f %ciB", bytes / Math.pow(1024, exp), pre);
    }

}
