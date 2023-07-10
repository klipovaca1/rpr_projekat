package ba.unsa.etf.rpr;

import ba.unsa.etf.rpr.business.LawsuitManager;
import ba.unsa.etf.rpr.business.UserManager;
import ba.unsa.etf.rpr.domain.Lawsuit;
import ba.unsa.etf.rpr.domain.User;
import ba.unsa.etf.rpr.exceptions.CourtroomException;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * A simplified representation of the created courtroom application through the use of the Command Line Interface (CLI)
 */
public class App {
    private static final Option addUser = new Option("addUser", true, "Add a new user to the database (2 arguments - name (String) and userType (String))");
    private static final Option deleteUser = new Option("deleteUser", true, "Delete an existing user from the database (1 argument - id (int))");
    private static final Option getAllUsers = new Option("getAllUsers", false, "Get all users from the database (0 arguments)");
    private static final Option addCase = new Option("addCase", true, "Add a new court case to the database (7 arguments - look them up with showCaseArguments)");
    private static final Option deleteCase = new Option("deleteCase", true, "Delete an existing court case from the database (1 argument - id (int))");
    private static final Option getAllCases = new Option("getAllCases", false, "Get all court cases from the database (0 arguments)");
    private static final Option showUserTypes = new Option("showUserTypes", false, "Shows all valid user types (0 arguments)");
    private static final Option showCaseArguments = new Option("showCaseArguments", false, "Shows all arguments needed to enter to create a new case (0 arguments)");
    private static final Option showVerdicts = new Option("showVerdicts", false, "Shows all valid verdicts (0 arguments)");
    private static final UserManager userManager = UserManager.getInstance();
    private static final LawsuitManager lawsuitManager = LawsuitManager.getInstance();

    private static Options loadOptions() {
        Options options = new Options();
        options.addOption(addUser);
        options.addOption(deleteUser);
        options.addOption(getAllUsers);
        options.addOption(addCase);
        options.addOption(deleteCase);
        options.addOption(getAllCases);
        options.addOption(showUserTypes);
        options.addOption(showCaseArguments);
        options.addOption(showVerdicts);
        return options;
    }
    private static void printFormattedOptions(Options options) {
        HelpFormatter helpFormatter = new HelpFormatter();
        PrintWriter printWriter = new PrintWriter(System.out);
        helpFormatter.printUsage(printWriter, 150, "java -jar projekatRPR-cli-jar-with-dependencies.jar -option ditto arguments");
        helpFormatter.printOptions(printWriter, 150, options, 2, 7);
        printWriter.close();
    }

    public static void main(String[] args) {
        Options options = loadOptions();
        try {
            CommandLineParser commandLineParser = new DefaultParser();
            CommandLine commandLine = commandLineParser.parse(options, args);
            List<String> arguments = commandLine.getArgList();
            if (commandLine.hasOption(addUser.getOpt())) {
                if (arguments.size() != 2) {
                    throw new IOException("2," + arguments.size());
                }
                User addedUser = userManager.addUser(arguments.get(0), arguments.get(1));
                System.out.println("\nSuccessfully added new user with ID of " + addedUser.getId() + " to database.\n");
            }
            else if (commandLine.hasOption(deleteUser.getOpt())) {
                if (arguments.size() != 1) {
                    throw new IOException("1," + arguments.size());
                }
                User deletedUser = userManager.deleteUser(Integer.parseInt(arguments.get(0)));
                System.out.println("\nSuccessfully deleted " + deletedUser.getName() + " from database.\n");
            }
            else if (commandLine.hasOption(getAllUsers.getOpt())) {
                if (!arguments.isEmpty()) {
                    throw new IOException("0," + arguments.size());
                }
                System.out.println("\nAll users in the database:\n" + userManager.getAllUsers() + '\n');
            }
            else if (commandLine.hasOption(addCase.getOpt())) {
                if (arguments.size() != 7) {
                    throw new IOException("7," + arguments.size());
                }
                Lawsuit lawsuit = lawsuitManager.addLawsuit(new Lawsuit(42, arguments.get(0), Long.parseLong(arguments.get(1)), userManager.getById(Integer.parseInt(arguments.get(2))), userManager.getById(Integer.parseInt(arguments.get(3))), userManager.getById(Integer.parseInt(arguments.get(4))), userManager.getById(Integer.parseInt(arguments.get(5))), arguments.get(6)));
                System.out.println("\nSuccessfully added new court case with ID of " + lawsuit.getId() + " to database.\n");
            }
            else if (commandLine.hasOption(deleteCase.getOpt())) {
                if (arguments.size() != 1) {
                    throw new IOException("1," + arguments.size());
                }
                lawsuitManager.deleteLawsuit(Integer.parseInt(arguments.get(0)));
                System.out.println("\nProvided case successfully deleted from database\n");
            }
            else if (commandLine.hasOption(getAllCases.getOpt())) {
                if (!arguments.isEmpty()) {
                    throw new IOException("0," + arguments.size());
                }
                System.out.println("\nAll court cases in the database:\n" + lawsuitManager.getAllLawsuits() + '\n');
            }
            else if (commandLine.hasOption(showUserTypes.getOpt())){
                if (!arguments.isEmpty()) {
                    throw new IOException("0," + arguments.size());
                }
                System.out.println("\nValid user types: \"Judge\", \"Defendant\", \"Lawyer\" and \"Prosecutor\".\n");
            }
            else if (commandLine.hasOption(showCaseArguments.getOpt())) {
                if (!arguments.isEmpty()) {
                    throw new IOException("0," + arguments.size());
                }
                System.out.println("\nCourt case arguments: title (String), UIN (long), judgeId (int), defendantId (int), lawyerId (int), prosecutorId (int) and verdict (String).\n");
            }
            else if (commandLine.hasOption(showVerdicts.getOpt())) {
                if (!arguments.isEmpty()) {
                    throw new IOException("0," + arguments.size());
                }
                System.out.println("\nValid verdicts: \"Undecided\", \"Dismissal\", \"Withdrawal\", \"Diversion\", \"Guilty\", \"Guilty plea\" and \"Not guilty\".\n");
            }
            else {
                System.out.println("\nUnknown command entered. Restarting systems...\n");
            }
        }
        catch (IOException exception) {
            String message = exception.getMessage();
            System.out.println("\nIncorrect number of arguments entered - expected " + message.substring(0, message.indexOf(",")) + ", found " + message.substring(message.indexOf(",") + 1) + ".\n");
        }
        catch (CourtroomException exception) {
            System.out.println('\n' + exception.getMessage() + '\n');
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        printFormattedOptions(options);
    }
}