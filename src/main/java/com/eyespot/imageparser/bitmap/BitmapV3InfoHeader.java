package com.eyespot.imageparser.bitmap;

import static com.eyespot.imageparser.util.ImageUtils.readInt;

/**
 * Represents the BITMAPV3INFOHEADER (56 bytes) DIB header. Adds an alpha mask to
 * BITMAPV2INFOHEADER. (Undocumented by Microsoft)
 */
public class BitmapV3InfoHeader extends BitmapV2InfoHeader {
  protected final long alphaMask;

  protected BitmapV3InfoHeader(byte[] data, int dibHeaderFileOffset) {
    // Parse V2-specific fields
    super(data, dibHeaderFileOffset);

    // Parse V3-specific field
    int currentOffset = dibHeaderFileOffset + BitmapConstants.BITMAPV2INFOHEADER_SIZE;

    this.alphaMask = 0xFFFFFFFFL & readInt(data, currentOffset);
  }

  @Override
  public InfoHeaderType getType() {
    return InfoHeaderType.BITMAPV3INFOHEADER;
  }

  public long getAlphaMask() {
    return alphaMask;
  }
}
