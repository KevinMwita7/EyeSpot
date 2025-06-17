package com.eyespot.imageparser.bitmap;

/**
 * Represents the {@code BITMAPINFOHEADER} structure in BMP files.
 *
 * <p>This is the most commonly used DIB header format, introduced with Windows 3.x. It extends the
 * older {@code BITMAPCOREHEADER} by adding support for compression, color management, resolution,
 * and palette information.
 *
 * <p>This class is a concrete implementation of the abstract {@link DIBHeader} base class and is
 * instantiated when the DIB header size is {@code 40} bytes.
 *
 * @see BitmapConstants#BITMAPINFOHEADER_SIZE
 * @see DIBHeader
 * @see InfoHeaderType
 * @author Kevin Babu
 */
class BitmapInfoHeader extends DIBHeader {
  /**
   * Constructs a {@code BitmapInfoHeader} by parsing the given BMP file data.
   *
   * @param data the complete BMP file as a byte array
   * @param headerOffset the byte offset to the start of the DIB header
   */
  protected BitmapInfoHeader(byte[] data, int headerOffset) {
    super(data, headerOffset);
  }

  /**
   * Returns the {@link InfoHeaderType} associated with this header.
   *
   * @return {@code InfoHeaderType.BITMAPINFOHEADER}
   */
  @Override
  public InfoHeaderType getType() {
    return InfoHeaderType.BITMAPINFOHEADER;
  }
}
