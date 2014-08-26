package me.StevenLawson.BukkitTelnetClient;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JPanel;

public class BTC_FavoriteButtonsPanel extends JPanel
{
    public BTC_FavoriteButtonsPanel(final List<FavoriteButtonData> buttonList)
    {
        super.setLayout(new GridLayout(0, 2, 1, 1));

        final ActionListener actionListener = new ActionListener()
        {
            @Override
            public void actionPerformed(final ActionEvent event)
            {
                if (BukkitTelnetClient.mainPanel != null)
                {
                    BukkitTelnetClient.mainPanel.getConnectionManager().sendDelayedCommand(event.getActionCommand(), true, 100);
                }
            }
        };

        for (final FavoriteButtonData buttonData : buttonList)
        {
            final JButton button = new JButton();
            button.setText(buttonData.getLabel());
            button.setActionCommand(buttonData.getCommand());
            button.addActionListener(actionListener);

            final Dimension max = button.getMaximumSize();
            max.setSize(max.getWidth(), 10);
            button.setMaximumSize(max);

            add(button);
        }
    }

    @Override
    public void setLayout(LayoutManager lm)
    {
        //Disable
    }
}
