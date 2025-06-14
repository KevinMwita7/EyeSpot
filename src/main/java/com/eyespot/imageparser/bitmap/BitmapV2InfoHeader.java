package com.eyespot.imageparser.bitmap;

import static com.eyespot.imageparser.util.ImageUtils.readInt;

/**
 * Represents the BITMAPV2INFOHEADER (52 bytes) DIB header. Adds color masks to BITMAPINFOHEADER.
 * (Undocumented by Microsoft)
 */
public class BitmapV2InfoHeader extends BitmapInfoHeader {
  protected final int redMask;
  protected final int greenMask;
  protected final int blueMask;

  protected BitmapV2InfoHeader(byte[] data, int dibHeaderFileOffset) {
    // Parse common BITMAPINFOHEADER fields
    super(data, dibHeaderFileOffset);

    // Parse V2-specific fields
    int currentOffset = dibHeaderFileOffset + BitmapConstants.BITMAPINFOHEADER_SIZE;

    // Basic bounds check
    if (data.length < currentOffset + 12) { // 3 masks * 4 bytes each
      throw new IllegalArgumentException("Byte array too short for BITMAPV2INFOHEADER masks.");
    }

    // Note: Couldn't find BITMAPV2INFOHEADER docs but generally it's often considered to only add
    // RGB masks.
    this.redMask = readInt(data, currentOffset);
    this.greenMask = readInt(data, currentOffset + 4);
    this.blueMask = readInt(data, currentOffset + 8);
  }

  @Override
  public InfoHeaderType getType() {
    return InfoHeaderType.BITMAPV2INFOHEADER;
  }

  public int getRedMask() {
    return redMask;
  }

  public int getGreenMask() {
    return greenMask;
  }

  public int getBlueMask() {
    return blueMask;
  }
}
