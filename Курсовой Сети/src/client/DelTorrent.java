package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.TimerTask;

import javax.swing.JOptionPane;

public class DelTorrent extends TimerTask
{
	private String	address	= "127.0.0.1";
	private String	torrent	= "";
	private String	created	= "";

	public DelTorrent(String address, String torrent, String created)
	{
		this.address = address;
		this.torrent = torrent;
		this.created = created;
	}

	@Override
	public void run()
	{
		System.out.println(getIP()+"   -   "+created);
		if (getIP().equals(created))
		{
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
					out.println("Task:2");

					// передача торрента
					out.println("Content:" + this.torrent);

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
		}
		else
		{
			 JOptionPane.showMessageDialog(null, "Нет прав для удаления");
		}
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
