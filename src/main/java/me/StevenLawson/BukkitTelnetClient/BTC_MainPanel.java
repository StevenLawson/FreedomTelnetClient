package me.StevenLawson.BukkitTelnetClient;

import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.regex.Pattern;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

public class BTC_MainPanel extends javax.swing.JFrame
{
    private static final String SERVERS_FILE_NAME = "btc_servers.cfg";

    private final BTC_ConnectionManager connectionManager = new BTC_ConnectionManager();
    private final LinkedList<String> serverList = new LinkedList<>();

    public BTC_MainPanel()
    {
        initComponents();

        this.txtServer.getEditor().getEditorComponent().addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyTyped(KeyEvent e)
            {
                if (e.getKeyChar() == KeyEvent.VK_ENTER)
                {
                    BTC_MainPanel.this.saveServersAndTriggerConnect();
                }
            }
        });

        this.loadServerList();

        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private static final Pattern CHAT_MESSAGE = Pattern.compile("^:\\[.+? INFO\\]: \\<");
    private static final Pattern SAY_MESSAGE = Pattern.compile("^:\\[.+? INFO\\]: \\[Server:");
    private static final Pattern ADMINSAY_MESSAGE = Pattern.compile("^:\\[.+? INFO\\]: \\[TotalFreedomMod\\] \\[ADMIN\\] ");
    private static final Pattern CSAY_MESSAGE = Pattern.compile("^:\\[.+? INFO\\]: \\[CONSOLE\\]<");

    private static final Pattern WORLD_EDIT = Pattern.compile("^:\\[.+? INFO\\]: WorldEdit: ");

    private static final Pattern PREPROCESS_COMMAND = Pattern.compile("^:\\[.+? INFO\\]: \\[PREPROCESS_COMMAND\\] ");
    private static final Color DARK_GREEN = new Color(86, 130, 3);

    private static final Pattern ISSUED_SERVER_COMMAND = Pattern.compile("^:\\[.+? INFO\\]: .+? issued server command: ");
    private static final Pattern PLAYER_COMMAND = Pattern.compile("^:\\[.+? INFO\\]: \\[PLAYER_COMMAND\\] ");

    public final boolean skipLine(String line)
    {
        if (this.chkShowChatOnly.isSelected())
        {
            if (!CHAT_MESSAGE.matcher(line).find() && !SAY_MESSAGE.matcher(line).find() && !ADMINSAY_MESSAGE.matcher(line).find() && !CSAY_MESSAGE.matcher(line).find())
            {
                return true;
            }
        }
        else if (this.chkIgnoreServerCommands.isSelected() && ISSUED_SERVER_COMMAND.matcher(line).find())
        {
            return true;
        }
        else if (this.chkIgnorePlayerCommands.isSelected() && PLAYER_COMMAND.matcher(line).find())
        {
            return true;
        }

        return false;
    }

    public final void updateTextPane(final String text)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                final StyledDocument styledDocument = mainOutput.getStyledDocument();

                Color color = Color.BLACK;
                if (CHAT_MESSAGE.matcher(text).find() || SAY_MESSAGE.matcher(text).find() || ADMINSAY_MESSAGE.matcher(text).find() || CSAY_MESSAGE.matcher(text).find())
                {
                    color = Color.BLUE;
                }
                else if (WORLD_EDIT.matcher(text).find())
                {
                    color = Color.RED;
                }
                else if (PREPROCESS_COMMAND.matcher(text).find())
                {
                    color = DARK_GREEN;
                }

                try
                {
                    styledDocument.insertString(
                            styledDocument.getLength(),
                            text,
                            StyleContext.getDefaultStyleContext().addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, color)
                    );
                }
                catch (BadLocationException ex)
                {
                    throw new RuntimeException(ex);
                }

                if (BTC_MainPanel.this.chkAutoScroll.isSelected() && BTC_MainPanel.this.mainOutput.getSelectedText() == null)
                {
                    BTC_MainPanel.this.mainOutput.setCaretPosition(styledDocument.getLength() - 1);
                }
            }
        });
    }

    public void updateConsole()
    {
        final String data = BukkitTelnetClient.CONSOLE.toString();
        BukkitTelnetClient.CONSOLE.reset();
        BTC_MainPanel.this.updateTextPane(data);
    }

    public final void loadServerList()
    {
        try
        {
            serverList.clear();
            txtServer.removeAllItems();

            File file = new File(SERVERS_FILE_NAME);
            if (file.exists())
            {
                try (BufferedReader in = new BufferedReader(new FileReader(file)))
                {
                    String line;
                    while ((line = in.readLine()) != null)
                    {
                        line = line.trim();
                        serverList.add(line);
                        txtServer.addItem(line);
                    }
                }
            }
        }
        catch (IOException ex)
        {
            BukkitTelnetClient.LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public final void saveServersAndTriggerConnect()
    {
        String selected_server = (String) txtServer.getSelectedItem();

        if (selected_server == null || selected_server.isEmpty())
        {
            System.out.println("Invalid server address.");
            return;
        }

        try
        {
            if (serverList.contains(selected_server))
            {
                serverList.remove(selected_server);
            }

            serverList.addFirst(selected_server);
            try (BufferedWriter out = new BufferedWriter(new FileWriter(new File(SERVERS_FILE_NAME))))
            {
                for (String server : serverList)
                {
                    out.write(server + '\n');
                }
            }
        }
        catch (IOException ex)
        {
            BukkitTelnetClient.LOGGER.log(Level.SEVERE, null, ex);
        }

        loadServerList();

        connectionManager.triggerConnect(selected_server);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        jScrollPane1 = new javax.swing.JScrollPane();
        mainOutput = new javax.swing.JTextPane();
        txtCommand = new javax.swing.JTextField();
        btnConnect = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        btnDisconnect = new javax.swing.JButton();
        btnSend = new javax.swing.JButton();
        txtServer = new javax.swing.JComboBox();
        chkAutoScroll = new javax.swing.JCheckBox();
        chkIgnorePlayerCommands = new javax.swing.JCheckBox();
        chkIgnoreServerCommands = new javax.swing.JCheckBox();
        chkShowChatOnly = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("BukkitTelnetClient");

        mainOutput.setEditable(false);
        mainOutput.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        jScrollPane1.setViewportView(mainOutput);

        txtCommand.setEnabled(false);
        txtCommand.addKeyListener(new java.awt.event.KeyAdapter()
        {
            public void keyPressed(java.awt.event.KeyEvent evt)
            {
                txtCommandKeyPressed(evt);
            }
        });

        btnConnect.setText("Connect");
        btnConnect.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                btnConnectActionPerformed(evt);
            }
        });

        jLabel1.setText("Command:");

        jLabel2.setText("Server:");

        btnDisconnect.setText("Disconnect");
        btnDisconnect.setEnabled(false);
        btnDisconnect.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                btnDisconnectActionPerformed(evt);
            }
        });

        btnSend.setText("Send");
        btnSend.setEnabled(false);
        btnSend.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                btnSendActionPerformed(evt);
            }
        });

        txtServer.setEditable(true);

        chkAutoScroll.setSelected(true);
        chkAutoScroll.setText("AutoScroll");
        chkAutoScroll.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                chkAutoScrollActionPerformed(evt);
            }
        });

        chkIgnorePlayerCommands.setSelected(true);
        chkIgnorePlayerCommands.setText("Ignore \"[PLAYER_COMMAND]\" messages");

        chkIgnoreServerCommands.setSelected(true);
        chkIgnoreServerCommands.setText("Ignore \"issued server command\" messages");

        chkShowChatOnly.setText("Show chat only");
        chkShowChatOnly.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                chkShowChatOnlyActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(chkIgnorePlayerCommands)
                        .addGap(18, 18, 18)
                        .addComponent(chkIgnoreServerCommands)
                        .addGap(18, 18, 18)
                        .addComponent(chkShowChatOnly)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtCommand, javax.swing.GroupLayout.DEFAULT_SIZE, 680, Short.MAX_VALUE)
                            .addComponent(txtServer, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnConnect, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnSend, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnDisconnect)
                            .addComponent(chkAutoScroll))))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnConnect, btnDisconnect, btnSend, chkAutoScroll});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 338, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkIgnorePlayerCommands, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkIgnoreServerCommands, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkShowChatOnly, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCommand, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(btnSend)
                    .addComponent(chkAutoScroll))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(btnConnect)
                    .addComponent(btnDisconnect)
                    .addComponent(txtServer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtCommandKeyPressed(java.awt.event.KeyEvent evt)//GEN-FIRST:event_txtCommandKeyPressed
    {//GEN-HEADEREND:event_txtCommandKeyPressed
        if (!txtCommand.isEnabled())
        {
            return;
        }
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            connectionManager.sendCommand(txtCommand.getText());
            txtCommand.selectAll();
        }
    }//GEN-LAST:event_txtCommandKeyPressed

    private void chkAutoScrollActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_chkAutoScrollActionPerformed
    {//GEN-HEADEREND:event_chkAutoScrollActionPerformed
        updateTextPane("");
    }//GEN-LAST:event_chkAutoScrollActionPerformed

    private void btnConnectActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnConnectActionPerformed
    {//GEN-HEADEREND:event_btnConnectActionPerformed
        if (!btnConnect.isEnabled())
        {
            return;
        }
        saveServersAndTriggerConnect();
    }//GEN-LAST:event_btnConnectActionPerformed

    private void btnDisconnectActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnDisconnectActionPerformed
    {//GEN-HEADEREND:event_btnDisconnectActionPerformed
        if (!btnDisconnect.isEnabled())
        {
            return;
        }
        connectionManager.triggerDisconnect();
    }//GEN-LAST:event_btnDisconnectActionPerformed

    private void btnSendActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnSendActionPerformed
    {//GEN-HEADEREND:event_btnSendActionPerformed
        if (!btnSend.isEnabled())
        {
            return;
        }
        connectionManager.sendCommand(txtCommand.getText());
        txtCommand.selectAll();
    }//GEN-LAST:event_btnSendActionPerformed

    private void chkShowChatOnlyActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_chkShowChatOnlyActionPerformed
    {//GEN-HEADEREND:event_chkShowChatOnlyActionPerformed
        boolean enable = !chkShowChatOnly.isSelected();
        chkIgnorePlayerCommands.setEnabled(enable);
        chkIgnoreServerCommands.setEnabled(enable);
    }//GEN-LAST:event_chkShowChatOnlyActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnConnect;
    private javax.swing.JButton btnDisconnect;
    private javax.swing.JButton btnSend;
    private javax.swing.JCheckBox chkAutoScroll;
    private javax.swing.JCheckBox chkIgnorePlayerCommands;
    private javax.swing.JCheckBox chkIgnoreServerCommands;
    private javax.swing.JCheckBox chkShowChatOnly;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextPane mainOutput;
    private javax.swing.JTextField txtCommand;
    private javax.swing.JComboBox txtServer;
    // End of variables declaration//GEN-END:variables

    public javax.swing.JButton getBtnConnect()
    {
        return btnConnect;
    }

    public javax.swing.JButton getBtnDisconnect()
    {
        return btnDisconnect;
    }

    public javax.swing.JButton getBtnSend()
    {
        return btnSend;
    }

    public javax.swing.JTextPane getMainOutput()
    {
        return mainOutput;
    }

    public javax.swing.JTextField getTxtCommand()
    {
        return txtCommand;
    }

    public javax.swing.JComboBox getTxtServer()
    {
        return txtServer;
    }
}
