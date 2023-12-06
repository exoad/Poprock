package pkg.exoad.softgradient.core.ui.java;

import javax.swing.JComponent;
import javax.swing.JLabel;

import pkg.exoad.softgradient.core.ColorObj;
import pkg.exoad.softgradient.core.services.mixins.DebuggableMixin;

@DefsEqual(
   target="Label_Type", reference={JLabel.class,JComponent.class}
) public class UXLabel
                       extends
                       JLabel
                       implements
                       DebuggableMixin
{
   public UXLabel(String rootedContent)
   {
      super(rootedContent);
   }

   public void setFgColor(ColorObj colorObj)
   {
      setForeground(
         colorObj
            .asAwt()
      );
   }
}
