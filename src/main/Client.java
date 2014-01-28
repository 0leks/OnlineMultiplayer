package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.border.Border;

import data.PlayerConfirm;
import data.ServerData;

public class Client implements Runnable{

	Socket socket;
	ObjectInputStream hostin;
	ObjectOutputStream hostout;
	Thread mythread;
	String ip;
	int port;
	Player thisplayer;
	String message;
	String message2;
	boolean nameconfirmed;
	boolean colorconfirmed;
	ServerData currentserverdata;
	ConnectFrame connectframe;
	public boolean startclient;
	public Client() {
		thisplayer = new Player();
		message = "asdf";
		message2 = "";
	    this.mythread = new Thread(this);
	    connectframe = new ConnectFrame();
	}
	public static void main(String[] args) {
		Client c = new Client();
		c.mythread.start();
	}
	public class ConnectFrame extends JFrame {
		JPanel panel;
		JTextField ipaddress;
		JTextField portbox;
		JButton start;
		JTextField usernamebox;
		JTextField redbox, greenbox, bluebox;
		Timer tim;
		private JTextArea serverinfo;
		public ConnectFrame() {
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			this.setSize(500, 500);
			this.setTitle("Client");
			panel = new JPanel() {
				@Override
				public void paintComponent(Graphics g) {
					super.paintComponent(g);
					g.setColor(Color.black);
					g.drawString("IP Address", 25, 25);
					g.drawString("Port", 285, 25);
					g.drawString("Username", 25, 75);
//					System.out.println("paint:"+thisplayer);
					g.setColor(thisplayer.color);
					g.fillRect(385, 80, 29, 29);
					g.setColor(Color.black);
					g.drawString("Color", 285, 75);
					g.drawString(thisplayer.toString(), 20, 130);
					g.drawString(message, 20, 145);
					g.drawString(message2, 20, 160);
				}
			};
			panel.setLayout(null);
			serverinfo = new JTextArea();
			serverinfo.setSize(400, 200);
			serverinfo.setLocation(20, 170);
			serverinfo.setBorder(BorderFactory.createLineBorder(Color.black));
			panel.add(serverinfo);
			ipaddress = new JTextField("localhost");
			ipaddress.setSize(250, 30);
			ipaddress.setLocation(20, 30);
			panel.add(ipaddress);
			portbox = new JTextField("34567");
			portbox.setSize(90, 30);
			portbox.setLocation(280, 30);
			panel.add(portbox);
			usernamebox = new JTextField("Noob");
			usernamebox.setSize(250, 30);
			usernamebox.setLocation(20, 80);
			panel.add(usernamebox);
			redbox = new JTextField(""+(int)(Math.random()*25)*10);
			greenbox = new JTextField(""+(int)(Math.random()*25)*10);
			bluebox = new JTextField(""+(int)(Math.random()*25)*10);
			redbox.addKeyListener(new KeyListener() {
				@Override
				public void keyPressed(KeyEvent e) {
					String trim = trimString(redbox.getText());
					if(!trim.equals(redbox.getText())) {
						redbox.setText(trim);
					}
				}
				@Override
				public void keyReleased(KeyEvent e) {
					keyPressed(e);
				}
				@Override
				public void keyTyped(KeyEvent e) {
					
				}
			});
			greenbox.addKeyListener(new KeyListener() {
				@Override
				public void keyPressed(KeyEvent e) {
					String trim = trimString(greenbox.getText());
					if(!trim.equals(greenbox.getText())) {
						greenbox.setText(trim);
					}
				}
				@Override
				public void keyReleased(KeyEvent e) {
					keyPressed(e);
				}
				@Override
				public void keyTyped(KeyEvent e) {
					
				}
			});
			bluebox.addKeyListener(new KeyListener() {
				@Override
				public void keyPressed(KeyEvent e) {
					String trim = trimString(bluebox.getText());
					if(!trim.equals(bluebox.getText())) {
						bluebox.setText(trim);
					}
				}
				@Override
				public void keyReleased(KeyEvent e) {
					keyPressed(e);
				}
				@Override
				public void keyTyped(KeyEvent e) {
					
				}
			});
			redbox.setSize(30, 30);
			greenbox.setSize(30, 30);
			bluebox.setSize(30, 30);
			redbox.setLocation(280, 80);
			greenbox.setLocation(315, 80);
			bluebox.setLocation(350, 80);
			panel.add(redbox);
			panel.add(greenbox);
			panel.add(bluebox);
			tim = new Timer(200, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					update();
				}
			});
			tim.start();
			start = new JButton("CONNECT");
			start.setSize(90, 30);
			start.setLocation(380, 30);
			start.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					System.out.println("hostin is:"+hostin);
					if(hostin==null) {
						ip = ipaddress.getText();
						try {
							int po = Integer.parseInt(portbox.getText());
							port = po;
							startclient = true;
						} catch(Exception e) {
//							portbox.setText("34567");
							e.printStackTrace();
						}
					} else {
						send(thisplayer);
					}
				}
			});
			panel.add(start);
			this.add(panel);
			this.setVisible(true);
		}
		public void update() {
			String name = usernamebox.getText();
			message = "";
			if(name.toCharArray().length>16) {
				message = "16 Character max for username";
			} else {
				thisplayer.name = name;
			}
			try {
				int red = Integer.parseInt(redbox.getText());
				int green = Integer.parseInt(greenbox.getText());
				int blue = Integer.parseInt(bluebox.getText());
				thisplayer.color = new Color(red, green, blue);
			} catch(Exception e) {
				message = "Range for colors is [0,255]";
				System.out.println("Color box is broken!");
			}
//				System.out.println("update:"+thisplayer);
			repaint();
		}
		public void addText(String s) {
			serverinfo.setText(serverinfo.getText()+s);
		}
		public void setText(String s) {
			serverinfo.setText(s);
		}
	}
	public String trimString(String s) {
		char[] chars = s.toCharArray();
		String toret = "";
		for(int a=0; a<chars.length; a++) {
			if(chars[a]>=48 && chars[a]<=57) {
				toret += ""+chars[a];
			}
		}
		return toret;
	}
	@Override
	public void run() {
		while(true) {
			if(startclient) {
				startclient = false;
				InetAddress hostIP = null;
				try {
					connectframe.addText("Converting ip adress:"+ip+"\n");
					hostIP = InetAddress.getByName(ip);
					connectframe.addText("  -Done converting, result is:"+hostIP+"\n");
					if (hostIP == null) {
						connectframe.addText("ip invalid, returning\n");
						return;
					}
					connectframe.addText("Creating socket on "+ip+":"+port+"\n");
					socket = new Socket(hostIP, port);
					connectframe.addText("Creating ObjectOutputStream\n");
					hostout = new ObjectOutputStream(socket.getOutputStream());
					connectframe.addText("Flushing ObjectOutputStream\n");
					hostout.flush();
					connectframe.addText("Creating ObjectInputStream\n");
					hostin = new ObjectInputStream(socket.getInputStream());
					System.out.println("Sending :"+thisplayer);
					send(thisplayer);
					connectframe.start.setText("Update");
					connectframe.setTitle(connectframe.getTitle()+" connected to ("+ip+":"+port+")");
					read();
				} catch (UnknownHostException e) {
					e.printStackTrace();
					connectframe.addText(e.getMessage()+" ("+ip+":"+port+")");
				} catch (IOException e) {
					e.printStackTrace();
					connectframe.addText(e.getMessage()+" ("+ip+":"+port+")");
				}
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	public void send(Object o) {
		try {
			hostout.writeUnshared(o);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void read() {
		while(true) {
			try {
				Object read = hostin.readUnshared();//.readObject();
				if(read instanceof PlayerConfirm) {
					String str = ((PlayerConfirm)read).msg;
					message2 = str;
				}
				if(read instanceof ServerData) {
					currentserverdata = (ServerData)read;
					connectframe.serverinfo.setText(currentserverdata.players);
				}
			} catch (ClassNotFoundException e) {
				connectframe.addText(e.getMessage()+" ("+ip+":"+port+")\n");
				e.printStackTrace();
			} catch (IOException e) {
				connectframe.addText(e.getMessage()+" ("+ip+":"+port+")\n");
				e.printStackTrace();
			}
		}
	}
}
