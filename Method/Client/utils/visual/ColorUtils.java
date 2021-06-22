package Method.Client.utils.visual;

import java.awt.Color;
import net.minecraft.client.renderer.GlStateManager;

public class ColorUtils {
  public static Color rainbow() {
    long offset = 999999999999L;
    float hue = (float)(System.nanoTime() + offset) / 1.0E10F % 1.0F;
    long color = Long.parseLong(Integer.toHexString(Color.HSBtoRGB(hue, 1.0F, 1.0F)), 16);
    return new Color((int)color);
  }
  
  public static Color wave(double wave, double satur, double bright) {
    float hue = (float)System.nanoTime() / 1.0E10F % 1.0F;
    hue = (float)(hue + wave / 10.0D);
    int color = Color.HSBtoRGB(hue, (float)satur, (float)bright);
    return new Color(color);
  }
  
  public static void glColor(int color) {
    GlStateManager.color((color >> 16 & 0xFF) / 255.0F, (color >> 8 & 0xFF) / 255.0F, (color & 0xFF) / 255.0F, (color >> 24 & 0xFF) / 255.0F);
  }
  
  public static int rgbToInt(int r, int g, int b, int a) {
    return r << 16 | g << 8 | b | a << 24;
  }
  
  public static int color(int r, int g, int b, int a) {
    return (new Color(r, g, b, a)).getRGB();
  }
  
  public static int color(float r, float g, float b, float a) {
    return (new Color(r, g, b, a)).getRGB();
  }
  
  public static float colorcalc(int c, int location) {
    return (c >> location & 0xFF) / 255.0F;
  }
  
  public static int rainbow(double saturation, double brightness, double alpha) {
    long offset = 999999999999L;
    float fade = 1.0F;
    float hue2 = (float)(System.nanoTime() + offset) / 1.0E10F % 1.0F;
    long color = Long.parseLong(Integer.toHexString(Color.HSBtoRGB(hue2, (float)saturation, (float)brightness)), 16);
    Color c = new Color((int)color);
    return (new Color(c.getRed() / 255.0F * fade, c.getGreen() / 255.0F * fade, c.getBlue() / 255.0F * fade, (float)alpha)).getRGB();
  }
}
