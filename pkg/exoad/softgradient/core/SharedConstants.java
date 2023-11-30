package pkg.exoad.softgradient.core;

import java.util.Random;

public final class SharedConstants
{
      private SharedConstants()
      {
      }

      public static final boolean DEV_MODE=true;
      public static final boolean LAYOUT_DEBUG_MODE=true;

      public static final int WINDOW_WIDTH=875;
      public static final int WINDOW_HEIGHT=550;

      public static final int GRADIENT_WINDOW_PADDING=10;
      public static final float ROUND_RECT_ARC=0.02f;
      public static final int CONTROLLER_SCROLLBAR_UNIT_INCREMENT=16;
      public static final int CONTROLLER_BLOCKS_PADDING=14;

      public static final String LAF_POPROCK_PRIMARY_1="#ff2667";
      public static final String LAF_POPROCK_PRIMARY_2="#17ff6c";
      public static final String LAF_POPROCK_PRIMARY_3="#fab916";
      public static final String LAF_POPROCK_BG_FG="#000000";

      public static final Random RNG=new Random();
}
