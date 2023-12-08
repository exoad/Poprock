package pkg.exoad.softgradient.core;

public class Shared<T>
{
   public static <E> Shared<E> of(E value)
   {
      return new Shared<>(value);
   }

   T obj;

   public Shared(T e)
   {
      this.obj=e;
   }

   public void setValue(T e)
   { this.obj=e; }

   public T getValue()
   { return obj; }

   public static <V extends Shared<?>> boolean computeEquality(
      V a,V b
   )
   {
      return a
         .getValue()==null||b
            .getValue()==null?false:a
               .getValue()
               .equals(
                  b
                     .getValue()
               );
   }

   @Override public boolean equals(Object r)
   {
      return r instanceof Shared<?> r1?Shared
         .computeEquality(this,r1):false;
   }
}
