package Method.Client.module.render;

import java.util.List;
import java.util.Set;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

class MotionBlurResourceManager implements IResourceManager {
  public Set<String> getResourceDomains() {
    return null;
  }
  
  public IResource getResource(ResourceLocation resourceLocation) {
    return new MotionBlurResource();
  }
  
  public List<IResource> getAllResources(ResourceLocation resourceLocation) {
    return null;
  }
}
