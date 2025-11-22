/******************************************************************************
 *  Compilation:  javac Picture.java
 *  Execution:    java Picture filename.jpg
 *  Dependencies: none
 *
 * <p>
 *  Copyright 2002-2025, Robert Sedgewick and Kevin Wayne.
 *  This file is part of algs4.jar, which accompanies the textbook
 *      Algorithms, 4th edition by Robert Sedgewick and Kevin Wayne,
 *      Addison-Wesley Professional, 2011, ISBN 0-321-57351-X.
 *      http://algs4.cs.princeton.edu
 *  algs4.jar is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  algs4.jar is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License
 *  along with algs4.jar.  If not, see http://www.gnu.org/licenses.
 ******************************************************************************/

package com.eyespot.imageparser;

import com.eyespot.imageparser.bitmap.BitmapParser;
import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

/**
 * The {@code Picture} data type provides a basic capability for manipulating the individual pixels
 * of an image. You can either create a blank image (of a given dimension) or read an image in a
 * supported file format (typically JPEG, PNG, GIF, TIFF, and BMP). This class also includes methods
 * for displaying the image in a window and saving it to a file.
 *
 * <p><b>Use in the curriculum.</b> The {@code Picture} class is intended for use in the curriculum
 * once objects are introduced.
 *
 * <p><b>Anatomy of an image.</b> An image is a <em>width</em>-by-<em>height</em> grid of pixels,
 * with pixel (0, 0) in the upper-left corner. Each pixel has a color that is represented using the
 * <em>RGB color model</em>, which specifies the levels of <em>red</em> (R), <em>green</em> (G), and
 * <em>blue</em> (B) on an integer scale from 0 to 255.
 *
 * <blockquote>
 *
 * <img src = "https://introcs.cs.princeton.edu/java/stdlib/AnatomyImage.png" width = 200 alt =
 * "anatomy of an image">
 *
 * </blockquote>
 *
 * <p><b>Creating pictures.</b> You can use the following constructors to create new {@code Picture}
 * objects:
 *
 * <ul>
 *   <li>{@link #Picture(Path filepath)}
 * </ul>
 *
 * <p>The first constructor read an image in a supported file format (typically JPEG, PNG, GIF,
 * TIFF, and BMP) and initializes the picture to that image. The second constructor creates a
 * <em>width</em>-by-<em>height</em> picture, with each pixel black.
 *
 * <p><b>Getting and setting the colors of the individual pixels.</b> You can use the following
 * methods to get and set the color of a specified pixel:
 *
 * <ul>
 *   <li>{@link #get(int col, int row)}
 *   <li>{@link #set(int col, int row, Color color)}
 * </ul>
 *
 * <p>The first method returns the color of pixel (<em>col</em>, <em>row</em>) as a {@code Color}
 * object. The second method sets the color of pixel (<em>col</em>, <em>row</em>) to the specified
 * color.
 *
 * <p><b>Iterating over the pixels.</b> A common operation in image processing is to iterate over
 * and process all the pixels in an image. Here is a prototypical example that creates a grayscale
 * version of a color image, using the NTSC formula <em>Y</em> = 0.299<em>r</em> + 0.587<em>g</em> +
 * 0.114<em>b</em>. Note that if the red, green, and blue components of an RGB color are all equal,
 * the color is a shade of gray.
 *
 * <pre>
 *  Picture picture   = new Picture("https://introcs.cs.princeton.edu/java/stdlib/mandrill.jpg");
 *  Picture grayscale = new Picture(picture.width(), picture.height());
 *  for (int col = 0; col &lt; picture.width(); col++) {
 *      for (int row = 0; row &lt; picture.height(); row++) {
 *          Color color = picture.get(col, row);
 *          int r = color.getRed();
 *          int g = color.getGreen();
 *          int b = color.getBlue();
 *          int y = (int) (Math.round(0.299*r + 0.587*g + 0.114*b));
 *          Color gray = new Color(y, y, y);
 *          grayscale.set(col, row, gray);
 *      }
 *  }
 *  picture.show();
 *  grayscale.show();
 *  </pre>
 *
 * <p><b>Transparency.</b> Both the {@link Color} and {@code Picture} classes support transparency,
 * using the <em>alpha channel</em>. The alpha value defines the transparency of a color, with 0
 * corresponding to completely transparent and 255 to completely opaque. If transparency is not
 * explicitly used, the alpha values is 255.
 *
 * <p><b>32-bit color.</b> Sometimes it is more convenient (or efficient) to manipulate the color of
 * a pixel as a single 32-bit integers instead of four 8-bit components. The following methods
 * support this:
 *
 * <ul>
 *   <li>{@link #getARGB(int col, int row)}
 *   <li>{@link #setARGB(int col, int row, int rgb)}
 * </ul>
 *
 * <p>The alpha (A), red (R), green (G), and blue (B) components are encoded as a single 32-bit
 * integer. Given a 32-bit {@code int} encoding the color, the following code extracts the ARGB
 * components:
 *
 * <blockquote>
 *
 * <pre>
 *  int a = (rgb &gt;&gt; 24) &amp; 0xFF;
 *  int r = (rgb &gt;&gt; 16) &amp; 0xFF;
 *  int g = (rgb &gt;&gt;  8) &amp; 0xFF;
 *  int b = (rgb &gt;&gt;  0) &amp; 0xFF;
 *  </pre>
 *
 * </blockquote>
 *
 * Given the ARGB components (8-bits each) of a color, the following statement packs it into a
 * 32-bit {@code int}:
 *
 * <blockquote>
 *
 * <pre>
 *  int argb = (a &lt;&lt; 24) | (r &lt;&lt; 16) | (g &lt;&lt; 8) | (b &lt;&lt; 0);
 *  </pre>
 *
 * </blockquote>
 *
 * <p><b>Coordinates.</b> Pixel (<em>col</em>, <em>row</em>) is column <em>col</em> and row
 * <em>row</em>. By default, the origin (0, 0) is the pixel in the upper-left corner. These are
 * common conventions in image processing and consistent with Java's {@link
 * java.awt.image.BufferedImage} data type. The following two methods allow you to change this
 * convention:
 *
 * <ul>
 *   <li>{@link #setOriginLowerLeft()}
 *   <li>{@link #setOriginUpperLeft()}
 * </ul>
 *
 * <p><b>Saving files.</b> The {@code Picture} class supports writing images to a supported file
 * format (typically JPEG, PNG, GIF, TIFF, and BMP). You can save the picture to a file using these
 * two methods:
 *
 * <ul>
 *   <li>{@link #save(String filename)}
 *   <li>{@link #save(File file)}
 * </ul>
 *
 * <p>Alternatively, you can save the picture interactively by using the menu option <em>File →
 * Save</em> from the picture window.
 *
 * <p><b>File formats.</b> The {@code Picture} class supports reading and writing images to any of
 * the file formats supported by {@link javax.imageio} (typically JPEG, PNG, GIF, TIFF, and BMP).
 * The file extensions corresponding to JPEG, PNG, GIF, TIFF, and BMP, are {@code .jpg}, {@code
 * .png}, {@code .gif}, {@code .tif}, and {@code .bmp}, respectively. The file formats JPEG and BMP
 * do not support transparency.
 *
 * <p><b>Memory usage.</b> A <em>W</em>-by-<em>H</em> picture uses ~ 4 <em>W H</em> bytes of memory,
 * since the color of each pixel is encoded as a 32-bit <code>int</code>.
 *
 * <p><b>Additional documentation.</b> For additional documentation, see <a
 * href="https://introcs.cs.princeton.edu/31datatype">Section 3.1</a> of <i>Computer Science: An
 * Interdisciplinary Approach</i> by Robert Sedgewick and Kevin Wayne.
 *
 * @author Robert Sedgewick
 * @author Kevin Wayne
 */
public class Picture implements ActionListener {
  private final BufferedImage image; // the rasterized image
  protected JFrame jframe; // on-screen view
  private String title; // window title (typically the name of the file)
  private boolean isOriginUpperLeft = true; // location of origin
  private boolean isVisible = false; // is the frame visible?
  private boolean isDisposed = false; // has the window been disposed?
  private final int width;
  private final int height; // width and height
  private final BitmapParser parser; // the image parser
  private static final Logger LOGGER = Logger.getLogger(Picture.class.getName()); // the logger

  /**
   * Creates a new picture that is a deep copy of the argument picture.
   *
   * @param path the picture's path
   * @throws IllegalArgumentException if {@code picture} is {@code null}
   */
  public Picture(Path path) throws IOException {
    if (path == null) {
      throw new IllegalArgumentException("constructor argument is null");
    }

    parser = new BitmapParser(path);
    width = parser.getWidth();
    height = parser.getHeight();
    image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    title = String.valueOf(path);

    int[][] pixels = parser.getPixels();
    for (int col = 0; col < width(); col++) {
      for (int row = 0; row < height(); row++) {
        image.setRGB(col, row, pixels[row][col]);
      }
    }
  }

  // create the GUI for viewing the image if needed
  private JFrame createGUI() {
    JFrame frame = new JFrame();
    JMenuBar menuBar = new JMenuBar();
    JMenu menu = new JMenu("File");
    menuBar.add(menu);
    JMenuItem menuItem1 = new JMenuItem(" Save...   ");
    menuItem1.addActionListener(this);
    // Java 11:  use getMenuShortcutKeyMaskEx()
    // Java 8:   use getMenuShortcutKeyMask()
    menuItem1.setAccelerator(
        KeyStroke.getKeyStroke(
            KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
    menu.add(menuItem1);
    frame.setJMenuBar(menuBar);

    frame.setContentPane(getJLabel());
    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    frame.setTitle(title);
    frame.setResizable(false);

    frame.pack();

    frame.addWindowListener(
        new WindowAdapter() {
          @Override
          public void windowClosing(WindowEvent event) {
            isVisible = false;
            isDisposed = true;
            super.windowClosing(event);
          }
        });
    return frame;
  }

  /**
   * Returns a {@link JLabel} containing this picture, for embedding in a {@link JPanel}, {@link
   * JFrame} or other GUI widget.
   *
   * @return the {@code JLabel}
   */
  public JLabel getJLabel() {
    ImageIcon icon = new ImageIcon(image);
    return new JLabel(icon);
  }

  /** Sets the origin (0, 0) to be the upper left pixel. This is the default. */
  public void setOriginUpperLeft() {
    isOriginUpperLeft = true;
  }

  /** Sets the origin (0, 0) to be the lower left pixel. */
  public void setOriginLowerLeft() {
    isOriginUpperLeft = false;
  }

  /** Displays the picture in a window on the screen. */
  // getMenuShortcutKeyMask() deprecated in Java 10 but its replacement
  // getMenuShortcutKeyMaskEx() is not available in Java 8
  public void show() {
    if (jframe == null && !isDisposed) {
      jframe = createGUI();
      isVisible = true;
      jframe.setVisible(true);
      jframe.repaint();
    }

    if (jframe != null && !isDisposed) {
      isVisible = true;
      jframe.setVisible(true);
      jframe.repaint();
    }
  }

  /** Hides the window containing the picture. */
  public void hide() {
    if (jframe != null) {
      isVisible = false;
      jframe.setVisible(false);
    }
  }

  /**
   * Is the window containing the picture visible?
   *
   * @return {@code true} if the picture is visible, and {@code false} otherwise
   */
  public boolean isVisible() {
    return isVisible;
  }

  /**
   * Returns the height of the picture.
   *
   * @return the height of the picture (in pixels)
   */
  public int height() {
    return height;
  }

  /**
   * Returns the width of the picture.
   *
   * @return the width of the picture (in pixels)
   */
  public int width() {
    return width;
  }

  private void validateRowIndex(int row) {
    if (row < 0 || row >= height()) {
      throw new IndexOutOfBoundsException(
          "row index must be between 0 and " + (height() - 1) + ": " + row);
    }
  }

  private void validateColumnIndex(int col) {
    if (col < 0 || col >= width()) {
      throw new IndexOutOfBoundsException(
          "column index must be between 0 and " + (width() - 1) + ": " + col);
    }
  }

  /**
   * Returns the color of pixel ({@code col}, {@code row}) as a {@link java.awt.Color} object.
   *
   * @param col the column index
   * @param row the row index
   * @return the color of pixel ({@code col}, {@code row})
   * @throws IndexOutOfBoundsException unless both {@code 0 <= col < width} and {@code 0 <= row <
   *     height}
   */
  public Color get(int col, int row) {
    validateColumnIndex(col);
    validateRowIndex(row);
    int argb = getARGB(col, row);
    return new Color(argb, true);
  }

  /**
   * Returns the ARGB color of pixel ({@code col}, {@code row}) as a 32-bit integer. Using this
   * method can be more efficient than {@link #get(int, int)} because it does not create a {@code
   * Color} object.
   *
   * @param col the column index
   * @param row the row index
   * @return the 32-bit integer representation of the ARGB color of pixel ({@code col}, {@code row})
   * @throws IndexOutOfBoundsException unless both {@code 0 <= col < width} and {@code 0 <= row <
   *     height}
   */
  public int getARGB(int col, int row) {
    validateColumnIndex(col);
    validateRowIndex(row);
    if (isOriginUpperLeft) {
      return image.getRGB(col, row);
    } else {
      return image.getRGB(col, height - row - 1);
    }
  }

  /**
   * Sets the color of pixel ({@code col}, {@code row}) to the given color.
   *
   * @param col the column index
   * @param row the row index
   * @param color the color
   * @throws IndexOutOfBoundsException unless both {@code 0 <= col < width} and {@code 0 <= row <
   *     height}
   * @throws IllegalArgumentException if {@code color} is {@code null}
   */
  public void set(int col, int row, Color color) {
    validateColumnIndex(col);
    validateRowIndex(row);
    if (color == null) {
      throw new IllegalArgumentException("color argument is null");
    }
    int argb = color.getRGB();
    setARGB(col, row, argb);
  }

  /**
   * Sets the color of pixel ({@code col}, {@code row}) to the given ARGB color.
   *
   * @param col the column index
   * @param row the row index
   * @param argb the 32-bit integer representation of the color
   * @throws IndexOutOfBoundsException unless both {@code 0 <= col < width} and {@code 0 <= row <
   *     height}
   */
  public void setARGB(int col, int row, int argb) {
    validateColumnIndex(col);
    validateRowIndex(row);
    if (isOriginUpperLeft) {
      image.setRGB(col, row, argb);
    } else {
      image.setRGB(col, height - row - 1, argb);
    }
  }

  /**
   * Returns {@code true} if this picture is equal to the argument picture, and {@code false}
   * otherwise.
   *
   * @param other the other picture
   * @return {@code true} if this picture is the same dimension as {@code other} and if all pixels
   *     have the same color; {@code false} otherwise
   */
  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if (other == null) {
      return false;
    }
    if (other.getClass() != this.getClass()) {
      return false;
    }
    Picture that = (Picture) other;
    if (this.width() != that.width()) {
      return false;
    }
    if (this.height() != that.height()) {
      return false;
    }
    for (int col = 0; col < width(); col++) {
      for (int row = 0; row < height(); row++) {
        if (this.getARGB(col, row) != that.getARGB(col, row)) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Returns a string representation of this picture. The result is a <code>width</code>-by-<code>
   * height</code> matrix of pixels, where the color of a pixel is represented using 6 hex digits to
   * encode the red, green, and blue components.
   *
   * @return a string representation of this picture
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(width).append("-by-").append(height).append(" picture (RGB values given in hex)\n");
    for (int row = 0; row < height; row++) {
      for (int col = 0; col < width; col++) {
        int rgb;
        if (isOriginUpperLeft) {
          rgb = image.getRGB(col, row);
        } else {
          rgb = image.getRGB(col, height - row - 1);
        }
        sb.append(String.format("#%06X ", rgb & 0xFFFFFF));
      }
      sb.append("\n");
    }
    return sb.toString().trim();
  }

  /**
   * This operation is not supported because pictures are mutable.
   *
   * @return does not return a value
   * @throws UnsupportedOperationException if called
   */
  @Override
  public int hashCode() {
    throw new UnsupportedOperationException(
        "hashCode() is not supported because pictures are mutable");
  }

  /**
   * Sets the title of this picture.
   *
   * @param title the title
   * @throws IllegalArgumentException if {@code title} is {@code null}
   */
  public void setTitle(String title) {
    if (title == null) {
      throw new IllegalArgumentException("title is null");
    }
    this.title = title;
  }

  // does this picture use transparency (i.e., alpha < 255 for some pixel)?
  public boolean hasAlpha() {
    return parser.hasAlphaChannel();
  }

  /**
   * Saves the picture to a file in a supported file format (typically JPEG, PNG, GIF, TIFF, and
   * BMP). The filetype extension must be {@code .jpg}, {@code .png}, {@code .gif}, {@code .bmp}, or
   * {@code .tif}. If the file format does not support transparency (such as JPEG or BMP), it will
   * be converted to be opaque (with purely transparent pixels converted to black).
   *
   * @param filename the name of the file
   * @throws IllegalArgumentException if {@code filename} is {@code null}
   * @throws IllegalArgumentException if {@code filename} is the empty string
   * @throws IllegalArgumentException if {@code filename} has invalid filetype extension
   * @throws IllegalArgumentException if it cannot write the file {@code filename}
   */
  public void save(String filename) {
    if (filename == null) {
      throw new IllegalArgumentException("argument to save() is null");
    }
    if (filename.isEmpty()) {
      throw new IllegalArgumentException("argument to save() is the empty string");
    }
    File file = new File(filename);
    save(file);
  }

  /**
   * Saves the picture to a file in a supported file format (typically JPEG, PNG, GIF, TIFF, and
   * BMP). The filetype extension must be {@code .jpg}, {@code .png}, {@code .gif}, {@code .bmp}, or
   * {@code .tif}. If the file format does not support transparency (such as JPEG or BMP), it will
   * be converted to be opaque (with purely transparent pixels converted to black).
   *
   * @param file the file
   * @throws IllegalArgumentException if {@code file} is {@code null}
   */
  public void save(File file) {
    if (file == null) {
      throw new IllegalArgumentException("argument to save() is null");
    }
    title = file.getName();

    String suffix = title.substring(title.lastIndexOf('.') + 1);
    if (!title.contains(".") || suffix.isEmpty()) {
      throw new IllegalArgumentException(
          "The filename '" + title + "' has no filetype extension, such as .jpg or .png");
    }

    try {
      // for formats that support transparency (e.g., PNG and GIF)
      if (ImageIO.write(image, suffix, file)) {
        return;
      }

      // for formats that don't support transparency (e.g., JPG and BMP)
      // create BufferedImage in RGB format and use white background
      BufferedImage imageRGB = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
      imageRGB.createGraphics().drawImage(image, 0, 0, Color.WHITE, null);
      if (ImageIO.write(imageRGB, suffix, file)) {
        return;
      }

      // failed to save the file; probably wrong format
      throw new IllegalArgumentException("The filetype '" + suffix + "' is not supported");
    } catch (IOException e) {
      throw new IllegalArgumentException("could not write file '" + title + "'", e);
    }
  }

  protected FileDialog createFileDialog() {
    return new FileDialog(
        jframe, "The filetype extension must be either .jpg or .png", FileDialog.SAVE);
  }

  /** Opens a save dialog box when the user selects "Save As" from the menu. */
  @Override
  public void actionPerformed(ActionEvent event) {
    FileDialog chooser = createFileDialog();
    chooser.setVisible(true);
    String selectedDirectory = chooser.getDirectory();
    String selectedFilename = chooser.getFile();
    if (selectedDirectory != null && selectedFilename != null) {
      try {
        save(selectedDirectory + selectedFilename);
      } catch (IllegalArgumentException e) {
        LOGGER.log(Level.SEVERE, e::getMessage);
      }
    }
  }

  public static void main(String[] args) throws IOException {
      Picture picture = new Picture(Path.of(args[0]));
      String message = String.format("%d-by-%d%n", picture.width, picture.height);
      LOGGER.log(Level.INFO, message);
      picture.show();
  }
}
