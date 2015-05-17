package net.didion.pml.editor;

import static net.didion.pml.util.StringUtils.*;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.didion.pml.util.swing.SwingUtils;
import net.miginfocom.swing.MigLayout;

class GenerateDialog extends JDialog {
    public static final int SAVE = 0;
    public static final int CANCEL = 1;
    
    private File dir;
    private String file;
    private boolean createDirectories;
    private boolean useXmlFormat;
    private int choice = -1;
    
    private JTextField browseField;
    private JTextField templateField;
    private JCheckBox useXmlFormatCheck;
    private JCheckBox createDirectoriesCheck;
    
    public GenerateDialog(JFrame frame, File outputDir, String fileName, final boolean multiFile, boolean useXmlFormat) {
        super(frame, "Generate Property Files", true);
        
        setLayout(new BorderLayout());
        setSize(new Dimension(600,200));
        setMinimumSize(new Dimension(600,200));
        SwingUtils.center(this);
        
        final JPanel centerPanel = new JPanel(new MigLayout());
        add(centerPanel, BorderLayout.CENTER);
        
        if (multiFile) {
            createMultiFileUi(centerPanel, outputDir, fileName, useXmlFormat);
        }
        else {
            createSingleFileUi(centerPanel, outputDir, fileName, useXmlFormat);
        }
                
        final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        add(buttonPanel, BorderLayout.SOUTH);
        
        final JButton save = new JButton("Generate");
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final String path = GenerateDialog.this.browseField.getText();
                if (null == path || path.trim().length() == 0) {
                    JOptionPane.showMessageDialog(GenerateDialog.this, "You must select a " + (multiFile ? "directory." : "file."), 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
                if (multiFile) {
                    final String template = GenerateDialog.this.templateField.getText();
                    if (null == template || template.trim().length() == 0) {
                        JOptionPane.showMessageDialog(GenerateDialog.this, "You must enter a file name template", "Error", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                    GenerateDialog.this.dir = new File(path);
                    GenerateDialog.this.file = template;
                    GenerateDialog.this.createDirectories = GenerateDialog.this.createDirectoriesCheck.isSelected();
                }
                else {
                    final File file = new File(path);
                    GenerateDialog.this.dir = file.getParentFile();
                    GenerateDialog.this.file = file.getName();
                }
                GenerateDialog.this.useXmlFormat = GenerateDialog.this.useXmlFormatCheck.isSelected();
                GenerateDialog.this.choice = SAVE;
                GenerateDialog.this.setVisible(false);
            }
        });
        buttonPanel.add(save);
        
        final JButton cancel = new JButton("Cancel");
        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GenerateDialog.this.choice = CANCEL;
                GenerateDialog.this.setVisible(false);
            }
        });
        buttonPanel.add(cancel);
    }
    
    private void createSingleFileUi(JPanel panel, File outputDir, String fileName, boolean useXmlFormat) {
        final File defaultFile = (null == outputDir || null == fileName) ? null : new File(outputDir, fileName);
        
        panel.add(new JLabel("File:"));
        
        this.browseField = new JTextField(40);
        if (defaultFile != null) {
            this.browseField.setText(defaultFile.getAbsolutePath());
        }
        this.browseField.setEditable(false);
        panel.add(this.browseField);
        
        final JButton browseButton = new JButton("Browse");
        browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final String currentPath = GenerateDialog.this.browseField.getText();
                final File currentFile = nullOrEmpty(currentPath) ? null : new File(currentPath);
                final JFileChooser chooser = new JFileChooser(currentFile);
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int rc = chooser.showSaveDialog(GenerateDialog.this);
                if (JFileChooser.APPROVE_OPTION != rc) {
                    return;
                }
                GenerateDialog.this.browseField.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        });
        panel.add(browseButton, "wrap");
        
        this.useXmlFormatCheck = new JCheckBox("Save using XML format", useXmlFormat);
        panel.add(this.useXmlFormatCheck);
    }
    
    private void createMultiFileUi(JPanel panel, final File outputDir, String fileName, boolean useXmlFormat) {
        panel.add(new JLabel("Directory:"));
        
        this.browseField = new JTextField(40);
        if (null != outputDir) {
            this.browseField.setText(outputDir.getAbsolutePath());
        }
        this.browseField.setEditable(false);
        panel.add(this.browseField);
        
        final JButton browseButton = new JButton("Browse");
        browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final String path = GenerateDialog.this.browseField.getText();
                final JFileChooser chooser = new JFileChooser(nullOrEmpty(path) ? null : new File(path));
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int rc = chooser.showSaveDialog(GenerateDialog.this);
                if (JFileChooser.APPROVE_OPTION != rc) {
                    return;
                }
                GenerateDialog.this.browseField.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        });
        panel.add(browseButton, "wrap");
        
        panel.add(new JLabel("File Name Template:"));
        
        this.templateField = new JTextField(fileName, 40);
        panel.add(this.templateField, "wrap");
        
        this.useXmlFormatCheck = new JCheckBox("Save using XML format", useXmlFormat);
        panel.add(this.useXmlFormatCheck, "span, wrap");
        
        this.createDirectoriesCheck = new JCheckBox("Create a directory for each file");
        panel.add(this.createDirectoriesCheck, "span");
    }
    
    public int open() {
        setVisible(true);
        return getChoice();
    }
    
    public File getDir() {
        return this.dir;
    }

    public String getFile() {
        return this.file;
    }

    public boolean getCreateDirectories() {
        return this.createDirectories;
    }

    public boolean getUseXmlFormat() {
        return this.useXmlFormat;
    }

    public int getChoice() {
        return this.choice;
    }
}
