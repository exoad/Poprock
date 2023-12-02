package pkg.exoad.softgradient.core.ui;

import java.util.function.Supplier;

import javax.swing.JComponent;

public class UIBuilderDelegate<T extends JComponent>
                              extends
                              UIDelegate<T>
{
      public static <B extends JComponent> UIBuilderDelegate<B> make(Supplier<UIDelegate<B>> component)
      {
            return new UIBuilderDelegate<>(component);
      }

      private Supplier<UIDelegate<T>> builder;

      private UIBuilderDelegate(Supplier<UIDelegate<T>> builder)
      {
            this.builder=builder;
      }

      @Override public T asComponent()
      {
            return builder
                  .get()
                  .asComponent();
      }
}
