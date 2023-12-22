package pkg.exoad.app.events.payloads;

import pkg.exoad.app.ui.UIControllerDelegateChilds;
import pkg.exoad.poprock.core.services.EventPoolService.EventPayload;

import java.util.Collection;

public record ControllerChildDelegatesEventPayload(
	Collection<UIControllerDelegateChilds.UIControllerDelegate> delegates
)
	implements
	EventPayload
{

}
