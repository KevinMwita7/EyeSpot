package com.eyespot.util;

import com.eyespot.imageparser.ImageType;
import com.eyespot.imageparser.util.ImageUtils;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

class ImageUtilsTest {

  static void GivenInvalidFilePath_ThrowsIOException() {
    boolean ioExceptionThrown = false;

    try {
      ImageUtils.detectType(Paths.get(""));
    } catch (IOException e) {
      ioExceptionThrown = true;
    }

    assert ioExceptionThrown : "IOException thrown on invalid file path";
  }

  static void GivenFilePath_ReturnsFileType_Bitmap() throws URISyntaxException, IOException {
    URL resource = ImageUtilsTest.class.getClassLoader().getResource("sample_bmp.bmp");
    assert resource != null;

    ImageType type = ImageUtils.detectType(Paths.get(resource.toURI()));
    assert ImageType.BITMAP.equals(type);
  }

  static void GivenFileBytes_ReturnsFileType_Bitmap() throws URISyntaxException, IOException {
    URL resource = ImageUtilsTest.class.getClassLoader().getResource("sample_bmp.bmp");
    assert resource != null;

    byte[] bytes = Files.readAllBytes(Paths.get(resource.toURI()));
    ImageType type = ImageUtils.detectType(bytes);
    assert ImageType.BITMAP.equals(type);
  }

  public static void main(String[] args) {
    try {
      GivenInvalidFilePath_ThrowsIOException();
      GivenFilePath_ReturnsFileType_Bitmap();
      GivenFileBytes_ReturnsFileType_Bitmap();
    } catch (Exception ex) {
      System.out.println(ex);
    }
  }
}
