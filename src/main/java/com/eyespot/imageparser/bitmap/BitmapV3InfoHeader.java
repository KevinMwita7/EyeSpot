package com.eyespot.imageparser.bitmap;

import static com.eyespot.imageparser.util.ImageUtils.readInt;

/**
 * Represents the BITMAPV3INFOHEADER (56 bytes) DIB header. Adds an alpha mask to
 * BITMAPV2INFOHEADER. (Undocumented by Microsoft)
 */
public class BitmapV3InfoHeader extends BitmapV2InfoHeader {
  protected final int alphaMask;

  protected BitmapV3InfoHeader(byte[] data, int dibHeaderFileOffset) {
    // Parse V2-specific fields
    super(data, dibHeaderFileOffset);

    // Parse V3-specific field
    int currentOffset = dibHeaderFileOffset + BitmapConstants.BITMAPV2INFOHEADER_SIZE;

    // Basic bounds check
    if (data.length < currentOffset + 4) {
      throw new IllegalArgumentException("Byte array too short for BITMAPV3INFOHEADER alpha mask.");
    }

    this.alphaMask = readInt(data, currentOffset);
  }

  @Override
  public InfoHeaderType getType() {
    return InfoHeaderType.BITMAPV3INFOHEADER;
  }

  public int getAlphaMask() {
    return alphaMask;
  }
}
