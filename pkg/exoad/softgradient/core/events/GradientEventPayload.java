package pkg.exoad.softgradient.core.events;

import pkg.exoad.softgradient.core.GradientColor;

public final record GradientEventPayload(GradientColor[] colors)
                                        implements
                                        EventPayload
{

}
