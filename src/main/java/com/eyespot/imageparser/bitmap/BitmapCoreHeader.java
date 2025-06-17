package com.eyespot.imageparser.bitmap;

/**
 * Represents the legacy {@code BITMAPCOREHEADER} structure in BMP files.
 *
 * <p>This header format was used in early BMP versions and provides basic image metadata such as
 * width, height, color planes, and bits per pixel. It lacks advanced fields like compression type
 * or resolution.
 *
 * <p>This class is a concrete implementation of the abstract {@link DIBHeader} base class and is
 * instantiated when the DIB header size is {@code 12} bytes.
 *
 * @see BitmapConstants#BITMAPCOREHEADER_SIZE
 * @see DIBHeader
 * @see InfoHeaderType
 * @author Kevin Babu
 */
class BitmapCoreHeader extends DIBHeader {
  /**
   * Constructs a {@code BitmapCoreHeader} by parsing the given BMP file data.
   *
   * @param data the complete BMP file as a byte array
   * @param dibHeaderFileOffset the byte offset to the start of the DIB header
   */
  protected BitmapCoreHeader(byte[] data, int dibHeaderFileOffset) {
    super(data, dibHeaderFileOffset);
  }

  /**
   * Returns the {@link InfoHeaderType} associated with this header.
   *
   * @return {@code InfoHeaderType.BITMAPCOREHEADER}
   */
  @Override
  public InfoHeaderType getType() {
    return InfoHeaderType.BITMAPCOREHEADER;
  }
}
