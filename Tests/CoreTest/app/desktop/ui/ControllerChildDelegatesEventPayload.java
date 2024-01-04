package app.desktop.ui;

import pkg.exoad.poprock.core.EventPoolService.EventPayload;

import java.util.Collection;

public record ControllerChildDelegatesEventPayload(
	Collection<UIControllerDelegateChilds.UIControllerDelegate> delegates
)
	implements
	EventPayload
{

}
