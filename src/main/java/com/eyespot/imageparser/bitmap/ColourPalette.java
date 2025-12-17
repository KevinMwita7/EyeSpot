package com.eyespot.imageparser.bitmap;

/**
 * Represents the colour palette (if present) in a BMP file.
 *
 * <p>Only present when bit-depth is ≤ 8. Supports RGBTRIPLE (3-byte) and RGBQUAD (4-byte) palette
 * entries.
 *
 * <p>This class parses and provides access to indexed colour data stored in BMP files. Each palette
 * entry represents an ARGB colour value.
 *
 * @author Kevin Babu
 * @see <a href="https://en.wikipedia.org/wiki/BMP_file_format#Colour_table">BMP Colour Table</a>
 */
final class ColourPalette {
  private final int[] colours;
  private final boolean hasAlphaChannel;

  /**
   * Parses the colour palette from the BMP byte array.
   *
   * @param data the image byte array
   * @param dibHeader the parsed DIB header
   * @param paletteStartFileOffset file offset where the palette begins
   * @throws IllegalArgumentException if palette data is truncated or out of bounds
   */
  ColourPalette(byte[] data, DIBHeader dibHeader, int paletteStartFileOffset) {
    int numEntries;
    // If the number of colours in the colour palette is 0 or colours used > important colours,
    // default to 2^n where n == bits per pixel
    if (dibHeader.getNColours() == 0
        || dibHeader.getNColours() > dibHeader.getImportantColours()) {
      numEntries = 1 << dibHeader.getBitsPerPixel(); // Max colours for bit-depth
    } else {
      numEntries = dibHeader.getNColours();
    }
    this.colours = new int[numEntries];
    int bytesPerPaletteEntry;

    // Determine bytes per palette entry based on DIB header type
    if (InfoHeaderType.BITMAPCOREHEADER.equals(dibHeader.getType())) {
      // RGBTRIPLE (no 4th byte for alpha)
      bytesPerPaletteEntry = 3;
    } else {
      // RGBQUAD (has the 4th byte)
      bytesPerPaletteEntry = 4;
    }

    boolean foundNonZeroReservedByte = false;
    boolean explicitAlphaFlag = false;

    // Check for explicit alpha mask in BITMAPV4/V5 headers
    if (dibHeader instanceof BitmapV4Header) {
      explicitAlphaFlag = ((BitmapV4Header) dibHeader).getAlphaMask() != 0;
    }

    for (int i = 0; i < numEntries; i++) {
      int entryOffset = paletteStartFileOffset + (i * bytesPerPaletteEntry);

      // Basic bounds check for reading from data array
      if (entryOffset + bytesPerPaletteEntry > data.length) {
        throw new IllegalArgumentException(
            String.format(
                "Palette data truncated or out of bounds at entry %d (offset: %d)",
                i, entryOffset));
      }

      // BMP stores BGR order. Alpha is often 0xFF (opaque) or ignored. But will try and handle it
      // Java lacks unsigned byte hence the need for bitwise operations
      int blue = data[entryOffset] & BitmapConstants.BYTE_MASK;
      int green = data[entryOffset + 1] & BitmapConstants.BYTE_MASK;
      int red = data[entryOffset + 2] & BitmapConstants.BYTE_MASK;

      // Default to opaque
      int alpha = BitmapConstants.OPAQUE_ALPHA;

      // Check alpha in bytes, if not explicitly stated in header
      if (bytesPerPaletteEntry == 4) {
        int parsedAlpha = data[entryOffset + 3] & BitmapConstants.BYTE_MASK;
        alpha = (parsedAlpha == 0) ? BitmapConstants.OPAQUE_ALPHA : parsedAlpha;
      }

      foundNonZeroReservedByte = alpha != BitmapConstants.OPAQUE_ALPHA;

      // Store as ARGB (AARRGGBB)
      this.colours[i] = (alpha << 24) | (red << 16) | (green << 8) | blue;
    }

    this.hasAlphaChannel = explicitAlphaFlag || foundNonZeroReservedByte;
  }

  /**
   * Returns the number of colour entries in the palette.
   *
   * @return the number of palette entries
   */
  int getNumberOfEntries() {
    return colours.length;
  }

  /**
   * Retrieves a colour entry from the palette.
   *
   * @param index the palette index
   * @return the ARGB colour as {@code 0xAARRGGBB}
   * @throws IndexOutOfBoundsException if index is out of bounds
   */
  int getColour(int index) {
    if (index < 0 || index >= colours.length) {
      throw new IndexOutOfBoundsException(
          String.format("Palette index %d out of bounds [0, %d]", index, colours.length - 1));
    }
    return colours[index];
  }

  /**
   * Indicates if any alpha channel data is present in the palette.
   *
   * @return true if alpha channel is likely present
   */
  boolean hasAlphaChannel() {
    return hasAlphaChannel;
  }
}
