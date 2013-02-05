package windows;

import jBittorrentAPI.ConnectionManager;
import jBittorrentAPI.TorrentProcessor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.List;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.UIManager;

import javax.swing.filechooser.FileFilter;

import torrent.Torrent;

import extend.ExtensionFileFilter;

public class PublishTorrent extends JFrame
{
	public final static int	WIDTH	= 600;					// ширина
	public final static int	HEIGHT	= 400;					// высота

	private JList			list	= new JList();

	private Vector<String>	name	= new Vector<String>();
	private Vector<String>	path	= new Vector<String>();

	private JTextField		text	= null;

	private String			address	= "127.0.0.1";

	public PublishTorrent(String address)
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e)
		{
		}

		this.address = address;
		this.list.setListData(this.name);

		// Проверяем на нажатие отмены в диалоге
		if (fileChooser() == JFileChooser.CANCEL_OPTION)
		{
			// была нажата отмена
			dispose();
		}
		else
		{
			// размер окна
			this.setSize(WIDTH, HEIGHT);

			// выставляем окно по середине рабочего стола
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			this.setLocation((screenSize.width - WIDTH) / 2, (screenSize.height - HEIGHT) / 2);

			// Титульная строка
			this.setTitle("Параметры торрента");

			// Меняем иконку чашки на утку
			this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("images/icon.gif")));

			// Уничтожение приложения, после закрытие осн. формы
			this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			this.addWindowListener(new WindowAdapter()
			{
				// закрываем программу
				public void windowClosing(WindowEvent e)
				{
					closeFrame();
				}
			});

			// Основная панель
			this.setContentPane(createPanel());

			// Проявление окна
			this.setVisible(true);

			// отрисовка
			this.validate();
		}
	}

	/**
	 * Создание панели с параметрами торрент-файла
	 * 
	 * @return панель
	 */
	private JPanel createPanel()
	{
		JPanel panel = new JPanel();

		panel.setLayout(new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		// Вывод на панель
		panel.add(namePanel(), BorderLayout.NORTH);
		panel.add(listPanel(), BorderLayout.CENTER);
		panel.add(actionTorrent(), BorderLayout.SOUTH);

		return panel;
	}

	/**
	 * Создание панели списка
	 * 
	 * @return панель со списком и функц. кнопками
	 */
	private JPanel listPanel()
	{

		JPanel panel = new JPanel();

		// параметры панели
		panel.setLayout(new BorderLayout(0, 0));
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		// добавление кнопок + и -

		// главная панель кнопок
		JPanel btn = new JPanel();
		btn.setLayout(new BorderLayout(0, 0));

		// подпанель кнопок
		JPanel sub_btn = new JPanel();

		JButton btn_plus = new JButton(" + ");
		JButton btn_minus = new JButton(" - ");

		// добавление в подпанель
		sub_btn.add(btn_plus);
		sub_btn.add(btn_minus);

		// добавление на осн панель кнопок
		btn.add(sub_btn, BorderLayout.EAST);

		// добавление на панель
		panel.add(btn, BorderLayout.NORTH);
		panel.add(new JScrollPane(list), BorderLayout.CENTER);

		// описание действий кнопок

		// удаление элементов из списка
		btn_minus.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				// TODO Auto-generated method stub
				// удаление из списка выбранных файлов

				if (list.isSelectedIndex(list.getSelectedIndex()))
				{
					path.removeElementAt(list.getSelectedIndex());
					name.removeElementAt(list.getSelectedIndex());
				}

				list.repaint();
				list.validate();
			}

		});

		// добавление элементов
		btn_plus.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				// TODO Auto-generated method stub
				fileChooser();
			}
		});

		return panel;
	}

	/**
	 * Создание панели для ввода названия торрент файла
	 * 
	 * @return панель с именем нового торрент файла
	 */
	private JPanel namePanel()
	{
		JPanel panel = new JPanel();

		// параметры панели
		panel.setLayout(new BorderLayout(20, 20));
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		// Описание кнопки
		JLabel label = new JLabel("Название торрент файла:");
		text = new JTextField(".torrent");

		// положение курсора(каретки) до точки
		text.setCaretPosition(0);

		// добавление на панель
		panel.add(label, BorderLayout.WEST);
		panel.add(text, BorderLayout.CENTER);

		return panel;
	}

	/**
	 * Панель с кнопками открыть и отмены
	 * 
	 * @return панель
	 */
	private JPanel actionTorrent()
	{
		JPanel panel = new JPanel();

		panel.setLayout(new BorderLayout(0, 0));
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		// Кнопка отмены
		JButton cancel = new JButton("Отмена");
		cancel.addActionListener(new ActionListener()
		{
			/**
			 * Закрытие окна
			 */
			public void actionPerformed(ActionEvent arg0)
			{
				// Закрываем окно
				closeFrame();
			}

		});

		// Кнопка Открыть
		JButton open = new JButton("Открыть");
		open.addActionListener(new ActionListener()
		{
			/**
			 * Добавление торрент файла в клиент
			 * закрытие окна
			 */
			public void actionPerformed(ActionEvent arg0)
			{

				TorrentProcessor tp = new TorrentProcessor();
				tp.setAnnounceURL("http://" + address + ":8081/announce");

				try
				{
					tp.setPieceLength(Integer.parseInt("256"));
				}
				catch (Exception e)
				{
					System.err.println("Piece length must be an integer");
					System.exit(0);
				}

				try
				{
//					List l = new List();
//					for (int i = 0; i < path.size(); i++)
//					{
//						// System.out.println(path.get(i));
//						//tp.addFile(new File(path.get(i)));
//						l.add(path.get(i));
//					}
					tp.addFiles(path);
				}
				catch (Exception e)
				{
					System.err.println("Problem when adding files to torrent. Check your data");
					System.exit(0);
				}

				tp.setCreator(getIP());
				tp.setComment("");
				try
				{
					System.out.println("Hashing the files...");
					System.out.flush();
					tp.generatePieceHashes();
					System.out.println("Hash complete... Saving...");

					FileOutputStream fos = new FileOutputStream(text.getText());

					fos.write(tp.generateTorrent());

					System.out.println("Torrent created successfully!!!");
				}
				catch (Exception e)
				{
					System.err.println("Error when writing to the torrent file...");
					System.exit(1);
				}

				File f = new File(text.getText());

				for (int i = 0; i < path.size(); i++)
				{
					String pathTorrent = new File("").getAbsolutePath() + "/download/" + f.getName().substring(0, f.getName().indexOf(".")) + "/";

					new File(pathTorrent).mkdirs();
					pathTorrent += name.get(i);
					copyFile(new File(path.get(i)), new File(pathTorrent));
				}

				try
				{
					ConnectionManager.publish(text.getText(), "http://" + address + ":8081/upload", "none", "none", f.getName(), "", getIP(), "7");
				}
				catch (Exception e)
				{
					System.exit(0);
				}

				// закрываем окно
				closeFrame();
			}
		});

		panel.add(cancel, BorderLayout.WEST);
		panel.add(open, BorderLayout.EAST);

		return panel;
	}

	/**
	 * Выбор файлов для торрент файла
	 * 
	 * @return результат диалога
	 */
	private int fileChooser()
	{
		// Диалог выбора файлов
		JFileChooser fileChooser = new JFileChooser(new File("").getAbsolutePath());
		fileChooser.setDialogTitle("Добавить файлы");
		fileChooser.setMultiSelectionEnabled(true);

		// открытие диалога
		int returnValue = fileChooser.showOpenDialog(this);

		if (returnValue == JFileChooser.APPROVE_OPTION)
		{
			// Выбранный торрент-файл
			File[] f = fileChooser.getSelectedFiles();

			for (int i = 0; i < f.length; i++)
			{
				this.name.add(f[i].getName());
				this.path.add(f[i].getAbsolutePath());
			}

			// обновление списка
			list.setListData(name);
			list.repaint();
			list.validate();
		}

		return returnValue;
	}

	/**
	 * Закрывает форму
	 */
	private void closeFrame()
	{
		this.dispose();
	}

	public static Boolean copyFile(File source, File dest)
	{
		FileInputStream is = null;
		FileOutputStream os = null;
		try
		{
			is = new FileInputStream(source);
			os = new FileOutputStream(dest);
			int nLength;
			byte[] buf = new byte[8000];
			while (true)
			{
				nLength = is.read(buf);
				if (nLength < 0)
				{
					break;
				}
				os.write(buf, 0, nLength);
			}
			return true;
		}
		catch (IOException ex)
		{

		}
		finally
		{
			if (is != null)
			{
				try
				{
					is.close();
				}
				catch (Exception ex)
				{
				}
			}
			if (os != null)
			{
				try
				{
					os.close();
				}
				catch (Exception ex)
				{
				}
			}
		}
		return false;
	}
	
	private String getIP()
	{
		String result = "";
		try
		{
			// адресс сервера
			InetAddress addr = InetAddress.getByName(this.address);

			// устанавливаем соединение
			Socket socket = new Socket(addr, 8888);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

			try
			{
				// определяем задачу
				out.println("Task:3");

				// передача торрент
				
				// полчение ответа
				String input;

				while ((input = in.readLine()) != null)
				{
					System.out.println(input);
					result = input;
				}

			}
			finally
			{
				socket.close();
			}
		}
		catch (java.net.ConnectException e)
		{
			System.out.println("Связь с сервером не установлена!");
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	
	
}
