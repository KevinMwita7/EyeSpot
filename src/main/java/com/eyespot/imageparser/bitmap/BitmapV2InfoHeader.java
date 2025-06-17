package com.eyespot.imageparser.bitmap;

import static com.eyespot.imageparser.util.ImageUtils.readInt;

/**
 * Represents the {@code BITMAPV2INFOHEADER} structure used in some extended BMP formats.
 *
 * <p>This header builds upon {@link BitmapInfoHeader} by adding support for explicit color channel
 * bit masks (red, green, and blue). While not officially standardized in all BMP documentation,
 * this format is often encountered in practice.
 *
 * <p>The {@code BITMAPV2INFOHEADER} is typically 52 bytes in size: 40 bytes from {@code
 * BITMAPINFOHEADER} plus 12 additional bytes for the masks.
 *
 * @author Kevin Babu
 * @see BitmapConstants#BITMAPV2INFOHEADER_SIZE
 * @see InfoHeaderType#BITMAPV2INFOHEADER
 */
public class BitmapV2InfoHeader extends BitmapInfoHeader {
  /** The red channel bit mask. */
  protected final int redMask;

  /** The green channel bit mask. */
  protected final int greenMask;

  /** The blue channel bit mask. */
  protected final int blueMask;

  /**
   * Constructs a {@code BitmapV2InfoHeader} by parsing the image data and extracting RGB color
   * masks in addition to the base {@code BITMAPINFOHEADER} fields.
   *
   * @param data the byte array containing the BMP data
   * @param dibHeaderFileOffset the byte offset to the start of the DIB header
   */
  protected BitmapV2InfoHeader(byte[] data, int dibHeaderFileOffset) {
    super(data, dibHeaderFileOffset);

    int currentOffset = dibHeaderFileOffset + BitmapConstants.BITMAPINFOHEADER_SIZE;

    // Parse RGB bit masks
    // Note: Couldn't find BITMAPV2INFOHEADER docs but generally it's often considered to only add
    // RGB masks.
    this.redMask = readInt(data, currentOffset);
    this.greenMask = readInt(data, currentOffset + 4);
    this.blueMask = readInt(data, currentOffset + 8);
  }

  /** @return the type of this header, {@link InfoHeaderType#BITMAPV2INFOHEADER} */
  @Override
  public InfoHeaderType getType() {
    return InfoHeaderType.BITMAPV2INFOHEADER;
  }

  /** @return the bit mask used for the red color channel */
  public int getRedMask() {
    return redMask;
  }

  /** @return the bit mask used for the green color channel */
  public int getGreenMask() {
    return greenMask;
  }

  /** @return the bit mask used for the blue color channel */
  public int getBlueMask() {
    return blueMask;
  }
}
