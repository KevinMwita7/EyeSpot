package com.eyespot.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.eyespot.imageparser.bitmap.BitmapConstants;
import com.eyespot.imageparser.bitmap.InfoHeaderType;
import org.junit.jupiter.api.Test;

class InfoHeaderTypeTest {

    @Test
    void GivenInfoHeaderType_GetSize_ReturnsCorrectValues() {
        assertEquals(BitmapConstants.BITMAPCOREHEADER_SIZE, InfoHeaderType.BITMAPCOREHEADER.getSize());
        assertEquals(BitmapConstants.BITMAPINFOHEADER_SIZE, InfoHeaderType.BITMAPINFOHEADER.getSize());
        assertEquals(BitmapConstants.BITMAPV2INFOHEADER_SIZE, InfoHeaderType.BITMAPV2INFOHEADER.getSize());
        assertEquals(BitmapConstants.BITMAPV3INFOHEADER_SIZE, InfoHeaderType.BITMAPV3INFOHEADER.getSize());
        assertEquals(BitmapConstants.BITMAPV4HEADER_SIZE, InfoHeaderType.BITMAPV4HEADER.getSize());
        assertEquals(BitmapConstants.BITMAPV5HEADER_SIZE, InfoHeaderType.BITMAPV5HEADER.getSize());
    }

    @Test
    void GivenInfoHeaderType_FromSizeWithValidSizes_ReturnsCorrectValues() {
        assertEquals(InfoHeaderType.BITMAPCOREHEADER, InfoHeaderType.fromSize(BitmapConstants.BITMAPCOREHEADER_SIZE));
        assertEquals(InfoHeaderType.BITMAPINFOHEADER, InfoHeaderType.fromSize(BitmapConstants.BITMAPINFOHEADER_SIZE));
        assertEquals(InfoHeaderType.BITMAPV2INFOHEADER, InfoHeaderType.fromSize(BitmapConstants.BITMAPV2INFOHEADER_SIZE));
        assertEquals(InfoHeaderType.BITMAPV3INFOHEADER, InfoHeaderType.fromSize(BitmapConstants.BITMAPV3INFOHEADER_SIZE));
        assertEquals(InfoHeaderType.BITMAPV4HEADER, InfoHeaderType.fromSize(BitmapConstants.BITMAPV4HEADER_SIZE));
        assertEquals(InfoHeaderType.BITMAPV5HEADER, InfoHeaderType.fromSize(BitmapConstants.BITMAPV5HEADER_SIZE));
    }

    @Test
    void GivenInfoHeaderType_FromSizeWithInvalidSize_ReturnsNull() {
        assertNull(InfoHeaderType.fromSize(999));
        assertNull(InfoHeaderType.fromSize(0));
        assertNull(InfoHeaderType.fromSize(-1));
    }

    @Test
    void GivenInfoHeaderType_TestUniquenessOfSizes() {
        long distinctSizes = java.util.Arrays.stream(InfoHeaderType.values())
                .mapToInt(InfoHeaderType::getSize)
                .distinct()
                .count();

        assertEquals(InfoHeaderType.values().length, distinctSizes,
                "All InfoHeaderType values should have unique sizes");
    }
}
