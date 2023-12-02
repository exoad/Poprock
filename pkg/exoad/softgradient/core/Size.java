package pkg.exoad.softgradient.core;

public final record Size(int width,int height)
{

      public int area()
      {
            return width*height;
      }

      public int perimeter()
      {
            return 2*width+2*height;
      }

      public java.awt.Dimension asAwt()
      {
            return new java.awt.Dimension(
                        width,
                        height
            );
      }
}
