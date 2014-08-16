package me.StevenLawson.BukkitTelnetClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UnsupportedLookAndFeelException;
import org.apache.commons.io.output.TeeOutputStream;

public class BukkitTelnetClient
{
    public static final String VERSION_STRING = "v2.01_b1";

    public static final Logger LOGGER = Logger.getLogger(BukkitTelnetClient.class.getName());
    public static final ByteArrayOutputStream CONSOLE = new ByteArrayOutputStream();

    public static BTC_MainPanel mainPanel = null;

    public static void main(String args[])
    {
        final PrintStream guiConsole = new PrintStream(CONSOLE, true)
        {
            @Override
            public void write(byte[] bytes) throws IOException
            {
                super.write(bytes);
                if (mainPanel != null)
                {
                    mainPanel.updateConsole();
                }
            }

            @Override
            public void write(int i)
            {
                super.write(i);
                if (mainPanel != null)
                {
                    mainPanel.updateConsole();
                }
            }

            @Override
            public void write(byte[] bytes, int i, int i1)
            {
                super.write(bytes, i, i1);
                if (mainPanel != null)
                {
                    mainPanel.updateConsole();
                }
            }

            @Override
            public void flush()
            {
                super.flush();
                if (mainPanel != null)
                {
                    mainPanel.updateConsole();
                }
            }
        };

        System.setOut(new PrintStream(new TeeOutputStream(System.out, guiConsole)));
        System.setErr(new PrintStream(new TeeOutputStream(System.err, guiConsole)));

        findAndSetLookAndFeel("Windows");

        java.awt.EventQueue.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                mainPanel = new BTC_MainPanel();
            }
        });
    }

    private static void findAndSetLookAndFeel(final String searchStyleName)
    {
        try
        {
            javax.swing.UIManager.LookAndFeelInfo foundStyle = null;
            javax.swing.UIManager.LookAndFeelInfo fallbackStyle = null;

            for (javax.swing.UIManager.LookAndFeelInfo style : javax.swing.UIManager.getInstalledLookAndFeels())
            {
                if (searchStyleName.equalsIgnoreCase(style.getName()))
                {
                    foundStyle = style;
                    break;
                }
                else if ("Nimbus".equalsIgnoreCase(style.getName()))
                {
                    fallbackStyle = style;
                }
            }

            if (foundStyle != null)
            {
                javax.swing.UIManager.setLookAndFeel(foundStyle.getClassName());
            }
            else if (fallbackStyle != null)
            {
                javax.swing.UIManager.setLookAndFeel(fallbackStyle.getClassName());
            }
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex)
        {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
}
