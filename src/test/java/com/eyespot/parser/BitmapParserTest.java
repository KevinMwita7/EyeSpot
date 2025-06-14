package com.eyespot.parser;

import com.eyespot.imageparser.bitmap.BitmapParser;
import com.eyespot.imageparser.bitmap.InfoHeaderType;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

class BitmapParserTest {

  private static BitmapParser parser;

  static void GivenBitmapImage_ReturnsFileData() throws URISyntaxException, IOException {
    URL resource = BitmapParserTest.class.getClassLoader().getResource("sample_bmp.bmp");
    assert resource != null;

    byte[] bytes = Files.readAllBytes(Paths.get(resource.toURI()));

    assert Arrays.equals(parser.getRawData(), bytes);
  }

  static void GivenCommonBitmapImage_GetSize_ReturnsImageSize() {
    assert parser.getSize() == 1_500_056;
  }

  static void GivenCommonBitmapImage_GetOffset_ReturnsOffset() {
    assert parser.getOffset() == 54;
  }

  static void GivenCommonBitmapImage_GetWidth_ReturnsWidth() {
    assert parser.getWidth() == 1000;
  }

  static void GivenCommonBitmapImage_GetHeight_ReturnsHeight() {
    assert parser.getHeight() == 500;
  }

  static void GivenCommonBitmapImage_GetHeaderSize_Returns40() {
    assert parser.getHeaderSize() == 40;
  }

  static void GivenCommonBitmapImage_GetDibHeaderType_ReturnsBitmapCoreHeader() {
    assert InfoHeaderType.BITMAPINFOHEADER.equals(parser.getDibHeaderType());
  }

  static void GivenCommonBitmapImage_GetBitsPerPixel_Returns24() {
    assert parser.getBitsPerPixel() == 24;
  }

  static void GivenCommonBitmapImage_GetCompression_Returns0() {
    // 0 == uncompressed
    assert parser.getCompression() == 0;
  }

  static void GivenCommonBitmapImage_GetXResolution_ReturnsXResolution() {
    assert parser.getXResolution() == 2834;
  }

  static void GivenCommonBitmapImage_GetYResolution_ReturnsYResolution() {
    assert parser.getYResolution() == 2834;
  }

  static void GivenCommonBitmapImage_GetImageDataSize_ReturnsImageDataSize() {
    assert parser.getImageDataSize() == 1_500_002;
  }

  static void GivenCommonBitmapImage_GetNColours_ReturnsNColours() {
    assert parser.getNColours() == 0;
  }

  static void GivenCommonBitmapImage_GetImportantColours_ReturnsImportantColours() {
    assert parser.getImportantColours() == 0;
  }

  static void GivenCommonBitmapImageNoColourPalette_HasColourPalette_ReturnsFalse() {
    assert !parser.hasColourPalette();
  }

  public static void main(String[] args) {
    try {
      URL resource = BitmapParserTest.class.getClassLoader().getResource("sample_bmp.bmp");
      assert resource != null;
      parser = new BitmapParser(Paths.get(resource.toURI()));

      GivenBitmapImage_ReturnsFileData();
      GivenCommonBitmapImage_GetSize_ReturnsImageSize();
      GivenCommonBitmapImage_GetOffset_ReturnsOffset();
      GivenCommonBitmapImage_GetHeaderSize_Returns40();
      GivenCommonBitmapImage_GetWidth_ReturnsWidth();
      GivenCommonBitmapImage_GetHeight_ReturnsHeight();
      GivenCommonBitmapImage_GetDibHeaderType_ReturnsBitmapCoreHeader();
      GivenCommonBitmapImage_GetBitsPerPixel_Returns24();
      GivenCommonBitmapImage_GetCompression_Returns0();
      GivenCommonBitmapImage_GetYResolution_ReturnsYResolution();
      GivenCommonBitmapImage_GetXResolution_ReturnsXResolution();
      GivenCommonBitmapImage_GetImageDataSize_ReturnsImageDataSize();
      GivenCommonBitmapImage_GetNColours_ReturnsNColours();
      GivenCommonBitmapImage_GetImportantColours_ReturnsImportantColours();
      GivenCommonBitmapImageNoColourPalette_HasColourPalette_ReturnsFalse();
    } catch (Exception ex) {
      System.out.println(ex);
    }
  }
}
