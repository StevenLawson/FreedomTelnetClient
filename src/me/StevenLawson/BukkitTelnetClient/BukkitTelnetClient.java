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
    private final LinkedList<String> server_list = new LinkedList<String>();

    public BukkitTelnetClient()
    {
        initComponents();
        txt_server.getEditor().getEditorComponent().addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyTyped(KeyEvent e)
            {
                if (e.getKeyChar() == KeyEvent.VK_ENTER)
                {
                    saveServersAndTriggerConnect();
                }
            }
        });
        redirectSystemStreams();
        loadServerList();
        connection_mgr = new BTC_ConnectionManager(this);
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
                main_output.setCaretPosition(doc.getLength() - 1);
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

    private void loadServerList()
    {
        try
        {
            server_list.clear();
            txt_server.removeAllItems();

            File file = new File(SERVERS_FILE_NAME);
            if (file.exists())
            {
                BufferedReader in = new BufferedReader(new FileReader(file));
                String line;
                while ((line = in.readLine()) != null)
                {
                    line = line.trim();
                    server_list.add(line);
                    txt_server.addItem(line);
                }
                in.close();
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

            BufferedWriter out = new BufferedWriter(new FileWriter(new File(SERVERS_FILE_NAME)));

            for (String server : server_list)
            {
                out.write(server + '\n');
            }

            out.close();
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
        btn_connect.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                btn_connectMouseClicked(evt);
            }
        });

        jLabel1.setText("Command:");

        jLabel2.setText("Server:");

        btn_disconnect.setText("Disconnect");
        btn_disconnect.setEnabled(false);
        btn_disconnect.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                btn_disconnectMouseClicked(evt);
            }
        });

        btn_send.setText("Send");
        btn_send.setEnabled(false);
        btn_send.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                btn_sendMouseClicked(evt);
            }
        });

        txt_server.setEditable(true);

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
                            .addComponent(txt_command, javax.swing.GroupLayout.DEFAULT_SIZE, 566, Short.MAX_VALUE)
                            .addComponent(txt_server, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btn_connect)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn_disconnect))
                            .addComponent(btn_send, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btn_connect, btn_disconnect});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 334, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_command, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(btn_send))
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

    private void btn_connectMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_btn_connectMouseClicked
    {//GEN-HEADEREND:event_btn_connectMouseClicked
        if (!btn_connect.isEnabled())
        {
            return;
        }
        saveServersAndTriggerConnect();
    }//GEN-LAST:event_btn_connectMouseClicked

    private void btn_disconnectMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_btn_disconnectMouseClicked
    {//GEN-HEADEREND:event_btn_disconnectMouseClicked
        if (!btn_disconnect.isEnabled())
        {
            return;
        }
        connection_mgr.trigger_disconnect();
    }//GEN-LAST:event_btn_disconnectMouseClicked

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

    private void btn_sendMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_btn_sendMouseClicked
    {//GEN-HEADEREND:event_btn_sendMouseClicked
        if (!btn_send.isEnabled())
        {
            return;
        }
        connection_mgr.send_command(txt_command.getText());
        txt_command.selectAll();
    }//GEN-LAST:event_btn_sendMouseClicked

    public static void main(String args[])
    {
        try
        {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels())
            {
                if ("Nimbus".equals(info.getName()))
                {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        }
        catch (Exception ex)
        {
            logger.log(Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                new BukkitTelnetClient().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_connect;
    private javax.swing.JButton btn_disconnect;
    private javax.swing.JButton btn_send;
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
