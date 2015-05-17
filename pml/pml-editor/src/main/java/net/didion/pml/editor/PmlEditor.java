package net.didion.pml.editor;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.xml.bind.JAXBException;

import net.didion.pml.PropertyModel;
import net.didion.pml.PropertyModelAdapter;
import net.didion.pml.model.PmlUtils;
import net.didion.pml.model.Property;
import net.didion.pml.model.PropertyDescriptor;
import net.didion.pml.util.properties.Properties;
import net.didion.pml.util.swing.SwingUtils;
import net.didion.pml.values.PmlValuesUtils;
import net.didion.pml.values.PropertyValueDescriptor;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

/**
 * Launches the default PML editor window.
 * 
 * @author John Didion
 */
public final class PmlEditor extends JFrame {
	/**
	 * Main loop. 
	 * 
	 * @param args	-f pml file (required)
	 * 				-i input property file or property file set descriptor
	 * 			   	-o output directory
	 * 				-x use XML property file format
	 * 				-h show help
	 */
	public static void main(String[] args) {
		final Options options = new Options();
		options.addOption("f", true, "The PML file. Required.");
		options.addOption("p", true, "The property file to edit.");
		options.addOption("d", true, "The property file set descriptor to edit.");
		options.addOption("o", true, "The output directory. Defaults to the working directory if -p is not specified.");
		options.addOption("x", false, "Read/write XML property files");
		options.addOption("h", true, "Print application help.");
		
		final CommandLineParser parser = new PosixParser();
		CommandLine cl = null;
		try {
			cl = parser.parse(options, args);
		}
		catch (ParseException ex) {
			printHelp(options);
			System.exit(1);
		}
		
		if (cl.hasOption("h")) {
			printHelp(options);
			System.exit(0);
		}
		if (!cl.hasOption('f')) {
			printHelp(options);
			System.exit(2);
		}
		
		PropertyDescriptor pd = null;
		try {
			pd = PmlUtils.parse(new File(cl.getOptionValue('f')));	
		}
		catch (JAXBException ex) {
			printError("Error parsing PML file", ex);
			System.exit(3);
		}
		
		final String propertyFilePath = cl.hasOption('p') ? cl.getOptionValue('p') : null;
		final File propertyFile = (null == propertyFilePath) ? null : new File(propertyFilePath);
		final String propertyFileName;
		
		final String outputDirPath = cl.hasOption('o') ? cl.getOptionValue('o') : null;
        final File outputDir;
		
        if (null == propertyFilePath) {
		    propertyFileName = null;
		    outputDir = new File("");
		}
		else {
    		File tempFile = new File(propertyFilePath);
    		File tempDir = null;
    		if (tempFile.isAbsolute()) {
    		    propertyFileName = tempFile.getName();
    		    tempDir = tempFile.getParentFile();
    		}
    		else {
    		    propertyFileName = propertyFilePath;
    		}
    		outputDir = (null != outputDirPath) ? new File(outputDirPath) : 
                (tempDir == null) ? new File(".") : tempDir;
		}
        
        final String descriptorFilePath = cl.hasOption('d') ? cl.getOptionValue('d') : null;
        final File descriptorFile = (null == descriptorFilePath) ? null : new File(descriptorFilePath);
        
        final boolean xml = cl.hasOption('x');
        
		PropertyModel model = null;
		
		if (null != descriptorFile && descriptorFile.exists()) {
		    PropertyValueDescriptor pvd;
		    try {
		        pvd = PmlValuesUtils.parse(descriptorFile);
		    }
		    catch (JAXBException ex) {
		        printError("Error reading property descriptor file", ex);
		        System.exit(4);
		    }
		    
		    model = new PropertyModel(pd, pvd);
		}
		else if (null != propertyFile && propertyFile.exists()) {
			FileInputStream in = null;
			try {
				in = new FileInputStream(propertyFile);
			
				final Properties<String> properties = Properties.getStringInstance();
				if (xml) {
					properties.loadFromXml(in);
				}
				else {
					properties.load(in);
				}
				
				model = new PropertyModel(pd, properties);
			}
			catch (InvalidPropertiesFormatException ex) {
				printError("Incorrectly formatted property file", ex);
				System.exit(5);
			}
			catch (IOException ex) {
				printError("Error reading properties file", ex);
				System.exit(6);
			}
			finally {
				try {
					in.close();
				}
				catch (IOException ex) {
				}
			}
		}
		else {
		    model = new PropertyModel(pd);
		}
		
		final PmlEditor editor = new PmlEditor(model, propertyFileName, outputDir, xml);
        SwingUtils.center(editor);
        editor.setVisible(true);
	}
	
	public PmlEditor(final PropertyModel model, final String propertyFileName, final File outputDir, final boolean xml) {
		super(model.getPropertyDescriptor().getName() + " Property Editor");
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		
		final JTabbedPane tabs = new JTabbedPane();
		
		final ConstantsPanel constants = new ConstantsPanel(this, model);
		tabs.addTab("Constants", constants);
		
		final VariablesPanel variables = new VariablesPanel(this, model);
		tabs.addTab("Variables", variables);
		
		getContentPane().add(tabs);
		
		model.addListener(new PropertyModelAdapter() {
            @Override
            public void variablePropertyAdded(Property property, String oldValue, List<String> newValue) {
                tabs.setSelectedIndex(1);
            }
        });
		
		final JPanel buttons = new JPanel();
		buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
		buttons.add(Box.createHorizontalGlue());
		
		final JButton save = new JButton("Save");
		save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final JFileChooser fc = new JFileChooser(outputDir);
                if (JFileChooser.APPROVE_OPTION == fc.showSaveDialog(PmlEditor.this)) {
                    try {
                        model.save(fc.getSelectedFile());
                    }
                    catch (JAXBException ex) {
                        JOptionPane.showMessageDialog(PmlEditor.this,
                            "There was an error saving PML state: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
		});
		
		final JButton generate = new JButton("Generate");
		generate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    final boolean multiFile = model.hasVariables();
			    String fileName = propertyFileName;
			    if (multiFile && null != fileName) {
			        int index = fileName.lastIndexOf('.');
			        if (index >= 0) {
			            fileName = fileName.substring(0, index);
			        }
			    }
				final GenerateDialog save = new GenerateDialog(PmlEditor.this, outputDir, fileName, multiFile, xml);
			    if (save.open() == GenerateDialog.SAVE) {
    			    try {
    			        model.generate(save.getDir(), save.getFile(), save.getCreateDirectories(), save.getUseXmlFormat());
    			    }
    			    catch (IOException ex) {
    			        JOptionPane.showMessageDialog(PmlEditor.this,
    			            "There was an error saving one or more property files: " + ex.getMessage(),
    			            "Error", JOptionPane.ERROR_MESSAGE);
    			    }
			    }
			}
		});
		buttons.add(generate);	
		buttons.add(Box.createRigidArea(new Dimension(5, 0)));
		
		final JButton quit = new JButton("Quit");
		quit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO: check if model is dirty, prompt for save
				setVisible(false);
				dispose();
			}
		});
		buttons.add(quit);
		buttons.add(Box.createRigidArea(new Dimension(5, 0)));
		
		getContentPane().add(buttons);
		
		addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent event) {
				System.exit(0);
			}
		});
		
		setSize(new Dimension(500, 300));
	}
	
	private static void printHelp(Options options) {
		new HelpFormatter().printHelp("java net.didion.pml.editor.PmlEditor", options);
	}
	
	private static void printError(String msg, Throwable t) {
		System.err.println(msg);
		t.printStackTrace();
	}
}
