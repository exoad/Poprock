package pkg.exoad.softgradient.core.events;

import java.util.Arrays;

import pkg.exoad.softgradient.core.ColorObj;
import pkg.exoad.softgradient.core.GradientColor;

/**
 * An identifier payload for deploying Gradient Events to the SoftGradient
 * application. It contains information for the SoftGradient app to process at
 * runtime. Thus, you should only listen to this payload type from the EventPool
 * if you are using the SoftGradient app as well.
 *
 * <p>
 * <strong>Listening to EventPool</strong>
 * </p>
 *
 * <blockquote><pre>
 * import pkg.exoad.softgradient.core.events.*;
 * //...
 * EventPool.attachListener(GradientEventPayload.class, () ->
 * System.out.println("It got dispatched!"));
 * </pre></blockquote>
 *
 * <em>Please note that the above also requires the
 * {@code GradientEventPayload.class} to actually be registered to the EventPool
 * or else the EventPool will throw an error.</em>
 *
 * <p>
 * <strong>Dispatching to EventPool</strong>
 * <br/>
 * <p>
 * Highly unsuggested as messing with the pre-exsiting ephemeral dispatching of
 * this payload
 * class within the SoftGradient app can result in this Payload being ignored,
 * overwritten, corrupted, or just causing errors because of synchronization
 * errors (race conditions, among others).
 *
 * <blockquote><pre>
 * import pkg.exoad.softgradient.core.events.*;
 * //...
 * EventPool.dispatchEvent(GradientEventPayload.class,GradientEventPayload.EMPTY);
 * </pre></blockquote>
 *
 * @see #EMPTY
 * @see pkg.exoad.softgradient.core.events.EventPool
 *
 * @author Jack Meng
 */
public final record GradientEventPayload(
      GradientColor[] colors,float startX,float startY,float endX,float endY
)
                                        implements
                                        EventPayload // eventpayload interface used as a marker interface for showing to the EventPool that it is a valid payload
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
                  new GradientColor[]{new GradientColor(
                        ColorObj
                              .randomColorObj(),
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
            if(obj==null||getClass()!=obj
                  .getClass()) return false;
            GradientEventPayload other=(GradientEventPayload)obj;
            if(colors!=null?!Arrays
                  .equals(
                        colors,
                        other.colors
                  ):other.colors!=null) return false;
            if(Float
                  .floatToIntBits(endX)!=Float
                        .floatToIntBits(other.endX)) return false;
            if(Float
                  .floatToIntBits(endY)!=Float
                        .floatToIntBits(other.endY)) return false;
            if(Float
                  .floatToIntBits(startX)!=Float
                        .floatToIntBits(other.startX)) return false;
            return Float
                  .floatToIntBits(startY)==Float
                        .floatToIntBits(other.startY);
      }

      @Override public int hashCode()
      {
            final int prime=31;
            int result=1;
            result=prime*result+((colors==null)?0:colors
                  .hashCode());
            result=prime*result+Float
                  .floatToIntBits(endX);
            result=prime*result+Float
                  .floatToIntBits(endY);
            result=prime*result+Float
                  .floatToIntBits(startX);
            result=prime*result+Float
                  .floatToIntBits(startY);
            return result;
      }

      @Override public String toString()
      {
            return "GradientEventPayload [colors="+Arrays
                  .toString(
                        colors
                  )+", startX="+startX+", startY="+startY+", endX="+endX+", endY="+endY+"]";
      }
}
