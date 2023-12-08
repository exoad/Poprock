package pkg.exoad.softgradient.core.services.payloads;

import java.util.Collection;

import pkg.exoad.softgradient.core.app.ui.UIControllerDelegateChilds.UIControllerDelegate;
import pkg.exoad.softgradient.core.services.EventPoolService.EventPayload;

public final record ControllerChildDelegatesEventPayload(
   Collection<UIControllerDelegate> delegates
)
                                                        implements
                                                        EventPayload
{

}
