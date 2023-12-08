package pkg.exoad.softgradient.core.services;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.WeakHashMap;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 * AssetsService - Naive class for loading things from a baked-in assets folder
 * that is bundled with the SoftGradient library and program. It can be used to
 * fetch outside of this baked-in folder, but highly unstable and untested.
 * <p>
 * All of the methods that try to fetch an object are given 2 tries to find the
 * required asset where it will
 * first try to search in the pre-baked folder, then outside the pre-baked
 * folder. If both of those fail for some reason,
 * a final {@code null} is returned which is highly unlikely if the asset is
 * actually present.
 * <p>
 * <em>There are plans to migrate to using {@link java.util.Optional}'s to allow
 * for better
 * catching of nonexistent assets, but that just introduces overhead as 99.9% of
 * the time, the asset should be there.</em>
 *
 * @author Jack Meng
 */
public final class AssetsService
{
      private AssetsService()
      {}

      // A very basic cache system for objects loaded from the assets folder, we use a weakhashmap so the gc can take care of making something expire
      private static WeakHashMap<String,Object> cache;

      /**
       * Set to where this service should search in and append paths after.
       * Rarely should this be touched.
       */
      public static String ASSETS_OFFSET="";

      static
      {
            // init the cache map
            cache=new WeakHashMap<>();
      }

      /**
       * Fetches an ImageIcon object from the baked in folder. This is usually
       * used directly with an UI object directly and requires no additional
       * processing by the program. Such as scaling, filtering, etc..
       * <p>
       * <strong>Example Usage</strong>
       * <p>
       * <blockquote><pre>
       * UIButtonDelegate
       *                .make()
       *                .withIcon(AssetsService.fetchImageIcon("assets/icons/home_64x64.png"));
       * </pre></blockquote>
       *
       * @param path The internal baked-in path to search from
       *
       * @return The loaded ImageIcon.
       */
      public static ImageIcon fetchImageIcon(String path)
      {
            path=ASSETS_OFFSET+path;
            if(cache
                  .containsKey(path)&&cache
                        .get(path) instanceof ImageIcon)
                  return (ImageIcon)cache
                        .get(path);
            ImageIcon res;
            try
            {
                  res=new ImageIcon(
                        Objects
                              .requireNonNull(
                                    AssetsService.class
                                          .getResource(path)
                              )
                  );
            }catch(NullPointerException e)
            {
                  res=new ImageIcon(path);
            }
            cache
                  .put(
                        path,
                        res
                  );
            return res;
      }

      /**
       * Fetchs an Image object. Similar to {@link #fetchImageIcon(String)}, but
       * instead this function is called when
       * we want to modify the image after, such as scaling, filtering, etc..
       * <p>
       * <strong>Example Usage</strong>
       * <p>
       * <blockquote><pre>
       * BufferedImage img=AssetsService.fetchBufferedImage("assets/icons/home_64x64.png");
       * </pre></blockquote>
       *
       * @param path The internal baked-in path to search from
       *
       * @return The loaded BufferedImage
       */
      public static BufferedImage fetchBufferedImage(String path)
      {
            path=ASSETS_OFFSET+path;
            if(cache
                  .containsKey(path)&&cache
                        .get(path) instanceof BufferedImage)
                  return (BufferedImage)cache
                        .get(path);
            BufferedImage res;
            try
            {
                  res=ImageIO
                        .read(
                              Objects
                                    .requireNonNull(
                                          AssetsService.class
                                                .getResource(path)
                                    )
                        );
            }catch(IOException e)
            {
                  try
                  {
                        res=ImageIO
                              .read(new File(path));
                  }catch(IOException e1)
                  {
                        return null; // uh oh, this is bad
                  }
            }
            cache
                  .put(
                        path,
                        res
                  );
            return res;
      }
}
