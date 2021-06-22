package Method.Client.module.Onscreen;

import Method.Client.clickgui.component.Component;
import Method.Client.managers.Setting;
import Method.Client.utils.font.CFontRenderer;
import java.awt.Font;
import java.text.DecimalFormat;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class PinableFrame {
  private final int width;
  
  public int defaultWidth;
  
  public int y;
  
  public int x;
  
  public int barHeight;
  
  private boolean isDragging;
  
  public int dragX;
  
  public int dragY;
  
  private boolean isPinned = false;
  
  public CFontRenderer FontRender;
  
  public String title;
  
  public String[] text;
  
  protected Minecraft mc = Minecraft.getMinecraft();
  
  public static void Toggle(String s, boolean b) {
    for (PinableFrame i : OnscreenGUI.pinableFrames) {
      if (i.title.equals(s)) {
        i.setPinned(b);
        break;
      } 
    } 
  }
  
  public PinableFrame(String title, String[] text, int y, int x) {
    this.FontRender = new CFontRenderer(new Font("Impact", 0, 18), true, false);
    this.title = title;
    this.text = text;
    this.x = x;
    this.y = y;
    this.width = 88;
    this.defaultWidth = 88;
    this.barHeight = 13;
    this.dragX = 0;
    this.isDragging = false;
  }
  
  protected DecimalFormat getDecimalFormat(int i) {
    switch (i) {
      case 0:
        return new DecimalFormat("0");
      case 1:
        return new DecimalFormat("0.0");
      case 2:
        return new DecimalFormat("0.00");
      case 3:
        return new DecimalFormat("0.000");
      case 4:
        return new DecimalFormat("0.0000");
      case 5:
        return new DecimalFormat("0.00000");
    } 
    return null;
  }
  
  protected void GetSetup(PinableFrame pinableFrame, Setting xpos, Setting ypos, Setting Frame, Setting FontSize) {
    pinableFrame.x = (int)xpos.getValDouble();
    pinableFrame.y = (int)ypos.getValDouble();
    if (Frame.getValString().equalsIgnoreCase("false") || Frame.getValString() == null)
      Frame.setValString("Times"); 
    pinableFrame.FontRender.setFontS(Frame.getValString(), FontSize.getValDouble(), this.FontRender);
  }
  
  protected void GetInit(PinableFrame pinableFrame, Setting xpos, Setting ypos, Setting Frame, Setting FontSize) {
    if (pinableFrame.FontRender.getSize() != (int)FontSize.getValDouble() || !pinableFrame.FontRender.getFont().getName().equalsIgnoreCase(Frame.getValString()))
      pinableFrame.FontRender.setFontS(Frame.getValString(), FontSize.getValDouble(), pinableFrame.FontRender); 
    if (!pinableFrame.getDrag().booleanValue()) {
      pinableFrame.x = (int)xpos.getValDouble();
      pinableFrame.y = (int)ypos.getValDouble();
    } else {
      xpos.setValDouble(pinableFrame.x);
      ypos.setValDouble(pinableFrame.y);
    } 
  }
  
  protected void fontSelect(Setting s, String name, float v, float v1, int color, boolean shadow) {
    if (s.getValString().equalsIgnoreCase("MC")) {
      if (shadow)
        this.mc.fontRenderer.drawStringWithShadow(name, (int)v, (int)v1, color); 
      if (!shadow)
        this.mc.fontRenderer.drawString(name, (int)v, (int)v1, color); 
    } else {
      if (shadow)
        this.FontRender.drawStringWithShadow(name, v, v1, color); 
      if (!shadow)
        this.FontRender.String(name, (int)v, (int)v1, color); 
    } 
  }
  
  protected int widthcal(Setting s, String name) {
    if (s.getValString().equalsIgnoreCase("MC"))
      return this.mc.fontRenderer.getStringWidth(name); 
    return this.FontRender.getStringWidth(name);
  }
  
  protected int heightcal(Setting s, String name) {
    if (s.getValString().equalsIgnoreCase("MC"))
      return 10; 
    return this.FontRender.getStringHeight(name);
  }
  
  public void renderFrame() {
    if (this.isPinned)
      Component.FontRend.drawStringWithShadow(this.title, (this.x + 3), (this.y + 3), -1); 
  }
  
  public void Ongui() {}
  
  public void renderFrameText() {
    int yCount = this.y + this.barHeight + 3;
    for (String line : this.text) {
      Component.FontRend.drawString(line, this.x + 3, yCount, -1);
      yCount += 10;
    } 
  }
  
  public void updatePosition(int mouseX, int mouseY) {
    if (this.isDragging && this.isPinned) {
      setX(mouseX - this.dragX);
      setY(mouseY - this.dragY);
    } 
  }
  
  public boolean isWithinHeader(int x, int y) {
    return (x >= this.x && x <= this.x + this.width && y >= this.y && y <= this.y + this.barHeight);
  }
  
  public boolean isWithinExtendRange(int x, int y) {
    return (x <= this.x + this.width - 2 && x >= this.x + this.width - 2 - 8 && y >= this.y + 2 && y <= this.y + 10);
  }
  
  public void setDrag(boolean drag) {
    this.isDragging = drag;
  }
  
  public Boolean getDrag() {
    return Boolean.valueOf(this.isDragging);
  }
  
  public int getX() {
    return this.x;
  }
  
  public void setX(int newX) {
    this.x = newX;
  }
  
  public int getY() {
    return this.y;
  }
  
  public void setY(int newY) {
    this.y = newY;
  }
  
  public int getWidth() {
    return this.width;
  }
  
  public boolean isPinned() {
    return this.isPinned;
  }
  
  public String getTitle() {
    return this.title;
  }
  
  public void setPinned(boolean pinned) {
    this.isPinned = pinned;
  }
  
  public void onRenderGameOverlay(RenderGameOverlayEvent.Text event) {}
  
  public void setup() {}
}
