package com.eyespot.imageparser.util;

import com.eyespot.imageparser.ImageType;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * Utility class for image-related operations
 *
 * @author Kevin Babu
 */
public class ImageUtils {

  /**
   * Detects the type of an image from its file path. This method reads all bytes from the specified
   * file and then delegates to the {@link #detectType(byte[])} method for type detection.
   *
   * @param path The {@link Path} to the image file.
   * @return The {@link ImageType} detected, or {@link ImageType#UNDETERMINED} if the type cannot be
   *     identified.
   * @throws IOException If an I/O error occurs while reading the file.
   */
  public static ImageType detectType(Path path) throws IOException {
    return detectType(Files.readAllBytes(path));
  }

  /**
   * Detects the type of an image from a given byte array. This method inspects the initial bytes
   * (magic numbers) of the image data to determine its format.
   *
   * <p>Currently, this method primarily checks for the following image types:
   *
   * <ul>
   *   <li>{@link ImageType#BITMAP} (BMP) - Identified by magic numbers 0x42 0x4D.
   * </ul>
   *
   * If the byte array is too short or the type cannot be determined from the available magic
   * numbers, {@link ImageType#UNDETERMINED} is returned.
   *
   * @param bytes The byte array containing the image data.
   * @return The {@link ImageType} detected, or {@link ImageType#UNDETERMINED} if the type cannot be
   *     identified.
   */
  public static ImageType detectType(byte[] bytes) {
    if (bytes == null || bytes.length < 2) {
      return ImageType.UNDETERMINED;
    }

    // Check for BMP magic number "BM" (0x42 0x4D)
    if (bytes[0] == 0x42 && bytes[1] == 0x4D) {
      return ImageType.BITMAP;
    }

    return ImageType.UNDETERMINED;
  }

  /**
   * Helper method to read 4 bytes from the byte array with little-endian order.
   *
   * @param data The byte array to read from.
   * @param offset The starting offset in the byte array.
   * @return The int value.
   * @throws IndexOutOfBoundsException if the offset is out of bounds.
   */
  public static int readInt(byte[] data, int offset) {
    return ByteBuffer.wrap(Arrays.copyOfRange(data, offset, offset + 4))
        .order(ByteOrder.LITTLE_ENDIAN)
        .getInt();
  }

  /**
   * Helper method to read 2 bytes from the byte array with little-endian order.
   *
   * @param data The byte array to read from.
   * @param offset The starting offset in the byte array.
   * @return The short value.
   * @throws IndexOutOfBoundsException if the offset is out of bounds.
   */
  public static short readShort(byte[] data, int offset) {
    return ByteBuffer.wrap(Arrays.copyOfRange(data, offset, offset + 2))
        .order(ByteOrder.LITTLE_ENDIAN)
        .getShort();
  }
}
