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
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class BitmapParserTest {

  private static BitmapParser commonParser;

  private static BitmapParser commonParserWithColourPalette;

  private static BitmapParser common8BitWithColourPaletteParser;

  private static BitmapParser common16BitParser;

  private static BitmapParser common32BitParser;

  private static BitmapParser coreParser;

  private static BitmapParser core1BitWithColourPaletteParser;

  private static BitmapParser core4BitWithColourPaletteParser;

  private static BitmapParser v2Parser;

  private static BitmapParser v3Parser;

  private static BitmapParser v4Parser;

  private static BitmapParser v4ParserWithPaletteAndAlpha;

  private static BitmapParser badBitCountParser;

  private static BitmapV4Header v4Header;

  private static BitmapInfoHeader infoHeader;

  private static BitmapCoreHeader coreHeader;

  private static BitmapV3InfoHeader v3InfoHeader;

  private static BitmapV2InfoHeader v2InfoHeader;

  private static BitmapParser v5Parser;

  private static BitmapV5Header v5Header;

  @BeforeAll
  static void setUp() throws URISyntaxException, IOException {
    // Parser and header for BITAPINFOHEADER
    URL commonResource = BitmapParserTest.class.getClassLoader().getResource("sample_bmp.bmp");
    Assertions.assertNotNull(commonResource);
    commonParser = new BitmapParser(Paths.get(commonResource.toURI()));

    URL commonWithColourPaletteResource =
        BitmapParserTest.class.getClassLoader().getResource("bmp_common_w_colour_palette.bmp");
    Assertions.assertNotNull(commonWithColourPaletteResource);
    commonParserWithColourPalette =
        new BitmapParser(Paths.get(commonWithColourPaletteResource.toURI()));

    infoHeader =
        (BitmapInfoHeader)
            DIBHeader.createDIBHeader(Arrays.copyOfRange(commonParser.getRawData(), 0, 54));

    // Parser for 8bpp bitmap with BITMAPINFOHEADER
    URL common8BitWithColourPaletteResource =
        BitmapParserTest.class.getClassLoader().getResource("info_header_8bit.bmp");
    Assertions.assertNotNull(common8BitWithColourPaletteResource);
    common8BitWithColourPaletteParser =
        new BitmapParser(Paths.get(common8BitWithColourPaletteResource.toURI()));

    // Parser for 16bpp bitmap with BITMAPINFOHEADER
    URL commonParser16BitResource =
        BitmapParserTest.class.getClassLoader().getResource("16bit.bmp");
    Assertions.assertNotNull(commonParser16BitResource);
    common16BitParser = new BitmapParser(Paths.get(commonParser16BitResource.toURI()));

    // Parser for 32bpp bitmap with BITMAPINFOHEADER
    URL commonParser32BitResource =
        BitmapParserTest.class.getClassLoader().getResource("info_header_32bit.bmp");
    Assertions.assertNotNull(commonParser32BitResource);
    common32BitParser = new BitmapParser(Paths.get(commonParser32BitResource.toURI()));

    // Parser and header for bitmap with BITMAPCOREHEADER
    URL coreResource = BitmapParserTest.class.getClassLoader().getResource("bmp_1000x500.bmp");
    Assertions.assertNotNull(coreResource);
    coreParser = new BitmapParser(Paths.get(coreResource.toURI()));

    coreHeader =
        (BitmapCoreHeader)
            DIBHeader.createDIBHeader(Arrays.copyOfRange(coreParser.getRawData(), 0, 26));

    // Parser for 1bpp bitmap with BITMAPCOREHEADER
    URL core1BitWithColourPaletteResource =
        BitmapParserTest.class.getClassLoader().getResource("core_header_1bit.bmp");
    Assertions.assertNotNull(core1BitWithColourPaletteResource);
    core1BitWithColourPaletteParser =
        new BitmapParser(Paths.get(core1BitWithColourPaletteResource.toURI()));

    // Parser for 4bpp bitmap with BITMAPCOREHEADER
    URL core4BitWithColourPaletteResource =
        BitmapParserTest.class.getClassLoader().getResource("core_header_4bit.bmp");
    Assertions.assertNotNull(core4BitWithColourPaletteResource);
    core4BitWithColourPaletteParser =
        new BitmapParser(Paths.get(core4BitWithColourPaletteResource.toURI()));

    // Parser and header for bitmap with BITMAPV2INFOHEADER
    URL v2Resource = BitmapParserTest.class.getClassLoader().getResource("bmp_v2_500x250.bmp");
    Assertions.assertNotNull(v2Resource);
    v2Parser = new BitmapParser(Paths.get(v2Resource.toURI()));

    v2InfoHeader =
        (BitmapV2InfoHeader)
            DIBHeader.createDIBHeader(Arrays.copyOfRange(v2Parser.getRawData(), 0, 66));

    // Parser and header for bitmap with BITMAPV3INFOHEADER
    URL v3Resource = BitmapParserTest.class.getClassLoader().getResource("bmp_v3_1000x500.bmp");
    Assertions.assertNotNull(v3Resource);
    v3Parser = new BitmapParser(Paths.get(v3Resource.toURI()));

    v3InfoHeader =
        (BitmapV3InfoHeader)
            DIBHeader.createDIBHeader(Arrays.copyOfRange(v3Parser.getRawData(), 0, 74));

    // Parser and header for bitmap with BITMAPV4HEADER
    URL v4Resource =
        BitmapParserTest.class.getClassLoader().getResource("32_bit_transparent_v4.bmp");
    Assertions.assertNotNull(v4Resource);
    v4Parser = new BitmapParser(Paths.get(v4Resource.toURI()));

    v4Header =
        (BitmapV4Header)
            DIBHeader.createDIBHeader(Arrays.copyOfRange(v4Parser.getRawData(), 0, 122));

    URL v4ResourceWithPaletteAndAlpha =
        BitmapParserTest.class.getClassLoader().getResource("bmp_v4_palette_alpha.bmp");
    Assertions.assertNotNull(v4ResourceWithPaletteAndAlpha);
    v4ParserWithPaletteAndAlpha =
        new BitmapParser(Paths.get(v4ResourceWithPaletteAndAlpha.toURI()));

    // Parser and header for bitmap with BITMAPV5HEADER
    URL v5Resource = BitmapParserTest.class.getClassLoader().getResource("32bit_v5.bmp");
    Assertions.assertNotNull(v5Resource);
    v5Parser = new BitmapParser(Paths.get(v5Resource.toURI()));

    v5Header =
        (BitmapV5Header)
            DIBHeader.createDIBHeader(Arrays.copyOfRange(v5Parser.getRawData(), 0, 138));

    // Parser for bitmap with a bad bit count
    URL badBitCounterResource =
        BitmapParserTest.class.getClassLoader().getResource("badbitcount.bmp");
    Assertions.assertNotNull(badBitCounterResource);
    badBitCountParser = new BitmapParser(Paths.get(badBitCounterResource.toURI()));
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
  void GivenDataWithWrongHeaderSize_ThrowsIllegalArgException() {
    byte[] bytes = {
      0x42, 0x4D, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
      0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
    };
    Assertions.assertThrows(IllegalArgumentException.class, () -> new BitmapParser(bytes));
  }

  @Test
  void GivenBitmapWithBadBitCount_GetPixels_ThrowsUnsupportedOperationException() {
    Executable executable = () -> badBitCountParser.getPixels();
    Assertions.assertThrows(UnsupportedOperationException.class, executable);
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
  void GivenCoreBitmap_PixelArraySize_EqualsDataArrayMinusOffset() {
    int[][] pixels = coreParser.getPixels();
    int totalElements = pixels[0].length * pixels.length;
    int expectedPixelSize = (coreParser.getRawData().length - coreParser.getOffset()) / 3;
    Assertions.assertEquals(expectedPixelSize, totalElements);
  }

  @Test
  void GivenCoreBitmapImage_GetColourPlanes_ReturnsCorrectValue() {
    Assertions.assertEquals(1, coreHeader.getColourPlanes());
  }

  @Test
  void GivenCoreBitmapImageWithColourPalette_HasColourPalette_ReturnsTrue() {
    Assertions.assertTrue(core1BitWithColourPaletteParser.hasColourPalette());
  }

  @Test
  void GivenCoreBitmap_GetPixels_ReturnsAllPixels() {
    int[][] pixels = coreParser.getPixels();
    int totalElements = pixels[0].length * pixels.length;
    Assertions.assertEquals(coreParser.getWidth() * coreParser.getHeight(), totalElements);
  }

  @Test
  void Given1bppCoreBitmapImageWithColourPalette_GetBitsPerPixel_Returns1() {
    Assertions.assertEquals(1, core1BitWithColourPaletteParser.getBitsPerPixel());
  }

  @Test
  void Given1bppCoreBitmapImageWithColourPalette_GetPixels_ReturnsAllPixels() {
    int[][] pixels = core1BitWithColourPaletteParser.getPixels();
    int totalElements = pixels[0].length * pixels.length;
    Assertions.assertEquals(
        core1BitWithColourPaletteParser.getWidth() * core1BitWithColourPaletteParser.getHeight(),
        totalElements);
  }

  @Test
  void Given4bppCoreBitmapImageWithColourPalette_GetBitsPerPixel_Returns4() {
    Assertions.assertEquals(4, core4BitWithColourPaletteParser.getBitsPerPixel());
  }

  @Test
  void Given4bppCoreBitmapImageWithColourPalette_GetPixels_ReturnsAllPixels() {
    int[][] pixels = core4BitWithColourPaletteParser.getPixels();
    int totalElements = pixels[0].length * pixels.length;
    Assertions.assertEquals(
        core4BitWithColourPaletteParser.getWidth() * core4BitWithColourPaletteParser.getHeight(),
        totalElements);
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
  void GivenCommonBitmapImage_HeaderSizeShort_ThrowsIllegalArgException() {
    byte[] bytes = Arrays.copyOfRange(commonParser.getRawData(), 0, 53);
    Assertions.assertThrows(IllegalArgumentException.class, () -> new BitmapParser(bytes));
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
  void GivenCommonBitmapImageWithColourPalette_HasColourPalette_ReturnsTrue() {
    Assertions.assertTrue(commonParserWithColourPalette.hasColourPalette());
  }

  @Test
  void GivenCommonBitmapImageWithColourPalette_HasAlphaChannel_ReturnsTrue() {
    Assertions.assertTrue(commonParserWithColourPalette.hasAlphaChannel());
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

  @Test
  void GivenCommonBitmapImage_GetColourPlanes_ReturnsCorrectValue() {
    Assertions.assertEquals(1, infoHeader.getColourPlanes());
  }

  @Test
  void GivenCommonBitmapImageWithSize0InHeader_CalculatesSizeFromDimensionsAndBpp()
      throws URISyntaxException, IOException {
    URL resource =
        BitmapParserTest.class.getClassLoader().getResource("common_bmp_no_size_in_header.bmp");
    Assertions.assertNotNull(resource);
    BitmapParser parser = new BitmapParser(Paths.get(resource.toURI()));

    Assertions.assertEquals(49_152, parser.getImageDataSize());
  }

  @Test
  void GivenCommonWithColourPaletteBitmap_GetPixels_ReturnsAllPixels() {
    int[][] pixels = commonParserWithColourPalette.getPixels();
    int totalElements = pixels[0].length * pixels.length;
    Assertions.assertEquals(
        commonParserWithColourPalette.getWidth() * commonParserWithColourPalette.getHeight(),
        totalElements);
  }

  @Test
  void GivenCommonWithColourPaletteBitmap_PixelArraySize_EqualsDataArrayMinusOffset() {
    int[][] pixels = commonParserWithColourPalette.getPixels();
    int totalElements = pixels[0].length * pixels.length;
    int expectedPixelSize =
        (commonParserWithColourPalette.getRawData().length
            - commonParserWithColourPalette.getOffset());
    Assertions.assertEquals(expectedPixelSize, totalElements);
  }

  @Test
  void GivenCommonBitmap_GetPixels_ReturnsAllPixels() {
    int[][] pixels = commonParser.getPixels();
    int totalElements = pixels[0].length * pixels.length;
    Assertions.assertEquals(commonParser.getWidth() * commonParser.getHeight(), totalElements);
  }

  @Test
  void GivenCommonBitmap_PixelArraySize_EqualsDataArrayMinusOffset() {
    int[][] pixels = commonParser.getPixels();
    int totalElements = pixels[0].length * pixels.length;
    int expectedPixelSize = (commonParser.getRawData().length - commonParser.getOffset()) / 3;
    Assertions.assertEquals(expectedPixelSize, totalElements);
  }

  @Test
  void Given8bppCommonBitmapImageWithColourPalette_GetBitsPerPixel_Returns8() {
    Assertions.assertEquals(8, common8BitWithColourPaletteParser.getBitsPerPixel());
  }

  @Test
  void Given16bppCommonBitmapImageWithoutColourPalette_GetPixels_ReturnsAllPixels() {
    int[][] pixels = common16BitParser.getPixels();
    int totalElements = pixels[0].length * pixels.length;
    Assertions.assertEquals(
        common16BitParser.getWidth() * common16BitParser.getHeight(), totalElements);
  }

  @Test
  void Given16bppCommonBitmapImageWithoutColourPalette_GetBitsPerPixel_Returns16() {
    Assertions.assertEquals(16, common16BitParser.getBitsPerPixel());
  }

  @Test
  void Given8bppCommonBitmapImageWithColourPalette_GetPixels_ReturnsAllPixels() {
    int[][] pixels = common8BitWithColourPaletteParser.getPixels();
    int totalElements = pixels[0].length * pixels.length;
    Assertions.assertEquals(
        common8BitWithColourPaletteParser.getWidth()
            * common8BitWithColourPaletteParser.getHeight(),
        totalElements);
  }

  @Test
  void Given32BppCommonBitmap_GetBitsPerPixel_Returns32() {
    Assertions.assertEquals(32, common32BitParser.getBitsPerPixel());
  }

  @Test
  void Given32BppCommonBitmap_GetPixels_ReturnsAllPixels() {
    int[][] pixels = common32BitParser.getPixels();
    int totalElements = pixels[0].length * pixels.length;
    Assertions.assertEquals(
        common32BitParser.getWidth() * common32BitParser.getHeight(), totalElements);
  }
  // Bitmap with BITMAPV2INFOHEADER tests
  @Test
  void GivenV2Bitmap_GetPixels_ReturnsAllPixels() {
    int[][] pixels = v2Parser.getPixels();
    int totalElements = pixels[0].length * pixels.length;
    Assertions.assertEquals(v2Parser.getWidth() * v2Parser.getHeight(), totalElements);
  }

  @Test
  void GivenV2Bitmap_PixelArraySize_EqualsDataArrayMinusOffset() {
    int[][] pixels = v2Parser.getPixels();
    int totalElements = pixels[0].length * pixels.length;
    int expectedPixelSize = (v2Parser.getRawData().length - v2Parser.getOffset()) / 3;
    Assertions.assertEquals(expectedPixelSize, totalElements);
  }

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
    Assertions.assertEquals(16_711_680, v2InfoHeader.getRedMask());
  }

  @Test
  void GivenV2Bitmap_GetGreenMask_ReturnsCorrectValue() {
    Assertions.assertEquals(65_280, v2InfoHeader.getGreenMask());
  }

  @Test
  void GivenV2Bitmap_GetBlueMask_ReturnsCorrectValue() {
    Assertions.assertEquals(255, v2InfoHeader.getBlueMask());
  }

  @Test
  void GivenV2Bitmap_GetAlphaMask_ThrowsUnsupportedOperationException() {
    Assertions.assertThrows(UnsupportedOperationException.class, () -> v2Parser.getAlphaMask());
  }

  @Test
  void GivenV2BitmapImage_GetProfileSize_ThrowsUnsupportedOperationException() {
    Assertions.assertThrows(UnsupportedOperationException.class, () -> v2Parser.getProfileSize());
  }

  @Test
  void GivenV2BitmapImage_GetColourPlanes_ReturnsCorrectValue() {
    Assertions.assertEquals(1, v2InfoHeader.getColourPlanes());
  }

  @Test
  void GivenV2BitmapImage_HeaderSizeShort_ThrowsIllegalArgException() {
    byte[] bytes = Arrays.copyOfRange(v2Parser.getRawData(), 0, 55);
    Assertions.assertThrows(IllegalArgumentException.class, () -> new BitmapParser(bytes));
  }

  @Test
  void GivenCompressedV2BitmapImage_WhenGetPixel_DoesNotThrow()
      throws URISyntaxException, IOException {
    URL paletteResource =
        BitmapParserTest.class.getClassLoader().getResource("v2_info_header_bi_bitfields.bmp");
    Assertions.assertNotNull(paletteResource);
    BitmapParser bitmapParser = new BitmapParser(Paths.get(paletteResource.toURI()));
    Assertions.assertDoesNotThrow(bitmapParser::getPixels);
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
    Assertions.assertEquals(16_711_680, v3InfoHeader.getRedMask());
  }

  @Test
  void GivenV3Bitmap_GetGreenMask_ReturnsCorrectValue() {
    Assertions.assertEquals(65_280, v3InfoHeader.getGreenMask());
  }

  @Test
  void GivenV3Bitmap_GetBlueMask_ReturnsCorrectValue() {
    Assertions.assertEquals(255, v3InfoHeader.getBlueMask());
  }

  @Test
  void GivenV3Bitmap_GetAlphaMask_ReturnsCorrectValue() {
    Assertions.assertEquals(2_169_624_778L, v3Parser.getAlphaMask());
  }

  @Test
  void GivenV3BitmapImage_GetProfileSize_ThrowsUnsupportedOperationException() {
    Assertions.assertThrows(UnsupportedOperationException.class, () -> v3Parser.getProfileSize());
  }

  @Test
  void GivenV3BitmapImage_GetColourPlanes_ReturnsCorrectValue() {
    Assertions.assertEquals(1, v3InfoHeader.getColourPlanes());
  }

  @Test
  void GivenV3Bitmap_GetPixels_ReturnsAllPixels() {
    int[][] pixels = v3Parser.getPixels();
    int totalElements = pixels[0].length * pixels.length;
    Assertions.assertEquals(v3Parser.getWidth() * v3Parser.getHeight(), totalElements);
  }

  @Test
  void GivenCompressedV3BitmapImage_WhenGetPixel_DoesNotThrow()
      throws URISyntaxException, IOException {
    URL paletteResource =
        BitmapParserTest.class.getClassLoader().getResource("v3_info_header_bi_bitfields.bmp");
    Assertions.assertNotNull(paletteResource);
    BitmapParser bitmapParser = new BitmapParser(Paths.get(paletteResource.toURI()));
    Assertions.assertDoesNotThrow(bitmapParser::getPixels);
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

  @Test
  void GivenV4BitmapImage_GetColourPlanes_ReturnsCorrectValue() {
    Assertions.assertEquals(1, v4Header.getColourPlanes());
  }

  @Test
  void GivenV4BitmapImageWithPaletteAndAlpha_HasAlphaChannel_ReturnsTrue() {
    Assertions.assertTrue(v4ParserWithPaletteAndAlpha.hasAlphaChannel());
  }

  @Test
  void GivenV4Bitmap_GetPixels_ReturnsAllPixels() {
    int[][] pixels = v4Parser.getPixels();
    int totalElements = pixels[0].length * pixels.length;
    Assertions.assertEquals(v4Parser.getWidth() * v4Parser.getHeight(), totalElements);
  }

  @Test
  void GivenV4Bitmap_PixelArraySize_EqualsDataArrayMinusOffset() {
    int[][] pixels = v4Parser.getPixels();
    int totalElements = pixels[0].length * pixels.length;
    int expectedPixelSize = (v4Parser.getRawData().length - v4Parser.getOffset()) / 4;
    Assertions.assertEquals(expectedPixelSize, totalElements);
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
  void GivenV5BitmapImage_GetColourPlanes_ReturnsCorrectValue() {
    Assertions.assertEquals(1, v5Header.getColourPlanes());
  }

  @Test
  void GivenV5Bitmap_HeaderShort_ThrowsIllegalArgumentException() {
    byte[] bytes = Arrays.copyOfRange(v5Parser.getRawData(), 0, 137);
    Assertions.assertThrows(IllegalArgumentException.class, () -> DIBHeader.createDIBHeader(bytes));
  }

  // Compression tests
  @ParameterizedTest
  @ValueSource(bytes = {1, 2, 4, 5, 6})
  void GivenUnsupportedCompression_GetPixels_ThrowsUnsupportedOperationException(byte i) {
    byte[] bytes = Arrays.copyOfRange(commonParser.getRawData(), 0, 55);
    bytes[30] = i;
    BitmapParser parser = new BitmapParser(bytes);
    Assertions.assertThrows(UnsupportedOperationException.class, parser::getPixels);
  }

  // Invalid pixel offset
  @Test
  void GivenBitmapImageWithOffsetGreaterThanDataLength_GetPixels_ThrowsIllegalArgumentException() {
    byte[] bytes =
        new byte[] {
          0x42,
          0x4D,
          0x00,
          0x00,
          0x00,
          0x00,
          0x00,
          0x00,
          0x00,
          0x00,
          54,
          0x00,
          0x00,
          0x00,
          40,
          0x00,
          0x00,
          0x00,
          0x01,
          0x00,
          0x00,
          0x00,
          1,
          0x00,
          0x00,
          0x00,
          0x01,
          0x00,
          32,
          0x00,
          3,
          0x00,
          0x00,
          0x00,
          0x00,
          0x00,
          0x00,
          0x00,
          0x00,
          0x00,
          0x00,
          0x00,
          0x00,
          0x00,
          0x00,
          0x00,
          0x00,
          0x00,
          0x00,
          0x00,
          0x00,
          0x00,
          0x00,
          0x00,
          (byte) 0xFF,
          (byte) 0x00,
          (byte) 0x00
        };
    BitmapParser parser = new BitmapParser(bytes);
    Assertions.assertThrows(IllegalArgumentException.class, parser::getPixels);
  }

  @Test
  void Given16BppCompressedBitmap_WhenGetPixels_ThenReturnCorrectPixels()
      throws URISyntaxException, IOException {
    URL paletteResource =
        BitmapParserTest.class.getClassLoader().getResource("16bit_555_bitfield.bmp");
    Assertions.assertNotNull(paletteResource);
    BitmapParser parser = new BitmapParser(Paths.get(paletteResource.toURI()));
    int[][] pixels = parser.getPixels();
    int totalElements = pixels[0].length * pixels.length;
    Assertions.assertEquals(parser.getWidth() * parser.getHeight(), totalElements);
  }

  // Negative height
  @Test
  void GivenBitmapWithNegativeHeight_WhenGetPixels_ThenDoesNotThrow() {
    byte[] bytes = Arrays.copyOfRange(commonParser.getRawData(), 0, 55);
    bytes[22] = (byte) 0x38;
    bytes[23] = (byte) 0xFF;
    bytes[24] = (byte) 0xFF;
    bytes[25] = (byte) 0xFF;
    BitmapParser parser = new BitmapParser(bytes);
    Assertions.assertThrows(IllegalArgumentException.class, parser::getPixels);
  }
}
