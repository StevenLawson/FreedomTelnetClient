package me.StevenLawson.BukkitTelnetClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.telnet.TelnetClient;

public class BTC_ConnectionManager
{
    private static final Logger log = Logger.getLogger("BukkitTelnetClient");
    private final BukkitTelnetClient btc;
    private final TelnetClient tc;
    private Thread connectThread;
    private String hostname;
    private int port;
    private boolean can_do_disconnect = false;

    public BTC_ConnectionManager(BukkitTelnetClient btc)
    {
        this.btc = btc;
        this.tc = new TelnetClient();
    }

    public void trigger_connect(String hostname, int port)
    {
        btc.getBtn_connect().setEnabled(false);
        btc.getTxt_server().setEnabled(false);
        btc.getBtn_disconnect().setEnabled(true);

        System.out.println("Connecting to " + hostname + ":" + port + "...");

        this.hostname = hostname;
        this.port = port;

        startConnectThread();
    }

    public void trigger_connect(String hostname_and_port)
    {
        String[] parts = StringUtils.split(hostname_and_port, ":");

        if (parts.length <= 1)
        {
            this.trigger_connect(parts[0], 23);
        }
        else
        {
            int t_port = 23;

            try
            {
                t_port = Integer.parseInt(parts[1]);
            }
            catch (NumberFormatException ex)
            {
            }

            this.trigger_connect(parts[0], t_port);
        }
    }

    public void trigger_disconnect()
    {
        if (can_do_disconnect)
        {
            can_do_disconnect = false;

            try
            {
                tc.disconnect();
            }
            catch (IOException ex)
            {
                log.log(Level.SEVERE, null, ex);
            }
        }
    }

    public void finish_disconnect()
    {
        btc.getBtn_connect().setEnabled(true);
        btc.getTxt_server().setEnabled(true);
        btc.getBtn_disconnect().setEnabled(false);
        btc.getBtn_send().setEnabled(false);
        btc.getTxt_command().setEnabled(false);

        System.out.println("\nDisconnected.");
    }

    public void send_command(String text)
    {
        try
        {
            System.out.println(text);
            tc.getOutputStream().write((text + "\n").getBytes());
            tc.getOutputStream().flush();
        }
        catch (IOException ex)
        {
            log.log(Level.SEVERE, null, ex);
        }
    }

    private void startConnectThread()
    {
        if (connectThread != null)
        {
            return;
        }

        connectThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    tc.connect(hostname, port);
                    can_do_disconnect = true;

                    btc.getBtn_send().setEnabled(true);
                    btc.getTxt_command().setEnabled(true);
                    btc.getTxt_command().requestFocusInWindow();

                    InputStream in = tc.getInputStream();

                    byte[] buffer = new byte[1];
                    int ret_read = 0;
                    do
                    {
                        ret_read = in.read(buffer);
                        if (ret_read > 0)
                        {
                            System.out.print(new String(buffer, 0, ret_read));
                        }
                    }
                    while (ret_read >= 0);

                    trigger_disconnect();
                }
                catch (Exception ex)
                {
                    log.log(Level.SEVERE, null, ex);
                }

                finish_disconnect();

                connectThread = null;
            }
        });
        connectThread.start();
    }
}
