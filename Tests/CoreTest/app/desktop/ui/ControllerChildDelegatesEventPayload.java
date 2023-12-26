package app.desktop.ui;

import pkg.exoad.poprock.core.services.EventPoolService.EventPayload;

import java.util.Collection;

public record ControllerChildDelegatesEventPayload(
	Collection<UIControllerDelegateChilds.UIControllerDelegate> delegates
)
	implements
	EventPayload
{

}
