package pkg.exoad.softgradient.core.services.mixins;

public interface SelfReportingMixin
{
      default String getNamedThis()
      {
            return this.getClass()
                       .getName()+"["+hashCode()+"]";
      }

      default String getCanonicallyNamedThis()
      {
            return this.getClass()
                       .getCanonicalName()+"["+hashCode()+"]";
      }
}
