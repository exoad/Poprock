package pkg.exoad.softgradient.core.debug;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

public class UIDebugPanelFakeDelegate
                                  extends
                                  JPanel
{
      private Color color;

      public UIDebugPanelFakeDelegate(Color c)
      {
            this.color=c;
      }

      @Override public void paintComponent(Graphics g)
      {
            super.paintComponent(g);
            g.setColor(color);
            g.fillRect(
                        0,
                        0,
                        getWidth(),
                        getHeight()
            );
            g.dispose();
      }
}
