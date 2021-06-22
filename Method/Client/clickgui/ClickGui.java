package Method.Client.clickgui;

import Method.Client.Main;
import Method.Client.clickgui.component.Component;
import Method.Client.clickgui.component.Frame;
import Method.Client.clickgui.component.components.Button;
import Method.Client.clickgui.component.components.sub.Keybind;
import Method.Client.managers.CommandManager;
import Method.Client.managers.FileManager;
import Method.Client.module.Category;
import Method.Client.module.Onscreen.OnscreenGUI;
import Method.Client.module.Onscreen.PinableFrame;
import Method.Client.module.command.Command;
import Method.Client.utils.visual.ColorUtils;
import Method.Client.utils.visual.RenderUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;

public class ClickGui extends GuiScreen {
  public static ArrayList<Frame> frames = new ArrayList<>();
  
  private GuiTextField textbox;
  
  boolean nomultidrag = false;
  
  boolean loaded;
  
  boolean Trycommand = false;
  
  public ClickGui() {
    int frameX = 5;
    for (Category category : Category.values()) {
      Frame frame = new Frame(category);
      frame.setX(frameX);
      frames.add(frame);
      frameX += frame.getWidth() + 1;
    } 
    FileManager.LoadMods();
    this.loaded = true;
    for (PinableFrame me : OnscreenGUI.pinableFrames)
      me.setup(); 
  }
  
  public void initGui() {
    this.textbox = new GuiTextField(0, this.fontRenderer, (int)(this.mc.displayWidth / 5.4D), 1, 240, this.mc.displayWidth / 100);
    this.textbox.setFocused(true);
    this.textbox.setMaxStringLength(999);
    this.textbox.setEnableBackgroundDrawing(false);
  }
  
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    drawDefaultBackground();
    GlStateManager.scale(1.0F, 1.0F, 0.5F);
    String parse = this.textbox.getText();
    this.textbox.drawTextBox();
    Frame.updateFont();
    Button.updateFont();
    for (Frame frame : frames) {
      if (frame.isWithinBounds(mouseX, mouseY))
        frame.handleScrollinput(); 
      frame.updatePosition(mouseX, mouseY);
      frame.renderFrame();
      if (frame.isOpen())
        for (Component comp : frame.getComponents()) {
          comp.RenderTooltip();
          if (comp.getName().toLowerCase().contains(parse.toLowerCase()) && !parse.isEmpty())
            RenderUtils.drawRectOutline(frame.getX(), comp.gety(), (frame.getX() + 88), (comp.gety() + 12), 1.0D, ColorUtils.rainbow().getRGB()); 
          try {
            comp.updateComponent(mouseX, mouseY);
          } catch (IOException e) {
            e.printStackTrace();
          } 
        }  
    } 
    drawGradientRect((int)(this.mc.displayWidth / 5.4D), 0, (int)(this.mc.displayWidth / 5.4D + 240.0D), 14, 865704345, 865704345);
    if (!parse.isEmpty()) {
      int add = 0;
      for (Command c : CommandManager.commands) {
        if (c.getCommand().toLowerCase().startsWith(parse.toLowerCase())) {
          Component.FontRend.drawStringWithShadow(c.getSyntax(), (float)(this.mc.displayWidth / 5.4D), (add + 10), -1);
          add += 10;
        } 
      } 
    } 
  }
  
  protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    if (this.nomultidrag) {
      Collections.reverse(frames);
      this.nomultidrag = false;
    } 
    for (Frame frame : frames) {
      if (frame.isOpen() && 
        !frame.getComponents().isEmpty() && 
        frame.isWithinBounds(mouseX, mouseY))
        for (Component component : frame.getComponents())
          component.mouseClicked(mouseX, mouseY, mouseButton);  
      if (frame.isWithinHeader(mouseX, mouseY) && mouseButton == 0 && !this.nomultidrag) {
        frame.setDrag(true);
        frame.dragX = mouseX - frame.getX();
        frame.dragY = mouseY - frame.getY();
        this.nomultidrag = true;
      } 
      if (frame.isWithinFooter(mouseX, mouseY) && mouseButton == 0) {
        frame.dragScrollstop = mouseY - frame.getScrollpos();
        frame.setDragBot(true);
      } 
      if (frame.isWithinHeader(mouseX, mouseY) && mouseButton == 1) {
        if (frame.getName().equalsIgnoreCase("Onscreen")) {
          this.mc.displayGuiScreen((GuiScreen)Main.OnscreenGUI);
          continue;
        } 
        frame.setOpen(!frame.isOpen());
      } 
    } 
    if (mouseButton == 0 && (mouseY >= 14 || mouseX >= this.mc.displayWidth / 5.4D + 240.0D || mouseX <= this.mc.displayWidth / 5.4D - 5.0D)) {
      this.textbox.setText("");
      this.Trycommand = false;
    } 
    if (mouseY < 14 && mouseButton == 0 && mouseX < this.mc.displayWidth / 5.4D + 240.0D && mouseX > this.mc.displayWidth / 5.4D - 5.0D)
      this.Trycommand = true; 
  }
  
  protected void keyTyped(char typedChar, int keyCode) {
    if (this.loaded) {
      FileManager.SaveMods();
      FileManager.saveframes();
      FileManager.savePROFILES();
    } 
    if (keyCode == 1)
      this.mc.displayGuiScreen(null); 
    if (!Keybind.PublicBinding)
      this.textbox.textboxKeyTyped(typedChar, keyCode); 
    for (Frame frame : frames) {
      if (frame.isOpen() && keyCode != 1 && 
        !frame.getComponents().isEmpty())
        for (Component component : frame.getComponents())
          component.keyTyped(typedChar, keyCode);  
    } 
    if (typedChar == '\017')
      for (Command c : CommandManager.commands) {
        String parse = this.textbox.getText();
        if (parse.length() > 0 && (
          c.getCommand().toLowerCase().startsWith(parse.toLowerCase().substring(0, parse.indexOf(' '))) || parse.substring(0, parse.indexOf(' ')).toLowerCase().startsWith(c.getCommand()))) {
          this.textbox.setText(c.getCommand());
          break;
        } 
      }  
    if (this.textbox.isFocused() && keyCode == 28 && this.Trycommand) {
      CommandManager.getInstance().runCommands(CommandManager.cmdPrefix + this.textbox.getText());
      this.mc.displayGuiScreen(null);
    } 
  }
  
  public void updateScreen() {
    this.textbox.updateCursorCounter();
    super.updateScreen();
  }
  
  protected void mouseReleased(int mouseX, int mouseY, int state) {
    for (Frame frame : frames) {
      frame.setDrag(false);
      frame.setDragBot(false);
      if (frame.isOpen() && 
        !frame.getComponents().isEmpty())
        for (Component component : frame.getComponents())
          component.mouseReleased(mouseX, mouseY, state);  
    } 
  }
  
  public boolean doesGuiPauseGame() {
    return false;
  }
}
