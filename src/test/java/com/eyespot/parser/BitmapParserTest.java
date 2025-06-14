package com.eyespot.parser;

import com.eyespot.imageparser.bitmap.BitmapParser;
import com.eyespot.imageparser.bitmap.InfoHeaderType;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class BitmapParserTest {

  private static BitmapParser parser;

  @BeforeAll
  static void setUp() throws URISyntaxException, IOException {
    URL resource = BitmapParserTest.class.getClassLoader().getResource("sample_bmp.bmp");
    Assertions.assertNotNull(resource);
    parser = new BitmapParser(Paths.get(resource.toURI()));
  }

  @Test
  void GivenBitmapImage_ReturnsFileData() throws URISyntaxException, IOException {
    URL resource = BitmapParserTest.class.getClassLoader().getResource("sample_bmp.bmp");
    Assertions.assertNotNull(resource);

    byte[] bytes = Files.readAllBytes(Paths.get(resource.toURI()));

    Assertions.assertArrayEquals(parser.getRawData(), bytes);
  }

  @Test
  void GivenCommonBitmapImage_GetSize_ReturnsImageSize() {
    Assertions.assertEquals(1_500_056, parser.getSize());
  }

  @Test
  void GivenCommonBitmapImage_GetOffset_ReturnsOffset() {
    Assertions.assertEquals(54, parser.getOffset());
  }

  @Test
  void GivenCommonBitmapImage_GetWidth_ReturnsWidth() {
    Assertions.assertEquals(1000, parser.getWidth());
  }

  @Test
  void GivenCommonBitmapImage_GetHeight_ReturnsHeight() {
    Assertions.assertEquals(500, parser.getHeight());
  }

  @Test
  void GivenCommonBitmapImage_GetHeaderSize_Returns40() {
    Assertions.assertEquals(40, parser.getHeaderSize());
  }

  @Test
  void GivenCommonBitmapImage_GetDibHeaderType_ReturnsBitmapCoreHeader() {
    Assertions.assertEquals(InfoHeaderType.BITMAPINFOHEADER, parser.getDibHeaderType());
  }

  @Test
  void GivenCommonBitmapImage_GetBitsPerPixel_Returns24() {
    Assertions.assertEquals(24, parser.getBitsPerPixel());
  }

  @Test
  void GivenCommonBitmapImage_GetCompression_Returns0() {
    // 0 == uncompressed
    Assertions.assertEquals(0, parser.getCompression());
  }

  @Test
  void GivenCommonBitmapImage_GetXResolution_ReturnsXResolution() {
    Assertions.assertEquals(2834, parser.getXResolution());
  }

  @Test
  void GivenCommonBitmapImage_GetYResolution_ReturnsYResolution() {
    Assertions.assertEquals(2834, parser.getYResolution());
  }

  @Test
  void GivenCommonBitmapImage_GetImageDataSize_ReturnsImageDataSize() {
    Assertions.assertEquals(1_500_002, parser.getImageDataSize());
  }

  @Test
  void GivenCommonBitmapImage_GetNColours_ReturnsNColours() {
    Assertions.assertEquals(0, parser.getNColours());
  }

  @Test
  void GivenCommonBitmapImage_GetImportantColours_ReturnsImportantColours() {
    Assertions.assertEquals(0, parser.getImportantColours());
  }

  @Test
  void GivenCommonBitmapImageNoColourPalette_HasColourPalette_ReturnsFalse() {
    Assertions.assertFalse(parser.hasColourPalette());
  }
}
