package pkg.exoad.poprock.ui.java;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.JTextComponent;

@DefsEqual(
	target="TextField_Type", reference={JTextField.class,JTextArea.class,
										JTextComponent.class,JTextPane.class,
										JPanel.class}
) public class UXTextField
	extends
	JTextField
{

}
