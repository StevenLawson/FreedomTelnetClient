package me.StevenLawson.BukkitTelnetClient;

import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;
import javax.swing.JCheckBox;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
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

        final URL icon = this.getClass().getResource("/icon.png");
        if (icon != null)
        {
            setIconImage(Toolkit.getDefaultToolkit().createImage(icon));
        }

        this.splitPane.setResizeWeight(1.0);

        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public final void updateTextPane(final String text)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                final StyledDocument styledDocument = mainOutput.getStyledDocument();

                try
                {
                    styledDocument.insertString(
                            styledDocument.getLength(),
                            text,
                            StyleContext.getDefaultStyleContext().addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, BTC_FormatHandler.getColor(text))
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

    public final void updatePlayerList(final Map<String, BTC_PlayerListDecoder.PlayerInfo> playerList)
    {
        EventQueue.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                final DefaultTableModel model = (DefaultTableModel) BTC_MainPanel.this.tblPlayers.getModel();

                model.setRowCount(0);

                final Iterator<Map.Entry<String, BTC_PlayerListDecoder.PlayerInfo>> it = playerList.entrySet().iterator();
                while (it.hasNext())
                {
                    final Map.Entry<String, BTC_PlayerListDecoder.PlayerInfo> entry = it.next();
                    BTC_PlayerListDecoder.PlayerInfo playerInfo = entry.getValue();

                    model.addRow(new Object[]
                    {
                        playerInfo.getName(),
                        playerInfo.getDisplayName(),
                        playerInfo.getIp()
                    });
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

    public JCheckBox getChkAutoScroll()
    {
        return chkAutoScroll;
    }

    public JCheckBox getChkIgnorePlayerCommands()
    {
        return chkIgnorePlayerCommands;
    }

    public JCheckBox getChkIgnoreServerCommands()
    {
        return chkIgnoreServerCommands;
    }

    public JCheckBox getChkShowChatOnly()
    {
        return chkShowChatOnly;
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

        splitPane = new javax.swing.JSplitPane();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        mainOutput = new javax.swing.JTextPane();
        btnDisconnect = new javax.swing.JButton();
        btnSend = new javax.swing.JButton();
        txtServer = new javax.swing.JComboBox();
        chkAutoScroll = new javax.swing.JCheckBox();
        txtCommand = new javax.swing.JTextField();
        btnConnect = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblPlayers = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        chkIgnorePlayerCommands = new javax.swing.JCheckBox();
        chkIgnoreServerCommands = new javax.swing.JCheckBox();
        chkShowChatOnly = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("BukkitTelnetClient");

        splitPane.setDividerLocation(650);

        mainOutput.setEditable(false);
        mainOutput.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        jScrollPane1.setViewportView(mainOutput);

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

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtCommand)
                            .addComponent(txtServer, 0, 378, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnConnect, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnSend, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnDisconnect)
                            .addComponent(chkAutoScroll))))
                .addContainerGap())
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnConnect, btnDisconnect, btnSend, chkAutoScroll});

        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCommand, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(btnSend)
                    .addComponent(chkAutoScroll))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(btnConnect)
                    .addComponent(btnDisconnect)
                    .addComponent(txtServer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        splitPane.setLeftComponent(jPanel3);

        tblPlayers.setAutoCreateRowSorter(true);
        tblPlayers.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][]
            {

            },
            new String []
            {
                "Name", "Display Name", "IP"
            }
        )
        {
            Class[] types = new Class []
            {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean []
            {
                false, false, false
            };

            public Class getColumnClass(int columnIndex)
            {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex)
            {
                return canEdit [columnIndex];
            }
        });
        tblPlayers.setColumnSelectionAllowed(true);
        jScrollPane2.setViewportView(tblPlayers);
        tblPlayers.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 346, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 395, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Player List", jPanel2);

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

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkIgnorePlayerCommands)
                    .addComponent(chkIgnoreServerCommands)
                    .addComponent(chkShowChatOnly))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkIgnorePlayerCommands, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkIgnoreServerCommands, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkShowChatOnly, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(341, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Filters", jPanel1);

        splitPane.setRightComponent(jTabbedPane1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(splitPane)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(splitPane)
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
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextPane mainOutput;
    private javax.swing.JSplitPane splitPane;
    private javax.swing.JTable tblPlayers;
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
