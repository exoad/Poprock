package pkg.exoad.softgradient.core.events;

import java.util.Collection;

import pkg.exoad.softgradient.core.ui.UIControllerDelegateChilds.UIControllerDelegate;

public final record ControllerChildDelegatesEventPayload(Collection< UIControllerDelegate > delegates)
                                                        implements
                                                        EventPayload
{

}
