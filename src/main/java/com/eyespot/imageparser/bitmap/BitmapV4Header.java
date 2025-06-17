package com.eyespot.imageparser.bitmap;

import com.eyespot.imageparser.util.ImageUtils;

/**
 * Represents the {@code BITMAPV4HEADER} structure, which extends {@code BITMAPINFOHEADER} to
 * include advanced color management features such as RGBA masks, color space type, CIE XYZ
 * endpoints, and gamma values.
 *
 * <p>This header is 108 bytes in size and is commonly used in Windows 95 and later BMP formats. It
 * supports high-fidelity color representation and optional transparency.
 *
 * @author Kevin Babu
 * @see BitmapConstants#BITMAPV4HEADER_SIZE
 * @see InfoHeaderType#BITMAPV4HEADER
 * @see CIEXYZTriple
 */
public class BitmapV4Header extends BitmapInfoHeader {

  private final int redMask;
  private final int greenMask;
  private final int blueMask;
  private final long alphaMask;
  private final int csType;
  private final CIEXYZTriple endpoints; // You'd need a CIEXYZTriple class
  private final int gammaRed;
  private final int gammaGreen;
  private final int gammaBlue;

  /**
   * Constructs a {@code BitmapV4Header} from the given BMP data.
   *
   * @param data the full byte array of the BMP image
   * @param headerOffset the offset where the DIB header starts
   */
  protected BitmapV4Header(byte[] data, int headerOffset) {
    super(data, headerOffset); // Parse common fields first

    int currentOffset = headerOffset + BitmapConstants.BITMAPINFOHEADER_SIZE;

    this.redMask = ImageUtils.readInt(data, currentOffset);
    this.greenMask = ImageUtils.readInt(data, currentOffset + 4);
    this.blueMask = ImageUtils.readInt(data, currentOffset + 8);
    this.alphaMask = 0xFFFFFFFFL & ImageUtils.readInt(data, currentOffset + 12);
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

  /** @return the header type {@code BITMAPV4HEADER} */
  @Override
  public InfoHeaderType getType() {
    return InfoHeaderType.BITMAPV4HEADER;
  }

  // Getters for V4-specific fields
  /** @return the red channel bit mask */
  public int getRedMask() {
    return redMask;
  }

  /** @return the green channel bit mask */
  public int getGreenMask() {
    return greenMask;
  }

  /** @return the blue channel bit mask */
  public int getBlueMask() {
    return blueMask;
  }

  /** @return the alpha channel bit mask */
  public long getAlphaMask() {
    return alphaMask;
  }

  /** @return the color space type (e.g., LCS_sRGB, LCS_WINDOWS_COLOR_SPACE) */
  public int getCsType() {
    return csType;
  }

  /** @return the CIE XYZ color space endpoints */
  public CIEXYZTriple getEndpoints() {
    return endpoints;
  }

  /** @return the gamma value for the red channel */
  public int getGammaRed() {
    return gammaRed;
  }

  /** @return the gamma value for the green channel */
  public int getGammaGreen() {
    return gammaGreen;
  }

  /** @return the gamma value for the blue channel */
  public int getGammaBlue() {
    return gammaBlue;
  }

  /**
   * Represents a {@code CIEXYZTRIPLE} structure, which defines the CIE XYZ color space endpoints
   * for red, green, and blue primaries.
   *
   * <p>Each primary color is described using three fixed-point values (X, Y, Z), stored as integers
   * in 16.16 fixed-point format.
   */
  public static class CIEXYZTriple {
    private final int redX;
    private final int redY;
    private final int redZ;
    private final int greenX;
    private final int greenY;
    private final int greenZ;
    private final int blueX;
    private final int blueY;
    private final int blueZ;

    /**
     * Constructs a new {@code CIEXYZTriple} with the given endpoint values.
     *
     * @param rx red X
     * @param ry red Y
     * @param rz red Z
     * @param gx green X
     * @param gy green Y
     * @param gz green Z
     * @param bx blue X
     * @param by blue Y
     * @param bz blue Z
     */
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
    /** @return red primary X component */
    public int getRedX() {
      return redX;
    }

    /** @return red primary Y component */
    public int getRedY() {
      return redY;
    }

    /** @return red primary Z component */
    public int getRedZ() {
      return redZ;
    }

    /** @return green primary X component */
    public int getGreenX() {
      return greenX;
    }

    /** @return green primary Y component */
    public int getGreenY() {
      return greenY;
    }

    /** @return green primary Z component */
    public int getGreenZ() {
      return greenZ;
    }

    /** @return blue primary X component */
    public int getBlueX() {
      return blueX;
    }

    /** @return blue primary Y component */
    public int getBlueY() {
      return blueY;
    }

    /** @return blue primary Z component */
    public int getBlueZ() {
      return blueZ;
    }
  }
}
