package com.eyespot.imageparser.bitmap;

/**
 * Defines constants used for parsing and interpreting various types of BMP (Bitmap) file headers,
 * including the BITMAPFILEHEADER, BITMAPCOREHEADER, BITMAPINFOHEADER, and extended DIB header
 * formats.
 *
 * <p>These constants represent byte sizes and offsets within BMP file structures, and are intended
 * for use when reading raw BMP binary data.
 *
 * <p>This class cannot be instantiated.
 *
 * @author Kevin Babu
 */
public final class BitmapConstants {

  /** Private constructor to prevent instantiation. */
  private BitmapConstants() {}

  // --- DIB Header Sizes ---
  /** Size of the BITMAPCOREHEADER in bytes. */
  public static final int BITMAPCOREHEADER_SIZE = 12;

  /** Size of the BITMAPINFOHEADER in bytes. */
  public static final int BITMAPINFOHEADER_SIZE = 40;

  /** Size of the BITMAPV2INFOHEADER in bytes. */
  public static final int BITMAPV2INFOHEADER_SIZE = 52;

  /** Size of the BITMAPV3INFOHEADER in bytes. */
  public static final int BITMAPV3INFOHEADER_SIZE = 56;

  /** Size of the BITMAPV4HEADER in bytes. */
  public static final int BITMAPV4HEADER_SIZE = 108;

  /** Size of the BITMAPV5HEADER in bytes. */
  public static final int BITMAPV5HEADER_SIZE = 124;

  // --- BITMAPFILEHEADER ---
  /** Size of the BITMAPFILEHEADER in bytes. */
  public static final int FILE_HEADER_SIZE = 14;

  /** Offset to the "BM" file type signature. */
  public static final int BF_TYPE_OFFSET = 0;

  /** Offset to the file size field. */
  public static final int BF_SIZE_OFFSET = 2;

  /** Offset to the first reserved field. */
  public static final int BF_RESERVED1_OFFSET = 6;

  /** Offset to the second reserved field. */
  public static final int BF_RESERVED2_OFFSET = 8;

  /** Offset to the pixel data (bitmap bits). */
  public static final int BF_OFFBITS_OFFSET = 10;

  // These offsets are relative to the START of the DIB header (which is usually byte 14)
  // --- DIB Header (BITMAPCOREHEADER and variants) ---
  /** Offset to the image width (in pixels) in BITMAPCOREHEADER. */
  public static final int BI_CORE_WIDTH_OFFSET = 4;

  /** Offset to the image height (in pixels) in BITMAPCOREHEADER. */
  public static final int BI_CORE_HEIGHT_OFFSET = 6;

  /** Offset to the number of color planes in BITMAPCOREHEADER. */
  public static final int BI_CORE_PLANES_OFFSET = 8;

  /** Offset to the bits-per-pixel field in BITMAPCOREHEADER. */
  public static final int BI_CORE_BITCOUNT_OFFSET = 10;

  // --- DIB Header (BITMAPINFOHEADER and variants) ---
  /** Offset to the size of the DIB header. */
  public static final int BI_SIZE_OFFSET = 0;

  /** Offset to the image width (in pixels). */
  public static final int BI_WIDTH_OFFSET = 4;

  /** Offset to the image height (in pixels). */
  public static final int BI_HEIGHT_OFFSET = 8;

  /** Offset to the number of color planes. */
  public static final int BI_PLANES_OFFSET = 12;

  /** Offset to the bits-per-pixel field. */
  public static final int BI_BITCOUNT_OFFSET = 14;

  /** Offset to the compression method field. */
  public static final int BI_COMPRESSION_OFFSET = 16;

  /** Offset to the size of the bitmap data. */
  public static final int BI_SIZEIMAGE_OFFSET = 20;

  /** Offset to the horizontal resolution (pixels per meter). */
  public static final int BI_X_PELS_PER_METER_OFFSET = 24;

  /** Offset to the vertical resolution (pixels per meter). */
  public static final int BI_Y_PELS_PER_METER_OFFSET = 28;

  /** Offset to the number of colors used in the bitmap. */
  public static final int BI_CLR_USED_OFFSET = 32;

  /** Offset to the number of important colors. */
  public static final int BI_CLR_IMPORTANT_OFFSET = 36;

  // --- BITMAPV4HEADER specific offsets --- (relative to DIB header start)
  /** Offset to the red channel mask. */
  public static final int BV4_RED_MASK_OFFSET = 40;

  /** Offset to the green channel mask. */
  public static final int BV4_GREEN_MASK_OFFSET = 44;

  /** Offset to the blue channel mask. */
  public static final int BV4_BLUE_MASK_OFFSET = 48;

  /** Offset to the alpha channel mask. */
  public static final int BV4_ALPHA_MASK_OFFSET = 52;

  /** Offset to the color space type. */
  public static final int BV4_CS_TYPE_OFFSET = 56;

  /** Offset to the endpoints structure. */
  public static final int BV4_ENDPOINTS_OFFSET = 60;

  /** Offset to the red gamma correction value. */
  public static final int BV4_GAMMA_RED_OFFSET = 96;

  /** Offset to the green gamma correction value. */
  public static final int BV4_GAMMA_GREEN_OFFSET = 100;

  /** Offset to the blue gamma correction value. */
  public static final int BV4_GAMMA_BLUE_OFFSET = 104;

  // --- BITMAPV5HEADER specific offsets (relative to DIB header start)
  /** Offset to the rendering intent field. */
  public static final int BV5_INTENT_OFFSET = 108;

  /** Offset to the profile data location. */
  public static final int BV5_PROFILE_DATA_OFFSET = 112;

  /** Offset to the profile data size. */
  public static final int BV5_PROFILE_SIZE_OFFSET = 116;

  /** Offset to the reserved field (usually zero). */
  public static final int BV5_RESERVED_OFFSET = 120;

  // --- Compression Types ---
  /** No compression (BI_RGB). */
  public static final int BI_RGB = 0;

  /** RLE 8-bit/pixel compression (BI_RLE8). */
  public static final int BI_RLE8 = 1;

  /** RLE 4-bit/pixel compression (BI_RLE4). */
  public static final int BI_RLE4 = 2;

  /** Bitfield compression (BI_BITFIELDS). */
  public static final int BI_BITFIELDS = 3;

  /** JPEG compression (BI_JPEG). Not supported for direct pixel reading. */
  public static final int BI_JPEG = 4;

  /** PNG compression (BI_PNG). Not supported for direct pixel reading. */
  public static final int BI_PNG = 5;

  /** Alpha bitfield compression (BI_ALPHABITFIELDS). */
  public static final int BI_ALPHABITFIELDS = 6;

  // --- Bit Manipulation Constants ---
  /** Mask for extracting a single byte (0xFF). */
  public static final int BYTE_MASK = 0xFF;

  /** Opaque alpha value (fully opaque). */
  public static final int OPAQUE_ALPHA = 0xFF;

  /** Maximum value for 8-bit colour component. */
  public static final int MAX_8BIT_VALUE = 255;

  /** Maximum value for 5-bit colour component (used in RGB555). */
  public static final int RGB5_MAX = 31;

  /** Red channel mask for RGB555 format (5 bits). */
  public static final int RGB5_RED_MASK = 0x7C00;

  /** Green channel mask for RGB555 format (5 bits). */
  public static final int RGB5_GREEN_MASK = 0x03E0;

  /** Blue channel mask for RGB555 format (5 bits). */
  public static final int RGB5_BLUE_MASK = 0x001F;

  /** Default 16-bit RGB565 red mask. */
  public static final int RGB565_RED_MASK = 0xF800;

  /** Default 16-bit RGB565 green mask. */
  public static final int RGB565_GREEN_MASK = 0x07E0;

  /** Default 16-bit RGB565 blue mask. */
  public static final int RGB565_BLUE_MASK = 0x001F;

  /** Default 32-bit red mask (ARGB8888). */
  public static final long RGB8_RED_MASK = 0x00FF0000L;

  /** Default 32-bit green mask (ARGB8888). */
  public static final long RGB8_GREEN_MASK = 0x0000FF00L;

  /** Default 32-bit blue mask (ARGB8888). */
  public static final long RGB8_BLUE_MASK = 0x000000FFL;

  /** Default 32-bit alpha mask (ARGB8888). */
  public static final long RGB8_ALPHA_MASK = 0xFF000000L;

  // --- Bitfield Mask Data Block Sizes ---
  /** Size of bitfield mask data for BI_BITFIELDS with 3 masks (12 bytes). */
  public static final int BITFIELD_MASKS_SIZE_V3 = 12;

  /** Size of bitfield mask data for BI_ALPHABITFIELDS with 4 masks (16 bytes). */
  public static final int BITFIELD_MASKS_SIZE_V4 = 16;
}
