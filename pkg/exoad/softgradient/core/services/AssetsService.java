package pkg.exoad.softgradient.core.services;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.WeakHashMap;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class AssetsService
{
      private static WeakHashMap< String, Object > cache;

      public static String ASSETS_OFFSET="";

      static
      {
            cache=new WeakHashMap<>();
      }

      public static ImageIcon fetchImageIcon(String path)
      {
            path=ASSETS_OFFSET+path;
            if(cache.containsKey(path)&&cache.get(path) instanceof ImageIcon)
                  return (ImageIcon)cache.get(path);
            ImageIcon res;
            try
            {
                  res=new ImageIcon(Objects.requireNonNull(AssetsService.class.getResource(path)));
            } catch(NullPointerException e)
            {
                  res=new ImageIcon(path);
            }
            cache.put(
                        path,
                        res
            );
            return res;
      }

      public static BufferedImage fetchBufferedImage(String path)
      {
            path=ASSETS_OFFSET+path;
            if(cache.containsKey(path)&&cache.get(path) instanceof BufferedImage)
                  return (BufferedImage)cache.get(path);
            BufferedImage res;
            try
            {
                  res=ImageIO.read(Objects.requireNonNull(AssetsService.class.getResource(path)));
            } catch(IOException e)
            {
                  try
                  {
                        res=ImageIO.read(new File(path));
                  } catch(IOException e1)
                  {
                        return null; // uh oh, this is bad
                  }
            }
            cache.put(
                        path,
                        res
            );
            return res;
      }
}
