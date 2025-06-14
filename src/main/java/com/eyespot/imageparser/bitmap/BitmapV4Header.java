package com.eyespot.imageparser.bitmap;

import com.eyespot.imageparser.util.ImageUtils;

/**
 * The bitmap info header type. See <a
 * href="https://web.archive.org/web/20141224112131/http://netghost.narod.ru/gff/graphics/summary/micbmp.htm">Bitmap
 * Information Header</a>">
 */
public class BitmapV4Header extends BitmapInfoHeader {
  private final int redMask;
  private final int greenMask;
  private final int blueMask;
  private final int alphaMask;
  private final int csType;
  private final CIEXYZTriple endpoints; // You'd need a CIEXYZTriple class
  private final int gammaRed;
  private final int gammaGreen;
  private final int gammaBlue;

  public BitmapV4Header(byte[] data, int headerOffset) {
    super(data, headerOffset); // Parse common fields first

    // Parse V4-specific fields
    int currentOffset =
        headerOffset + BitmapConstants.BITMAPINFOHEADER_SIZE; // Start where BITMAPINFOHEADER ends

    this.redMask = ImageUtils.readInt(data, currentOffset);
    this.greenMask = ImageUtils.readInt(data, currentOffset + 4);
    this.blueMask = ImageUtils.readInt(data, currentOffset + 8);
    this.alphaMask = ImageUtils.readInt(data, currentOffset + 12);
    this.csType = ImageUtils.readInt(data, currentOffset + 16); // bV4CSType

    // Parse CIEXYZTriple (36 bytes: 3 CIEXYZ structs, each 3 ints)
    this.endpoints =
        new CIEXYZTriple(
            ImageUtils.readInt(data, currentOffset + 20), // fxCIEXYZRedX
            ImageUtils.readInt(data, currentOffset + 24), // fxCIEXYZRedY
            ImageUtils.readInt(data, currentOffset + 28), // fxCIEXYZRedZ
            ImageUtils.readInt(data, currentOffset + 32), // fxCIEXYZGreenX
            ImageUtils.readInt(data, currentOffset + 36), // fxCIEXYZGreenY
            ImageUtils.readInt(data, currentOffset + 40), // fxCIEXYZGreenZ
            ImageUtils.readInt(data, currentOffset + 44), // fxCIEXYZBlueX
            ImageUtils.readInt(data, currentOffset + 48), // fxCIEXYZBlueY
            ImageUtils.readInt(data, currentOffset + 52) // fxCIEXYZBlueZ
            );

    this.gammaRed = ImageUtils.readInt(data, currentOffset + 56); // bV4GammaRed
    this.gammaGreen = ImageUtils.readInt(data, currentOffset + 60); // bV4GammaGreen
    this.gammaBlue = ImageUtils.readInt(data, currentOffset + 64); // bV4GammaBlue
  }

  @Override
  public InfoHeaderType getType() {
    return InfoHeaderType.BITMAPV4HEADER;
  }

  // Getters for V4-specific fields
  public int getRedMask() {
    return redMask;
  }

  public int getGreenMask() {
    return greenMask;
  }

  public int getBlueMask() {
    return blueMask;
  }

  public int getAlphaMask() {
    return alphaMask;
  }

  public int getCsType() {
    return csType;
  }

  public CIEXYZTriple getEndpoints() {
    return endpoints;
  }

  public int getGammaRed() {
    return gammaRed;
  }

  public int getGammaGreen() {
    return gammaGreen;
  }

  public int getGammaBlue() {
    return gammaBlue;
  }

  // --- Inner class for CIEXYZTriple ---
  public static class CIEXYZTriple {
    private final int redX, redY, redZ;
    private final int greenX, greenY, greenZ;
    private final int blueX, blueY, blueZ;

    public CIEXYZTriple(int rx, int ry, int rz, int gx, int gy, int gz, int bx, int by, int bz) {
      this.redX = rx;
      this.redY = ry;
      this.redZ = rz;
      this.greenX = gx;
      this.greenY = gy;
      this.greenZ = gz;
      this.blueX = bx;
      this.blueY = by;
      this.blueZ = bz;
    }

    // Getters for individual CIEXYZ components
    public int getRedX() {
      return redX;
    }
    // ... and so on for all 9 components
  }
}
