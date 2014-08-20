package me.StevenLawson.BukkitTelnetClient;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
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

        btc.writeToConsole("Connecting to " + hostname + ":" + port + "...");

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

        btc.writeToConsole("Disconnected.");
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
                BukkitTelnetClient.mainPanel.writeToConsole(":" + text);
            }

            this.telnetClient.getOutputStream().write((text + "\r\n").getBytes());
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

                try
                {
                    BTC_ConnectionManager.this.telnetClient.connect(hostname, port);
                    BTC_ConnectionManager.this.canDoDisconnect = true;

                    btc.getBtnSend().setEnabled(true);
                    btc.getTxtCommand().setEnabled(true);
                    btc.getTxtCommand().requestFocusInWindow();

                    try (final BufferedReader reader = new BufferedReader(new InputStreamReader(telnetClient.getInputStream())))
                    {
                        String line;
                        while ((line = reader.readLine()) != null)
                        {
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
                        }
                    }

                    triggerDisconnect();
                }
                catch (IOException ex)
                {
                    ex.printStackTrace(BTC_MainPanel.CONSOLE_STREAM);
                    btc.updateConsole();
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
