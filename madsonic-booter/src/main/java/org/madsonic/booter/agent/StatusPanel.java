package org.madsonic.booter.agent;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.util.Locale;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import org.madsonic.booter.deployer.DeploymentStatus;
import com.jgoodies.forms.builder.ButtonBarBuilder;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.FormLayout;

/**
 * Panel displaying the status of the Madsonic service.
 *
 * @author Sindre Mehus
 */
public class StatusPanel extends JPanel implements MadsonicListener {

    private static final DateFormat DATE_FORMAT = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.US);

    private final MadsonicAgent madsonicAgent;

    private JTextField statusTextField;
    private JTextField startedTextField;
    private JTextField memoryTextField;
    private JTextArea errorTextField;
    private JButton startButton;
    private JButton stopButton;
    private JButton urlButton;

    public StatusPanel(MadsonicAgent madsonicAgent) {
        this.madsonicAgent = madsonicAgent;
        createComponents();
        configureComponents();
        layoutComponents();
        addBehaviour();
        madsonicAgent.addListener(this);
    }

    private void createComponents() {
        statusTextField = new JTextField();
        startedTextField = new JTextField();
        memoryTextField = new JTextField();
        errorTextField = new JTextArea(3, 24);
        startButton = new JButton("Start");
        stopButton = new JButton("Stop");
        urlButton = new JButton();
    }

    private void configureComponents() {
        statusTextField.setEditable(false);
        startedTextField.setEditable(false);
        memoryTextField.setEditable(false);
        errorTextField.setEditable(false);

        errorTextField.setLineWrap(true);
        errorTextField.setBorder(startedTextField.getBorder());

        urlButton.setBorderPainted(false);
        urlButton.setContentAreaFilled(false);
        urlButton.setForeground(Color.BLUE.darker());
        urlButton.setHorizontalAlignment(SwingConstants.LEFT);
    }

    private void layoutComponents() {
        JPanel buttons = new ButtonBarBuilder()
            .addGlue()
            .addButton(startButton)
            .addRelatedGap()
            .addButton(stopButton)
            .build();
        FormLayout layout = new FormLayout("right:d, 6dlu, max(d;30dlu):grow");
        DefaultFormBuilder builder = new DefaultFormBuilder(layout, this);
        builder.append("Service status", statusTextField);
        builder.append("", buttons);
        builder.appendParagraphGapRow();
        builder.nextRow();
        builder.append("Started on", startedTextField);
        builder.append("Memory used", memoryTextField);
        builder.append("Error message", errorTextField);
        builder.append("Server address", urlButton);

        setBorder(Borders.DIALOG_BORDER);
    }

    private void addBehaviour() {
        urlButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                madsonicAgent.openBrowser();
            }
        });
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                madsonicAgent.checkElevation("-start");
                madsonicAgent.startOrStopService(true);
            }
        });
        stopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                madsonicAgent.checkElevation("-stop");
                madsonicAgent.startOrStopService(false);
            }
        });
    }

    public void notifyDeploymentStatus(DeploymentStatus status) {
        startedTextField.setText(status == null ? null : DATE_FORMAT.format(status.getStartTime()));
        memoryTextField.setText(status == null ? null : status.getMemoryUsed() + " MB");
        errorTextField.setText(status == null ? null : status.getErrorMessage());
        urlButton.setText(status == null ? null : status.getURL());
    }

    public void notifyServiceStatus(String serviceStatus) {
        statusTextField.setText(serviceStatus);
    }
}
