package com.eyespot.imageparser.bitmap;

/**
 * Enum representing the different types of DIB (Device Independent Bitmap) headers found in BMP
 * (Bitmap) image files.
 *
 * <p>Each constant corresponds to a specific BMP header structure and is associated with a known
 * fixed size in bytes.
 *
 * <p>This enum can be used for identifying and dispatching parsing logic for a specific header type
 * based on the header size.
 *
 * @author Kevin Babu
 * @see BitmapConstants
 */
public enum InfoHeaderType {
  /** The original Windows 2.x {@code BITMAPCOREHEADER} (12 bytes). */
  BITMAPCOREHEADER(BitmapConstants.BITMAPCOREHEADER_SIZE),

  /** The Windows 3.x {@code BITMAPINFOHEADER} (40 bytes). */
  BITMAPINFOHEADER(BitmapConstants.BITMAPINFOHEADER_SIZE),

  /** The unofficial {@code BITMAPV2INFOHEADER} with RGB bit masks (52 bytes). */
  BITMAPV2INFOHEADER(BitmapConstants.BITMAPV2INFOHEADER_SIZE),

  /** The {@code BITMAPV3INFOHEADER} that adds an alpha channel mask (56 bytes). */
  BITMAPV3INFOHEADER(BitmapConstants.BITMAPV3INFOHEADER_SIZE),

  /** The {@code BITMAPV4HEADER} including color space and gamma data (108 bytes). */
  BITMAPV4HEADER(BitmapConstants.BITMAPV4HEADER_SIZE),

  /** The {@code BITMAPV5HEADER} with ICC profile support (124 bytes). */
  BITMAPV5HEADER(BitmapConstants.BITMAPV5HEADER_SIZE);

  private final int size;

  /**
   * Associates each enum constant with its corresponding header size.
   *
   * @param size the size of the header in bytes
   */
  InfoHeaderType(int size) {
    this.size = size;
  }

  /**
   * Gets the size in bytes of the corresponding DIB header type.
   *
   * @return the header size in bytes
   */
  public int getSize() {
    return size;
  }

  /**
   * Attempts to match a header size with a known {@code InfoHeaderType}.
   *
   * @param size the size of the DIB header in bytes
   * @return the matching {@code InfoHeaderType}, or {@code null} if unknown
   */
  public static InfoHeaderType fromSize(int size) {
    for (InfoHeaderType type : values()) {
      if (type.getSize() == size) {
        return type;
      }
    }
    return null;
  }
}
