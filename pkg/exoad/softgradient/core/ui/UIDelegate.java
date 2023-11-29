package pkg.exoad.softgradient.core.ui;

import javax.swing.JComponent;
import javax.swing.border.Border;

import java.awt.Dimension;
import java.awt.Component;

import javax.swing.BorderFactory;

public class UIDelegate< T extends JComponent >
{
      protected T rootDelegate;

      public final T asComponent()
      {
            assert rootDelegate!=null;
            return rootDelegate;
      }

      public enum Alignment
      {
            TOP(Component.TOP_ALIGNMENT),

            CENTER(Component.CENTER_ALIGNMENT),

            RIGHT(Component.RIGHT_ALIGNMENT),

            LEFT(Component.LEFT_ALIGNMENT);

            final float axis;

            Alignment(float x)
            {
                  this.axis=x;
            }
      }

      protected void setRootDelegate(T e)
      {
            rootDelegate=e;
      }

      public UIDelegate< T > withAlignmentX(Alignment alignment)
      {
            rootDelegate.setAlignmentX(alignment.axis);
            return this;
      }

      public UIDelegate< T > withAlignmentY(Alignment alignment)
      {
            rootDelegate.setAlignmentY(alignment.axis);
            return this;
      }

      public UIDelegate< T > withBorder(Border border)
      {
            rootDelegate.setBorder(border);
            return this;
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
