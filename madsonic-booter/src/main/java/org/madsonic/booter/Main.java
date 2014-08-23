package org.madsonic.booter;

import java.util.Arrays;
import java.util.List;

import javax.swing.JOptionPane;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import org.madsonic.booter.agent.SettingsPanel;
import org.madsonic.booter.agent.MadsonicAgent;

/**
 * Application entry point for Madsonic booter.
 * <p/>
 * Use command line argument "-agent" to start the Windows service monitoring agent,
 * or "-mac" to start the Mac version of the deployer.
 *
 * @author Sindre Mehus
 */
public class Main {

    @SuppressWarnings("resource")
	public Main(String contextName, List<String> args) {
    	
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext" + contextName + ".xml");

        if ("-agent".equals(contextName)) {

            MadsonicAgent agent = (MadsonicAgent) context.getBean("agent");
            SettingsPanel settingsPanel = (SettingsPanel) context.getBean("settingsPanel");

            agent.setElevated(args.contains("-elevated"));

            if (args.contains("-balloon")) {
                agent.showTrayIconMessage();
            }

            if (args.contains("-stop")) {
                agent.startOrStopService(false);
                agent.showStatusPanel();
            } else if (args.contains("-start")) {
                agent.startOrStopService(true);
                agent.showStatusPanel();
            }

            if (args.contains("-settings")) {
                String[] settings = args.get(args.indexOf("-settings") + 1).split(",");
                try {
                    agent.showSettingsPanel();
                    settingsPanel.saveSettings(Integer.valueOf(settings[0]), Integer.valueOf(settings[1]), Integer.valueOf(settings[2]), Integer.valueOf(settings[3]), settings[4]);
                    settingsPanel.readValues();
                } catch (Exception x) {
                    JOptionPane.showMessageDialog(settingsPanel, x.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
                }
            }
        }
    }

    public static void main(String[] args) {
        String context = "-deployer";
        if (args.length > 0) {
            context = args[0];
        }
        new Main(context, Arrays.asList(args));
    }
}
