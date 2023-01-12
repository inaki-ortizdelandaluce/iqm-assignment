package iqm.assessment;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class EchoHttpServerCmd {
	
	private static final String OPTION_PORT = "p";
	private static final String OPTION_PORT_LONG = "port";
	private static final String OPTION_MESSAGE = "m";
	private static final String OPTION_MESSAGE_LONG = "message";
	
	public static void main(String args[]) {
		if (hasHelpOption(args)) {
            printHelp();
            return;
        }

		try {
	        CommandLine commandLine = new DefaultParser().parse(getOptions(), args);
	        
	        int port = Integer.parseInt(commandLine.getOptionValue(OPTION_PORT));
	        String message = commandLine.getOptionValue(OPTION_MESSAGE);
	        
	        EchoHttpServer server = new EchoHttpServer(port, message);
	        server.start();
	        
		} catch(NumberFormatException e) {
			System.out.println("Port value is not a valid integer");
			printHelp();
			System.exit(0);
		} catch(ParseException e) {
			printHelp();
			System.exit(0);
		}
	}
	
    private static void printHelp() {
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.setNewLine("\n");
        helpFormatter.setWidth(120);
        helpFormatter.printHelp("echo-http-server.sh", getOptions(), true);
    }
    
    private static Options getOptions() {
        Options options = new Options();
        Option portOption = Option.builder().longOpt(OPTION_PORT_LONG).option(OPTION_PORT)
                .required(true)
                .hasArg(true).build();

        Option messageOption = Option.builder().longOpt(OPTION_MESSAGE_LONG).option(OPTION_MESSAGE)
                .required(true)
                .hasArg(true).build();

        // add options
        options.addOption(portOption);
        options.addOption(messageOption);
        return options;
    }
    
    public static boolean hasHelpOption(String[] args) {
        boolean hasHelp = true;
        try {
            Options options = new Options();
            Option helpOption = Option.builder().option("h").longOpt("help").required(false).build();
            options.addOption(helpOption);

            CommandLine cmd = new DefaultParser().parse(options, args);
            if (!cmd.hasOption(helpOption.getOpt())) {
                hasHelp = false;
            }
        } catch (ParseException e) {
            hasHelp = false;
        }
        return hasHelp;
    }

}
