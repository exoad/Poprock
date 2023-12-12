package pkg.exoad.softgradient.app.events.payloads;

import java.util.Collection;

import pkg.exoad.softgradient.app.ui.UIControllerDelegateChilds.UIControllerDelegate;
import pkg.exoad.softgradient.core.services.EventPoolService.EventPayload;

public final record ControllerChildDelegatesEventPayload(
	Collection<UIControllerDelegate> delegates
)
	implements
	EventPayload
{

}
