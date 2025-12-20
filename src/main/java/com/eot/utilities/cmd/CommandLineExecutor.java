package com.eot.utilities.cmd;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class CommandLineExecutor {
    private static final int DEFAULT_COMMAND_TIMEOUT = 60;
    private static String commandPrefix = "notset";
    private static String commandSwitch = "notset";

    private CommandLineExecutor() {
    }

    public static CommandLineResponse execCommand(final String[] command) throws IOException, InterruptedException {
        return execCommand(command, DEFAULT_COMMAND_TIMEOUT);
    }

    public static CommandLineResponse execCommand(final String[] command, int timeoutInSeconds) throws InterruptedException, IOException {
        setOSTypeAndCommandPrefix();
        String jointCommand = String.join(" ", command);
        String message = "\tExecuting Command: " + jointCommand;
        System.out.println(message);
        CommandLineResponse response = new CommandLineResponse();
        ProcessBuilder builder = new ProcessBuilder(command);
        builder.command(commandPrefix, commandSwitch, jointCommand);
        Process process = builder.start();
        process.waitFor(timeoutInSeconds, TimeUnit.SECONDS);
        response.setStdOut(
                IOUtils.toString(process.getInputStream(), StandardCharsets.UTF_8).trim());
        response.setErrOut(
                IOUtils.toString(process.getErrorStream(), StandardCharsets.UTF_8).trim());
        String responseMessage = String.format("\tExit code: %d %n\tResponse:%n%s\t", process.exitValue(), response);
        System.out.println(responseMessage);
        response.setExitCode(process.exitValue());
        return response;
    }

    private static void setOSTypeAndCommandPrefix() {
        String os = System.getProperty("os.name").toLowerCase();
        String osType = "notset";
        if (os.contains("win")) {
            osType = "Windows";
            commandPrefix = "cmd.exe";
            commandSwitch = "/c";
        } else if (os.contains("mac")) {
            osType = "Mac OS";
            commandPrefix = "sh";
            commandSwitch = "-c";
        } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
            osType = "Unix/Linux";
            commandPrefix = "sh";
            commandSwitch = "-c";
        } else if (os.contains("sunos")) {
            osType = "Solaris";
            commandPrefix = "sh";
            commandSwitch = "-c";
        } else {
            System.out.println("Unknown OS");
        }
        System.out.println("OSType: " + osType + ": Command: " + commandPrefix + " " + commandSwitch);
    }
}
