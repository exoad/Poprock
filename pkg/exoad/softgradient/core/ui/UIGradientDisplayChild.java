package pkg.exoad.softgradient.core.ui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

import pkg.exoad.softgradient.core.SharedConstants;
import pkg.exoad.softgradient.core.events.EventPool;
import pkg.exoad.softgradient.core.events.GradientEventPayload;
import pkg.exoad.softgradient.core.services.ColorService;

class UIGradientDisplayChild
                             extends
                             JPanel
{
      public UIGradientDisplayChild()
      {
            EventPool.attachListener(
                        GradientEventPayload.class,
                        this::repaint
            );
      }

      @Override public void paintComponent(Graphics g)
      {
            super.paintComponent(g);
            Graphics2D g2=(Graphics2D)g;
            g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
            );
            if(EventPool.getPayload(GradientEventPayload.class)!=null&&!EventPool.getPayload(GradientEventPayload.class)
                                                                                 .equals(GradientEventPayload.EMPTY))
            {
                  GradientEventPayload e=(GradientEventPayload)EventPool.getPayload(GradientEventPayload.class);
                  g.setColor(
                              e.colors()[0].color()
                                           .asAwt()
                  );
                  g.fillRoundRect(
                              (getWidth()-Math.min(
                                          getWidth(),
                                          getHeight()
                              ))/2,
                              (getHeight()-Math.min(
                                          getWidth(),
                                          getHeight()
                              ))/2,
                              Math.min(
                                          getWidth(),
                                          getHeight()
                              ),
                              Math.min(
                                          getWidth(),
                                          getHeight()
                              ),
                              (int)(Math.min(
                                          getWidth(),
                                          getHeight()
                              )*SharedConstants.ROUND_RECT_ARC),
                              (int)(Math.min(
                                          getWidth(),
                                          getHeight()
                              )*SharedConstants.ROUND_RECT_ARC)
                  );
            }
            else
            {
                  g.setColor(ColorService.hexToColor(SharedConstants.LAF_POPROCK_BG_FG));
                  g.fillRect(
                              0,
                              0,
                              getWidth(),
                              getHeight()
                  );
            }
            g.dispose();
      }
}