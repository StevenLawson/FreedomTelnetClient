/* 
 * Copyright (C) 2012-2014 Steven Lawson
 *
 * This file is part of FreedomTelnetClient.
 *
 * FreedomTelnetClient is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package me.StevenLawson.BukkitTelnetClient;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.Queue;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.table.AbstractTableModel;
import javax.swing.text.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

public class BTC_MainPanel extends javax.swing.JFrame
{
    private final BTC_ConnectionManager connectionManager = new BTC_ConnectionManager();
    private final List<PlayerInfo> playerList = new ArrayList<>();
    private final PlayerListTableModel playerListTableModel = new PlayerListTableModel(playerList);
    private final Collection<FavoriteButtonEntry> favButtonList = BukkitTelnetClient.config.getFavoriteButtons();

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

        this.getConnectionManager().updateTitle(false);

        this.tblPlayers.setModel(playerListTableModel);

        this.tblPlayers.getRowSorter().toggleSortOrder(0);

        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private final Queue<BTC_TelnetMessage> telnetErrorQueue = new LinkedList<>();
    private boolean isQueueing = false;

    private void flushTelnetErrorQueue()
    {
        BTC_TelnetMessage queuedMessage;
        while ((queuedMessage = telnetErrorQueue.poll()) != null)
        {
            queuedMessage.setColor(Color.GRAY);
            writeToConsoleImmediately(queuedMessage, true);
        }
    }

    public void writeToConsole(final BTC_ConsoleMessage message)
    {
        if (message.getMessage().isEmpty())
        {
            return;
        }

        if (message instanceof BTC_TelnetMessage)
        {
            final BTC_TelnetMessage telnetMessage = (BTC_TelnetMessage) message;

            if (telnetMessage.isInfoMessage())
            {
                isQueueing = false;
                flushTelnetErrorQueue();
            }
            else if (telnetMessage.isErrorMessage() || isQueueing)
            {
                isQueueing = true;
                telnetErrorQueue.add(telnetMessage);
            }

            if (!isQueueing)
            {
                writeToConsoleImmediately(telnetMessage, false);
            }
        }
        else
        {
            isQueueing = false;
            flushTelnetErrorQueue();
            writeToConsoleImmediately(message, false);
        }
    }

    private void writeToConsoleImmediately(final BTC_ConsoleMessage message, final boolean isTelnetError)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                if (isTelnetError && chkIgnoreErrors.isSelected())
                {
                    return;
                }

                final StyledDocument styledDocument = mainOutput.getStyledDocument();

                int startLength = styledDocument.getLength();

                try
                {
                    styledDocument.insertString(
                            styledDocument.getLength(),
                            message.getMessage() + SystemUtils.LINE_SEPARATOR,
                            StyleContext.getDefaultStyleContext().addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, message.getColor())
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
                        if (vScroll.getValue() + vScroll.getModel().getExtent() >= (vScroll.getMaximum() - 50))
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

    public final PlayerInfo getSelectedPlayer()
    {
        final JTable table = BTC_MainPanel.this.tblPlayers;

        final int selectedRow = table.getSelectedRow();
        if (selectedRow < 0 || selectedRow >= playerList.size())
        {
            return null;
        }

        return playerList.get(table.convertRowIndexToModel(selectedRow));
    }

    public static class PlayerListTableModel extends AbstractTableModel
    {
        private final List<PlayerInfo> _playerList;

        public PlayerListTableModel(List<PlayerInfo> playerList)
        {
            this._playerList = playerList;
        }

        @Override
        public int getRowCount()
        {
            return _playerList.size();
        }

        @Override
        public int getColumnCount()
        {
            return PlayerInfo.numColumns;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex)
        {
            if (rowIndex >= _playerList.size())
            {
                return null;
            }

            return _playerList.get(rowIndex).getColumnValue(columnIndex);
        }

        @Override
        public String getColumnName(int columnIndex)
        {
            return columnIndex < getColumnCount() ? PlayerInfo.columnNames[columnIndex] : "null";
        }

        public List<PlayerInfo> getPlayerList()
        {
            return _playerList;
        }
    }

    public final void updatePlayerList(final String selectedPlayerName)
    {
        EventQueue.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                playerListTableModel.fireTableDataChanged();

                BTC_MainPanel.this.txtNumPlayers.setText("" + playerList.size());

                if (selectedPlayerName != null)
                {
                    final JTable table = BTC_MainPanel.this.tblPlayers;
                    final ListSelectionModel selectionModel = table.getSelectionModel();

                    for (PlayerInfo player : playerList)
                    {
                        if (player.getName().equals(selectedPlayerName))
                        {
                            selectionModel.setSelectionInterval(0, table.convertRowIndexToView(playerList.indexOf(player)));
                        }
                    }
                }
            }
        });
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
        private final PlayerCommandEntry command;

        public PlayerListPopupItem_Command(String text, PlayerInfo player, PlayerCommandEntry command)
        {
            super(text, player);
            this.command = command;
        }

        public PlayerCommandEntry getCommand()
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

                if ((SwingUtilities.isRightMouseButton(mouseEvent) || mouseEvent.isControlDown()) && mouseEvent.getComponent() instanceof JTable)
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
                                    final String output = source.getCommand().buildOutput(source.getPlayer(), true);
                                    BTC_MainPanel.this.getConnectionManager().sendDelayedCommand(output, true, 100);
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
                                            BTC_MainPanel.this.writeToConsole(new BTC_ConsoleMessage("Copied IP to clipboard: " + _player.getIp()));
                                            break;
                                        }
                                        case "Copy Name":
                                        {
                                            copyToClipboard(_player.getName());
                                            BTC_MainPanel.this.writeToConsole(new BTC_ConsoleMessage("Copied name to clipboard: " + _player.getName()));
                                            break;
                                        }
                                        case "Copy UUID":
                                        {
                                            copyToClipboard(_player.getUuid());
                                            BTC_MainPanel.this.writeToConsole(new BTC_ConsoleMessage("Copied UUID to clipboard: " + _player.getUuid()));
                                            break;
                                        }
                                    }
                                }
                            }
                        };

                        for (final PlayerCommandEntry command : BukkitTelnetClient.config.getCommands())
                        {
                            final PlayerListPopupItem_Command item = new PlayerListPopupItem_Command(command.getName(), player, command);
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

                        item = new PlayerListPopupItem("Copy UUID", player);
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
        txtServer.removeAllItems();
        for (final ServerEntry serverEntry : BukkitTelnetClient.config.getServers())
        {
            txtServer.addItem(serverEntry);
            if (serverEntry.isLastUsed())
            {
                txtServer.setSelectedItem(serverEntry);
            }
        }
    }

    public final void saveServersAndTriggerConnect()
    {
        final Object selectedItem = txtServer.getSelectedItem();
        if (selectedItem == null)
        {
            return;
        }

        ServerEntry entry;
        if (selectedItem instanceof ServerEntry)
        {
            entry = (ServerEntry) selectedItem;
        }
        else
        {
            final String serverAddress = StringUtils.trimToNull(selectedItem.toString());
            if (serverAddress == null)
            {
                return;
            }

            String serverName = JOptionPane.showInputDialog(this, "Enter server name:", "Server Name", JOptionPane.PLAIN_MESSAGE);
            if (serverName == null)
            {
                return;
            }

            serverName = StringUtils.trimToEmpty(serverName);
            if (serverName.isEmpty())
            {
                serverName = "Unnamed";
            }

            entry = new ServerEntry(serverName, serverAddress);

            BukkitTelnetClient.config.getServers().add(entry);
        }

        for (final ServerEntry existingEntry : BukkitTelnetClient.config.getServers())
        {
            if (entry.equals(existingEntry))
            {
                entry = existingEntry;
            }
            existingEntry.setLastUsed(false);
        }

        entry.setLastUsed(true);

        BukkitTelnetClient.config.save();

        loadServerList();

        getConnectionManager().triggerConnect(entry.getAddress());
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
        txtServer = new javax.swing.JComboBox<me.StevenLawson.BukkitTelnetClient.ServerEntry>();
        chkAutoScroll = new javax.swing.JCheckBox();
        txtCommand = new javax.swing.JTextField();
        btnConnect = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        tblPlayersScroll = new javax.swing.JScrollPane();
        tblPlayers = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        txtNumPlayers = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        chkIgnorePlayerCommands = new javax.swing.JCheckBox();
        chkIgnoreServerCommands = new javax.swing.JCheckBox();
        chkShowChatOnly = new javax.swing.JCheckBox();
        chkIgnoreErrors = new javax.swing.JCheckBox();
        jPanel4 = new javax.swing.JPanel();
        favoriteButtonsPanelHolder = new javax.swing.JPanel();
        favoriteButtonsPanelScroll = new javax.swing.JScrollPane();
        favoriteButtonsPanel = new BTC_FavoriteButtonsPanel(favButtonList);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("BukkitTelnetClient");

        splitPane.setResizeWeight(1.0);

        mainOutput.setEditable(false);
        mainOutput.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
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
                            .addComponent(txtServer, 0, 431, Short.MAX_VALUE))
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
                .addComponent(mainOutputScoll, javax.swing.GroupLayout.DEFAULT_SIZE, 365, Short.MAX_VALUE)
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
        tblPlayers.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblPlayersScroll.setViewportView(tblPlayers);
        tblPlayers.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        jLabel3.setText("# Players:");

        txtNumPlayers.setEditable(false);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tblPlayersScroll, javax.swing.GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtNumPlayers, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tblPlayersScroll, javax.swing.GroupLayout.DEFAULT_SIZE, 369, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtNumPlayers, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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

        chkIgnoreErrors.setText("Ignore warnings and errors");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkIgnorePlayerCommands)
                    .addComponent(chkIgnoreServerCommands)
                    .addComponent(chkShowChatOnly)
                    .addComponent(chkIgnoreErrors))
                .addContainerGap(76, Short.MAX_VALUE))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkIgnoreErrors, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(323, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Filters", jPanel1);

        favoriteButtonsPanelHolder.setLayout(new java.awt.BorderLayout());

        favoriteButtonsPanelScroll.setBorder(null);

        favoriteButtonsPanel.setLayout(null);
        favoriteButtonsPanelScroll.setViewportView(favoriteButtonsPanel);

        favoriteButtonsPanelHolder.add(favoriteButtonsPanelScroll, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(favoriteButtonsPanelHolder, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(favoriteButtonsPanelHolder, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Commands", jPanel4);

        splitPane.setRightComponent(jTabbedPane1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(splitPane)
                .addGap(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(splitPane)
                .addGap(0, 0, 0))
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
            getConnectionManager().sendCommand(txtCommand.getText());
            txtCommand.selectAll();
        }
    }//GEN-LAST:event_txtCommandKeyPressed

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
        getConnectionManager().triggerDisconnect();
    }//GEN-LAST:event_btnDisconnectActionPerformed

    private void btnSendActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnSendActionPerformed
    {//GEN-HEADEREND:event_btnSendActionPerformed
        if (!btnSend.isEnabled())
        {
            return;
        }
        getConnectionManager().sendCommand(txtCommand.getText());
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
    private javax.swing.JCheckBox chkIgnoreErrors;
    private javax.swing.JCheckBox chkIgnorePlayerCommands;
    private javax.swing.JCheckBox chkIgnoreServerCommands;
    private javax.swing.JCheckBox chkShowChatOnly;
    private javax.swing.JPanel favoriteButtonsPanel;
    private javax.swing.JPanel favoriteButtonsPanelHolder;
    private javax.swing.JScrollPane favoriteButtonsPanelScroll;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextPane mainOutput;
    private javax.swing.JScrollPane mainOutputScoll;
    private javax.swing.JSplitPane splitPane;
    private javax.swing.JTable tblPlayers;
    private javax.swing.JScrollPane tblPlayersScroll;
    private javax.swing.JTextField txtCommand;
    private javax.swing.JTextField txtNumPlayers;
    private javax.swing.JComboBox<me.StevenLawson.BukkitTelnetClient.ServerEntry> txtServer;
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

    public javax.swing.JComboBox<ServerEntry> getTxtServer()
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

    public JCheckBox getChkIgnoreErrors()
    {
        return chkIgnoreErrors;
    }

    public List<PlayerInfo> getPlayerList()
    {
        return playerList;
    }

    public BTC_ConnectionManager getConnectionManager()
    {
        return connectionManager;
    }
}
