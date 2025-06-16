package com.eyespot.parser;

import com.eyespot.imageparser.ImageType;
import com.eyespot.imageparser.bitmap.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class BitmapParserTest {

  private static BitmapParser commonParser;

  private static BitmapParser coreParser;

  private static BitmapParser v2Parser;

  private static BitmapParser v3Parser;

  private static BitmapParser v4Parser;

  private static BitmapV4Header v4Header;

  private static BitmapParser v5Parser;

  private static BitmapV5Header v5Header;

  @BeforeAll
  static void setUp() throws URISyntaxException, IOException {
    // BITAPINFOHEADER
    URL commonResource = BitmapParserTest.class.getClassLoader().getResource("sample_bmp.bmp");
    Assertions.assertNotNull(commonResource);
    commonParser = new BitmapParser(Paths.get(commonResource.toURI()));

    // Parser for bitmap with BITMAPCOREHEADER
    URL coreResource = BitmapParserTest.class.getClassLoader().getResource("bmp_1000x500.bmp");
    Assertions.assertNotNull(coreResource);
    coreParser = new BitmapParser(Paths.get(coreResource.toURI()));

    // Parser for bitmap with BITMAPV2INFOHEADER
    URL v2Resource = BitmapParserTest.class.getClassLoader().getResource("bmp_v2_500x250.bmp");
    Assertions.assertNotNull(v2Resource);
    v2Parser = new BitmapParser(Paths.get(v2Resource.toURI()));

    // Parser for bitmap with BITMAPV3INFOHEADER
    URL v3Resource = BitmapParserTest.class.getClassLoader().getResource("bmp_v3_1000x500.bmp");
    Assertions.assertNotNull(v3Resource);
    v3Parser = new BitmapParser(Paths.get(v3Resource.toURI()));

    // Parser and header for bitmap with BITMAPV4HEADER
    URL v4Resource =
        BitmapParserTest.class.getClassLoader().getResource("32_bit_transparent_v4.bmp");
    Assertions.assertNotNull(v4Resource);
    v4Parser = new BitmapParser(Paths.get(v4Resource.toURI()));

    v4Header =
        (BitmapV4Header)
            DIBHeader.createDIBHeader(Arrays.copyOfRange(v4Parser.getRawData(), 0, 122));

    // Parser and header for bitmap with BITMAPV5HEADER
    URL v5Resource = BitmapParserTest.class.getClassLoader().getResource("32bit_v5.bmp");
    Assertions.assertNotNull(v5Resource);
    v5Parser = new BitmapParser(Paths.get(v5Resource.toURI()));

    v5Header =
        (BitmapV5Header)
            DIBHeader.createDIBHeader(Arrays.copyOfRange(v5Parser.getRawData(), 0, 138));
  }

  @Test
  void GivenEmptyImage_ThrowsIllegalArgException() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> new BitmapParser(new byte[] {}));
  }

  @Test
  void GivenMalformedMagicNumber_ThrowsIllegalArgException() {
    byte[] bytes = {
      0x56, 0x58, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
      0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
    };
    Assertions.assertThrows(IllegalArgumentException.class, () -> new BitmapParser(bytes));
  }

  @Test
  void GivenCoreBitmapImage_HeaderSizeShort_ThrowsIllegalArgException() {
    byte[] bytes = Arrays.copyOfRange(coreParser.getRawData(), 0, 25);
    Assertions.assertThrows(IllegalArgumentException.class, () -> new BitmapParser(bytes));
  }

  @Test
  void GivenCoreBitmapImage_GetAlphaMask_ThrowsUnsupportedOperationException() {
    Assertions.assertThrows(UnsupportedOperationException.class, () -> coreParser.getAlphaMask());
  }

  @Test
  void GivenCoreBitmapImage_GetProfileSize_ThrowsUnsupportedOperationException() {
    Assertions.assertThrows(UnsupportedOperationException.class, () -> coreParser.getProfileSize());
  }

  @Test
  void GivenCommonBitmapImage_HeaderSizeShort_ThrowsIllegalArgException() {
    byte[] bytes = Arrays.copyOfRange(commonParser.getRawData(), 0, 53);
    Assertions.assertThrows(IllegalArgumentException.class, () -> new BitmapParser(bytes));
  }

  @Test
  void GivenV2BitmapImage_HeaderSizeShort_ThrowsIllegalArgException() {
    byte[] bytes = Arrays.copyOfRange(v2Parser.getRawData(), 0, 55);
    Assertions.assertThrows(IllegalArgumentException.class, () -> new BitmapParser(bytes));
  }

  // Bitmap with BITMAPINFOHEADER tests
  @Test
  void GivenBitmapImage_ReturnsFileData() throws URISyntaxException, IOException {
    URL resource = BitmapParserTest.class.getClassLoader().getResource("sample_bmp.bmp");
    Assertions.assertNotNull(resource);

    byte[] bytes = Files.readAllBytes(Paths.get(resource.toURI()));

    Assertions.assertArrayEquals(commonParser.getRawData(), bytes);
  }

  @Test
  void GivenCommonBitmapImage_GetType_ReturnsBitmap() {
    Assertions.assertEquals(ImageType.BITMAP, commonParser.getType());
  }

  @Test
  void GivenCommonBitmapImage_GetSize_ReturnsImageSize() {
    Assertions.assertEquals(1_500_056, commonParser.getSize());
  }

  @Test
  void GivenCommonBitmapImage_GetOffset_ReturnsOffset() {
    Assertions.assertEquals(54, commonParser.getOffset());
  }

  @Test
  void GivenCommonBitmapImage_GetWidth_ReturnsWidth() {
    Assertions.assertEquals(1000, commonParser.getWidth());
  }

  @Test
  void GivenCommonBitmapImage_GetHeight_ReturnsHeight() {
    Assertions.assertEquals(500, commonParser.getHeight());
  }

  @Test
  void GivenCommonBitmapImage_GetHeaderSize_Returns40() {
    Assertions.assertEquals(40, commonParser.getHeaderSize());
  }

  @Test
  void GivenCommonBitmapImage_GetDibHeaderType_ReturnsBitmapCoreHeader() {
    Assertions.assertEquals(InfoHeaderType.BITMAPINFOHEADER, commonParser.getDibHeaderType());
  }

  @Test
  void GivenCommonBitmapImage_GetBitsPerPixel_Returns24() {
    Assertions.assertEquals(24, commonParser.getBitsPerPixel());
  }

  @Test
  void GivenCommonBitmapImage_GetCompression_Returns0() {
    // 0 == uncompressed
    Assertions.assertEquals(0, commonParser.getCompression());
  }

  @Test
  void GivenCommonBitmapImage_GetXResolution_ReturnsXResolution() {
    Assertions.assertEquals(2834, commonParser.getXResolution());
  }

  @Test
  void GivenCommonBitmapImage_GetYResolution_ReturnsYResolution() {
    Assertions.assertEquals(2834, commonParser.getYResolution());
  }

  @Test
  void GivenCommonBitmapImage_GetImageDataSize_ReturnsImageDataSize() {
    Assertions.assertEquals(1_500_002, commonParser.getImageDataSize());
  }

  @Test
  void GivenCommonBitmapImage_GetNColours_ReturnsNColours() {
    Assertions.assertEquals(0, commonParser.getNColours());
  }

  @Test
  void GivenCommonBitmapImage_GetImportantColours_ReturnsImportantColours() {
    Assertions.assertEquals(0, commonParser.getImportantColours());
  }

  @Test
  void GivenCommonBitmapImageNoColourPalette_HasColourPalette_ReturnsFalse() {
    Assertions.assertFalse(commonParser.hasColourPalette());
  }

  @Test
  void GivenCommonBitmapImage_GetAlphaMask_ThrowsUnsupportedOperationException() {
    Assertions.assertThrows(UnsupportedOperationException.class, () -> commonParser.getAlphaMask());
  }

  @Test
  void GivenCommonBitmapImage_GetProfileSize_ThrowsUnsupportedOperationException() {
    Assertions.assertThrows(
        UnsupportedOperationException.class, () -> commonParser.getProfileSize());
  }

  // Bitmap with BITMAPCOREHEADER tests
  @Test
  void GivenCoreBitmapImage_GetType_ReturnsBitmap() {
    Assertions.assertEquals(ImageType.BITMAP, coreParser.getType());
  }

  @Test
  void GivenCoreBitmap_GetHeaderType_ReturnsBitmapCoreHeader() {
    Assertions.assertEquals(InfoHeaderType.BITMAPCOREHEADER, coreParser.getDibHeaderType());
  }

  // Bitmap with BITMAPV2INFOHEADER tests
  @Test
  void GivenV2BitmapImage_GetType_ReturnsBitmap() {
    Assertions.assertEquals(ImageType.BITMAP, v2Parser.getType());
  }

  @Test
  void GivenV2Bitmap_GetHeaderType_ReturnsBitmapV2InfoHeader() {
    Assertions.assertEquals(InfoHeaderType.BITMAPV2INFOHEADER, v2Parser.getDibHeaderType());
  }

  @Test
  void GivenV2Bitmap_GetRedMask_ReturnsCorrectValue() {
    BitmapV2InfoHeader header =
        (BitmapV2InfoHeader)
            DIBHeader.createDIBHeader(Arrays.copyOfRange(v2Parser.getRawData(), 0, 66));
    Assertions.assertEquals(16_711_680, header.getRedMask());
  }

  @Test
  void GivenV2Bitmap_GetGreenMask_ReturnsCorrectValue() {
    BitmapV2InfoHeader header =
        (BitmapV2InfoHeader)
            DIBHeader.createDIBHeader(Arrays.copyOfRange(v2Parser.getRawData(), 0, 66));
    Assertions.assertEquals(65_280, header.getGreenMask());
  }

  @Test
  void GivenV2Bitmap_GetBlueMask_ReturnsCorrectValue() {
    BitmapV2InfoHeader header =
        (BitmapV2InfoHeader)
            DIBHeader.createDIBHeader(Arrays.copyOfRange(v2Parser.getRawData(), 0, 66));
    Assertions.assertEquals(255, header.getBlueMask());
  }

  @Test
  void GivenV2Bitmap_GetAlphaMask_ThrowsUnsupportedOperationException() {
    Assertions.assertThrows(UnsupportedOperationException.class, () -> v2Parser.getAlphaMask());
  }

  @Test
  void GivenV2BitmapImage_GetProfileSize_ThrowsUnsupportedOperationException() {
    Assertions.assertThrows(UnsupportedOperationException.class, () -> v2Parser.getProfileSize());
  }

  // Bitmap with BITMAPV3INFOHEADER tests
  @Test
  void GivenV3BitmapImage_GetType_ReturnsBitmap() {
    Assertions.assertEquals(ImageType.BITMAP, v3Parser.getType());
  }

  @Test
  void GivenV3Bitmap_GetHeaderType_ReturnsBitmapV3InfoHeader() {
    Assertions.assertEquals(InfoHeaderType.BITMAPV3INFOHEADER, v3Parser.getDibHeaderType());
  }

  @Test
  void GivenV3Bitmap_GetRedMask_ReturnsCorrectValue() {
    BitmapV3InfoHeader header =
        (BitmapV3InfoHeader)
            DIBHeader.createDIBHeader(Arrays.copyOfRange(v3Parser.getRawData(), 0, 74));
    Assertions.assertEquals(16_711_680, header.getRedMask());
  }

  @Test
  void GivenV3Bitmap_GetGreenMask_ReturnsCorrectValue() {
    BitmapV3InfoHeader header =
        (BitmapV3InfoHeader)
            DIBHeader.createDIBHeader(Arrays.copyOfRange(v3Parser.getRawData(), 0, 74));
    Assertions.assertEquals(65_280, header.getGreenMask());
  }

  @Test
  void GivenV3Bitmap_GetBlueMask_ReturnsCorrectValue() {
    BitmapV3InfoHeader header =
        (BitmapV3InfoHeader)
            DIBHeader.createDIBHeader(Arrays.copyOfRange(v3Parser.getRawData(), 0, 74));
    Assertions.assertEquals(255, header.getBlueMask());
  }

  @Test
  void GivenV3Bitmap_GetAlphaMask_ReturnsCorrectValue() {
    Assertions.assertEquals(2_169_624_778L, v3Parser.getAlphaMask());
  }

  @Test
  void GivenV3BitmapImage_GetProfileSize_ThrowsUnsupportedOperationException() {
    Assertions.assertThrows(UnsupportedOperationException.class, () -> v3Parser.getProfileSize());
  }

  // Bitmap with BITMAPV4HEADER tests
  @Test
  void GivenV4Bitmap_GetInfoHeaderType_ReturnsBitmapV4Header() {
    Assertions.assertEquals(InfoHeaderType.BITMAPV4HEADER, v4Header.getType());
  }

  @Test
  void GivenV4Bitmap_GetRedMask_ReturnsCorrectValue() {
    Assertions.assertEquals(16_711_680, v4Header.getRedMask());
  }

  @Test
  void GivenV4Bitmap_GetGreenMask_ReturnsCorrectValue() {
    Assertions.assertEquals(65_280, v4Header.getGreenMask());
  }

  @Test
  void GivenV4Bitmap_GetBlueMask_ReturnsCorrectValue() {
    Assertions.assertEquals(255, v4Header.getBlueMask());
  }

  @Test
  void GivenV4Bitmap_GetAlphaMask_ReturnsCorrectValue() {
    Assertions.assertEquals(4_278_190_080L, v4Header.getAlphaMask());
  }

  @Test
  void GivenV4Bitmap_GetCsType_ReturnsCorrectValue() {
    Assertions.assertEquals(1, v4Header.getCsType());
  }

  @Test
  void GivenV4BitmapParser_GetAlphaMask_ReturnsCorrectValue() {
    Assertions.assertEquals(4_278_190_080L, v4Parser.getAlphaMask());
  }

  @Test
  void GivenV4Bitmap_GetCIEXYZRx_ReturnsCorrectValue() {
    Assertions.assertEquals(0, v4Header.getEndpoints().getRedX());
  }

  @Test
  void GivenV4Bitmap_GetCIEXYZRy_ReturnsCorrectValue() {
    Assertions.assertEquals(0, v4Header.getEndpoints().getRedY());
  }

  @Test
  void GivenV4Bitmap_GetCIEXYZRz_ReturnsCorrectValue() {
    Assertions.assertEquals(1, v4Header.getEndpoints().getRedZ());
  }

  @Test
  void GivenV4Bitmap_GetCIEXYZGx_ReturnsCorrectValue() {
    Assertions.assertEquals(0, v4Header.getEndpoints().getGreenX());
  }

  @Test
  void GivenV4Bitmap_GetCIEXYZGy_ReturnsCorrectValue() {
    Assertions.assertEquals(0, v4Header.getEndpoints().getGreenY());
  }

  @Test
  void GivenV4Bitmap_GetCIEXYZGz_ReturnsCorrectValue() {
    Assertions.assertEquals(1, v4Header.getEndpoints().getGreenZ());
  }

  @Test
  void GivenV4Bitmap_GetCIEXYZBx_ReturnsCorrectValue() {
    Assertions.assertEquals(0, v4Header.getEndpoints().getBlueX());
  }

  @Test
  void GivenV4Bitmap_GetCIEXYZBy_ReturnsCorrectValue() {
    Assertions.assertEquals(0, v4Header.getEndpoints().getBlueY());
  }

  @Test
  void GivenV4Bitmap_GetCIEXYZBz_ReturnsCorrectValue() {
    Assertions.assertEquals(1, v4Header.getEndpoints().getBlueZ());
  }

  @Test
  void GivenV4Bitmap_GetGammaRed_ReturnsCorrectValue() {
    Assertions.assertEquals(0, v4Header.getGammaRed());
  }

  @Test
  void GivenV4Bitmap_GetGammaGreen_ReturnsCorrectValue() {
    Assertions.assertEquals(0, v4Header.getGammaGreen());
  }

  @Test
  void GivenV4Bitmap_GetGammaBlue_ReturnsCorrectValue() {
    Assertions.assertEquals(0, v4Header.getGammaBlue());
  }

  @Test
  void GivenV4Bitmap_HeaderShort_ThrowsIllegalArgumentException() {
    byte[] bytes = Arrays.copyOfRange(v4Parser.getRawData(), 0, 121);
    Assertions.assertThrows(IllegalArgumentException.class, () -> DIBHeader.createDIBHeader(bytes));
  }

  @Test
  void GivenV4BitmapImage_GetProfileSize_ThrowsUnsupportedOperationException() {
    Assertions.assertThrows(UnsupportedOperationException.class, () -> v4Parser.getProfileSize());
  }

  // Bitmap with BITMAPV5HEADER tests
  @Test
  void GivenV5Bitmap_GetType_ReturnsCorrectValue() {
    Assertions.assertEquals(InfoHeaderType.BITMAPV5HEADER, v5Header.getType());
  }

  @Test
  void GivenV5Bitmap_GetIntent_ReturnsCorrectValue() {
    Assertions.assertEquals(8, v5Header.getIntent());
  }

  @Test
  void GivenV5Bitmap_GetProfileData_ReturnsCorrectValue() {
    Assertions.assertEquals(0, v5Header.getProfileData());
  }

  @Test
  void GivenV5Bitmap_GetProfileSize_ReturnsCorrectValue() {
    Assertions.assertEquals(0, v5Header.getProfileSize());
  }

  @Test
  void GivenV5BitmapParser_GetProfileSize_ReturnsCorrectValue() {
    Assertions.assertEquals(0, v5Parser.getProfileSize());
  }

  @Test
  void GivenV5Bitmap_GetReservedV5_ReturnsCorrectValue() {
    Assertions.assertEquals(0, v5Header.getReservedV5());
  }

  @Test
  void GivenV5BitmapParser_GetAlphaMask_ReturnsCorrectValue() {
    Assertions.assertEquals(4_278_190_080L, v5Parser.getAlphaMask());
  }

  @Test
  void GivenV5Bitmap_HeaderShort_ThrowsIllegalArgumentException() {
    byte[] bytes = Arrays.copyOfRange(v5Parser.getRawData(), 0, 137);
    Assertions.assertThrows(IllegalArgumentException.class, () -> DIBHeader.createDIBHeader(bytes));
  }
}
