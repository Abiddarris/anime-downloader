package com.aabid.animedownloader.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ByteFormatTest {

    @Test
    void formatBytesLessThanKibibyte() {
        assertEquals("0 B", ByteFormat.formatBytes(0));
        assertEquals("500 B", ByteFormat.formatBytes(500));
        assertEquals("1023 B", ByteFormat.formatBytes(1023));
    }

    @Test
    void formatBytesExactlyKibibyte() {
        assertEquals("1.00 KiB", ByteFormat.formatBytes(1024));
    }

    @Test
    void formatBytesKibibytes() {
        assertEquals("1.50 KiB", ByteFormat.formatBytes(1536));
        assertEquals("10.00 KiB", ByteFormat.formatBytes(10240));
    }

    @Test
    void formatBytesMebibytes() {
        assertEquals("1.00 MiB", ByteFormat.formatBytes(1048576));
        assertEquals("5.50 MiB", ByteFormat.formatBytes(5767168));
    }

    @Test
    void formatBytesGibibytes() {
        assertEquals("1.00 GiB", ByteFormat.formatBytes(1073741824));
    }

    @Test
    void formatBytesTebibytes() {
        assertEquals("1.00 TiB", ByteFormat.formatBytes(1099511627776L));
    }

    @Test
    void formatBytesPebibytes() {
        assertEquals("1.00 PiB", ByteFormat.formatBytes(1125899906842624L));
    }

    @Test
    void formatBytesExbibytes() {
        assertEquals("1.00 EiB", ByteFormat.formatBytes(1152921504606846976L));
    }
}