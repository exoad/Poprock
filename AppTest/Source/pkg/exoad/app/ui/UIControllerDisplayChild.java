package pkg.exoad.app.ui;

import pkg.exoad.app.SharedConstants;
import pkg.exoad.app.events.payloads.ControllerChildDelegatesEventPayload;
import pkg.exoad.app.events.payloads.GradientEventPayload;
import pkg.exoad.poprock.Offset;
import pkg.exoad.poprock.PublicConstants;
import pkg.exoad.poprock.ThemeConstants;
import pkg.exoad.poprock.services.BasicService;
import pkg.exoad.poprock.services.ColorService;
import pkg.exoad.poprock.services.DebugService;
import pkg.exoad.poprock.services.EventPoolService;
import pkg.exoad.poprock.ui.*;
import pkg.exoad.poprock.ui.UIDelegate.Alignment;
import pkg.exoad.poprock.ui.UIPanelDelegate.BoxLayoutAlignment;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

class UIControllerDisplayChild
	extends
	JPanel
{
	private final JPanel blocksPanel;
	private final JScrollPane scrollPane;
	private final JPanel bottomButtonsPanel;
	public UIControllerDisplayChild()
	{
		// init all listeners for the eventpool registry["1"]
		EventPoolService
			.getPool(1)
			.attachListener(
				ControllerChildDelegatesEventPayload.class,
				()->{
					EventPoolService
						.getPool(1)
						.getPayload(
							ControllerChildDelegatesEventPayload.class
						);
				}
			);
		blocksPanel=UIPanelDelegate
			.make()
			.withBoxLayout(BoxLayoutAlignment.Y_AXIS)
			.asComponent();
		setBorder(
			BorderFactory
				.createEmptyBorder()
		);
		scrollPane=new JScrollPane(blocksPanel);
		scrollPane
			.setViewportBorder(
				UIHelper
					.makeLinedBorder(
						ColorService
							.hexToColor(
								ThemeConstants.LAF_POPROCK_PRIMARY_2
							)
							.asAwt()
					)
			);
		scrollPane
			.getVerticalScrollBar()
			.setUnitIncrement(
				SharedConstants.CONTROLLER_SCROLLBAR_UNIT_INCREMENT
			);
		scrollPane
			.setVerticalScrollBarPolicy(
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS
			);
		scrollPane
			.setBorder(
				BorderFactory
					.createEmptyBorder()
			);
		setLayout(new BorderLayout());
		add(
			scrollPane,
			BorderLayout.CENTER
		);
		bottomButtonsPanel=UIPanelDelegate
			.make()
			.withLayout(
				new FlowLayout(
					FlowLayout.RIGHT,
					6,
					0
				)
			)
			.withComponent(
				UIBuilderDelegate
					.make(()->{
						UIButtonDelegate button=UIButtonDelegate
							.make()
							.withText("New")
							.withBackgroundColor(
								ColorService
									.hexToColor(
										ThemeConstants.LAF_POPROCK_PRIMARY_2
									)
							)
							.withAction(
								()->EventPoolService
									.getPool(
										1
									)
									.dispatchEvent(
										GradientEventPayload.class,
										GradientEventPayload.EMPTY
									)
							);
						UIDelegate<JPopupMenu> popupMenu=UIHelper
							.makePopupMenu(
								new UIPopupItemChilds.PopupConfiguration(
									"Add New Logic",
									true,
									button
										.asComponent()
								),
								new UIPopupItemChilds.SimplePopupDelegate(
									"Test#1",
									BasicService
										.emptyRunnable()
								),
								new UIPopupItemChilds.SimplePopupDelegate(
									"Test#2",
									BasicService
										.emptyRunnable()
								)
							);
						button
							.withAction(
								()->UIPopupItemChilds
									.showPopupMenuDelegate(
										popupMenu,
										Offset.ZERO
									)
							);
						return button;
					})
			
			)
			.withComponentIf(
				PublicConstants.DEV_MODE,
				UIButtonDelegate
					.make()
					.withText("TEST")
					.withAction(()->insertNewBlock(
						Objects.requireNonNull(
							InnerControllerBlock
								.make(
									new UIControllerDelegateChilds.WindowSetupChildBlock()
										.withBorder(
											UIHelper
												.makeLinedBorder(
													Color.white
												)
										)
								))
					))
			)
			.withComponentIf(
				PublicConstants.DEV_MODE,
				UIButtonDelegate
					.make()
					.withText(
						"RandomColor"
					)
					.withAction(
						()->EventPoolService
							.getPool(1)
							.dispatchEvent(
								GradientEventPayload.class,
								GradientEventPayload
									.makeRandomColor()
							)
					)
					.withBackgroundColor(
						ColorService
							.hexToColor(
								ThemeConstants.LAF_POPROCK_PRIMARY_2
							)
					)
			)
			.withTransparency(true)
			.asComponent();
		add(
			bottomButtonsPanel,
			BorderLayout.SOUTH
		);
		
	}

	private void insertNewBlock(InnerControllerBlock block)
	{
		block
			.setAlignmentX(Component.CENTER_ALIGNMENT);
		block
			.setAlignmentY(Component.TOP_ALIGNMENT);
		block
			.setMaximumSize(
				new java.awt.Dimension(
					Integer.MAX_VALUE,
					block
						.getPreferredSize().height
				)
			);
		blocksPanel
			.add(block);
		scrollPane
			.revalidate();
		scrollPane
			.repaint();
	}
	
	private static final class InnerControllerBlock
		extends
		JPanel
	
	{
		private InnerControllerBlock(
			String name,UIBasicDelegate<?> delegate
		)
		{
			setBorder(
				UIHelper
					.makeEmptyBorder()
			);
			setLayout(new BorderLayout());
			add(
				UIPanelDelegate
					.make()
					.withBoxLayout(BoxLayoutAlignment.Y_AXIS)
					.withLatePaintDelegate(
						g->{
							g
								.setRenderingHint(
									RenderingHints.KEY_ANTIALIASING,
									RenderingHints.VALUE_ANTIALIAS_ON
								);
							g
								.setColor(
									ColorService
										.hexToColor(
											ThemeConstants.LAF_POPROCK_PRIMARY_1
										)
										.asAwt()
								);
							g
								.fillRoundRect(
									SharedConstants.CONTROLLER_BLOCKS_PADDING,
									SharedConstants.CONTROLLER_BLOCKS_PADDING,
									getWidth()-SharedConstants.CONTROLLER_BLOCKS_PADDING,
									getHeight()-SharedConstants.CONTROLLER_BLOCKS_PADDING,
									6,
									6
								
								);
						}
					)
					.withComponent(
						UILabelDelegate
							.make(name)
							.withForegroundColor(
								ColorService
									.hexToColor(
										ThemeConstants.LAF_POPROCK_BG_FG
									)
							)
							.withPadding(4)
							.withTransparency(true)
							.withAlignmentY(Alignment.LEFT)
					)
					.withComponent(delegate)
					.withPadding(
						SharedConstants.CONTROLLER_BLOCKS_PADDING-2,
						SharedConstants.CONTROLLER_BLOCKS_PADDING+5,
						SharedConstants.CONTROLLER_BLOCKS_PADDING-5,
						SharedConstants.CONTROLLER_BLOCKS_PADDING+5
					)
					.asComponent(),
				BorderLayout.CENTER
			);
		}
		
		public static InnerControllerBlock make(UIDelegate<?> delegate)
		{
			if(delegate instanceof UIControllerDelegateChilds.UIControllerDelegate e)
				return new InnerControllerBlock(
					e
						.getHeaderName(),
					e
				);
			DebugService
				.throwNow(
					"delegate for 'make' of InnerControllerBlock received a none instance of UIControllerDelegate!"
				);
			return null;
		}
	}
}