package extend;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import torrent.Torrent;


public class TorrentListCellRenderer implements ListCellRenderer
{

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
	{
		// TODO Auto-generated method stub

		JPanel result = ((Torrent) value).getPanel();

		if (isSelected)
		{
			result.setBackground(new Color(242, 122, 74));
			((Torrent) value).setForegroundColor(Color.WHITE);
		}
		else
		{
			result.setBackground(Color.WHITE);
			((Torrent) value).setForegroundColor(Color.BLACK);
		}

		return result;
	}

}
