package pkg.exoad.softgradient.core.ui;

import javax.swing.JComponent;
import javax.swing.border.Border;

import pkg.exoad.softgradient.core.ColorObj;
import pkg.exoad.softgradient.core.Offset;
import pkg.exoad.softgradient.core.Size;
import pkg.exoad.softgradient.core.services.AwtMixerService;

import java.awt.Dimension;
import java.awt.Component;

import javax.swing.BorderFactory;

public class UIDelegate< T extends JComponent >
                       implements
                       UIBasicDelegate< T >
{
      public final record DelegateProperties(
                  Size prefSize,Size size,Size minSize,Size maxSize,Offset origin,boolean visibility
      )
      {

      }

      public static < A extends JComponent > UIDelegate< A > makeWith(A component)
      {
            UIDelegate< A > delegate=new UIDelegate<>();
            delegate.setRootDelegate(component);
            return delegate;
      }

      public interface UIStrutContainedDelegate< T >
      {
            T withStrut(int strut);
      }

      protected T rootDelegate;

      @Override public T asComponent()
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

      public DelegateProperties exposeProperties()
      {
            return new DelegateProperties(
                        AwtMixerService.convertDimensionClass(rootDelegate.getPreferredSize()),
                        AwtMixerService.convertDimensionClass(rootDelegate.getSize()),
                        AwtMixerService.convertDimensionClass(rootDelegate.getMinimumSize()),
                        AwtMixerService.convertDimensionClass(rootDelegate.getMaximumSize()),
                        AwtMixerService.convertPointClass(rootDelegate.getLocation()),
                        rootDelegate.isVisible()
            );
      }

      protected void setRootDelegate(T e)
      {
            rootDelegate=e;
      }

      public void setVisibility(boolean visibility)
      {
            rootDelegate.setVisible(visibility);
      }

      public UIDelegate< T > withVisibility(boolean visibility)
      {
            setVisibility(visibility);
            return this;
      }

      public UIDelegate< T > withTransparency(boolean transparency)
      {
            if(transparency)
                  rootDelegate.setBackground(ColorObj.TRANSPARENT.asAwt());
            rootDelegate.setOpaque(transparency);
            return this;
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
