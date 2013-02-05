package extend;

import java.io.File;
import java.util.Vector;

import jBittorrentAPI.Constants;
import jBittorrentAPI.DownloadManager;
import jBittorrentAPI.TorrentFile;
import jBittorrentAPI.TorrentProcessor;
import jBittorrentAPI.Utils;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

public class TorrentPlay extends SwingWorker
{
	TorrentProcessor	tp			= new TorrentProcessor();
	TorrentFile			t			= null;
	DownloadManager		dm			= null;

	private File		torrentFile	= null;
	private String		pathTorrent	= null;

	public JProgressBar	progress	= new JProgressBar();
	private JList		list		= null;
	private JLabel		rate		= null;

	private String		created		= "";


	@Override
	protected Object doInBackground() throws Exception
	{
		tp = new TorrentProcessor();
		t = tp.getTorrentFile(tp.parseTorrent(this.torrentFile.getAbsoluteFile()));

		created = t.createdBy.toString();
		
		Constants.SAVEPATH = pathTorrent;
		dm = new DownloadManager(t, Utils.generateID(), list, rate, progress);

		// dm.setProgressBar(progress);
		// dm.setList(list);
		// dm.setRate(rate);

		dm.startListening(6881, 6889);

		dm.startTrackerUpdate();
		dm.blockUntilCompletion();
		
		dm.stopTrackerUpdate();
		dm.closeTempFiles();
		return null;
	}

	public void cancelDownload()
	{
		dm.setCancel(true);
		dm.stopTrackerUpdate();
		dm.closeTempFiles();

		try
		{
			Thread.currentThread().sleep(2000);
			System.out.println("stop: " + this.torrentFile.getName());
			this.cancel(true);
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setTorrentFile(File torrentFile)
	{
		this.torrentFile = torrentFile;
	}

	public void setPathTorrent(String pathTorrent)
	{
		this.pathTorrent = pathTorrent;
	}

	public void setList(JList list)
	{
		this.list = list;
	}

	public void setProgressBar(JProgressBar progress)
	{
		this.progress = progress;
	}

	public void setRate(JLabel rate)
	{
		this.rate = rate;
	}

	public String getCreated()
	{
		return created;
	}

	public Vector<String> getPeerList()
	{
		Vector<String> peerList = new Vector<String>();
		
		try
		{
			peerList = dm.getPeerList();
		}
		catch (Exception e) 
		{
			// TODO: handle exception
		}
		return peerList;
	}

}
