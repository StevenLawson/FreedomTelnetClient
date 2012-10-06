/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.StevenLawson.BukkitTelnetClient;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

/**

 @author
 Steven
 */
public class BukkitTelnetClient extends javax.swing.JFrame
{
    private BTC_ConnectionManager connection_mgr;

    public BukkitTelnetClient()
    {
        initComponents();
        redirectSystemStreams();
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

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        main_output = new javax.swing.JTextPane();
        txt_command = new javax.swing.JTextField();
        btn_connect = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txt_server = new javax.swing.JTextField();
        btn_disconnect = new javax.swing.JButton();
        btn_send = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("BukkitTelnetClient");

        main_output.setEditable(false);
        main_output.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        jScrollPane1.setViewportView(main_output);

        txt_command.setEnabled(false);
        txt_command.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_commandKeyPressed(evt);
            }
        });

        btn_connect.setText("Connect");
        btn_connect.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_connectMouseClicked(evt);
            }
        });

        jLabel1.setText("Command:");

        jLabel2.setText("Server:");

        txt_server.setText("tf.madgeekonline.com:28995");
        txt_server.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_serverKeyPressed(evt);
            }
        });

        btn_disconnect.setText("Disconnect");
        btn_disconnect.setEnabled(false);
        btn_disconnect.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_disconnectMouseClicked(evt);
            }
        });

        btn_send.setText("Send");
        btn_send.setEnabled(false);
        btn_send.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_sendMouseClicked(evt);
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
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(txt_command))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(17, 17, 17)
                                .addComponent(txt_server, javax.swing.GroupLayout.DEFAULT_SIZE, 567, Short.MAX_VALUE)))
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
                    .addComponent(txt_server, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(btn_connect)
                    .addComponent(btn_disconnect))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txt_serverKeyPressed(java.awt.event.KeyEvent evt)//GEN-FIRST:event_txt_serverKeyPressed
    {//GEN-HEADEREND:event_txt_serverKeyPressed
        if (!txt_server.isEnabled())
        {
            return;
        }
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            connection_mgr.trigger_connect(txt_server.getText());
        }
    }//GEN-LAST:event_txt_serverKeyPressed

    private void btn_connectMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_btn_connectMouseClicked
    {//GEN-HEADEREND:event_btn_connectMouseClicked
        if (!btn_connect.isEnabled())
        {
            return;
        }
        connection_mgr.trigger_connect(txt_server.getText());
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
        catch (ClassNotFoundException ex)
        {
            java.util.logging.Logger.getLogger(BukkitTelnetClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        catch (InstantiationException ex)
        {
            java.util.logging.Logger.getLogger(BukkitTelnetClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        catch (IllegalAccessException ex)
        {
            java.util.logging.Logger.getLogger(BukkitTelnetClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        catch (javax.swing.UnsupportedLookAndFeelException ex)
        {
            java.util.logging.Logger.getLogger(BukkitTelnetClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
    private javax.swing.JTextField txt_server;
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

    public javax.swing.JTextField getTxt_server()
    {
        return txt_server;
    }
}
