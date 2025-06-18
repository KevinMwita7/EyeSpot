package com.eyespot.imageparser.bitmap;

import static com.eyespot.imageparser.util.ImageUtils.readInt;
import static com.eyespot.imageparser.util.ImageUtils.readShort;

import com.eyespot.imageparser.IParser;
import com.eyespot.imageparser.ImageType;
import com.eyespot.imageparser.util.ImageUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;

/**
 * The {@code Parser} class implements the {@link IParser} interface and provides parsing
 * functionality for image files. It supports:
 *
 * <ul>
 *   <li>Detection of images based on the file's magic number
 * </ul>
 *
 * <p>The parser reads either a {@link Path} to a file or a byte array directly and attempts to
 * determine the image type. It also exposes methods to retrieve image metadata such as type, size,
 * and offset
 *
 * <p>This implementation is read-only and does not modify the image data.
 *
 * @author Kevin Babu
 * @see DIBHeader
 * @see BitmapConstants
 * @see InfoHeaderType
 * @see ImageType
 */
public class BitmapParser implements IParser {

  /** The image data in bytes. */
  private final byte[] data;

  /** The bitmap file header. */
  private final Header header;

  /** The bitmap DIB (Device Independent Bitmap) header. */
  private final DIBHeader dibHeader;

  /** The bitmap colour palette, if present. */
  private final ColourPalette colourPalette;

  /**
   * Represents the {@code BITMAPFILEHEADER} structure (14 bytes). Contains basic metadata such as
   * file type, size, and offset to pixel data.
   */
  private static final class Header {
    final ImageType type;
    final int size;
    final int offset; // Offset to actual image data
    final short reserved1;
    final short reserved2;

    /**
     * Constructs a Header from the byte array.
     *
     * @param data the byte array containing the file header
     * @throws IllegalArgumentException if data is too short to contain a valid header
     */
    private Header(byte[] data) {
      if (data.length < BitmapConstants.FILE_HEADER_SIZE) {
        throw new IllegalArgumentException("Byte array too short for bitmap file header.");
      }

      this.type = ImageType.BITMAP;
      this.size = readInt(data, BitmapConstants.BF_SIZE_OFFSET);
      this.reserved1 = readShort(data, BitmapConstants.BF_RESERVED1_OFFSET);
      this.reserved2 = readShort(data, BitmapConstants.BF_RESERVED2_OFFSET);
      this.offset = readInt(data, BitmapConstants.BF_OFFBITS_OFFSET);
    }

    /** @return The image type, typically {@code BITMAP} */
    public ImageType getType() {
      return type;
    }

    /** @return The total size of the BMP file in bytes */
    public int getSize() {
      return size;
    }

    /** @return The byte offset from start of file to pixel data */
    public int getOffset() {
      return offset;
    }
  }

  /**
   * Represents the colour palette (if present) in the BMP file.
   *
   * <p>Only present when bit depth is ≤ 8. Supports RGBTRIPLE (3-byte) and RGBQUAD (4-byte)
   * entries.
   *
   * @see <a href="https://en.wikipedia.org/wiki/BMP_file_format#Color_table">BMP Colour Table</a>
   */
  private static final class ColourPalette {
    // Array to store parsed ARGB colours (AARRGGBB)
    private final int[] colours;
    private final boolean hasAlphaChannel;

    /**
     * Parses the colour palette from the BMP byte array.
     *
     * @param data the image byte array
     * @param dibHeader the parsed DIB header
     * @param paletteStartFileOffset file offset where the palette begins
     */
    private ColourPalette(byte[] data, DIBHeader dibHeader, int paletteStartFileOffset) {
      if (dibHeader.getBitsPerPixel() > 8) {
        throw new IllegalArgumentException("Colour palette is not expected for bit depth > 8.");
      }

      int numEntries = 0;
      // If the number of colors in the color palette is 0, default to 2^n
      // n == bits per pixel
      if (dibHeader.getNColours() == 0) {
        numEntries = 1 << dibHeader.getBitsPerPixel(); // Max colours for bit depth
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
        BitmapV4Header v4Header = (BitmapV4Header) dibHeader;
        if (v4Header.getAlphaMask() != 0) {
          explicitAlphaFlag = true;
        }
      }

      for (int i = 0; i < numEntries; i++) {
        int entryOffset = paletteStartFileOffset + (i * bytesPerPaletteEntry);

        // Basic bounds check for reading from data array
        if (entryOffset < 0 || entryOffset + bytesPerPaletteEntry > data.length) {
          throw new IllegalArgumentException(
              String.format(
                  "Palette data truncated or out of bounds at entry %d (offset: %d)",
                  i, entryOffset));
        }

        // BMP stores BGR order. Alpha is often 0xFF (opaque) or ignored. But will try and handle it
        // Java lacks unsigned byte hence the need for bitwise operations
        int blue = data[entryOffset] & 0xFF;
        int green = data[entryOffset + 1] & 0xFF;
        int red = data[entryOffset + 2] & 0xFF;

        // Default to opaque (255)
        int alpha = 0xFF;

        // Check alpha in bytes, ig not explicitly stated in header
        if (bytesPerPaletteEntry == 4) {
          alpha = data[entryOffset + 3] & 0xFF;
          if (alpha != 0xFF) {
            foundNonZeroReservedByte = true;
          }
        }

        // Store as ARGB (AARRGGBB)
        this.colours[i] = (alpha << 24) | (red << 16) | (green << 8) | blue;
      }

      this.hasAlphaChannel = explicitAlphaFlag || foundNonZeroReservedByte;
    }

    /** @return the number of colour entries in the palette */
    public int getNumberOfEntries() {
      return colours.length;
    }

    /**
     * Retrieves a colour entry from the palette.
     *
     * @param index the palette index
     * @return the ARGB colour as {@code 0xAARRGGBB}
     * @throws IndexOutOfBoundsException if index is out of bounds
     */
    public int getColour(int index) {
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
    public boolean hasAlphaChannel() {
      return hasAlphaChannel;
    }
  }

  /**
   * Constructs a new {@code BitmapParser} by reading all bytes from the given file path.
   *
   * @param path the path to a BMP image file
   * @throws IOException if an I/O error occurs reading from the file
   * @throws IllegalArgumentException if the file does not appear to be a valid BMP
   */
  public BitmapParser(Path path) throws IOException {
    this(Files.readAllBytes(path));
  }

  /**
   * Constructs a new {@code BitmapParser} using the provided byte array.
   *
   * <p>This constructor checks the first few bytes, commonly referred to as Magic Number, to
   * identify the image type. It also checks the image header to derive other image metadata such as
   * size and offset.
   *
   * @param bytes the byte array representing image data
   * @throws IllegalArgumentException if the byte array does not represent a valid BMP image
   */
  public BitmapParser(byte[] bytes) {
    Objects.requireNonNull(bytes, "Input byte array cannot be null.");
    if (bytes.length < BitmapConstants.FILE_HEADER_SIZE + BitmapConstants.BITMAPCOREHEADER_SIZE) {
      throw new IllegalArgumentException("Byte array too short to be a minimal BMP image.");
    }

    this.data = Arrays.copyOf(bytes, bytes.length);

    // Validate the image type before attempting to parse headers.
    if (!ImageType.BITMAP.equals(ImageUtils.detectType(data))) {
      throw new IllegalArgumentException(
          "Provided data is not a valid BMP image (magic number mismatch).");
    }

    this.header = new Header(this.data);
    this.dibHeader = DIBHeader.createDIBHeader(this.data);

    if (this.hasColourPalette()) {
      int bitmask = 0;
      if (dibHeader.getCompression() == 3) {
        bitmask = 12;
      } else if (dibHeader.getCompression() == 6) {
        bitmask = 16;
      }
      int paletteStartOffset =
          BitmapConstants.FILE_HEADER_SIZE + this.dibHeader.getHeaderSize() + bitmask;
      this.colourPalette = new ColourPalette(this.data, this.dibHeader, paletteStartOffset);
    } else {
      this.colourPalette = null;
    }
  }

  /** @return the {@link ImageType} for this parser (always {@code BITMAP}) */
  @Override
  public ImageType getType() {
    return header.getType();
  }

  /** @return total file size in bytes */
  @Override
  public int getSize() {
    return header.getSize();
  }

  /** @return the byte offset to the pixel data */
  @Override
  public int getOffset() {
    return header.getOffset();
  }

  /** @return a defensive copy of the raw image data */
  public byte[] getRawData() {
    return Arrays.copyOf(data, data.length);
  }

  // Getters
  /** @return the parsed DIB header type */
  public InfoHeaderType getDibHeaderType() {
    return dibHeader.getType();
  }

  /** @return image width in pixels */
  public int getWidth() {
    return dibHeader.getWidth();
  }

  /** @return image height in pixels */
  public int getHeight() {
    return dibHeader.getHeight();
  }

  /** @return number of bits per pixel */
  public short getBitsPerPixel() {
    return dibHeader.getBitsPerPixel();
  }

  /** @return compression method (e.g., BI_RGB, BI_RLE8) */
  public int getCompression() {
    return dibHeader.getCompression();
  }

  /** @return size of bitmap pixel data in bytes */
  public int getImageDataSize() {
    return dibHeader.getImageDataSize();
  }

  /** @return number of colours used in the palette */
  public int getNColours() {
    return dibHeader.getNColours();
  }

  /** @return number of important colours specified in the header */
  public int getImportantColours() {
    return dibHeader.getImportantColours();
  }

  /** @return horizontal resolution in pixels per meter */
  public int getXResolution() {
    return dibHeader.getXResolution();
  }

  /** @return vertical resolution in pixels per meter */
  public int getYResolution() {
    return dibHeader.getYResolution();
  }

  /**
   * Returns the alpha mask for V3/V4/V5 headers.
   *
   * @return the alpha bitmask
   * @throws UnsupportedOperationException if alpha mask is not defined for this header type
   */
  // TODO: add more specific getters for V4/V5 header fields with proper casting and checks
  public long getAlphaMask() {
    if (InfoHeaderType.BITMAPV3INFOHEADER.equals(dibHeader.getType())) {
      return ((BitmapV3InfoHeader) dibHeader).getAlphaMask();
    }
    if (InfoHeaderType.BITMAPV4HEADER.equals(dibHeader.getType())) {
      return ((BitmapV4Header) dibHeader).getAlphaMask();
    }
    if (InfoHeaderType.BITMAPV5HEADER.equals(dibHeader.getType())) {
      return ((BitmapV5Header) dibHeader).getAlphaMask();
    }
    // For earlier headers, the concept of a dedicated alpha mask doesn't exist
    throw new UnsupportedOperationException(
        String.format("No alpha mask for image with %s header", dibHeader.getType()));
  }

  /**
   * @return ICC profile size for BITMAPV5HEADER
   * @throws UnsupportedOperationException if not a V5 header
   */
  public int getProfileSize() {
    if (dibHeader instanceof BitmapV5Header) {
      return ((BitmapV5Header) dibHeader).getProfileSize();
    }
    throw new UnsupportedOperationException(
        String.format("No profile size for image with %s header", dibHeader.getType()));
  }

  /** @return size of the DIB header in bytes */
  public int getHeaderSize() {
    return dibHeader.getHeaderSize();
  }

  /** @return true if a colour palette is present (typically for ≤ 8 bpp images) */
  public boolean hasColourPalette() {
    return dibHeader.getBitsPerPixel() <= 8; // Typically for 1, 4, or 8 bpp
  }

  /** @return true if alpha channel is present in the colour palette and false otherwise */
  public boolean hasAlphaChannel() {
    return Objects.requireNonNull(colourPalette).hasAlphaChannel();
  }
}
