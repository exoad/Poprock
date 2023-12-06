package pkg.exoad.softgradient.core.ui.java;

import javax.swing.JButton;
import javax.swing.JPanel;

import pkg.exoad.softgradient.core.ColorObj;

@DefsEqual(
   target="Button_Type", reference={JButton.class,JPanel.class}
) public class UXButton
                        extends
                        JButton
{
   public void setBgColor(ColorObj color)
   {
      super.setBackground(
         color
            .asAwt()
      );
   }

   public void setFgColor(ColorObj color)
   {
      super.setForeground(
         color
            .asAwt()
      );
   }
}
