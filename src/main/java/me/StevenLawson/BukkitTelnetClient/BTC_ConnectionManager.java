package me.StevenLawson.BukkitTelnetClient;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.logging.Level;
import javax.swing.Timer;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.telnet.TelnetClient;

public class BTC_ConnectionManager
{
    private final TelnetClient telnetClient;
    private Thread connectThread;
    private String hostname;
    private int port;
    private boolean canDoDisconnect = false;
    private String loginName;

    public BTC_ConnectionManager()
    {
        this.telnetClient = new TelnetClient();
    }

    public void triggerConnect(final String hostname, final int port)
    {
        final BTC_MainPanel btc = BukkitTelnetClient.mainPanel;

        btc.getBtnConnect().setEnabled(false);
        btc.getTxtServer().setEnabled(false);
        btc.getBtnDisconnect().setEnabled(true);

        btc.writeToConsole("Connecting to " + hostname + ":" + port + "...\n");

        this.hostname = hostname;
        this.port = port;
        this.loginName = null;
        updateTitle(true);

        startConnectThread();
    }

    public void triggerConnect(final String hostnameAndPort)
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

        loginName = null;

        updateTitle(false);

        btc.writeToConsole("\nDisconnected.\n");
    }

    public void sendCommand(final String text)
    {
        sendCommand(text, true);
    }

    public void sendCommand(final String text, final boolean verbose)
    {
        try
        {
            if (verbose)
            {
                BTC_MainPanel.CONSOLE_STREAM.format("%s\r\n", text);
            }

            this.telnetClient.getOutputStream().write((text + "\n").getBytes());
            this.telnetClient.getOutputStream().flush();
        }
        catch (IOException ex)
        {
            BukkitTelnetClient.LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public void sendDelayedCommand(final String text, final boolean verbose, final int delay)
    {
        final Timer timer = new Timer(delay, new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent ae)
            {
                sendCommand(text, verbose);
            }
        });

        timer.setRepeats(false);
        timer.start();
    }

    private void startConnectThread()
    {
        if (this.connectThread != null)
        {
            return;
        }

        this.connectThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                final BTC_MainPanel btc = BukkitTelnetClient.mainPanel;
                final ByteArrayOutputStream consoleBuffer = new ByteArrayOutputStream();

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

                                    String _loginName = null;
                                    if (BTC_ConnectionManager.this.loginName == null)
                                    {
                                        _loginName = BTC_PlayerListDecoder.checkForLoginMessage(line);
                                    }
                                    if (_loginName != null)
                                    {
                                        BTC_ConnectionManager.this.loginName = _loginName;
                                        updateTitle(true);
                                        sendDelayedCommand("telnet.enhanced", false, 100);
                                    }
                                    else
                                    {
                                        final Map<String, PlayerInfo> playerList = BTC_PlayerListDecoder.checkForPlayerListMessage(line);
                                        if (playerList != null)
                                        {
                                            btc.updatePlayerList(playerList);
                                        }
                                        else
                                        {
                                            if (!BTC_FormatHandler.skipLine(line))
                                            {
                                                btc.writeToConsole(line);
                                            }
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
                                    btc.writeToConsole(line);
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

    public final void updateTitle(final boolean isConnected)
    {
        final BTC_MainPanel mainPanel = BukkitTelnetClient.mainPanel;
        if (mainPanel == null)
        {
            return;
        }

        String title;

        if (isConnected)
        {
            if (loginName == null)
            {
                title = String.format("BukkitTelnetClient - %s - %s:%d", BukkitTelnetClient.VERSION_STRING, hostname, port);
            }
            else
            {
                title = String.format("BukkitTelnetClient - %s - %s@%s:%d", BukkitTelnetClient.VERSION_STRING, loginName, hostname, port);
            }
        }
        else
        {
            title = String.format("BukkitTelnetClient - %s - Disconnected", BukkitTelnetClient.VERSION_STRING);
        }

        mainPanel.setTitle(title);
    }
}
