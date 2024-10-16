package utils;

public class Command {
    public final String cmdType;
    public final String cmdArg;
    public final String cmdLine;
    public final boolean isValid;

    public Command(String cmdLine) {
        this.cmdLine = cmdLine;

        // Split.
        String[] splitLine = cmdLine.split(" ", 2);
        cmdType = splitLine[0];

        // Validate.
        cmdArg = splitLine.length > 1 ? splitLine[1] : "";
        isValid = (!cmdArg.isEmpty()
                && (cmdType.equalsIgnoreCase("get") || cmdType.equalsIgnoreCase("put")))
                || cmdType.equalsIgnoreCase("quit");
    }

    public Command(Command cmd, String newArg) {
        this.cmdType = cmd.cmdType;
        this.cmdArg = newArg;
        this.cmdLine = cmd.cmdType + " " + newArg;
        this.isValid = true;
    }
}
