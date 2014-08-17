package me.StevenLawson.BukkitTelnetClient;

import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;
import javax.swing.JCheckBox;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

public class BTC_MainPanel extends javax.swing.JFrame
{
    private static final String SERVERS_FILE_NAME = "btc_servers.cfg";

    public static final ByteArrayOutputStream CONSOLE = new ByteArrayOutputStream();
    public static final PrintStream CONSOLE_STREAM = new PrintStream(CONSOLE);

    private final BTC_ConnectionManager connectionManager = new BTC_ConnectionManager();
    private final LinkedList<String> serverList = new LinkedList<>();

    public BTC_MainPanel()
    {
        initComponents();
    }

    public void setup()
    {
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

        setupTablePopup();

        this.connectionManager.updateTitle(false);

        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public final void updateTextPane(final String line)
    {
        if (line.isEmpty())
        {
            return;
        }

        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                final StyledDocument styledDocument = mainOutput.getStyledDocument();

                int startLength = styledDocument.getLength();

                try
                {
                    styledDocument.insertString(
                            styledDocument.getLength(),
                            line,
                            StyleContext.getDefaultStyleContext().addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, BTC_FormatHandler.getColor(line))
                    );
                }
                catch (BadLocationException ex)
                {
                    throw new RuntimeException(ex);
                }

                if (BTC_MainPanel.this.chkAutoScroll.isSelected() && BTC_MainPanel.this.mainOutput.getSelectedText() == null)
                {
                    final JScrollBar vScroll = mainOutputScoll.getVerticalScrollBar();

                    if (!vScroll.getValueIsAdjusting())
                    {
                        if (vScroll.getValue() + vScroll.getModel().getExtent() >= (vScroll.getMaximum() - 10))
                        {
                            BTC_MainPanel.this.mainOutput.setCaretPosition(startLength);

                            final Timer timer = new Timer(10, new ActionListener()
                            {
                                @Override
                                public void actionPerformed(ActionEvent ae)
                                {
                                    vScroll.setValue(vScroll.getMaximum());
                                }
                            });
                            timer.setRepeats(false);
                            timer.start();
                        }
                    }
                }
            }
        });
    }

    public final void updateConsole()
    {
        final String data = CONSOLE.toString();
        CONSOLE.reset();

        final String[] lines = data.split("\\n");
        for (String line : lines)
        {
            if (!line.isEmpty())
            {
                updateTextPane(line + '\n');
            }
        }
    }

    public final void writeToConsole(String line)
    {
        CONSOLE_STREAM.append(line);
        updateConsole();
    }

    public final PlayerInfo getSelectedPlayer()
    {
        String name = null;
        String ip = null;
        String displayName = null;

        final JTable table = BTC_MainPanel.this.tblPlayers;
        final DefaultTableModel model = (DefaultTableModel) table.getModel();

        final int selectedRow = table.getSelectedRow();
        if (selectedRow < 0)
        {
            return null;
        }

        for (int col = 0; col <= 2; col++)
        {
            final int modelRow = table.convertRowIndexToModel(selectedRow);
            final int modelCol = table.convertColumnIndexToModel(col);

            final String colName = model.getColumnName(modelCol);
            final Object value = model.getValueAt(modelRow, modelCol);

            if (null != colName)
            {
                switch (colName)
                {
                    case "Name":
                    {
                        name = value.toString();
                        break;
                    }
                    case "IP":
                    {
                        ip = value.toString();
                        break;
                    }
                    case "Display Name":
                    {
                        displayName = value.toString();
                        break;
                    }
                }
            }
        }

        if (name != null && ip != null & displayName != null)
        {
            return new PlayerInfo(name, ip, displayName);
        }
        else
        {
            return null;
        }
    }

    public final void updatePlayerList(final Map<String, PlayerInfo> playerList)
    {
        EventQueue.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                final JTable table = BTC_MainPanel.this.tblPlayers;
                final DefaultTableModel model = (DefaultTableModel) table.getModel();

                final PlayerInfo player = getSelectedPlayer();

                model.setRowCount(0);

                final Iterator<Map.Entry<String, PlayerInfo>> it = playerList.entrySet().iterator();
                while (it.hasNext())
                {
                    final Map.Entry<String, PlayerInfo> entry = it.next();
                    PlayerInfo playerInfo = entry.getValue();

                    model.addRow(new Object[]
                    {
                        playerInfo.getName(),
                        playerInfo.getDisplayName(),
                        playerInfo.getIp()
                    });
                }

                if (player != null)
                {
                    final int modelCol = table.convertColumnIndexToModel(0);
                    final int rowCount = table.getRowCount();
                    for (int row = 0; row < rowCount; row++)
                    {
                        if (player.getName().equals(table.getValueAt(row, modelCol).toString()))
                        {
                            final ListSelectionModel selectionModel = table.getSelectionModel();
                            selectionModel.setSelectionInterval(0, row);
                            break;
                        }
                    }
                }
            }
        });
    }

    public static enum ServerCommand
    {
        BAN("Ban", "glist ban %s"),
        MUTE("Toggle Mute", "mute %s"),
        KICK("Kick", "tempban %s 10s Kicked"),
        TEMPBAN("Tempban 5m", "tempban %s 5m"),
        SMITE("Smite", "smite %s"),
        OP("Op", "op %s"),
        DEOP("Deop", "deop %s"),
        GTFO("GTFO", "gtfo %s");

        private final String commandName;
        private final String commandFormat;

        private ServerCommand(String commandName, String commandFormat)
        {
            this.commandName = commandName;
            this.commandFormat = commandFormat;
        }

        public String getCommandName()
        {
            return commandName;
        }

        public String getCommandFormat()
        {
            return commandFormat;
        }
    }

    public static class PlayerListPopupItem extends JMenuItem
    {
        private final PlayerInfo player;

        public PlayerListPopupItem(String text, PlayerInfo player)
        {
            super(text);
            this.player = player;
        }

        public PlayerInfo getPlayer()
        {
            return player;
        }
    }

    public static class PlayerListPopupItem_Command extends PlayerListPopupItem
    {
        private final ServerCommand command;

        public PlayerListPopupItem_Command(String text, PlayerInfo player, ServerCommand command)
        {
            super(text, player);
            this.command = command;
        }

        public ServerCommand getCommand()
        {
            return command;
        }
    }

    public final void setupTablePopup()
    {
        this.tblPlayers.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseReleased(final MouseEvent mouseEvent)
            {
                final JTable table = BTC_MainPanel.this.tblPlayers;

                final int r = table.rowAtPoint(mouseEvent.getPoint());
                if (r >= 0 && r < table.getRowCount())
                {
                    table.setRowSelectionInterval(r, r);
                }
                else
                {
                    table.clearSelection();
                }

                final int rowindex = table.getSelectedRow();
                if (rowindex < 0)
                {
                    return;
                }
                if (mouseEvent.isPopupTrigger() && mouseEvent.getComponent() instanceof JTable)
                {
                    final PlayerInfo player = getSelectedPlayer();
                    if (player != null)
                    {
                        final JPopupMenu popup = new JPopupMenu(player.getName());

                        final JMenuItem header = new JMenuItem("Apply action to " + player.getName() + ":");
                        header.setEnabled(false);
                        popup.add(header);

                        popup.addSeparator();

                        final ActionListener popupAction = new ActionListener()
                        {
                            @Override
                            public void actionPerformed(ActionEvent actionEvent)
                            {
                                Object _source = actionEvent.getSource();
                                if (_source instanceof PlayerListPopupItem_Command)
                                {
                                    final PlayerListPopupItem_Command source = (PlayerListPopupItem_Command) _source;

                                    final PlayerInfo _player = source.getPlayer();
                                    final ServerCommand _command = source.getCommand();

                                    final String output = String.format(_command.getCommandFormat(), _player.getName());

                                    BTC_MainPanel.this.connectionManager.sendDelayedCommand(output, true, 100);
                                }
                                else if (_source instanceof PlayerListPopupItem)
                                {
                                    final PlayerListPopupItem source = (PlayerListPopupItem) _source;

                                    final PlayerInfo _player = source.getPlayer();

                                    switch (actionEvent.getActionCommand())
                                    {
                                        case "Copy IP":
                                        {
                                            copyToClipboard(_player.getIp());
                                            BTC_MainPanel.this.writeToConsole("Copied IP to clipboard: " + _player.getIp());
                                            break;
                                        }
                                        case "Copy Name":
                                        {
                                            copyToClipboard(_player.getName());
                                            BTC_MainPanel.this.writeToConsole("Copied name to clipboard: " + _player.getName());
                                            break;
                                        }
                                    }
                                }
                            }
                        };

                        for (final ServerCommand command : ServerCommand.values())
                        {
                            final PlayerListPopupItem_Command item = new PlayerListPopupItem_Command(command.getCommandName(), player, command);
                            item.addActionListener(popupAction);
                            popup.add(item);
                        }

                        popup.addSeparator();

                        JMenuItem item;

                        item = new PlayerListPopupItem("Copy Name", player);
                        item.addActionListener(popupAction);
                        popup.add(item);

                        item = new PlayerListPopupItem("Copy IP", player);
                        item.addActionListener(popupAction);
                        popup.add(item);

                        popup.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
                    }
                }
            }
        });
    }

    public void copyToClipboard(final String myString)
    {
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(myString), null);
    }

    public final void loadServerList()
    {
        try
        {
            serverList.clear();
            txtServer.removeAllItems();

            final File file = new File(SERVERS_FILE_NAME);
            if (file.exists())
            {
                try (final BufferedReader in = new BufferedReader(new FileReader(file)))
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
        final String selectedServer = (String) txtServer.getSelectedItem();

        if (selectedServer == null || selectedServer.isEmpty())
        {
            writeToConsole("Invalid server address.\n");
            return;
        }

        try
        {
            if (serverList.contains(selectedServer))
            {
                serverList.remove(selectedServer);
            }

            serverList.addFirst(selectedServer);
            try (final BufferedWriter out = new BufferedWriter(new FileWriter(new File(SERVERS_FILE_NAME))))
            {
                for (final String server : serverList)
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

        connectionManager.triggerConnect(selectedServer);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        splitPane = new javax.swing.JSplitPane();
        jPanel3 = new javax.swing.JPanel();
        mainOutputScoll = new javax.swing.JScrollPane();
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

        splitPane.setResizeWeight(1.0);

        mainOutput.setEditable(false);
        mainOutput.setFont(new java.awt.Font("Courier New", 0, 11)); // NOI18N
        mainOutputScoll.setViewportView(mainOutput);

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
                    .addComponent(mainOutputScoll)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtCommand)
                            .addComponent(txtServer, 0, 428, Short.MAX_VALUE))
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
                .addComponent(mainOutputScoll, javax.swing.GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE)
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
        tblPlayers.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane2.setViewportView(tblPlayers);
        tblPlayers.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE)
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
//        updateTextPane("");
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
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextPane mainOutput;
    private javax.swing.JScrollPane mainOutputScoll;
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
}
