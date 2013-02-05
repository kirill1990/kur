package torrent;

import jBittorrentAPI.Constants;
import jBittorrentAPI.DownloadManager;
import jBittorrentAPI.TorrentFile;
import jBittorrentAPI.TorrentProcessor;
import jBittorrentAPI.Utils;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Random;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import extend.TorrentPlay;

public class Torrent
{
	private JPanel			panel		= new JPanel();

	private JLabel			title		= new JLabel();
	private JLabel			rate		= new JLabel();
	private JLabel			status		= new JLabel();

	private JProgressBar	progress	= new JProgressBar();
	private JList			list		= null;

	private File			torrentFile	= null;
	public String			pathTorrent	= null;
	
	private Vector<String>	peerList	= new Vector<String>();

	private String			created		= "";

	private boolean			isActive	= false;

	TorrentPlay				doPlay		= null;

	public Torrent()
	{
		// super();
	}

	/**
	 * Задаёт цвет всех надписей
	 * 
	 * @param color
	 *            - новый цвет
	 */
	public void setForegroundColor(Color color)
	{
		title.setForeground(color);
		rate.setForeground(color);
		status.setForeground(color);
	}

	public void setTorrentFile(File torrentFile)
	{
		this.torrentFile = torrentFile;
	}

	public void setProgressBar(JProgressBar progress)
	{
		this.progress = progress;
	}

	public void setPathTorrent(String pathTorrent)
	{
		this.pathTorrent = pathTorrent;
	}

	public void setList(JList list)
	{
		this.list = list;
	}

	public boolean getActive()
	{
		return this.isActive;
	}

	public String getName()
	{
		return torrentFile.getName();
	}

	public boolean remove()
	{
		cancelDownload();

		File file = new File(pathTorrent);

		System.out.println("File remove: "+file.getAbsolutePath());
		if (!file.exists())
			return false;
		if (file.isDirectory())
		{
			for (File f : file.listFiles())
				f.delete();
			file.delete();
		}
		else
		{
			file.delete();
		}
		
		
		return true;
	}

	public Integer getHash()
	{
		return torrentFile.hashCode();
	}

	public void ini()
	{
		panel.setLayout(new GridLayout(3, 1));
		panel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

		if (this.torrentFile != null)
		{
			title.setText(this.torrentFile.getName());
			pathTorrent += this.torrentFile.getName().substring(0, this.torrentFile.getName().indexOf(".")) + "/";
		}
		else
		{
			title.setText("error");
		}

		// rate.setText("Устанавливаем соединение...");
		// status.setText("Очень длинная надпись");

		setForegroundColor(Color.BLACK);

		panel.add(title);
		panel.add(rate);
		panel.add(progress);
		// panel.add(status);
	}

	public JPanel getPanel()
	{
		return this.panel;
	}

	public void play()
	{
		this.doPlay = new TorrentPlay();
		// this.rate.setText("Проверка...");

		
		this.doPlay.setList(list);
		this.doPlay.setProgressBar(progress);
		this.doPlay.setRate(rate);

		this.doPlay.setTorrentFile(torrentFile);
		this.doPlay.setPathTorrent(pathTorrent);

		this.doPlay.execute();

		this.isActive = true;
		try
		{
			Thread.sleep(500);
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		created = this.doPlay.getCreated();
		
		peerList = this.doPlay.getPeerList();	
	}

	public void cancelDownload()
	{
		this.doPlay.cancelDownload();

		rate.setText("Приостановлен...");

		this.isActive = false;
	}

	public String getCreated()
	{
		return created;
	}

	public Vector<String> getPeerList()
	{
		// TODO Auto-generated method stub
		return this.peerList;
	}
}
