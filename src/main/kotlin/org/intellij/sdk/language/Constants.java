package org.intellij.sdk.language;

import com.intellij.util.containers.BidirectionalMap;
import com.intellij.util.ui.UIUtil;
import org.intellij.sdk.language.minimessage.tag.MiniMessageTag;
import org.intellij.sdk.language.minimessage.tag.impl.*;
import org.intellij.sdk.language.util.KeyRegistry;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class Constants {

  public static final NamedDecoration OBFUSCATED = new NamedDecoration("obfuscated", "k");
  public static final NamedDecoration BOLD = new NamedDecoration("bold", "l");
  public static final NamedDecoration STRIKETHROUGH = new NamedDecoration("strikethrough", "m");
  public static final NamedDecoration UNDERLINED = new NamedDecoration("underlined", "n");
  public static final NamedDecoration ITALIC = new NamedDecoration("italic", "o");

  private static final int BLACK_VALUE = 0x000000;
  public static final NamedColor BLACK = new NamedColor("black", BLACK_VALUE, "0");
  private static final int DARK_BLUE_VALUE = 0x0000aa;
  public static final NamedColor DARK_BLUE = new NamedColor("dark_blue", DARK_BLUE_VALUE, "1");
  private static final int DARK_GREEN_VALUE = 0x00aa00;
  public static final NamedColor DARK_GREEN = new NamedColor("dark_green", DARK_GREEN_VALUE, "2");
  private static final int DARK_AQUA_VALUE = 0x00aaaa;
  public static final NamedColor DARK_AQUA = new NamedColor("dark_aqua", DARK_AQUA_VALUE, "3");
  private static final int DARK_RED_VALUE = 0xaa0000;
  public static final NamedColor DARK_RED = new NamedColor("dark_red", DARK_RED_VALUE, "4");
  private static final int DARK_PURPLE_VALUE = 0xaa00aa;
  public static final NamedColor DARK_PURPLE = new NamedColor("dark_purple", DARK_PURPLE_VALUE, "5");
  private static final int GOLD_VALUE = 0xffaa00;
  public static final NamedColor GOLD = new NamedColor("gold", GOLD_VALUE, "6");
  private static final int GRAY_VALUE = 0xaaaaaa;
  public static final NamedColor GRAY = new NamedColor("gray", GRAY_VALUE, "7");
  private static final int DARK_GRAY_VALUE = 0x555555;
  public static final NamedColor DARK_GRAY = new NamedColor("dark_gray", DARK_GRAY_VALUE, "8");
  private static final int BLUE_VALUE = 0x5555ff;
  public static final NamedColor BLUE = new NamedColor("blue", BLUE_VALUE, "9");
  private static final int GREEN_VALUE = 0x55ff55;
  public static final NamedColor GREEN = new NamedColor("green", GREEN_VALUE, "a");
  private static final int AQUA_VALUE = 0x55ffff;
  public static final NamedColor AQUA = new NamedColor("aqua", AQUA_VALUE, "b");
  private static final int RED_VALUE = 0xff5555;
  public static final NamedColor RED = new NamedColor("red", RED_VALUE, "c");
  private static final int LIGHT_PURPLE_VALUE = 0xff55ff;
  public static final NamedColor LIGHT_PURPLE = new NamedColor("light_purple", LIGHT_PURPLE_VALUE, "d");
  private static final int YELLOW_VALUE = 0xffff55;
  public static final NamedColor YELLOW = new NamedColor("yellow", YELLOW_VALUE, "e");
  private static final int WHITE_VALUE = 0xffffff;
  public static final NamedColor WHITE = new NamedColor("white", WHITE_VALUE, "f");

  public static final BidirectionalMap<String, NamedColor> COLORS = new BidirectionalMap<>();
  public static final BidirectionalMap<String, NamedDecoration> DECORATIONS = new BidirectionalMap<>();

  public static final KeyRegistry TRANSLATION_KEYS = new KeyRegistry();
  public static final Properties TRANSLATION_KEY_VALUES = new Properties();
  public static final KeyRegistry CONTROL_KEYS = new KeyRegistry();
  public static final Properties CONTROL_KEY_NAMES = new Properties();
  public static final Properties GLOBAL_STYLES = new Properties();

  static {
    try {
      Properties props = new Properties();
      props.load(Constants.class.getResourceAsStream("/translation_keys.properties"));
      props.keySet().forEach(s -> TRANSLATION_KEYS.add((String) s));
      TRANSLATION_KEY_VALUES.putAll(props);

      props.clear();
      props.load(Constants.class.getResourceAsStream("/control_keys.properties"));
      props.keySet().forEach(s -> CONTROL_KEYS.add((String) s));
      CONTROL_KEY_NAMES.putAll(props);

      props.clear();
      props.load(Constants.class.getResourceAsStream("/global_styles.properties"));
      GLOBAL_STYLES.putAll(props);

    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    COLORS.put(BLACK.name, BLACK);
    COLORS.put(DARK_BLUE.name, DARK_BLUE);
    COLORS.put(DARK_GREEN.name, DARK_GREEN);
    COLORS.put(DARK_AQUA.name, DARK_AQUA);
    COLORS.put(DARK_RED.name, DARK_RED);
    COLORS.put(DARK_PURPLE.name, DARK_PURPLE);
    COLORS.put(GOLD.name, GOLD);
    COLORS.put(GRAY.name, GRAY);
    COLORS.put(DARK_GRAY.name, DARK_GRAY);
    COLORS.put(BLUE.name, BLUE);
    COLORS.put(GREEN.name, GREEN);
    COLORS.put(AQUA.name, AQUA);
    COLORS.put(RED.name, RED);
    COLORS.put(LIGHT_PURPLE.name, LIGHT_PURPLE);
    COLORS.put(YELLOW.name, YELLOW);
    COLORS.put(WHITE.name, WHITE);

    DECORATIONS.put(OBFUSCATED.name, OBFUSCATED);
    DECORATIONS.put(BOLD.name, BOLD);
    DECORATIONS.put(STRIKETHROUGH.name, STRIKETHROUGH);
    DECORATIONS.put(UNDERLINED.name, UNDERLINED);
    DECORATIONS.put(ITALIC.name, ITALIC);
  }

  public record Suggestion(Icon icon, String sug, String name) {
  }

  private static Suggestion ampersandColor(NamedColor col, char symbol) {
    return new Suggestion(createImageIcon(new Color(col.value()), 14, 14), "&" + symbol, col.toString().toLowerCase());
  }

  public static List<Suggestion> AMP_SUGGESTIONS_LIST = new ArrayList<>(List.of(
  new Suggestion(null, "&m", "strikethrough")
  ));
  public static List<Suggestion> MM_SUGGESTIONS_LIST = new LinkedList<>(List.of(
  new Suggestion(null, "strikethrough", "strikethrough")
  ));

  static {
    COLORS.forEach((k, v) -> AMP_SUGGESTIONS_LIST.add(ampersandColor(v, v.symbol.toCharArray()[0])));
    COLORS.forEach((k, v) -> MM_SUGGESTIONS_LIST.add(
    new Suggestion(createImageIcon(new Color(v.value), 14, 14), k.toString().toLowerCase(), k.toString().toLowerCase()))
    );
  }

  public static ImageIcon createImageIcon(Color color, int width, int height) {
    BufferedImage image = UIUtil.createImage(new Box(0), width, height, BufferedImage.TYPE_INT_RGB);
    Graphics2D graphics = image.createGraphics();
    graphics.setPaint(color);
    graphics.fillRect(0, 0, width, height);
    return new ImageIcon(image);
  }

  public record NamedDecoration(String name, String symbol) {

  }

  public record NamedColor(String name, int value, String symbol) {
    @Override
    public String toString() {
      return name();
    }
  }
}
