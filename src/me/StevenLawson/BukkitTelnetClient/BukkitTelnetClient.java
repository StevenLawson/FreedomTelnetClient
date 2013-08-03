package me.StevenLawson.BukkitTelnetClient;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

public class BukkitTelnetClient extends javax.swing.JFrame
{
    public static final String SERVERS_FILE_NAME = "btc_servers.cfg";
    private static final Logger logger = Logger.getLogger("BukkitTelnetClient");
    private BTC_ConnectionManager connection_mgr;
    private final LinkedList<String> server_list = new LinkedList<>();
    private static BukkitTelnetClient btc = null;

    private static void setup()
    {
        btc = new BukkitTelnetClient();

        btc.initComponents();

        btc.setLocationRelativeTo(null);

        btc.txt_server.getEditor().getEditorComponent().addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyTyped(KeyEvent e)
            {
                if (e.getKeyChar() == KeyEvent.VK_ENTER)
                {
                    btc.saveServersAndTriggerConnect();
                }
            }
        });

        btc.redirectSystemStreams();

        btc.loadServerList();

        btc.connection_mgr = new BTC_ConnectionManager(btc);

        btc.setVisible(true);
    }

    private void updateTextPane(final String text)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                Document doc = main_output.getDocument();
                try
                {
                    doc.insertString(doc.getLength(), text, null);
                }
                catch (BadLocationException e)
                {
                    throw new RuntimeException(e);
                }
                if (btc.chkAutoScroll.isSelected() && btc.main_output.getSelectedText() == null)
                {
                    btc.main_output.setCaretPosition(doc.getLength() - 1);
                }
            }
        });
    }

    private void redirectSystemStreams()
    {
        OutputStream out = new OutputStream()
        {
            @Override
            public void write(final int b) throws IOException
            {
                updateTextPane(String.valueOf((char) b));
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException
            {
                updateTextPane(new String(b, off, len));
            }

            @Override
            public void write(byte[] b) throws IOException
            {
                write(b, 0, b.length);
            }
        };

        System.setOut(new PrintStream(out, true));
        System.setErr(new PrintStream(out, true));
    }

    @SuppressWarnings("unchecked")
    private void loadServerList()
    {
        try
        {
            server_list.clear();
            txt_server.removeAllItems();

            File file = new File(SERVERS_FILE_NAME);
            if (file.exists())
            {
                try (BufferedReader in = new BufferedReader(new FileReader(file)))
                {
                    String line;
                    while ((line = in.readLine()) != null)
                    {
                        line = line.trim();
                        server_list.add(line);
                        txt_server.addItem(line);
                    }
                }
            }
        }
        catch (Exception ex)
        {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    private void saveServersAndTriggerConnect()
    {
        String selected_server = (String) txt_server.getSelectedItem();

        if (selected_server == null || selected_server.isEmpty())
        {
            System.out.println("Invalid server address.");
            return;
        }

        try
        {
            if (server_list.contains(selected_server))
            {
                server_list.remove(selected_server);
            }

            server_list.addFirst(selected_server);
            try (BufferedWriter out = new BufferedWriter(new FileWriter(new File(SERVERS_FILE_NAME))))
            {
                for (String server : server_list)
                {
                    out.write(server + '\n');
                }
            }
        }
        catch (Exception ex)
        {
            logger.log(Level.SEVERE, null, ex);
        }

        loadServerList();

        connection_mgr.trigger_connect(selected_server);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        jScrollPane1 = new javax.swing.JScrollPane();
        main_output = new javax.swing.JTextPane();
        txt_command = new javax.swing.JTextField();
        btn_connect = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        btn_disconnect = new javax.swing.JButton();
        btn_send = new javax.swing.JButton();
        txt_server = new javax.swing.JComboBox();
        chkAutoScroll = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("BukkitTelnetClient");

        main_output.setEditable(false);
        main_output.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        jScrollPane1.setViewportView(main_output);

        txt_command.setEnabled(false);
        txt_command.addKeyListener(new java.awt.event.KeyAdapter()
        {
            public void keyPressed(java.awt.event.KeyEvent evt)
            {
                txt_commandKeyPressed(evt);
            }
        });

        btn_connect.setText("Connect");
        btn_connect.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                btn_connectActionPerformed(evt);
            }
        });

        jLabel1.setText("Command:");

        jLabel2.setText("Server:");

        btn_disconnect.setText("Disconnect");
        btn_disconnect.setEnabled(false);
        btn_disconnect.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                btn_disconnectActionPerformed(evt);
            }
        });

        btn_send.setText("Send");
        btn_send.setEnabled(false);
        btn_send.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                btn_sendActionPerformed(evt);
            }
        });

        txt_server.setEditable(true);

        chkAutoScroll.setSelected(true);
        chkAutoScroll.setText("AutoScroll");
        chkAutoScroll.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                chkAutoScrollActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_command, javax.swing.GroupLayout.DEFAULT_SIZE, 483, Short.MAX_VALUE)
                            .addComponent(txt_server, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btn_connect, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btn_send, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btn_disconnect)
                            .addComponent(chkAutoScroll))))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btn_connect, btn_disconnect, btn_send, chkAutoScroll});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 275, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_command, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(btn_send)
                    .addComponent(chkAutoScroll))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(btn_connect)
                    .addComponent(btn_disconnect)
                    .addComponent(txt_server, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txt_commandKeyPressed(java.awt.event.KeyEvent evt)//GEN-FIRST:event_txt_commandKeyPressed
    {//GEN-HEADEREND:event_txt_commandKeyPressed
        if (!txt_command.isEnabled())
        {
            return;
        }
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            connection_mgr.send_command(txt_command.getText());
            txt_command.selectAll();
        }
    }//GEN-LAST:event_txt_commandKeyPressed

    private void chkAutoScrollActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_chkAutoScrollActionPerformed
    {//GEN-HEADEREND:event_chkAutoScrollActionPerformed
        updateTextPane("");
    }//GEN-LAST:event_chkAutoScrollActionPerformed

    private void btn_connectActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btn_connectActionPerformed
    {//GEN-HEADEREND:event_btn_connectActionPerformed
        if (!btn_connect.isEnabled())
        {
            return;
        }
        saveServersAndTriggerConnect();
    }//GEN-LAST:event_btn_connectActionPerformed

    private void btn_disconnectActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btn_disconnectActionPerformed
    {//GEN-HEADEREND:event_btn_disconnectActionPerformed
        if (!btn_disconnect.isEnabled())
        {
            return;
        }
        connection_mgr.trigger_disconnect();
    }//GEN-LAST:event_btn_disconnectActionPerformed

    private void btn_sendActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btn_sendActionPerformed
    {//GEN-HEADEREND:event_btn_sendActionPerformed
        if (!btn_send.isEnabled())
        {
            return;
        }
        connection_mgr.send_command(txt_command.getText());
        txt_command.selectAll();
    }//GEN-LAST:event_btn_sendActionPerformed

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
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex)
        {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String args[])
    {
        findAndSetLookAndFeel("Windows");

        java.awt.EventQueue.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                BukkitTelnetClient.setup();
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_connect;
    private javax.swing.JButton btn_disconnect;
    private javax.swing.JButton btn_send;
    private javax.swing.JCheckBox chkAutoScroll;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextPane main_output;
    private javax.swing.JTextField txt_command;
    private javax.swing.JComboBox txt_server;
    // End of variables declaration//GEN-END:variables

    public javax.swing.JButton getBtn_connect()
    {
        return btn_connect;
    }

    public javax.swing.JButton getBtn_disconnect()
    {
        return btn_disconnect;
    }

    public javax.swing.JButton getBtn_send()
    {
        return btn_send;
    }

    public javax.swing.JTextPane getMain_output()
    {
        return main_output;
    }

    public javax.swing.JTextField getTxt_command()
    {
        return txt_command;
    }

    public javax.swing.JComboBox getTxt_server()
    {
        return txt_server;
    }
}
