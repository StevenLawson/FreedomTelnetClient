package me.StevenLawson.BukkitTelnetClient;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Map;
import java.util.logging.Level;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.telnet.TelnetClient;

public class BTC_ConnectionManager
{
    private final TelnetClient telnetClient;
    private Thread connectThread;
    private String hostname;
    private int port;
    private boolean canDoDisconnect = false;

    public BTC_ConnectionManager()
    {
        this.telnetClient = new TelnetClient();
    }

    public void triggerConnect(String hostname, int port)
    {
        final BTC_MainPanel btc = BukkitTelnetClient.mainPanel;

        btc.getBtnConnect().setEnabled(false);
        btc.getTxtServer().setEnabled(false);
        btc.getBtnDisconnect().setEnabled(true);

        btc.setTitle("BukkitTelnetClient - " + BukkitTelnetClient.VERSION_STRING + " - " + hostname + ":" + port);

        System.out.println("Connecting to " + hostname + ":" + port + "...");

        this.hostname = hostname;
        this.port = port;

        startConnectThread();
    }

    public void triggerConnect(String hostnameAndPort)
    {
        final String[] parts = StringUtils.split(hostnameAndPort, ":");

        if (parts.length <= 1)
        {
            this.triggerConnect(parts[0], 23);
        }
        else
        {
            int _port = 23;

            try
            {
                _port = Integer.parseInt(parts[1]);
            }
            catch (NumberFormatException ex)
            {
            }

            this.triggerConnect(parts[0], _port);
        }
    }

    public void triggerDisconnect()
    {
        if (this.canDoDisconnect)
        {
            this.canDoDisconnect = false;

            try
            {
                this.telnetClient.disconnect();
            }
            catch (IOException ex)
            {
                BukkitTelnetClient.LOGGER.log(Level.SEVERE, null, ex);
            }
        }
    }

    public void finishDisconnect()
    {
        final BTC_MainPanel btc = BukkitTelnetClient.mainPanel;

        btc.getBtnConnect().setEnabled(true);
        btc.getTxtServer().setEnabled(true);
        btc.getBtnDisconnect().setEnabled(false);
        btc.getBtnSend().setEnabled(false);
        btc.getTxtCommand().setEnabled(false);

        btc.setTitle("BukkitTelnetClient - " + BukkitTelnetClient.VERSION_STRING + " - Disconnected");

        System.out.println("\nDisconnected.");
    }

    private final ByteArrayOutputStream consoleBuffer = new ByteArrayOutputStream();
    private final PrintStream consoleStream = new PrintStream(consoleBuffer);

    public void sendCommand(String text)
    {
        try
        {
            consoleStream.format("%s\r\n", text);

            this.telnetClient.getOutputStream().write((text + "\n").getBytes());
            this.telnetClient.getOutputStream().flush();
        }
        catch (IOException ex)
        {
            BukkitTelnetClient.LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    private void startConnectThread()
    {
        if (this.connectThread != null)
        {
            return;
        }

        this.connectThread = new Thread(new Runnable()
        {
            private final BTC_MainPanel btc = BukkitTelnetClient.mainPanel;

            @Override
            public void run()
            {
                try
                {
                    BTC_ConnectionManager.this.telnetClient.connect(hostname, port);
                    BTC_ConnectionManager.this.canDoDisconnect = true;

                    btc.getBtnSend().setEnabled(true);
                    btc.getTxtCommand().setEnabled(true);
                    btc.getTxtCommand().requestFocusInWindow();

                    try (final BufferedReader reader = new BufferedReader(new InputStreamReader(telnetClient.getInputStream())))
                    {

                        int read = 0;
                        while (read != -1)
                        {
                            boolean block = true;

                            while (block || reader.ready())
                            {
                                block = false;

                                read = reader.read();
                                if (read != -1)
                                {
                                    consoleBuffer.write(read);
                                }

                                if (read == '\n')
                                {
                                    final String line = consoleBuffer.toString();

                                    final Map<String, BTC_PlayerListDecoder.PlayerInfo> playerList = BTC_PlayerListDecoder.decodePlayerListMessage(line);

                                    if (playerList != null)
                                    {
                                        btc.updatePlayerList(playerList);
                                    }
                                    else
                                    {
                                        if (!BTC_FormatHandler.skipLine(line))
                                        {
                                            System.out.print(line);
                                        }
                                    }

                                    consoleBuffer.reset();
                                }
                            }

                            if (consoleBuffer.size() > 0)
                            {
                                final String line = consoleBuffer.toString();
                                if (line.endsWith("Username: ") || line.endsWith("Password: "))
                                {
                                    System.out.print(consoleBuffer.toString());
                                    consoleBuffer.reset();
                                }
                            }
                        }
                    }

                    triggerDisconnect();
                }
                catch (IOException ex)
                {
                    BukkitTelnetClient.LOGGER.log(Level.SEVERE, null, ex);
                }

                finishDisconnect();

                BTC_ConnectionManager.this.connectThread = null;
            }
        });
        this.connectThread.start();
    }
}
