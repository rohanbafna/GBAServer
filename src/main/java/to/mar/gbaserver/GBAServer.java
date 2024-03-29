package to.mar.gbaserver;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;
import java.util.logging.*;

public class GBAServer {
    private static Logger logger = Logger.getLogger("to.mar.gbaserver");
    private static FileHandler fh;
    public static void main(String[] args) {
        try {
            fh = new FileHandler("GBAServer_log.txt");
            fh.setFormatter(new SimpleFormatter());
            logger.addHandler(fh);
            logger.setLevel(Level.INFO);
            logger.setUseParentHandlers(false);

            ServerSocket socket = new ServerSocket(2346);

            while (true) {
                Socket client = socket.accept();
                InputStreamReader inputStream = new InputStreamReader(client.getInputStream());
                BufferedReader reader = new BufferedReader(inputStream);

                OSUtil.OS operatingSystem = Objects.requireNonNull(OSUtil.getOperatingSystem(), "Your operating system is not supported.");
                String command = operatingSystem.command;

                String input = reader.readLine();
                input = input.substring(13);

                if (input.endsWith(" GDB")) {
                    command += operatingSystem.gdbSuffix;
                    input = input.substring(0, input.length() - 4);
                }

                inputStream.close();
                client.close();

                String cli = String.format(command, input);
                logger.info(String.format("Running command: %s", cli));

                Runtime.getRuntime().exec(cli);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
