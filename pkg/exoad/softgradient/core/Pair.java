package pkg.exoad.softgradient.core;

public final class Pair< A,B >
{
      private A first;
      private B second;

      public Pair(A first,B second)
      {
            this.first=first;
            this.second=second;
      }

      public A first()
      {
            return first;
      }

      public B second()
      {
            return second;
      }

      public void first(A first)
      {
            this.first=first;
      }

      public void second(B second)
      {
            this.second=second;
      }

}
