package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;

import data.ServerData;

public class Server implements Runnable {
	public ServerSocket server;
	Socket sock;
	Thread thread;
	CreationFrame creationframe;
	WorldFrame worldframe;
	int portNumber = 34567;
	ArrayList<Connection> connections;
	Timer servertimer;
	ServerData serverdata;
	int currentworldWidth;
	public Server() {
		thread = new Thread(this);
		creationframe = new CreationFrame();
		connections = new ArrayList<Connection>();
		servertimer = new Timer(100, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				serverdata.players = worldframe.players.getText();
				serverdata.width = currentworldWidth;
				for(Connection c : connections) {
					c.send(serverdata);
				}
			}
		});
		serverdata = new ServerData();
	}
	public static void main(String[] args) {
		Server s = new Server();
	}
	public boolean isNameGood(String name, String used) {
		for(int a=0; a<connections.size(); a++) {
			if(connections.get(a).player!=null) {
				if(!connections.get(a).player.name.equals(used)) {
					if(name.equals(connections.get(a).player.name)) {
						return false;
					}
				}
			}
		}
		return true;
	}
	public boolean isColorGood(Color color, Color used) {
		for(int a=0; a<connections.size(); a++) {
			Color cur = connections.get(a).player.color;
			if(!cur.equals(used)) {
				int dif = Math.abs(cur.getRed()-color.getRed());
				dif += Math.abs(cur.getGreen()-color.getGreen());
				dif += Math.abs(cur.getBlue()-color.getBlue());
				if(dif<50) {
					return false;
				}
			}
		}
		return true;
	}
//	public PlayerConfirm isPlayerGood(Player p) {
//		PlayerConfirm pc = new PlayerConfirm();
//		boolean name = false;
//		boolean color = false;
//		for(int a=0; a<connections.size(); a++) {
//			if(connections.get(a).player!=null) {
//				if(p.name.equals(connections.get(a).player.name) && name==false) {
//					pc.msg+="Name "+p.name+" is taken";
//					name = true;
//				}
//				if(color==false) {
//					Color cur = connections.get(a).player.color;
//					int dif = Math.abs(cur.getRed()-p.color.getRed());
//					dif += Math.abs(cur.getGreen()-p.color.getGreen());
//					dif += Math.abs(cur.getBlue()-p.color.getBlue());
//					if(dif<50) {
//						pc.msg+=", Color ("+cur.getRed()+","+cur.getGreen()+","+cur.getBlue()+") is taken";
//						color = true;
//					}
//				}
//			}
//		}
//		return pc;
//	}
	public void start() {
		thread.start();
	}
//	public void addConnection() {
//		
//	}
	@Override
	public void run() {
		worldframe = new WorldFrame();
		try {
			System.out.println("Creating Server on port:"+portNumber);
			server = new ServerSocket(portNumber);
			servertimer.start();
			while(true) {
				System.out.println("waiting for connection");
				sock = server.accept();
				System.out.println("Creating outputstream");
				ObjectOutputStream hostout = new ObjectOutputStream(sock.getOutputStream());
				hostout.flush();
				System.out.println("making reader");
				ObjectInputStream hostin = new ObjectInputStream(sock.getInputStream());
				Connection con = new Connection(this, hostin, hostout);
				connections.add(con);
				con.start();
//				if(world.canJoin()) {
//					world.addConnection(new Connection(world, hostin, hostout));
//				} else {
//					hostout.writeInt(10012);
//					hostout.writeInt(1);
//					hostin.close();
//					hostout.close();
//				}
			}
		} catch (IOException e) {
			System.out.println("Error Creating Server");
			e.printStackTrace();
		}
	}
	public class WorldFrame extends JFrame {
		JTextArea players;
		JTextField width;
		JPanel panel;
		JButton create;
		Timer tim;
		String title;
		public WorldFrame() {
			this.setSize(500, 500);
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			title = "Server ";
			try {
				title += InetAddress.getLocalHost().getHostAddress()+":";
			} catch (UnknownHostException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			title += portNumber;
			this.setTitle(title);
			players = new JTextArea("Players:\ntest\ntest");
			players.setFocusable(false);
			players.setFont(new Font("Nyala", Font.PLAIN, 20));
			players.setSize(400, 200);
			players.setLocation(50, 50);
			width = new JTextField("3000");
			currentworldWidth = 3000;
			width.setSize(190, 20);
			width.setLocation(50, 280);
			panel = new JPanel() {
				@Override
				public void paintComponent(Graphics g) {
					super.paintComponent(g);
					g.setColor(Color.black);
					g.setFont(new Font("Arial", Font.PLAIN, 25));
					g.drawString(title, 20, 35);
					g.setFont(new Font("Arial", Font.PLAIN, 20));
					g.drawString("World Width", 55, 275);
				}
			};
			create = new JButton("START GAME");
			create.setSize(190, 40);
			create.setLocation(50, 320);
			this.add(panel, BorderLayout.CENTER);
			panel.setLayout(null);
			panel.add(players);
			panel.add(width);
			panel.add(create);
			tim = new Timer(100, new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					String text = "Players:\n";
					for(Connection c : connections) {
						if(c.player!=null) {
							text+=c.player+"\n";
						} else {
							text+="null\n";
						}
					}
//					if(!players.getText().equals(text)) {
//						playerschanged = true;
//					}
					players.setText(text);
					try {
						int temp = Integer.parseInt(width.getText());
						currentworldWidth = temp;
					} catch(Exception e) {
						
					}
					repaint();
				}
				
			});
			tim.start();
			this.setVisible(true);
		}
		public void refreshText() {
			
		}
	}
	public class CreationFrame extends JFrame {
		public JPanel panel;
		JTextField port;
		String text = "";
		public CreationFrame() {
			port = new JTextField("34567");
			port.setSize(200, 40);
			port.setLocation(50, 50);
			port.setToolTipText("port");
			JButton start = new JButton("CREATE");
			start.setSize(100, 40);
			start.setLocation(50, 250);
			start.setFocusable(false);
			start.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					boolean good = true;
					int num = 0;
					try {
						num = Integer.parseInt(port.getText());
					} catch (Exception ex) {
						good = false;
						text = " -- example: 34567";
						repaint();
					}
					if(good) {
						portNumber = num;
						setVisible(false);
						start();
					}
				}
			});
			this.setSize(500, 500);
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			panel = new JPanel() {
				@Override
				public void paintComponent(Graphics g) {
					super.paintComponent(g);
					g.setColor(Color.black);
					g.drawString("port"+text, port.getX(), port.getY()-5);
				}
			};
			panel.setLayout(null);
			panel.add(port);
			panel.add(start);
			panel.requestFocus();
			this.add(panel, BorderLayout.CENTER);
			this.setTitle("Server");
			this.setVisible(true);
		}
	}
}
