package net.exoad.annotations.poprock.desktop.ui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.function.Consumer;

public class UITextFieldDelegate
	extends
	UIDelegate<JTextField>
{
	private UITextFieldDelegate()
	{
		rootDelegate=new JTextField();
		withPadding(
			2,
			6,
			2,
			2
		);
	}
	
	public static UITextFieldDelegate make()
	{
		return new UITextFieldDelegate();
	}
	
	public UITextFieldDelegate withColumns(int columns)
	{
		assert columns>=0;
		rootDelegate
			.setColumns(columns);
		return this;
	}
	
	public UITextFieldDelegate withText(String initialText)
	{
		rootDelegate
			.setText(initialText);
		return this;
	}
	
	@Override public UITextFieldDelegate withBorder(Border border)
	{
		rootDelegate
			.setBorder(border);
		return this;
	}
	
	public UITextFieldDelegate withForegroundColor(Color color)
	{
		rootDelegate
			.setForeground(color);
		return this;
	}
	
	public UITextFieldDelegate withChangeListener(Consumer<String> listener)
	{
		rootDelegate
			.addActionListener(
				e->listener
					.accept(
						rootDelegate
							.getText()
					)
			);
		return this;
	}
	
	public UITextFieldDelegate withUsable(boolean usable)
	{
		rootDelegate
			.setEnabled(usable);
		return this;
	}
}
