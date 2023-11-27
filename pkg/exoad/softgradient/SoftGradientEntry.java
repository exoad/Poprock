package pkg.exoad.softgradient;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatArcDarkIJTheme;

import pkg.exoad.softgradient.core.AssetsFetcher;
import pkg.exoad.softgradient.core.ColorObj;
import pkg.exoad.softgradient.core.SharedConstants;
import pkg.exoad.softgradient.core.events.EventPool;
import pkg.exoad.softgradient.core.events.GradientEventPayload;
import pkg.exoad.softgradient.core.ui.UIAppMainDelegate;
import pkg.exoad.softgradient.core.ui.UIWindow;

class SoftGradientEntry
{

      static
      {
            System.setProperty(
                        "sun.java2d.opengl",
                        "True"
            );
            System.setProperty(
                        "flatlaf.useWindowDecorations",
                        "true"
            );
            System.setProperty(
                        "flatlaf.menuBarEmbedded",
                        "true"
            );

            UIManager.put(
                        "ScrollBar.background",
                        ColorObj.TRANSPARENT
            );
            UIManager.put(
                        "ScrollBar.showButtons",
                        false
            );
            UIManager.put(
                        "JScrollPane.smoothScrolling",
                        true
            );
            UIManager.put(
                        "SplitPaneDivider.gripDotCount",
                        4
            );
            UIManager.put(
                        "Component.focusedBorderColor",
                        ColorObj.TRANSPARENT
            );
            UIManager.put(
                        "Component.focusColor",
                        ColorObj.TRANSPARENT
            );
            UIManager.put(
                        "TabbedPane.tabSeparatorsFullHeight",
                        false
            );
            UIManager.put(
                        "TabbedPane.showTabSeparators",
                        true
            );

            try
            {
                  UIManager.setLookAndFeel(new FlatArcDarkIJTheme());
            } catch(UnsupportedLookAndFeelException e)
            {
                  e.printStackTrace();
            }
      }

      public static void main(String[] args)
      {
            EventPool.registerEvent(
                        GradientEventPayload.class,
                        null
            );
            EventPool.attachListener(
                        GradientEventPayload.class,
                        ()-> {
                              System.out.println("Event Dispatched");
                        }
            );

            UIWindow.make()
                    .withTitle("SoftGradient ~ exoad")
                    .withSize(
                                SharedConstants.WINDOW_WIDTH,
                                SharedConstants.WINDOW_HEIGHT
                    )
                    .withIcon(AssetsFetcher.fetchImageIcon("assets/app-icon.png"))
                    .withMinSize(
                                SharedConstants.WINDOW_WIDTH,
                                SharedConstants.WINDOW_HEIGHT
                    )
                    .withMainDelegate(new UIAppMainDelegate())
                    .run();
      }
}