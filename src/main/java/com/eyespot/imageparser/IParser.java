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

  /**
   * Reads and returns the pixel data as a 2D array of ARGB integers. Each integer represents a
   * pixel in AARRGGBB format.
   *
   * @return A 2D array (height x width) of pixel data.
   */
  int[][] getPixels();

  /** @return a defensive copy of the raw image data */
  byte[] getRawData();

  /** @return image width in pixels */
  int getWidth();

  /** @return image height in pixels */
  int getHeight();

  /** @return number of bits per pixel */
  int getBitsPerPixel();

  /** @return compression method (e.g., BI_RGB, BI_RLE8) */
  int getCompression();

  /** @return size of bitmap pixel data in bytes */
  int getImageDataSize();

  /** @return number of colours used in the palette */
  int getNColours();

  /** @return number of important colours specified in the header */
  int getImportantColours();

  /** @return horizontal resolution in pixels per meter */
  int getXResolution();

  /** @return vertical resolution in pixels per meter */
  int getYResolution();

  /**
   * Returns the alpha mask for V3/V4/V5 headers.
   *
   * @return the alpha bitmask
   */
  long getAlphaMask();

  /** @return ICC profile size for BITMAPV5HEADER */
  int getProfileSize();

  /** @return size of the DIB header in bytes */
  int getHeaderSize();

  /** @return true if a colour palette is present (typically for ≤ 8 bpp images) */
  boolean hasColourPalette();

  /** @return true if alpha channel is present in the colour palette and false otherwise */
  boolean hasAlphaChannel();
}
