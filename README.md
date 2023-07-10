# Courtroom BUTIK
This is an application that people can use to track their court case history - special users like judges or admins have the ability to add and new and/or edit existing court cases. The application can be used in two different manners - the friendly manner, graphical user interface (GUI) and the less friendly manner, command line interface (CLI). The second way of use, CLI, is available only for admins of the application. The data used within the application is stored inside of a MySQL database.

To open the application in a friendly environment (GUI), JavaFX needs to be properly setup on the device. If accessed from an environment like IntelliJ, VM options need to be configured. For those that wish to open up the application through the command line, type _mvn clean install_ followed by _mvn javafx:run_.

To open the application the old-fashioned way (CLI), position yourself to the folder with this project's files and type _mvn clean install_ afterwhich you will navigate to the target folder and follow that up by typing _java -jar projekat-rpr-cli-jar-with-dependencies_ where further instructions await you on the terminal.
