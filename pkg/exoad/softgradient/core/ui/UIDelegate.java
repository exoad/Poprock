package pkg.exoad.softgradient.core.ui;

import javax.swing.JComponent;

import java.awt.Dimension;

import javax.swing.BorderFactory;

public class UIDelegate< T extends JComponent >
{
      protected T rootDelegate;

      public final T asComponent()
      {
            assert rootDelegate!=null;
            return rootDelegate;
      }

      public UIDelegate< T > withPadding(int top,int left,int bottom,int right)
      {
            rootDelegate.setBorder(
                        BorderFactory.createEmptyBorder(
                                    top,
                                    left,
                                    bottom,
                                    right
                        )
            );
            return this;
      }

      public UIDelegate< T > withPadding(int padding)
      {
            return withPadding(
                        padding,
                        padding,
                        padding,
                        padding
            );
      }

      public UIDelegate< T > withPreferredSize(int width,int height)
      {
            rootDelegate.setPreferredSize(
                        new Dimension(
                                    width,
                                    height
                        )
            );
            return this;
      }
}
