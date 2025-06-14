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
 * @author Kevin Babu
 * @version 1.0
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

  /** The pixel row size. */
  private final int pixelRowSize;

  /** The pixel array size */
  private final int pixelArraySize;

  /** Represents the BITMAPFILEHEADER structure (14 bytes). */
  private static class Header {
    final ImageType type;
    final int size;
    final int offset; // Offset to actual image data
    final short reserved1, reserved2;

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

    public ImageType getType() {
      return type;
    }

    public int getSize() {
      return size;
    }

    public int getOffset() {
      return offset;
    }
  }

  /**
   * Represents the colour palette if present in the BMP file. See <a
   * href="https://en.wikipedia.org/wiki/BMP_file_format#Color_table">Colour table</a>
   */
  private static class ColourPalette {
    // Array to store parsed ARGB colours (AARRGGBB)
    private final int[] colours;
    private final boolean hasAlphaChannel;

    private ColourPalette(byte[] data, DIBHeader dibHeader, int paletteStartFileOffset) {
      if (dibHeader.getBitsPerPixel() > 8) {
        throw new IllegalArgumentException("Colour palette is not expected for bit depth > 8.");
      }

      int numEntries = dibHeader.getNColours();
      // If the number of colors in the color palette is 0, default to 2^n
      // n == bits per pixel
      if (numEntries == 0) {
        numEntries = 1 << dibHeader.getBitsPerPixel(); // Max colours for bit depth
      }

      this.colours = new int[numEntries];
      int bytesPerPaletteEntry;

      // Determine bytes per palette entry based on DIB header type
      if (dibHeader.getType() == InfoHeaderType.BITMAPCOREHEADER) {
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

    public int getNumberOfEntries() {
      return colours.length;
    }

    public int getColour(int index) {
      if (index < 0 || index >= colours.length) {
        throw new IndexOutOfBoundsException(
            String.format("Palette index %d out of bounds [0, %d]", index, colours.length - 1));
      }
      return colours[index];
    }

    /**
     * Returns true if the palette is believed to contain active alpha values
     * (i.e. not all entries are fully opaque or strictly follow the "reserved=0" rule).
     * @return true if alpha is likely present, else, return false.
     */
    public boolean hasAlphaChannel() {
      return hasAlphaChannel;
    }
  }

  /**
   * Constructs a new {@code BitmapParser} by reading all bytes from the given file path.
   *
   * @param path the file path to the image
   * @throws IOException if an I/O error occurs reading from the file
   * @throws IllegalArgumentException if the file does not represent a valid BMP image
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
    this.dibHeader = createDIBHeader(this.data);
    this.pixelRowSize = (int) (Math.ceil((this.getBitsPerPixel() * this.getWidth()) / 32.0) * 4);
    this.pixelArraySize = this.pixelRowSize * Math.abs(this.getHeight());

    if (this.hasColourPalette()) {
      int bitmask = dibHeader.getCompression() == 3 ? 12 : dibHeader.getCompression() == 6 ? 16 : 0;
      int paletteStartOffset =
          BitmapConstants.FILE_HEADER_SIZE + this.dibHeader.getHeaderSize() + bitmask;
      this.colourPalette = new ColourPalette(this.data, this.dibHeader, paletteStartOffset);
    } else {
      this.colourPalette = null;
    }
  }

  /** Factory method to create the correct DIBHeader subclass based on header size. */
  private DIBHeader createDIBHeader(byte[] data) {
    // DIB header starts after file header
    int dibHeaderFileOffset = BitmapConstants.FILE_HEADER_SIZE;

    // Read the biSize field from the DIB header
    int headerSize = readInt(data, dibHeaderFileOffset + BitmapConstants.BI_SIZE_OFFSET);

    if (data.length < dibHeaderFileOffset + headerSize) {
      throw new IllegalArgumentException(
          "Byte array too short for declared DIB header size: " + headerSize);
    }

    // Determine which DIBHeader type to instantiate
    switch (headerSize) {
      case BitmapConstants.BITMAPCOREHEADER_SIZE: // 12 bytes
        return new BitmapCoreHeader(data, dibHeaderFileOffset);
      case BitmapConstants.BITMAPINFOHEADER_SIZE: // 40 bytes
        return new BitmapInfoHeader(data, dibHeaderFileOffset);
      case BitmapConstants.BITMAPV2INFOHEADER_SIZE: // 52 bytes
        return new BitmapV2InfoHeader(data, dibHeaderFileOffset);
      case BitmapConstants.BITMAPV3INFOHEADER_SIZE: // 56 bytes
        return new BitmapV3InfoHeader(data, dibHeaderFileOffset);
      case BitmapConstants.BITMAPV4HEADER_SIZE: // 108 bytes
        return new BitmapV4Header(data, dibHeaderFileOffset);
      case BitmapConstants.BITMAPV5HEADER_SIZE: // 124 bytes
        return new BitmapV5Header(data, dibHeaderFileOffset);
      default:
        throw new IllegalArgumentException("Unknown or unsupported DIB header size: " + headerSize);
    }
  }

  @Override
  public ImageType getType() {
    return header.getType();
  }

  @Override
  public int getSize() {
    return header.getSize();
  }

  @Override
  public int getOffset() {
    return header.getOffset();
  }

  /**
   * Returns a defensive copy of the raw image data byte array.
   *
   * @return A copy of the image data.
   */
  public byte[] getRawData() {
    return Arrays.copyOf(data, data.length);
  }

  // Getters
  public InfoHeaderType getDibHeaderType() {
    return dibHeader.getType();
  }

  public int getWidth() {
    return dibHeader.getWidth();
  }

  public int getHeight() {
    return dibHeader.getHeight();
  }

  public short getBitsPerPixel() {
    return dibHeader.getBitsPerPixel();
  }

  public int getCompression() {
    return dibHeader.getCompression();
  }

  public int getImageDataSize() {
    return dibHeader.getImageDataSize();
  }

  public int getNColours() {
    return dibHeader.getNColours();
  }

  public int getImportantColours() {
    return dibHeader.getImportantColours();
  }

  public int getXResolution() {
    return dibHeader.getXResolution();
  }

  public int getYResolution() {
    return dibHeader.getYResolution();
  }

  // You can add more specific getters for V4/V5 header fields with proper casting and checks
  public int getAlphaMask() {
    if (dibHeader
        instanceof BitmapV4Header) { // V2/V3 might also have it depending on your exact definition
      return ((BitmapV4Header) dibHeader).getAlphaMask();
    }
    // For earlier headers, the concept of a dedicated alpha mask doesn't exist
    return 0; // Or throw UnsupportedOperationException
  }

  public int getProfileSize() {
    if (dibHeader instanceof BitmapV5Header) {
      return ((BitmapV5Header) dibHeader).getProfileSize();
    }
    return 0; // Or throw UnsupportedOperationException
  }

  public int getHeaderSize() {
    return dibHeader.getHeaderSize();
  }

  /**
   * Determines if a colour palette is present in the BMP file based on bit depth.
   *
   * @return true if a colour palette is expected, false otherwise.
   */
  public boolean hasColourPalette() {
    return dibHeader.getBitsPerPixel() <= 8; // Typically for 1, 4, or 8 bpp
  }
}
