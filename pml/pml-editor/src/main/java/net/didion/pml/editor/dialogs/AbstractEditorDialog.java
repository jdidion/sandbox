package net.didion.pml.editor.dialogs;

import static javax.swing.SwingConstants.*;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.BreakIterator;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;

import net.didion.pml.model.Property;
import net.didion.pml.util.swing.SwingUtils;

public abstract class AbstractEditorDialog extends JDialog {
	public static final int OK = 1;
	public static final int CANCEL = 0;
	
	protected final Property property;
	protected JPanel contentPanel;
	protected SpringLayout layout;
	protected Component northAnchor;
	private int choice;
	
	public AbstractEditorDialog(JFrame parent, Property property) {
		super(parent, true);
		this.property = property;
	}
	
	public abstract void createSingleValueUi(String currentValue);
	
	protected void initUi() {
	    final String name = getPropertyDisplayName();
        
	    setTitle("Editing Property " + name);
		setLayout(new BorderLayout());
		setSize(new Dimension(600, 350));
		setMinimumSize(new Dimension(600, 350));
		
		this.layout = new SpringLayout();
		this.contentPanel = new JPanel(this.layout);
        this.contentPanel.setSize(new Dimension(600, 350));
		this.contentPanel.setMinimumSize(new Dimension(600, 350));
        this.contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		add(this.contentPanel, BorderLayout.CENTER);
		
		final JLabel propertyNameDescription = new JLabel("", LEFT);
		this.contentPanel.add(propertyNameDescription);
		this.layout.putConstraint(SpringLayout.WEST, propertyNameDescription, 0, SpringLayout.WEST, this.contentPanel);
		this.layout.putConstraint(SpringLayout.NORTH, propertyNameDescription, 0, SpringLayout.NORTH, this.contentPanel);
		
		if (null == property.getDescription()) {
		    wrapLabelText(propertyNameDescription, new String[] { name, "<b>", "</b>" });
		}
		else { 
		    wrapLabelText(propertyNameDescription, 
		        new String[] { name + ":", "<b>", "</b>" },
		        new String[] { " " + property.getDescription() });
		}
		
		final String explanationText = getExplanationText();
		if (null == explanationText) {
		    this.northAnchor = propertyNameDescription;
		}
		else {
			final JLabel explanation = new JLabel("", LEFT);
			this.contentPanel.add(explanation);
			this.layout.putConstraint(SpringLayout.WEST, explanation, 0, SpringLayout.WEST, this.contentPanel);
			this.layout.putConstraint(SpringLayout.NORTH, explanation, 5, SpringLayout.SOUTH, propertyNameDescription);
			wrapLabelText(explanation, new String[] { explanationText, "<font color='red'>", "</font>" });
			this.northAnchor = explanation;
		}
		
		final JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		add(buttonPanel, BorderLayout.SOUTH);
		
		buttonPanel.add(Box.createHorizontalGlue());
        
		final JButton ok = new JButton("Ok");
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AbstractEditorDialog.this.choice = OK;
				if (okPressed()) {
					setVisible(false);
				}
			}
		});
		buttonPanel.add(ok, BorderLayout.EAST);
		buttonPanel.add(Box.createRigidArea(new Dimension(5, 0)));
		
		final JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AbstractEditorDialog.this.choice = CANCEL;
				if (cancelPressed()) {
					setVisible(false);
				}
			}
		});
		buttonPanel.add(cancel, BorderLayout.EAST);
		buttonPanel.add(Box.createRigidArea(new Dimension(5, 0)));
	}
	
	/**
	 * Property format some text so it wraps when displayed in a JLabel. The texts
	 * array passed in is two dimensional. Each row contains at least one element -
	 * the text - and optionally contains start and end html tags that should wrap
	 * that text.
	 * 
	 * @param label
	 * @param texts
	 */
	protected void wrapLabelText(JLabel label, String[]... texts) {
	    final FontMetrics fm = label.getFontMetrics(label.getFont());
	    final Container container = label.getParent();
	    int containerWidth = container.getWidth();
	    if (containerWidth == 0) {
	        containerWidth = container.getPreferredSize().width;
	    }
	 
	    final BreakIterator wordBoundaries = BreakIterator.getWordInstance();
	    final StringBuilder rawText = new StringBuilder();
	    final int[] textBoundaries = new int[texts.length];
	    for (int i = 0; i < texts.length; i++) {
	        rawText.append(texts[i][0]);
	        textBoundaries[i] = rawText.length();
	    }
	    wordBoundaries.setText(rawText.toString());
	 
	    final StringBuilder finalText = new StringBuilder("<html>");
	    if (texts[0].length > 1) {
	        finalText.append(texts[0][1]);
	    }
	    StringBuilder trial = new StringBuilder();
        int currentText = 0;
	    
	    for (int start = wordBoundaries.first(), end = Math.min(wordBoundaries.next(), textBoundaries[currentText]); 
	         end != BreakIterator.DONE; 
	         start = end, end = wordBoundaries.next()) {
	        final String word = rawText.substring(start, end);
	        trial.append(word);
	        final int trialWidth = SwingUtilities.computeStringWidth(fm, trial.toString());
	        if (trialWidth > containerWidth) {
	            trial = new StringBuilder(word);
	            finalText.append("<br>");
	        }
	        finalText.append(word);
	        if (end == textBoundaries[currentText]) {
	            if (texts[currentText].length > 1) {
	                finalText.append(texts[currentText][2]);
	            }
	            currentText++;
	        }
	    }
	 
	    finalText.append("</html>");
	 
	    label.setText(finalText.toString());
	}
	
	protected abstract String getExplanationText();
	
	protected void addMinMaxNumValuesText(StringBuilder sb) {
	    final Integer[] minMax = getMinMaxNumValues();
	    final int min = minMax[0];
	    final Integer max = minMax[1];
	    
	    if (null == max) {
	        sb.append(" You must enter at least ")
              .append(min)
              .append(" value");
	        if (min > 1) {
	            sb.append(", seperated by commas.");
	        }
	        else {
	            sb.append(".");
	        }
	    }
	    else if (min == max) {
	        sb.append(" You must enter exactly ")
              .append(max)
              .append(" value");
	        if (min > 1) {
	            sb.append("s, seperated by commas.");    
	        }
	        else {
	            sb.append(".");
	        }
	    }
	    else {
	        sb.append(" You must enter between ")
    	      .append(min)
    	      .append(" and ")
    	      .append(max)
    	      .append(" values, seperated by commas.");
	    }
	}
	
	protected Integer[] getMinMaxNumValues() {
	    final int min = (this.property.getMinNumValues() == null) ? 1 : this.property.getMinNumValues().intValue();
        final Integer max;
        if (this.property.getMaxNumValues() == null) {
            max = min;
        }
        else if (this.property.getMaxNumValues().equals("unbounded")) {
            max = null;
        }
        else {
            max = Integer.valueOf(this.property.getMaxNumValues());
        }
        return new Integer[] { min, max };
    }

    protected void addMinMaxValueText(StringBuilder sb) {
	    if (null != this.property.getMinValue() && null != this.property.getMaxValue()) {
            sb.append(" The value(s) must be between ")
              .append(this.property.getMinValue().getValue());
            addInclusiveExclusive(this.property.getMinValue().isInclusive(), sb);
            sb.append(" and ")
              .append(this.property.getMaxValue().getValue());
            addInclusiveExclusive(this.property.getMaxValue().isInclusive(), sb);
            sb.append(".");
        }
        else if (null != this.property.getMinValue() && null != this.property.getMinValue().getValue()) {
            sb.append(" The value(s) must be greater than")
              .append(this.property.getMinValue().getValue());
            addInclusiveExclusive(this.property.getMinValue().isInclusive(), sb);
            sb.append(".");
        }
        else if (null != this.property.getMaxValue() && null != this.property.getMaxValue().getValue()) {
            sb.append(" The value(s) must be less than")
              .append(this.property.getMaxValue().getValue());
            addInclusiveExclusive(this.property.getMaxValue().isInclusive(), sb);
            sb.append(".");
        }
	}
	
	private void addInclusiveExclusive(boolean inclusive, StringBuilder sb) {
	    if (inclusive) {
            sb.append(" (inclusive)");
        }
        else {
            sb.append(" (exclusive)");
        }
	}
	
	protected String getPropertyDisplayName() {
	    return (null == this.property.getDisplayName()) ? 
            this.property.getName() : this.property.getDisplayName();
	}
	
	protected boolean okPressed() {
		return true;
	}
	
	protected boolean cancelPressed() {
		return true;
	}
	
	public int getChoice() {
		return this.choice;
	}
	
	public abstract String getStringValue();
	
	public int open() {
	    SwingUtils.center(this);
		setVisible(true);
		return getChoice();
	}
}
