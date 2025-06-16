package com.eyespot.util;

import com.eyespot.imageparser.ImageType;
import com.eyespot.imageparser.util.ImageUtils;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ImageUtilsTest {

  @Test
  void GivenInvalidFilePath_ThrowsIOException() {
    Assertions.assertThrows(IOException.class, () -> ImageUtils.detectType(Paths.get("")));
  }

  @Test
  void GivenFilePath_ReturnsFileType_Bitmap() throws URISyntaxException, IOException {
    URL resource = ImageUtilsTest.class.getClassLoader().getResource("sample_bmp.bmp");
    Assertions.assertNotNull(resource);

    Assertions.assertEquals(ImageType.BITMAP, ImageUtils.detectType(Paths.get(resource.toURI())));
  }

  @Test
  void GivenFileBytes_ReturnsFileType_Bitmap() throws URISyntaxException, IOException {
    URL resource = ImageUtilsTest.class.getClassLoader().getResource("sample_bmp.bmp");
    Assertions.assertNotNull(resource);

    byte[] bytes = Files.readAllBytes(Paths.get(resource.toURI()));
    Assertions.assertEquals(ImageType.BITMAP, ImageUtils.detectType(bytes));
  }

  @Test
  void GivenBytes_ReadInt_Reads4BytesInLittleEndian() {
    byte[] bytes = {(byte) 0xE8, 0x03, 0x00, 0x00};
    Assertions.assertEquals(1000, ImageUtils.readInt(bytes, 0));
  }

  @Test
  void GivenBytes_ReadShort_Reads2BytesInLittleEndian() {
    byte[] bytes = {(byte) 0x0A, 0x00};
    Assertions.assertEquals(10, ImageUtils.readShort(bytes, 0));
  }

  @Test
  void GivenBytes_IsEmpty_ReturnsUndetermined() {
    byte[] bytes = null;
    Assertions.assertEquals(ImageType.UNDETERMINED, ImageUtils.detectType(bytes));
  }

  @Test
  void GivenBytes_IsShorterThan2Bytes_ReturnsUndetermined() {
    byte[] bytes = {0x00};
    Assertions.assertEquals(ImageType.UNDETERMINED, ImageUtils.detectType(bytes));
  }

  @Test
  void GivenBytes_MissingBitmapMagicNumber_ReturnsUndetermined() {
    byte[] bytes = {0x42, 0x34};
    Assertions.assertEquals(ImageType.UNDETERMINED, ImageUtils.detectType(bytes));
  }
}
