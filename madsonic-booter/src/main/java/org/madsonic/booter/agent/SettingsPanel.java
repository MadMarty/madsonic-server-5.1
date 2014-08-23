package org.madsonic.booter.agent;


import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Format;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.madsonic.booter.deployer.DeploymentStatus;
import org.madsonic.booter.deployer.MadsonicDeployer;

import com.jgoodies.forms.builder.ButtonBarBuilder;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.FormLayout;
/**
 * Panel displaying the settings of the Madsonic service.
 *
 * @author Sindre Mehus
 */
public class SettingsPanel extends JPanel implements MadsonicListener {


	private static final long serialVersionUID = 9143443400276730597L;

	private static final Format INTEGER_FORMAT = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.UK));

    private final MadsonicAgent madsonicAgent;
    private JFormattedTextField portTextField;
    private JCheckBox httpsPortCheckBox;
    private JFormattedTextField httpsPortTextField;
    private JComboBox<String> contextPathComboBox;
    private JFormattedTextField memoryInitTextField;    
    private JFormattedTextField memoryTextField;
    private JButton defaultButton;
    private JButton saveButton;
    public SettingsPanel(MadsonicAgent madsonicAgent) {
        this.madsonicAgent = madsonicAgent;
        createComponents();
        configureComponents();
        layoutComponents();
        addBehaviour();
        readValues();
        madsonicAgent.addListener(this);
    }

    public void readValues() {
        portTextField.setValue(getPortFromOptionsFile());
        memoryInitTextField.setValue(getMemoryInitFromOptionsFile());
        memoryTextField.setValue(getMemoryLimitFromOptionsFile());
        contextPathComboBox.setSelectedItem(getContextPathFromOptionsFile());
        int httpsPort = getHttpsPortFromOptionsFile();
        boolean httpsEnabled = httpsPort != 0;
        httpsPortTextField.setValue(httpsEnabled ? httpsPort : 4443);
        httpsPortTextField.setEnabled(httpsEnabled);
        httpsPortCheckBox.setSelected(httpsEnabled);
    }

    private int getHttpsPortFromOptionsFile() {
        try {
            String s = grep("-Dmadsonic.httpsPort=(\\d+)");
            return Integer.parseInt(s);
        } catch (Exception x) {
            x.printStackTrace();
            return MadsonicDeployer.DEFAULT_HTTPS_PORT;
        }
    }

    private int getPortFromOptionsFile() {
        try {
            String s = grep("-Dmadsonic.port=(\\d+)");
            return Integer.parseInt(s);
        } catch (Exception x) {
            x.printStackTrace();
            return MadsonicDeployer.DEFAULT_PORT;
        }
    }


    private int getMemoryInitFromOptionsFile() {
         try {
             String s = grep("-Xms(\\d+)m");
             return Integer.parseInt(s);
         } catch (Exception x) {
             x.printStackTrace();
             return MadsonicDeployer.DEFAULT_MEMORY_INIT;
         }
     }
    
    private int getMemoryLimitFromOptionsFile() {
        try {
            String s = grep("-Xmx(\\d+)m");
            return Integer.parseInt(s);
        } catch (Exception x) {
            x.printStackTrace();
            return MadsonicDeployer.DEFAULT_MEMORY_LIMIT;
        }
    }
	
    private String getContextPathFromOptionsFile() {
        try {
            String s = grep("-Dmadsonic.contextPath=(.*)");
            if (s == null) {
                throw new NullPointerException();
            }
            return s;
        } catch (Exception x) {
            x.printStackTrace();
            return MadsonicDeployer.DEFAULT_CONTEXT_PATH;
        }
    }

    private void createComponents() {
        portTextField = new JFormattedTextField(INTEGER_FORMAT);
        httpsPortTextField = new JFormattedTextField(INTEGER_FORMAT);
        httpsPortCheckBox = new JCheckBox("Enable https on port");
        contextPathComboBox = new JComboBox<String>();
        memoryInitTextField = new JFormattedTextField(INTEGER_FORMAT);
        memoryTextField = new JFormattedTextField(INTEGER_FORMAT);
        defaultButton = new JButton("Restore defaults");
        saveButton = new JButton("Save settings");
    }

    private void configureComponents() {
        contextPathComboBox.setEditable(true);
        contextPathComboBox.addItem("/");
        contextPathComboBox.addItem("/MP3");
        contextPathComboBox.addItem("/audio");
        contextPathComboBox.addItem("/music");
        contextPathComboBox.addItem("/libary");
        contextPathComboBox.addItem("/jukebox");
        contextPathComboBox.addItem("/madsonic");
    }

    @SuppressWarnings("deprecation")
	private void layoutComponents() {
        FormLayout layout = new FormLayout("d, 6dlu, max(d;30dlu):grow");
        DefaultFormBuilder builder = new DefaultFormBuilder(layout);
        builder.append("Port number", portTextField);
        builder.append(httpsPortCheckBox, httpsPortTextField);
        builder.append("Memory init (MB)", memoryInitTextField);
        builder.append("Memory limit (MB)", memoryTextField);
        builder.append("Context path", contextPathComboBox);

        setBorder(Borders.DIALOG_BORDER);

        setLayout(new BorderLayout(12, 12));
        add(builder.getPanel(), BorderLayout.CENTER);
        JPanel buttons = new ButtonBarBuilder()
            .addGlue()
            .addButton(defaultButton)
            .addRelatedGap()
            .addButton(saveButton)
            .addGlue()
            .build();
        add(buttons, BorderLayout.SOUTH);
    }

    private void addBehaviour() {
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    madsonicAgent.checkElevation("-settings", getMemoryInit() + "," + getMemoryLimit() + "," + getPort() + "," + getHttpsPort() + "," + getContextPath());
                    saveSettings(getMemoryInit(), getMemoryLimit(), getPort(), getHttpsPort(), getContextPath());
                } catch (Exception x) {
                    JOptionPane.showMessageDialog(SettingsPanel.this, x.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        defaultButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                portTextField.setValue(MadsonicDeployer.DEFAULT_PORT);
                httpsPortTextField.setValue(4443);
                httpsPortTextField.setEnabled(false);
                httpsPortCheckBox.setSelected(false);
                memoryInitTextField.setValue(MadsonicDeployer.DEFAULT_MEMORY_INIT);
                memoryTextField.setValue(MadsonicDeployer.DEFAULT_MEMORY_LIMIT);
                contextPathComboBox.setSelectedItem(MadsonicDeployer.DEFAULT_CONTEXT_PATH);
            }
        });

        httpsPortCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                httpsPortTextField.setEnabled(httpsPortCheckBox.isSelected());
            }
        });
    }

    private String getContextPath() throws SettingsException {
        String contextPath = (String) contextPathComboBox.getSelectedItem();
        if (contextPath.contains(" ") || !contextPath.startsWith("/")) {
            throw new SettingsException("Please specify a valid context path.");
        }
        return contextPath;
    }

    private int getMemoryInit() throws SettingsException {
        int memoryInit;
        try {
            memoryInit = ((Number) memoryInitTextField.getValue()).intValue();
            if (memoryInit < 5) {
                throw new Exception();
            }
        } catch (Exception x) {
            throw new SettingsException("Please specify a valid memory limit.", x);
        }
        return memoryInit;
    }
    
    private int getMemoryLimit() throws SettingsException {
        int memoryLimit;
        try {
            memoryLimit = ((Number) memoryTextField.getValue()).intValue();
            if (memoryLimit < 5) {
                throw new Exception();
            }
        } catch (Exception x) {
            throw new SettingsException("Please specify a valid memory limit.", x);
        }
        return memoryLimit;
    }

    private int getPort() throws SettingsException {
        int port;
        try {
            port = ((Number) portTextField.getValue()).intValue();
            if (port < 1 || port > 65535) {
                throw new Exception();
            }
        } catch (Exception x) {
            throw new SettingsException("Please specify a valid port number.", x);
        }
        return port;
    }

    private int getHttpsPort() throws SettingsException {
        if (!httpsPortCheckBox.isSelected()) {
            return 0;
        }

        int port;
        try {
            port = ((Number) httpsPortTextField.getValue()).intValue();
            if (port < 1 || port > 65535) {
                throw new Exception();
            }
        } catch (Exception x) {
            throw new SettingsException("Please specify a valid https port number.", x);
        }
        return port;
    }

    public void saveSettings(int memoryInit, int memoryLimit, int port, int httpsPort, String contextPath) throws SettingsException {
        File file = getOptionsFile();

        java.util.List<String> lines = readLines(file);
        java.util.List<String> newLines = new ArrayList<String>();

        boolean memoryInitAdded = false;
        boolean memoryLimitAdded = false;
        boolean portAdded = false;
        boolean httpsPortAdded = false;
        boolean contextPathAdded = false;

        for (String line : lines) {
            if (line.startsWith("-Xms")) {
                newLines.add("-Xms" + memoryInit + "m");
                memoryInitAdded = true;
            } else if (line.startsWith("-Xmx")) {
                newLines.add("-Xmx" + memoryLimit + "m");
                memoryLimitAdded = true;
            } else if (line.startsWith("-Dmadsonic.port=")) {
                newLines.add("-Dmadsonic.port=" + port);
                portAdded = true;
            } else if (line.startsWith("-Dmadsonic.httpsPort=")) {
                newLines.add("-Dmadsonic.httpsPort=" + httpsPort);
                httpsPortAdded = true;
            } else if (line.startsWith("-Dmadsonic.contextPath=")) {
                newLines.add("-Dmadsonic.contextPath=" + contextPath);
                contextPathAdded = true;
            } else {
                newLines.add(line);
            }
        }

        if (!memoryInitAdded) {
            newLines.add("-Xms" + memoryInit + "m");
        }
        if (!memoryLimitAdded) {
            newLines.add("-Xmx" + memoryLimit + "m");
        }
        if (!portAdded) {
            newLines.add("-Dmadsonic.port=" + port);
        }
        if (!httpsPortAdded) {
            newLines.add("-Dmadsonic.httpsPort=" + httpsPort);
        }
        if (!contextPathAdded) {
            newLines.add("-Dmadsonic.contextPath=" + contextPath);
        }

        writeLines(file, newLines);

        JOptionPane.showMessageDialog(SettingsPanel.this,
                "Please restart Madsonic for the new settings to take effect.",
                "Settings changed", JOptionPane.INFORMATION_MESSAGE);

    }

    private File getOptionsFile() throws SettingsException {
        File file = new File("madsonic-service.exe.vmoptions");
        if (!file.isFile() || !file.exists()) {
            throw new SettingsException("File " + file.getAbsolutePath() + " not found.");
        }
        return file;
    }

    private List<String> readLines(File file) throws SettingsException {
        List<String> lines = new ArrayList<String>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                lines.add(line);
            }
            return lines;
        } catch (IOException x) {
            throw new SettingsException("Failed to read from file " + file.getAbsolutePath(), x);
        } finally {
            closeQuietly(reader);
        }
    }

    private void writeLines(File file, List<String> lines) throws SettingsException {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new FileWriter(file));
            for (String line : lines) {
                writer.println(line);
            }
        } catch (IOException x) {
            throw new SettingsException("Failed to write to file " + file.getAbsolutePath(), x);
        } finally {
            closeQuietly(writer);
        }
    }

    private String grep(String regexp) throws SettingsException {
        Pattern pattern = Pattern.compile(regexp);
        File file = getOptionsFile();
        for (String line : readLines(file)) {
            Matcher matcher = pattern.matcher(line);
            if (matcher.matches()) {
                return matcher.group(1);
            }
        }
        return null;
    }

    private void closeQuietly(Reader reader) {
        if (reader == null) {
            return;
        }

        try {
            reader.close();
        } catch (IOException x) {
            // Intentionally ignored.
        }
    }

    private void closeQuietly(Writer writer) {
        if (writer == null) {
            return;
        }

        try {
            writer.close();
        } catch (IOException x) {
            // Intentionally ignored.
        }
    }

    public void notifyDeploymentStatus(DeploymentStatus deploymentStatus) {
        // Nothing here yet.
    }

    public void notifyServiceStatus(String serviceStatus) {
        // Nothing here yet.
    }

    public static class SettingsException extends Exception {

        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public SettingsException(String message, Throwable cause) {
            super(message, cause);
        }

        public SettingsException(String message) {
            this(message, null);
        }

        @Override
        public String getMessage() {
            if (getCause() == null || getCause().getMessage() == null) {
                return super.getMessage();
            }
            return super.getMessage() + " " + getCause().getMessage();
        }
    }
}
