package Method.Client.clickgui.component.components.sub;

import Method.Client.clickgui.component.Component;
import Method.Client.clickgui.component.components.Button;
import Method.Client.module.misc.GuiModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class Keybind extends Component {
  private boolean hovered;
  
  private boolean binding;
  
  public static boolean PublicBinding;
  
  private final Button parent;
  
  private int offset;
  
  private int x;
  
  private int y;
  
  private boolean LControl = false;
  
  private boolean LShift = false;
  
  private boolean LAlt = false;
  
  protected Minecraft mc = Minecraft.getMinecraft();
  
  public Keybind(Button button, int offset) {
    this.parent = button;
    this.x = button.parent.getX() + button.parent.getWidth();
    this.y = button.parent.getY() + button.offset;
    this.offset = offset;
  }
  
  public void setOff(int newOff) {
    this.offset = newOff;
  }
  
  public void renderComponent() {
    GL11.glEnable(3042);
    Gui.drawRect(this.parent.parent.getX() + 2, this.parent.parent.getY() + this.offset, this.parent.parent.getX() + this.parent.parent.getWidth(), this.parent.parent.getY() + this.offset + 12, this.hovered ? GuiModule.Hover.getcolor() : GuiModule.innercolor.getcolor());
    Gui.drawRect(this.parent.parent.getX(), this.parent.parent.getY() + this.offset, this.parent.parent.getX() + 2, this.parent.parent.getY() + this.offset + 12, -1508830959);
    GL11.glPushMatrix();
    StringBuilder Keys = new StringBuilder();
    for (Integer key : this.parent.mod.getKeys())
      Keys.append(" ").append(Keyboard.getKeyName(key.intValue())); 
    Button.fontSelectButton(this.binding ? "Press a key..." : ("Key: " + Keys), (this.parent.parent
        .getX() + 7), (this.parent.parent.getY() + this.offset + 2), -1);
    GlStateManager.popMatrix();
  }
  
  public void RenderTooltip() {
    if (this.hovered && this.parent.open)
      Button.fontSelect("Press End To Clear", 0.0F, (float)(this.mc.displayHeight / 2.085D), -1); 
  }
  
  public void updateComponent(int mouseX, int mouseY) {
    this.y = this.parent.parent.getY() + this.offset;
    this.x = this.parent.parent.getX();
    this.hovered = isMouseOnButton(mouseX, mouseY);
  }
  
  public void mouseClicked(int mouseX, int mouseY, int button) {
    if (this.hovered && button == 0 && this.parent.open) {
      this.binding = !this.binding;
      PublicBinding = !PublicBinding;
    } 
  }
  
  public void keyTyped(char typedChar, int key) {
    if (this.binding) {
      if (key == 29) {
        this.LControl = !this.LControl;
        return;
      } 
      if (key == 42) {
        this.LShift = !this.LShift;
        return;
      } 
      if (key == 56) {
        this.LAlt = !this.LAlt;
        return;
      } 
      this.parent.mod.setKey(key, this.LControl, this.LShift, this.LAlt);
      this.binding = false;
      this.LAlt = false;
      this.LControl = false;
      this.LShift = false;
      PublicBinding = false;
      if (key == 207)
        this.parent.mod.setKey(key, false, false, false); 
    } 
  }
  
  public boolean isMouseOnButton(int x, int y) {
    return (x > this.x && x < this.x + this.parent.parent.getWidth() && y > this.y && y < this.y + this.parent.parent.getBarHeight());
  }
}
