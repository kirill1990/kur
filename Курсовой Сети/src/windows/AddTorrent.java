package windows;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.UIManager;

import javax.swing.filechooser.FileFilter;

import torrent.Torrent;

import extend.ExtensionFileFilter;

public class AddTorrent
{
	private File selectedFile = null;
	private String			pathFile		= new File("").getAbsolutePath() + "/download";

	private Vector<Torrent>	torrentList		= null;
	private JList			list			= null;

	public AddTorrent(String file)
	{
		this.selectedFile = new File(file);
		this.pathFile = pathFile;
	}

	/**
	 * Запускает окно выбора файла, затем параметры торрент-файла
	 */
	public void start()
	{
		Torrent torrent = new Torrent();
		
		// передача файла и пути к месту загрузки
		torrent.setTorrentFile(selectedFile);
		torrent.setPathTorrent(pathFile + "/");
		
		// передеча компонента для послед. его обновление
		// (отслеживание параметров торрент файла)
		torrent.setList(list);

		// инициализация панели для JList
		torrent.ini();

		// запуск загрузки
		torrent.play();

		// добавление в список со всеми
		torrentList.add(torrent);
		// добавление в компонент JList
		list.setListData(torrentList);
	}

	/**
	 * Задаёт список с торрентами
	 * 
	 * @param torrentList
	 *            список с торрентами
	 */
	public void setTorrent(Vector<Torrent> torrentList)
	{
		this.torrentList = torrentList;
	}

	/**
	 * Задаёт список с торрентами
	 * 
	 * @param torrentList
	 *            список с торрентами
	 */
	public void setList(JList list)
	{
		this.list = list;
	}

}
