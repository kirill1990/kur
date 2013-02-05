package client;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.TimerTask;
import java.util.Vector;

import javax.swing.JList;

import torrent.Torrent;
import windows.AddTorrent;

public class Client extends TimerTask
{
	private Vector<Torrent>	torrentList	= null;
	private JList			list		= null;
	private String			address		= "127.0.0.1";
	private Vector<String>	path		= new Vector<String>();

	public Client(Vector<Torrent> torrentList, JList list,String address)
	{
		this.torrentList = torrentList;
		this.list = list;
		this.address = address;
		
	}

	@Override
	public void run()
	{
		Vector<String> content = new Vector<String>();
		Vector<String> removeTorrent = new Vector<String>();

		// определяем активные торренты клиента
		for (int i = 0; i < torrentList.size(); i++)
		{
			content.add(torrentList.get(i).getName());
		}

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
				out.println("Task:1");
				//out.println(this.address);

				
				
				// передача списка торрентов
				out.println("Content:" + content.size());

				for (int i = 0; i < content.size(); i++)
				{
					out.println(content.get(i));
				}

				// полчение ответа
				String input;

				while ((input = in.readLine()) != null)
				{
					// завершение прослушки
					if (input.equals("@END"))
					{
						for (int i = 0; i < removeTorrent.size(); i++)
						{
							// удаление торрента из прослушивания
							for (int p = 0; p < torrentList.size(); p++)
							{
								if(removeTorrent.get(i).equals(torrentList.get(p).getName()))
								{
									System.out.println("File remove test: "+torrentList.get(p).pathTorrent);
									torrentList.get(p).remove();
									torrentList.remove(p);
									list.repaint();
									list.validate();
									break;
								}
							}
						}
						break;
					}

					// приём торрент файлов
					if (input.equals("@FILE_SEND"))
					{
						String fileName = in.readLine();
						long s = Long.parseLong(in.readLine());

						System.out.println("File name: " + fileName);
						System.out.println("File size: " + s / 1024 + "Kb");

						byte[] byteArray = new byte[1024];

						new File(System.getProperty("user.dir") + "/Recieved").mkdir();
						File f = new File(System.getProperty("user.dir") + "/Recieved/" + fileName);
						System.out.println("File path: " + f.getAbsolutePath());

						f.createNewFile();
						FileOutputStream fos = new FileOutputStream(f);

						int sp = (int) (s / 1024);

						if (s % 1024 != 0)
							sp++;

						BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());

						while (s > 0)
						{
							int i = bis.read(byteArray);
							fos.write(byteArray, 0, i);
							s -= i;
						}
						fos.close();

						
						path.add(f.getAbsolutePath());
						
					}

					if (input.equals("@FILE_REMOVE"))
					{
						removeTorrent.add(in.readLine());
					}
				}
			}
			finally
			{
				
				for(int i=0;i<path.size();i++)
				{
					// Запуск файла
					AddTorrent add = new AddTorrent(path.get(i));

					add.setTorrent(torrentList);
					add.setList(list);

					add.start();
				}
				
				path.removeAllElements();
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
	}
}
