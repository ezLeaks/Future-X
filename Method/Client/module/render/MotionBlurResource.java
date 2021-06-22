package Method.Client.module.render;

import java.io.InputStream;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;

class MotionBlurResource implements IResource {
  public ResourceLocation getResourceLocation() {
    return null;
  }
  
  public InputStream getInputStream() {
    double amount = 0.7D + MotionBlur.blurAmount.getValDouble() / 100.0D * 3.0D - 0.01D;
    return IOUtils.toInputStream(String.format(Locale.ENGLISH, "{\"targets\":[\"swap\",\"previous\"],\"passes\":[{\"name\":\"phosphor\",\"intarget\":\"minecraft:main\",\"outtarget\":\"swap\",\"auxtargets\":[{\"name\":\"PrevSampler\",\"id\":\"previous\"}],\"uniforms\":[{\"name\":\"Phosphor\",\"values\":[%.2f, %.2f, %.2f]}]},{\"name\":\"blit\",\"intarget\":\"swap\",\"outtarget\":\"previous\"},{\"name\":\"blit\",\"intarget\":\"swap\",\"outtarget\":\"minecraft:main\"}]}", new Object[] { Double.valueOf(amount), Double.valueOf(amount), Double.valueOf(amount) }));
  }
  
  public boolean hasMetadata() {
    return false;
  }
  
  @Nullable
  public <T extends net.minecraft.client.resources.data.IMetadataSection> T getMetadata(String s) {
    return null;
  }
  
  public String getResourcePackName() {
    return null;
  }
  
  public void close() {}
}
