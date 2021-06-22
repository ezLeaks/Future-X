package Method.Client.utils.Screens.Override;

import Method.Client.utils.Screens.Screen;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketUpdateSign;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.GuiOpenEvent;
import org.lwjgl.input.Keyboard;

public class SignInsert extends Screen {
  public void GuiOpen(GuiOpenEvent event) {
    if (event.getGui() instanceof GuiEditSign)
      event.setGui(new BetterSignGui(((GuiEditSign)event.getGui()).tileSign)); 
  }
  
  class BetterSignGui extends GuiScreen {
    private int focusedField = 0;
    
    public final TileEntitySign sign;
    
    private List<GuiTextField> textFields;
    
    private String[] defaultStrings;
    
    public BetterSignGui(TileEntitySign sign) {
      this.sign = sign;
    }
    
    public void initGui() {
      this.buttonList.clear();
      Keyboard.enableRepeatEvents(true);
      this.textFields = new LinkedList<>();
      this.defaultStrings = new String[4];
      int i;
      for (i = 0; i < 4; i++) {
        GuiTextField field = new GuiTextField(i, this.fontRenderer, this.width / 2 + 4, 75 + i * 24, 120, 20);
        field.setValidator(this::validateText);
        field.setMaxStringLength(100);
        String text = this.sign.signText[i].getUnformattedText();
        this.defaultStrings[i] = text;
        field.setText(text);
        this.textFields.add(field);
      } 
      ((GuiTextField)this.textFields.get(this.focusedField)).setFocused(true);
      addButton(new GuiButton(4, this.width / 2 + 5, this.height / 4 + 120, 120, 20, "Done"));
      addButton(new GuiButton(5, this.width / 2 - 125, this.height / 4 + 120, 120, 20, "Cancel"));
      addButton(new GuiButton(6, this.width / 2 - 41, 147, 40, 20, "Shift"));
      addButton(new GuiButton(7, this.width / 2 - 41, 123, 40, 20, "Clear"));
      addButton(new GuiButton(8, this.width / 2 + 130, 124, 40, 20, "Lag"));
      addButton(new GuiButton(9, this.width / 2 + 130, 99, 40, 20, "FillMax"));
      for (i = 0; i < 22; i++)
        addButton(new GuiButton(i + 11, this.width / 2 + 130 + i % 5 * 15, 215 - i / 5 * 15, 15, 15, SignInsert.this.ColorfromInt(i) + "&A")); 
      this.sign.setEditable(false);
    }
    
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      super.mouseClicked(mouseX, mouseY, mouseButton);
      this.textFields.forEach(field -> {
            field.mouseClicked(mouseX, mouseY, mouseButton);
            if (field.isFocused())
              this.focusedField = field.getId(); 
          });
      if (!((GuiTextField)this.textFields.get(this.focusedField)).isFocused())
        ((GuiTextField)this.textFields.get(this.focusedField)).setFocused(true); 
    }
    
    protected void keyTyped(char typedChar, int keyCode) {
      int change;
      switch (keyCode) {
        case 1:
          exit();
          return;
        case 15:
          change = isShiftKeyDown() ? -1 : 1;
          tabFocus(change);
          return;
        case 200:
          tabFocus(-1);
          return;
        case 28:
        case 156:
        case 208:
          tabFocus(1);
          break;
      } 
      this.textFields.forEach(field -> field.textboxKeyTyped(typedChar, keyCode));
      this.sign.signText[this.focusedField] = (ITextComponent)new TextComponentString(((GuiTextField)this.textFields.get(this.focusedField)).getText());
    }
    
    protected void actionPerformed(GuiButton button) throws IOException {
      int i;
      String[] replacements;
      int k;
      StringBuilder lagStringBuilder;
      int j;
      String[] Builder;
      int m;
      String line, rando[];
      int n;
      super.actionPerformed(button);
      switch (button.id) {
        case 5:
          for (i = 0; i < 4; i++)
            this.sign.signText[i] = (ITextComponent)new TextComponentString(this.defaultStrings[i]); 
        case 4:
          exit();
          return;
        case 6:
          replacements = new String[4];
          for (k = 0; k < 4; k++) {
            int change = isShiftKeyDown() ? 1 : -1;
            int target = wrapLine(k + change);
            replacements[k] = this.sign.signText[target].getUnformattedText();
          } 
          applytext(replacements);
          return;
        case 7:
          this.textFields.forEach(field -> {
                int id = field.getId();
                field.setText("");
                this.sign.signText[id] = (ITextComponent)new TextComponentString("");
              });
          return;
        case 8:
          lagStringBuilder = new StringBuilder();
          for (j = 0; j < 500; j++)
            lagStringBuilder.append("/(!Â§()%/Â§)=/(!Â§()%/Â§)=/(!Â§()%/Â§)="); 
          Builder = new String[4];
          for (m = 0; m < 4; m++)
            Builder[m] = lagStringBuilder.toString(); 
          applytext(Builder);
          return;
        case 9:
          line = Random();
          rando = new String[4];
          for (n = 0; n < 4; n++)
            rando[n] = line.substring(n * 384, (n + 1) * 384); 
          applytext(rando);
          return;
      } 
      if (button.id < 27)
        ((GuiTextField)this.textFields.get(this.focusedField)).text += "&" + Integer.toHexString(button.id - 11); 
      if (button.id == 27)
        ((GuiTextField)this.textFields.get(this.focusedField)).text += "&k"; 
      if (button.id == 28)
        ((GuiTextField)this.textFields.get(this.focusedField)).text += "&l"; 
      if (button.id == 29)
        ((GuiTextField)this.textFields.get(this.focusedField)).text += "&m"; 
      if (button.id == 30)
        ((GuiTextField)this.textFields.get(this.focusedField)).text += "&n"; 
      if (button.id == 31)
        ((GuiTextField)this.textFields.get(this.focusedField)).text += "&o"; 
      if (button.id == 32)
        ((GuiTextField)this.textFields.get(this.focusedField)).text += "&r"; 
    }
    
    void applytext(String[] index) {
      this.textFields.forEach(field -> {
            int id = field.getId();
            field.setText(index[id]);
            this.sign.signText[id] = (ITextComponent)new TextComponentString(index[id]);
          });
    }
    
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      drawDefaultBackground();
      drawCenteredString(this.fontRenderer, I18n.format("sign.edit", new Object[0]), this.width / 2, 40, 16777215);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.pushMatrix();
      GlStateManager.translate((this.width / 2) - 63.0F, 0.0F, 50.0F);
      GlStateManager.scale(-93.75F, -93.75F, -93.75F);
      GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
      Block block = this.sign.getBlockType();
      if (block == Blocks.STANDING_SIGN) {
        float f1 = (this.sign.getBlockMetadata() * 360) / 16.0F;
        GlStateManager.rotate(f1, 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(0.0F, -1.0625F, 0.0F);
      } else {
        int i = this.sign.getBlockMetadata();
        float f2 = (i == 2) ? 180.0F : ((i == 4) ? 90.0F : ((i == 5) ? -90.0F : 0.0F));
        GlStateManager.rotate(f2, 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(0.0F, -0.7625F, 0.0F);
      } 
      this.sign.lineBeingEdited = -1;
      TileEntityRendererDispatcher.instance.render((TileEntity)this.sign, -0.5D, -0.75D, -0.5D, 0.0F);
      GlStateManager.popMatrix();
      this.textFields.forEach(GuiTextField::func_146194_f);
      super.drawScreen(mouseX, mouseY, partialTicks);
    }
    
    void exit() {
      this.sign.markDirty();
      this.mc.displayGuiScreen(null);
    }
    
    private String Random() {
      IntStream gen = (new Random()).ints(128, 1112063).map(i -> (i < 55296) ? i : (i + 2048));
      return gen.limit(1536L).<CharSequence>mapToObj(i -> String.valueOf((char)i)).collect(Collectors.joining());
    }
    
    public void onGuiClosed() {
      String[] color = new String[4];
      for (int i = 0; i < 4; i++)
        color[i] = this.sign.signText[i].getUnformattedText().replace("&", "§§a"); 
      applytext(color);
      Keyboard.enableRepeatEvents(false);
      NetHandlerPlayClient nethandlerplayclient = this.mc.getConnection();
      if (nethandlerplayclient != null)
        nethandlerplayclient.sendPacket((Packet)new CPacketUpdateSign(this.sign.getPos(), this.sign.signText)); 
      this.sign.setEditable(true);
    }
    
    void tabFocus(int change) {
      ((GuiTextField)this.textFields.get(this.focusedField)).setFocused(false);
      this.focusedField = wrapLine(this.focusedField + change);
      ((GuiTextField)this.textFields.get(this.focusedField)).setFocused(true);
    }
    
    int wrapLine(int line) {
      return (line > 3) ? 0 : ((line < 0) ? 3 : line);
    }
    
    boolean validateText(String s) {
      if (this.fontRenderer.getStringWidth(s) > 90)
        return false; 
      for (char c : s.toCharArray()) {
        if (!ChatAllowedCharacters.isAllowedCharacter(c))
          return false; 
      } 
      return true;
    }
  }
}
