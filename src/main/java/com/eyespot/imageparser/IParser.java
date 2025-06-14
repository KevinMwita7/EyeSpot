package com.eyespot.imageparser;

/**
 * The {@code IParser} interface defines methods for retrieving metadata from parsed image data.
 * Implementations of this interface are responsible for analyzing image files or byte streams and
 * exposing information such as the image type, size, and data offset.
 *
 * @author Kevin Babu
 * @version 1.0
 */
public interface IParser {
  /**
   * Returns the detected {@link ImageType} of the image.
   *
   * @return the image type, or {@code ImageType.UNDETERMINED} if the type could not be determined
   */
  ImageType getType();

  /**
   * Returns the total size of the image file in bytes.
   *
   * @return the total size of the image file in bytes
   */
  int getSize();

  /**
   * Returns the offset to the actual image pixel data within the file.
   *
   * @return the offset in bytes to the start of the image data
   */
  int getOffset();
}
