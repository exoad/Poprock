package pkg.exoad.softgradient;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.formdev.flatlaf.intellijthemes.FlatHighContrastIJTheme;

import pkg.exoad.softgradient.core.ColorObj;
import pkg.exoad.softgradient.core.SharedConstants;
import pkg.exoad.softgradient.core.events.ControllerChildDelegatesEventPayload;
import pkg.exoad.softgradient.core.events.EventPool;
import pkg.exoad.softgradient.core.events.GradientEventPayload;
import pkg.exoad.softgradient.core.services.AssetsService;
import pkg.exoad.softgradient.core.services.ColorService;
import pkg.exoad.softgradient.core.ui.UIAppMainDelegate;
import pkg.exoad.softgradient.core.ui.UIWindow;

import javax.swing.plaf.ColorUIResource;

/**
 * Main entry point into the SoftGradient application. Should not be called if
 * you are using it as a library!
 *
 * @author Jack Meng
 */
class SoftGradientEntry
{
      static
      {
            // do all pre-runtime property settings and checks before the main function is called
            System
                  .setProperty(
                        "sun.java2d.opengl",
                        "True"
                  );
            System
                  .setProperty(
                        "flatlaf.useWindowDecorations",
                        "true"
                  );
            System
                  .setProperty(
                        "flatlaf.menuBarEmbedded",
                        "true"
                  );
            UIManager
                  .put(
                        "ScrollBar.background",
                        ColorObj.TRANSPARENT
                  );
            UIManager
                  .put(
                        "ScrollBar.showButtons",
                        false
                  );
            UIManager
                  .put(
                        "JScrollPane.smoothScrolling",
                        true
                  );
            UIManager
                  .put(
                        "Component.focusedBorderColor",
                        ColorObj.TRANSPARENT
                  );
            UIManager
                  .put(
                        "Component.focusColor",
                        ColorObj.TRANSPARENT
                  );
            UIManager
                  .put(
                        "TabbedPane.tabSeparatorsFullHeight",
                        false
                  );
            UIManager
                  .put(
                        "TabbedPane.showTabSeparators",
                        true
                  );
            UIManager
                  .put(
                        "Button.arc",
                        10
                  );
            UIManager
                  .put(
                        "ScrollBar.thumb",
                        new ColorUIResource(
                              ColorService
                                    .hexToColor(
                                          SharedConstants.LAF_POPROCK_PRIMARY_1
                                    )
                                    .asAwt()
                        )
                  );
            try
            {
                  UIManager
                        .setLookAndFeel(new FlatHighContrastIJTheme());
            }catch(UnsupportedLookAndFeelException e)
            {
                  e
                        .printStackTrace();
            }
      }

      public static void main(String[] args)
      {
            // EVENT POOL REGISTRATION
            //
            // Pool_ID "1" -> Default color pipeline pool
            // Pool_ID "2" -> Registration for all UIControllerChildDelegates (shenanigans)
            EventPool
                  .registerEventPool(1);
            EventPool
                  .getPool(1)
                  .registerEvent(
                        GradientEventPayload.class,
                        null
                  );
            EventPool
                  .getPool(1)
                  .registerEvent(
                        ControllerChildDelegatesEventPayload.class,
                        null
                  );
            EventPool
                  .getPool(1)
                  .attachListener(
                        GradientEventPayload.class,
                        ()->System.out
                              .println(
                                    "Event Dispatched: "+EventPool
                                          .getPool(1)
                                          .getPayload(
                                                GradientEventPayload.class
                                          )
                              )

                  );
            UIWindow
                  .make()
                  .withTitle("SoftGradient ~ exoad")
                  .withSize(
                        SharedConstants.WINDOW_WIDTH,
                        SharedConstants.WINDOW_HEIGHT
                  )
                  .withIcon(
                        AssetsService
                              .fetchImageIcon("assets/app-icon.png")
                  )
                  .withMinSize(
                        SharedConstants.WINDOW_WIDTH,
                        SharedConstants.WINDOW_HEIGHT
                  )
                  .withMainDelegate(
                        UIAppMainDelegate.OBJECT
                              .withPadding(
                                    SharedConstants.GRADIENT_WINDOW_PADDING
                              )
                  )
                  .run();
      }
}
