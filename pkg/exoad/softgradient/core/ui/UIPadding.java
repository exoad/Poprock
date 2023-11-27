package pkg.exoad.softgradient.core.ui;

import javax.swing.*;
import java.awt.*;

public final class UIPadding
                             extends
                             UIDelegate< JPanel >
{
      public static UIPadding wrapAllSides(JComponent panel,int padding)
      {
            return new UIPadding(
                        panel,
                        padding,
                        padding,
                        padding,
                        padding
            );
      }

      public static UIPadding wrap(JComponent panel,int top,int left,int bottom,int right)
      {
            return new UIPadding(
                        panel,
                        top,
                        left,
                        bottom,
                        right
            );
      }

      private UIPadding(JComponent panel,int top,int left,int bottom,int right)
      {
            rootDelegate=new JPanel();
            rootDelegate.setBorder(
                        BorderFactory.createEmptyBorder(
                                    top,
                                    left,
                                    bottom,
                                    right
                        )
            );
            rootDelegate.setLayout(new BorderLayout());
            rootDelegate.add(
                        panel,
                        BorderLayout.CENTER
            );
      }
}