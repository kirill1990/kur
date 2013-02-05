package windows;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import client.Client;
import client.DelTorrent;

import extend.TorrentListCellRenderer;

import torrent.Torrent;

public class BitTorrent extends JFrame
{
	public final static int			WIDTH		= 70;
	public final static int			HEIGHT		= 410;

	public static String			address		= "127.0.0.1";

	private static JList			list		= null;

	private JButton					playButton	= null;
	private JButton					pauseButton	= null;
	private JButton					delButton	= null;

	public static Vector<Torrent>	torrentList	= new Vector<Torrent>();

	public BitTorrent()
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e)
		{
		}

		// размер окна
		this.setSize(WIDTH, HEIGHT);

		// выставляем окно по середине рабочего стола
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((screenSize.width - WIDTH) / 2, (screenSize.height - HEIGHT) / 2);

		// Титульная строка
		this.setTitle("Курсовой проект \"Сети и ТК\" : Демьянов К. А. ЭВМ-92");

		// Меняем иконку чашки на утку
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("images/icon.gif")));

		// Уничтожение приложения, после закрытие осн. формы
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Меню бар
		// this.setJMenuBar(createMenuBar());

		// Основная панель
		this.setContentPane(createMainPanel());

		// Проявление окна
		this.setVisible(true);

		// отрисовка
		this.validate();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			FileReader fr = new FileReader("client.xml");

			Document rDoc = new SAXBuilder().build(fr);

			@SuppressWarnings("unchecked") List<Element> client = rDoc.getRootElement().getChildren();

			address = client.get(0).getValue();

		}
		catch (Exception ex)
		{
			System.out.println(ex.getMessage());
		}

		Random random = new Random();
		// TODO Auto-generated method stub
		new BitTorrent();

		Timer timer1 = new Timer();
		long delay1 = 10 * 1000;

		timer1.schedule(new Client(torrentList, list, address), 0, delay1);
	}

	/**
	 * Создание меню бара
	 * 
	 * @return меню
	 */
	public JMenuBar createMenuBar()
	{
		Font font = new Font("Verdana", Font.PLAIN, 14);

		JMenuBar menuBar = new JMenuBar();

		JMenu fileMenu = new JMenu("Файл");
		fileMenu.setFont(font);

		JMenu newMenu = new JMenu("Открыть");
		newMenu.setFont(font);
		fileMenu.add(newMenu);

		JMenuItem txtFileItem = new JMenuItem("Text file");
		txtFileItem.setFont(font);
		newMenu.add(txtFileItem);

		JMenuItem imgFileItem = new JMenuItem("Image file");
		imgFileItem.setFont(font);
		newMenu.add(imgFileItem);

		JMenuItem folderItem = new JMenuItem("Folder");
		folderItem.setFont(font);
		newMenu.add(folderItem);

		JMenuItem openItem = new JMenuItem("Open");
		openItem.setFont(font);
		fileMenu.add(openItem);

		JMenuItem closeItem = new JMenuItem("Close");
		closeItem.setFont(font);
		fileMenu.add(closeItem);

		JMenuItem closeAllItem = new JMenuItem("Close all");
		closeAllItem.setFont(font);
		fileMenu.add(closeAllItem);

		JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.setFont(font);
		fileMenu.add(exitItem);

		exitItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				System.exit(0);
			}
		});

		menuBar.add(fileMenu);

		return menuBar;
	}

	/**
	 * Создание основной панели с инструментами
	 * 
	 * @return основная панель
	 */
	public JPanel createMainPanel()
	{
		JPanel panel = new JPanel();

		panel.setLayout(new BorderLayout(5, 5));
		panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

		panel.add(createToolBar(), BorderLayout.NORTH);
		panel.add(createLsit(), BorderLayout.CENTER);
		panel.add(createStatus(), BorderLayout.SOUTH);

		return panel;
	}

	/**
	 * Создание панели инструментов для работы с 1ним торрентом
	 * 
	 * @return панель инструментов
	 */
	public JToolBar createToolBar()
	{
		JToolBar toolbar = new JToolBar();

		// Добавление таблиц
		JButton addButton = new JButton("Создать");
		addButton.setToolTipText("Добавление торрента");
		addButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				new PublishTorrent(address);
			}

		});
		toolbar.add(addButton, null);

		delButton = new JButton("Удалить");
		delButton.setEnabled(false);
		delButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				Object[] selected = list.getSelectedValues();

				// Iterate all selected items
				for (int i = 0; i < selected.length; i++)
				{
					Torrent sel = (Torrent) selected[i];
					new DelTorrent(address, sel.getName(), sel.getCreated()).run();
				}

			}

		});
		toolbar.add(delButton, null);

		playButton = new JButton("Старт");
		pauseButton = new JButton("Стоп");

		toolbar.add(playButton, null);
		toolbar.add(pauseButton, null);

		playButton.addActionListener(new ActionListener()
		{
			/**
			 * Запуск загрузки торрент файла
			 */
			public void actionPerformed(ActionEvent arg0)
			{

				start();
			}

		});

		pauseButton.addActionListener(new ActionListener()
		{
			/**
			 * Остановка загрузки торрент файлов
			 */
			public void actionPerformed(ActionEvent arg0)
			{
				stop();
			}

		});

		playButton.setEnabled(false);
		pauseButton.setEnabled(false);

		return toolbar;
	}

	protected void stop()
	{
		// TODO Auto-generated method stub
		playButton.setEnabled(true);
		pauseButton.setEnabled(false);

		// Get all selected items
		Object[] selected = list.getSelectedValues();

		// Iterate all selected items
		for (int i = 0; i < selected.length; i++)
		{
			Torrent sel = (Torrent) selected[i];
			sel.cancelDownload();
		}
	}

	protected void start()
	{
		// TODO Auto-generated method stub
		playButton.setEnabled(false);
		pauseButton.setEnabled(true);

		// Get all selected items
		Object[] selected = list.getSelectedValues();

		// Iterate all selected items
		for (int i = 0; i < selected.length; i++)
		{
			Torrent sel = (Torrent) selected[i];
			sel.play();
		}
	}

	/**
	 * Создание списка со всеми торрентами
	 * 
	 * @return список торрентов
	 */
	public JScrollPane createLsit()
	{
		list = new JList();

		list.setCellRenderer(new TorrentListCellRenderer());

		list.setListData(torrentList);
		list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		list.addMouseListener(new MouseAdapter()
		{
			public void mouseReleased(MouseEvent Me)
			{
				if (0 < list.getSelectedValues().length && Me.isMetaDown())
				{
					JPopupMenu Pmenu = new JPopupMenu();

					Torrent tor = (Torrent) list.getSelectedValue();

					JMenuItem start = new JMenuItem("Старт");
					if (playButton.isEnabled())
					{
						Pmenu.add(start);
					}
					start.addActionListener(new ActionListener()
					{
						public void actionPerformed(ActionEvent e)
						{
							start();
						}
					});

					JMenuItem stop = new JMenuItem("Стоп");
					if (pauseButton.isEnabled())
					{
						Pmenu.add(stop);
					}

					stop.addActionListener(new ActionListener()
					{
						public void actionPerformed(ActionEvent e)
						{
							stop();
						}
					});

					// удаляем выделенные элементы
					JMenuItem delRecords = new JMenuItem("Удалить");
					Pmenu.add(delRecords);

					// удаление записей
					delRecords.addActionListener(new ActionListener()
					{
						public void actionPerformed(ActionEvent e)
						{
							Torrent tor = (Torrent) list.getSelectedValue();
							new DelTorrent(address, tor.getName(), tor.getCreated()).run();
						}
					});

					if (tor.getPeerList().size() > 0)
					{
						JMenuItem space = new JMenuItem("-------------------------");
						Pmenu.add(space);

						JMenuItem peer = new JMenuItem("Список Peers:");
						Pmenu.add(peer);
					}

					for (int i = 0; i < tor.getPeerList().size(); i++)
					{
						JMenuItem peer = new JMenuItem("IP: " + tor.getPeerList().get(i).toString());
						Pmenu.add(peer);
					}

					// показываем PopUp меню
					Pmenu.show(Me.getComponent(), Me.getX(), Me.getY());
				}
			}
		});

		list.addListSelectionListener(new ListSelectionListener()
		{
			/**
			 * Определяет активность кнопок play и pause
			 */
			public void valueChanged(ListSelectionEvent evt)
			{
				// TODO Auto-generated method stub
				if (!evt.getValueIsAdjusting())
				{
					delButton.setEnabled(true);

					boolean allPlay = false;
					boolean allStop = false;

					JList list = (JList) evt.getSource();

					// Get all selected items
					Object[] selected = list.getSelectedValues();

					// Iterate all selected items
					for (int i = 0; i < selected.length; i++)
					{
						Torrent sel = (Torrent) selected[i];

						if (sel.getActive())
						{
							allStop = true;
						}
						else
						{
							allPlay = true;
						}
					}

					playButton.setEnabled(allPlay);
					pauseButton.setEnabled(allStop);
				}
				else
				{
					delButton.setEnabled(false);
				}
			}

		});

		JScrollPane scroll = new JScrollPane(list);

		return scroll;
	}

	/**
	 * Создание панели статуса.
	 * 
	 * @return панель статуса
	 */
	public JPanel createStatus()
	{
		JPanel status = new JPanel();

		return status;
	}

}
