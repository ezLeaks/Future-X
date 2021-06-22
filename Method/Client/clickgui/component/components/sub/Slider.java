package Method.Client.clickgui.component.components.sub;

import Method.Client.clickgui.component.Component;
import Method.Client.clickgui.component.components.Button;
import Method.Client.managers.Setting;
import Method.Client.module.misc.GuiModule;
import java.math.BigDecimal;
import java.math.RoundingMode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class Slider extends Component {
  protected Minecraft mc = Minecraft.getMinecraft();
  
  private boolean hovered;
  
  private final Setting set;
  
  private final Button parent;
  
  private int offset;
  
  private int x;
  
  private int y;
  
  private boolean dragging = false;
  
  private GuiTextField Inputbox;
  
  public double renderWidth;
  
  public Slider(Setting value, Button button, int offset) {
    this.set = value;
    this.parent = button;
    this.x = button.parent.getX() + button.parent.getWidth();
    this.y = button.parent.getY() + button.offset;
    this.offset = offset;
  }
  
  public void renderComponent() {
    GL11.glEnable(3042);
    Gui.drawRect(this.parent.parent.getX() + 2, this.parent.parent.getY() + this.offset, this.parent.parent.getX() + this.parent.parent.getWidth(), this.parent.parent.getY() + this.offset + 12, this.hovered ? GuiModule.Hover.getcolor() : GuiModule.innercolor.getcolor());
    Gui.drawRect(this.parent.parent.getX() + 2, this.parent.parent.getY() + this.offset, this.parent.parent.getX() + (int)this.renderWidth, this.parent.parent.getY() + this.offset + 12, this.hovered ? -1084926635 : -1086045116);
    Gui.drawRect(this.parent.parent.getX(), this.parent.parent.getY() + this.offset, this.parent.parent.getX() + 2, this.parent.parent.getY() + this.offset + 12, (this.set.getValDouble() > this.set.getMax() || this.set.getValDouble() < this.set.getMin()) ? -1500442351 : -1500409455);
    GlStateManager.pushMatrix();
    GlStateManager.scale(1.0F, 1.0F, 0.5F);
    Button.fontSelectButton(this.set.getName() + ": " + this.set.getValDouble(), (this.parent.parent.getX() + 8), (this.parent.parent.getY() + this.offset + 2), -1);
    GlStateManager.popMatrix();
    if (this.hovered)
      Button.fontSelectButton(this.set.getTooltip(), 0.0F, (float)(this.mc.displayHeight / 2.085D), this.hovered ? -1499883111 : -1); 
    if (this.Inputbox != null)
      this.Inputbox.drawTextBox(); 
  }
  
  public void setOff(int newOff) {
    this.offset = newOff;
  }
  
  public void RenderTooltip() {
    if (this.hovered && this.parent.open)
      Button.fontSelect("Press Ctrl Click For Exact Input.", 0.0F, (float)(this.mc.displayHeight / 2.085D), -1); 
  }
  
  public String getName() {
    return this.set.getName();
  }
  
  public void updateComponent(int mouseX, int mouseY) {
    if (this.Inputbox != null)
      this.Inputbox.updateCursorCounter(); 
    this.y = this.parent.parent.getY() + this.offset;
    this.x = this.parent.parent.getX();
    this.hovered = isMouseOnButton(mouseX, mouseY);
    double diff = Math.min(88, Math.max(0, mouseX - this.x));
    double min = this.set.getMin();
    double max = this.set.getMax();
    this.renderWidth = 88.0D * (this.set.getValDouble() - min) / (max - min);
    if (this.set.getValDouble() > max || this.set.getValDouble() < min)
      this.renderWidth = 0.1D; 
    if (this.dragging && (this.mc.currentScreen instanceof Method.Client.clickgui.ClickGui || this.mc.currentScreen instanceof Method.Client.module.Onscreen.OnscreenGUI))
      if (diff == 0.0D) {
        this.set.setValDouble(this.set.getMin());
      } else {
        double newValue = roundToPlace(diff / 88.0D * (max - min) + min);
        this.set.setValDouble(newValue);
      }  
    if (this.dragging && !(this.mc.currentScreen instanceof Method.Client.clickgui.ClickGui) && !(this.mc.currentScreen instanceof Method.Client.module.Onscreen.OnscreenGUI))
      this.dragging = false; 
  }
  
  public void mouseClicked(int mouseX, int mouseY, int button) {
    if (this.Inputbox != null)
      this.Inputbox.setFocused(false); 
    if (this.hovered && button == 0 && this.parent.open && this.Inputbox == null)
      this.dragging = true; 
    if (this.hovered && button == 0 && this.parent.open && this.Inputbox != null)
      this.Inputbox.setFocused(true); 
    if (this.hovered && Keyboard.isKeyDown(29) && this.Inputbox == null) {
      Input();
      this.dragging = false;
    } 
  }
  
  void Input() {
    this.Inputbox = new GuiTextField(0, FontRend, this.x + 5, this.y, 64, 10);
    this.Inputbox.setFocused(true);
    this.Inputbox.writeText(String.valueOf(this.set.getValDouble()));
  }
  
  public void keyTyped(char typedChar, int keyCode) {
    if (this.Inputbox != null) {
      this.Inputbox.textboxKeyTyped(typedChar, keyCode);
      if (Keyboard.getEventKey() == 28)
        try {
          this.set.setValDouble(Double.parseDouble(this.Inputbox.getText()));
          this.Inputbox = null;
        } catch (Exception e) {
          e.printStackTrace();
        }  
    } 
  }
  
  public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
    this.dragging = false;
  }
  
  public boolean isMouseOnButton(int x, int y) {
    return (x > this.x && x < this.x + this.parent.parent.getWidth() && y > this.y && y < this.y + this.parent.parent.getBarHeight());
  }
  
  private static double roundToPlace(double value) {
    BigDecimal bd = new BigDecimal(value);
    bd = bd.setScale(2, RoundingMode.HALF_UP);
    return bd.doubleValue();
  }
}
