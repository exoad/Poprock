package pkg.exoad.softgradient.core;

public final record Offset(float dx,float dy)
{
      public static Offset make(float dx,float dy)
      {
            return new Offset(
                        dx,
                        dy
            );
      }

      public static final Offset ZERO=new Offset(
                  0,
                  0
      );

      public float distanceOf(Offset another)
      {
            return (float)Math.hypot(
                        another.dx-dx,
                        another.dy-dy
            );
      }

      public int x()
      {
            return (int)dx;
      }

      public int y()
      {
            return (int)dy;
      }

      public java.awt.Point asAwt()
      {
            return new java.awt.Point(
                        x(),
                        y()
            );
      }
}
