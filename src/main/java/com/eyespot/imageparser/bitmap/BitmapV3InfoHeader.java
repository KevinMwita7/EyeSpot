package com.eyespot.imageparser.bitmap;

import static com.eyespot.imageparser.util.ImageUtils.readInt;

/**
 * Represents the {@code BITMAPV3INFOHEADER} structure, an extended variant of the BMP header format
 * that includes an explicit alpha channel bit mask.
 *
 * <p>This class builds upon {@link BitmapV2InfoHeader} by adding support for an {@code alphaMask}
 * field, making it suitable for 32-bit BMP images with transparency.
 *
 * <p>The {@code BITMAPV3INFOHEADER} is typically 56 bytes in size: 52 bytes from V2 plus 4 bytes
 * for alpha.
 *
 * @author Kevin Babu
 * @see BitmapConstants#BITMAPV3INFOHEADER_SIZE
 * @see InfoHeaderType#BITMAPV3INFOHEADER
 */
public class BitmapV3InfoHeader extends BitmapV2InfoHeader {
  /** The alpha channel bit mask. */
  protected final long alphaMask;

  /**
   * Constructs a {@code BitmapV3InfoHeader} by parsing the given BMP byte array.
   *
   * @param data the byte array containing the BMP data
   * @param dibHeaderFileOffset the offset where the DIB header starts in the file
   */
  protected BitmapV3InfoHeader(byte[] data, int dibHeaderFileOffset) {
    // Parse V2-specific fields
    super(data, dibHeaderFileOffset);
    int currentOffset = dibHeaderFileOffset + BitmapConstants.BITMAPV2INFOHEADER_SIZE;

    // Parse the alpha mask, ensuring it's treated as unsigned DWORD
    this.alphaMask = 0xFFFFFFFFL & readInt(data, currentOffset);
  }

  /** @return the type of this header, {@link InfoHeaderType#BITMAPV3INFOHEADER} */
  @Override
  public InfoHeaderType getType() {
    return InfoHeaderType.BITMAPV3INFOHEADER;
  }

  /** @return the bit mask used for the alpha (transparency) channel */
  public long getAlphaMask() {
    return alphaMask;
  }
}
