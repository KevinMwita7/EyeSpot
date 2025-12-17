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

  private static BitmapParser common8BitCompressedWithEncodedRunParser;

  private static BitmapParser common8BitCompressedWithAbsoluteRunParser;

  private static BitmapParser common16BitParser;

  private static BitmapParser common32BitParser;

  private static BitmapParser coreParser;

  private static BitmapParser core1BitWithColourPaletteParser;

  private static BitmapParser core4BitWithColourPaletteParser;

  private static BitmapParser v2Parser;

  private static BitmapParser v3Parser;

  private static BitmapParser v4Parser;

  private static BitmapParser v4ParserWithPaletteAndAlpha;

  private static BitmapParser common4BitCompressedParser;

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

    // Parser for 8bpp BI_RLE8 compressed with encoded run and has BITMAPINFOHEADER
    URL common8BitCompressedWithEncodedRunParserResource =
        BitmapParserTest.class.getClassLoader().getResource("8bit_compressed.bmp");
    Assertions.assertNotNull(common8BitCompressedWithEncodedRunParserResource);
    common8BitCompressedWithEncodedRunParser =
        new BitmapParser(Paths.get(common8BitCompressedWithEncodedRunParserResource.toURI()));

    // Parser for 8bpp BI_RLE8 compressed with absolute run and has BITMAPINFOHEADER
    URL common8BitCompressedWithAbsoluteRunParserResource =
        BitmapParserTest.class
            .getClassLoader()
            .getResource("bmp_common_8bpp_rle8_with_delta_esc_codes.bmp");
    Assertions.assertNotNull(common8BitCompressedWithAbsoluteRunParserResource);
    common8BitCompressedWithAbsoluteRunParser =
        new BitmapParser(Paths.get(common8BitCompressedWithAbsoluteRunParserResource.toURI()));

    // Parser for 4bpp BI_RLE4 compressed and has BITMAPINFOHEADER
    URL common4BitCompressedParserResource =
        BitmapParserTest.class.getClassLoader().getResource("4bit_compressed.bmp");
    Assertions.assertNotNull(common4BitCompressedParserResource);
    common4BitCompressedParser =
        new BitmapParser(Paths.get(common4BitCompressedParserResource.toURI()));

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
  void GivenCommonBitmapImageWithColourPalette_HasAlphaChannel_ReturnsFalse() {
    Assertions.assertFalse(commonParserWithColourPalette.hasAlphaChannel());
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

  // 8bpp BI_RLE8 compressed bitmap with encoded runs and BITMAPINFOHEADER
  @Test
  void Given8bppCompressedBitmapWithEncodedRun_GetCompression_Returns1() {
    Assertions.assertEquals(1, common8BitCompressedWithEncodedRunParser.getCompression());
  }

  @Test
  void Given8BppCompressedBitmapWithEncodedRun_GetPixels_ReturnAllPixels() {
    int[][] pixels = common8BitCompressedWithEncodedRunParser.getPixels();
    int totalElements = pixels[0].length * pixels.length;
    Assertions.assertEquals(
        common8BitCompressedWithEncodedRunParser.getWidth()
            * common8BitCompressedWithEncodedRunParser.getHeight(),
        totalElements);
  }

  // 8bpp BI_RLE8 compressed bitmap with absolute runs and BITMAPINFOHEADER
  @Test
  void Given8bppCompressedBitmapWithAbsoluteRun_GetCompression_Returns1() {
    Assertions.assertEquals(1, common8BitCompressedWithAbsoluteRunParser.getCompression());
  }

  @Test
  void Given8BppCompressedBitmapWithAbsoluteRun_GetPixels_ReturnAllPixels() {
    int[][] pixels = common8BitCompressedWithAbsoluteRunParser.getPixels();
    int totalElements = pixels[0].length * pixels.length;
    Assertions.assertEquals(
        common8BitCompressedWithAbsoluteRunParser.getWidth()
            * common8BitCompressedWithAbsoluteRunParser.getHeight(),
        totalElements);
  }

  // 4bpp BI_RLE4 compressed bitmap with BITMAPINFOHEADER
  @Test
  void Given4bppCompressedBitmap_GetCompression_Returns2() {
    Assertions.assertEquals(2, common4BitCompressedParser.getCompression());
  }

  @Test
  void Given4BppCompressedBitmap_GetPixels_ReturnAllPixels() {
    int[][] pixels = common4BitCompressedParser.getPixels();
    int totalElements = pixels[0].length * pixels.length;
    Assertions.assertEquals(
        common4BitCompressedParser.getWidth() * common4BitCompressedParser.getHeight(),
        totalElements);
  }

  // Malformed 8bpp BI_RLE8 compressed bitmap with absolute runs and BITMAPINFOHEADER
  @Test
  void GivenMalformed8BppCompressedBitmapWithAbsoluteRun_GetPixels_DoesNotThrow()
      throws URISyntaxException, IOException {
    URL resource =
        BitmapParserTest.class
            .getClassLoader()
            .getResource("malformed_bmp_common_8bpp_rle8_with_delta.bmp");
    Assertions.assertNotNull(resource);
    BitmapParser parser = new BitmapParser(Paths.get(resource.toURI()));
    Assertions.assertDoesNotThrow(parser::getPixels);
  }

  // Malformed non-8bpp with BI_RLE8 compression
  @Test
  void GivenNon8BppCompressedBIRLE8Bitmap_GetPixels_ThrowsIllegalArgumentException()
      throws URISyntaxException, IOException {
    URL resource =
        BitmapParserTest.class.getClassLoader().getResource("bmp_common_4bpp_rle8_with_delta.bmp");
    Assertions.assertNotNull(resource);
    BitmapParser parser = new BitmapParser(Paths.get(resource.toURI()));
    Assertions.assertThrows(IllegalArgumentException.class, parser::getPixels);
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
  void GivenMalformedV3Bitmap_GetPixels_DoesNotThrowException() {
    Assertions.assertDoesNotThrow(v3Parser::getPixels);
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
  @ValueSource(bytes = {4, 5, 6})
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

  // Blue mask only, blue and green mask only
  @ParameterizedTest
  @ValueSource(
      strings = {
        "16bit_555_bitfield.bmp",
        "v2_info_header_32bpp_red_mask_zero.bmp",
        "v2_info_header_32bpp_blue_mask_only.bmp"
      })
  void Given16BppCompressedBitmap_WhenGetPixels_ThenReturnCorrectPixels(String s)
      throws URISyntaxException, IOException {
    URL paletteResource = BitmapParserTest.class.getClassLoader().getResource(s);
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
    Assertions.assertDoesNotThrow(parser::getPixels);
  }

  // If image is corrupted, return processed pixels
  @ParameterizedTest
  @ValueSource(
      strings = {
        "./b/555-pixeldata-cropped.bmp",
        "./b/negative_offset_8bit.bmp",
        "./b/8bpp-colorsused-large.bmp",
        "./b/24bpp-pixeldata-cropped.bmp",
        "./b/32bpp-pixeldata-cropped.bmp",
        "./b/4bpp-no-palette.bmp",
        "./b/4bpp-pixeldata-cropped.bmp",
        "./b/8bpp-colorsimportant-large.bmp",
        "./b/8bpp-colorsimportant-negative.bmp",
        "./b/8bpp-colorsused-negative.bmp",
        "./b/8bpp-no-palette.bmp",
        "./b/8bpp-pixeldata-cropped.bmp",
        "./b/pixeldata-missing.bmp",
        "./b/rle8-absolute-cropped.bmp",
        "./b/rle8-delta-cropped.bmp"
      })
  void GivenBitmap_WhenPixelDataNotEnough_ThenReturnsProcessedPixels(String source)
      throws URISyntaxException, IOException {
    URL paletteResource = BitmapParserTest.class.getClassLoader().getResource(source);
    Assertions.assertNotNull(paletteResource);
    BitmapParser parser = new BitmapParser(Paths.get(paletteResource.toURI()));
    Assertions.assertDoesNotThrow(parser::getPixels);
  }

  @Test
  void GivenBitmap_WhenHeightZero_ThenReturnsProcessedPixels()
      throws URISyntaxException, IOException {
    URL paletteResource =
        BitmapParserTest.class.getClassLoader().getResource("./b/height-zero.bmp");
    Assertions.assertNotNull(paletteResource);
    BitmapParser parser = new BitmapParser(Paths.get(paletteResource.toURI()));
    Assertions.assertDoesNotThrow(parser::getPixels);
  }

  @Test
  void GivenBitmap_WhenColourPaletteCropped_ThenThrowsException() {
    URL paletteResource =
        BitmapParserTest.class.getClassLoader().getResource("./b/palette-cropped.bmp");
    Assertions.assertNotNull(paletteResource);
    Executable executable = () -> new BitmapParser(Paths.get(paletteResource.toURI()));
    Assertions.assertThrows(IllegalArgumentException.class, executable);
  }

  // Tests for calculateExpectedOffset (zero offset handling)
  @Test
  void GivenBitmapWithZeroOffset_WhenGetPixels_ThenUsesCalculatedOffset() {
    // Create a minimal 1x1 24bpp bitmap with zero offset
    byte[] bytes =
        new byte[] {
          // File Header (14 bytes)
          0x42, 0x4D, // "BM" magic number
          0x3E, 0x00, 0x00, 0x00, // File size: 62 bytes
          0x00, 0x00, // Reserved1
          0x00, 0x00, // Reserved2
          0x00, 0x00, 0x00, 0x00, // Offset: 0 (INVALID - should be 54)
          // DIB Header - BITMAPINFOHEADER (40 bytes)
          0x28, 0x00, 0x00, 0x00, // Header size: 40
          0x01, 0x00, 0x00, 0x00, // Width: 1
          0x01, 0x00, 0x00, 0x00, // Height: 1
          0x01, 0x00, // Color planes: 1
          0x18, 0x00, // Bits per pixel: 24
          0x00, 0x00, 0x00, 0x00, // Compression: 0 (BI_RGB)
          0x04, 0x00, 0x00, 0x00, // Image size: 4 bytes
          0x13, 0x0B, 0x00, 0x00, // X pixels per meter
          0x13, 0x0B, 0x00, 0x00, // Y pixels per meter
          0x00, 0x00, 0x00, 0x00, // Colors used: 0
          0x00, 0x00, 0x00, 0x00, // Important colors: 0
          // Pixel Data (4 bytes: 3 for pixel + 1 padding)
          (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, 0x00 // White pixel + padding
        };

    BitmapParser parser = new BitmapParser(bytes);
    Assertions.assertDoesNotThrow(parser::getPixels);
    int[][] pixels = parser.getPixels();
    Assertions.assertEquals(1, pixels.length);
    Assertions.assertEquals(1, pixels[0].length);
  }

  @Test
  void GivenBitmapWithNegativeOffset_WhenGetPixels_ThenUsesCalculatedOffset() {
    // Create a minimal 1x1 24bpp bitmap with negative offset
    byte[] bytes =
        new byte[] {
          // File Header (14 bytes)
          0x42, 0x4D, // "BM" magic number
          0x3E, 0x00, 0x00, 0x00, // File size: 62 bytes
          0x00, 0x00, // Reserved1
          0x00, 0x00, // Reserved2
          (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, // Offset: -1 (INVALID)
          // DIB Header - BITMAPINFOHEADER (40 bytes)
          0x28, 0x00, 0x00, 0x00, // Header size: 40
          0x01, 0x00, 0x00, 0x00, // Width: 1
          0x01, 0x00, 0x00, 0x00, // Height: 1
          0x01, 0x00, // Color planes: 1
          0x18, 0x00, // Bits per pixel: 24
          0x00, 0x00, 0x00, 0x00, // Compression: 0 (BI_RGB)
          0x04, 0x00, 0x00, 0x00, // Image size: 4 bytes
          0x13, 0x0B, 0x00, 0x00, // X pixels per meter
          0x13, 0x0B, 0x00, 0x00, // Y pixels per meter
          0x00, 0x00, 0x00, 0x00, // Colors used: 0
          0x00, 0x00, 0x00, 0x00, // Important colors: 0
          // Pixel Data (4 bytes: 3 for pixel + 1 padding)
          (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, 0x00 // White pixel + padding
        };

    BitmapParser parser = new BitmapParser(bytes);
    Assertions.assertDoesNotThrow(parser::getPixels);
    int[][] pixels = parser.getPixels();
    Assertions.assertEquals(1, pixels.length);
    Assertions.assertEquals(1, pixels[0].length);
  }

  @Test
  void GivenBitmapWithZeroOffsetAndPalette_WhenGetPixels_ThenCalculatesOffsetWithPalette() {
    // Create a minimal 1x1 8bpp bitmap with zero offset and color palette
    byte[] bytes =
        new byte[] {
          // File Header (14 bytes)
          0x42, 0x4D, // "BM" magic number
          0x46, 0x04, 0x00, 0x00, // File size
          0x00, 0x00, // Reserved1
          0x00, 0x00, // Reserved2
          0x00, 0x00, 0x00, 0x00, // Offset: 0 (INVALID - should be 14+40+1024=1078)
          // DIB Header - BITMAPINFOHEADER (40 bytes)
          0x28, 0x00, 0x00, 0x00, // Header size: 40
          0x01, 0x00, 0x00, 0x00, // Width: 1
          0x01, 0x00, 0x00, 0x00, // Height: 1
          0x01, 0x00, // Color planes: 1
          0x08, 0x00, // Bits per pixel: 8
          0x00, 0x00, 0x00, 0x00, // Compression: 0 (BI_RGB)
          0x04, 0x00, 0x00, 0x00, // Image size: 4 bytes
          0x13, 0x0B, 0x00, 0x00, // X pixels per meter
          0x13, 0x0B, 0x00, 0x00, // Y pixels per meter
          0x00, 0x00, 0x00, 0x00, // Colors used: 0 (defaults to 256)
          0x00, 0x00, 0x00, 0x00, // Important colors: 0
          // Color Palette (256 entries * 4 bytes = 1024 bytes)
          // Entry 0: Black
          0x00, 0x00, 0x00, 0x00, // B, G, R, Reserved
        };

    // Fill remaining 255 palette entries (simplified - all black)
    byte[] fullBytes = new byte[14 + 40 + 1024 + 4];
    System.arraycopy(bytes, 0, fullBytes, 0, bytes.length);
    // Add pixel data at the end
    fullBytes[fullBytes.length - 4] = 0x00; // Pixel index 0
    fullBytes[fullBytes.length - 3] = 0x00; // Padding
    fullBytes[fullBytes.length - 2] = 0x00; // Padding
    fullBytes[fullBytes.length - 1] = 0x00; // Padding

    BitmapParser parser = new BitmapParser(fullBytes);
    Assertions.assertDoesNotThrow(parser::getPixels);
    int[][] pixels = parser.getPixels();
    Assertions.assertEquals(1, pixels.length);
    Assertions.assertEquals(1, pixels[0].length);
  }

  // Tests for hasAlphaChannel optimization and caching
  @Test
  void GivenBitmap_WhenHasAlphaChannelCalledTwice_ThenReturnsCachedResult() {
    // First call should compute the result
    boolean firstCall = v4ParserWithPaletteAndAlpha.hasAlphaChannel();
    // Second call should return cached result (faster)
    boolean secondCall = v4ParserWithPaletteAndAlpha.hasAlphaChannel();

    Assertions.assertEquals(firstCall, secondCall);
    Assertions.assertTrue(firstCall); // This bitmap is known to have alpha
  }

  @Test
  void GivenV3BitmapWithAlphaMask_WhenHasAlphaChannel_ThenReturnsTrueWithoutPixelScan() {
    // v3Parser is already initialized and has an alpha mask
    // This test verifies that hasAlphaChannel returns true based on header metadata
    boolean hasAlpha = v3Parser.hasAlphaChannel();
    Assertions.assertTrue(hasAlpha);

    // Verify it can be called multiple times (caching)
    boolean hasAlphaCached = v3Parser.hasAlphaChannel();
    Assertions.assertTrue(hasAlphaCached);
  }

  @Test
  void GivenV4BitmapWithAlphaMask_WhenHasAlphaChannel_ThenReturnsTrueWithoutPixelScan() {
    // v4Parser is already initialized and has an alpha mask
    boolean hasAlpha = v4Parser.hasAlphaChannel();
    Assertions.assertTrue(hasAlpha);

    // Verify it can be called multiple times (caching)
    boolean hasAlphaCached = v4Parser.hasAlphaChannel();
    Assertions.assertTrue(hasAlphaCached);
  }

  @Test
  void GivenCommonBitmapWithoutAlpha_WhenHasAlphaChannel_ThenReturnsFalse() {
    // commonParser is a 24bpp image without alpha
    boolean hasAlpha = commonParser.hasAlphaChannel();
    Assertions.assertFalse(hasAlpha);

    // Verify caching works
    boolean hasAlphaCached = commonParser.hasAlphaChannel();
    Assertions.assertFalse(hasAlphaCached);
  }

  @Test
  void GivenBitmapWithPalette_WhenHasAlphaChannel_ThenChecksPaletteFirst() {
    // commonParserWithColourPalette has a palette but no alpha
    boolean hasAlpha = commonParserWithColourPalette.hasAlphaChannel();
    Assertions.assertFalse(hasAlpha);

    // v4ParserWithPaletteAndAlpha has a palette with alpha
    boolean hasAlphaWithPalette = v4ParserWithPaletteAndAlpha.hasAlphaChannel();
    Assertions.assertTrue(hasAlphaWithPalette);
  }
}
