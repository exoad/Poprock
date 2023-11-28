package pkg.exoad.softgradient.core.events;

import java.util.Arrays;

import pkg.exoad.softgradient.core.ColorObj;
import pkg.exoad.softgradient.core.GradientColor;

public final record GradientEventPayload(GradientColor[] colors,float startX,float startY,float endX,float endY)
                                        implements
                                        EventPayload
{

      public static final GradientEventPayload EMPTY=new GradientEventPayload(
                  new GradientColor[0],
                  -1,
                  -1,
                  -1,
                  -1
      );

      public static GradientEventPayload makeRandomColor()
      {
            return new GradientEventPayload(
                        new GradientColor[] {new GradientColor(
                                    ColorObj.randomColorObj(),
                                    1f
                        )},
                        0,
                        0,
                        1f,
                        1f
            );
      }

      @Override public boolean equals(Object obj)
      {
            if(this==obj) return true;
            if(obj==null||getClass()!=obj.getClass()) return false;
            GradientEventPayload other=(GradientEventPayload)obj;
            if(colors!=null
                        ? !Arrays.equals(
                                    colors,
                                    other.colors
                        )
                        : other.colors!=null) return false;
            if(Float.floatToIntBits(endX)!=Float.floatToIntBits(other.endX)) return false;
            if(Float.floatToIntBits(endY)!=Float.floatToIntBits(other.endY)) return false;
            if(Float.floatToIntBits(startX)!=Float.floatToIntBits(other.startX)) return false;
            return Float.floatToIntBits(startY)==Float.floatToIntBits(other.startY);
      }

      @Override public int hashCode()
      {
            final int prime=31;
            int result=1;
            result=prime*result+((colors==null)
                        ? 0
                        : colors.hashCode());
            result=prime*result+Float.floatToIntBits(endX);
            result=prime*result+Float.floatToIntBits(endY);
            result=prime*result+Float.floatToIntBits(startX);
            result=prime*result+Float.floatToIntBits(startY);
            return result;
      }

      @Override public String toString()
      {
            return "GradientEventPayload [colors="+Arrays.toString(colors)+", startX="+startX+", startY="+startY
                        +", endX="+endX+", endY="
                        +endY+"]";
      }
}
