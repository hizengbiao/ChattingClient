package org.HeXiaoMin.newchat.client;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.HeXiaoMin.newchat.common.CommonUtil;

public class LoginChat extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;

	// 登陆窗口的组建
	private JLabel UserName = new JLabel(CommonUtil.USERNAME);// 标签：在登录窗口上显示“您的昵称”
	private JTextField Name = new JTextField(); // 输入框，用于输入昵称
	private JLabel UserPaw = new JLabel(CommonUtil.PASSWORD);// 标签：在登录窗口上显示“登录密码”
	private JPasswordField Paw = new JPasswordField(); // 输入框，用于输入密码
	private JLabel ServerHost = new JLabel(CommonUtil.HOSTSERVER);// 标签：在登录窗口上显示“服务器地址”
	private JTextField Host = new JTextField(); // 输入框，用于输入服务器地址
	private JLabel ServerPort = new JLabel(CommonUtil.SERVERPORT);// 标签：在登录窗口上显示“服务器端口”
	private JTextField Port = new JTextField(); // 输入框，用于输入服务器端口
	private JButton Load = new JButton(CommonUtil.LOAD); // 登录按钮
	private JButton Quit = new JButton(CommonUtil.QUIT); // 退出按钮

	// 连接参数
	private Socket s = null; // 套接字
	private DataOutputStream dos = null; // 输出流
	private DataInputStream dis = null; // 输入流
	private boolean bconnected = false;//当前连接状态

	private String message;
	private String host;
	private int port;

	private JPanel contentPane;

	/**
	 * @Title:LoadFrame
	 * @Description:TODO 登陆窗口，启动的入口
	 * @param
	 * @return void
	 * @throws
	 */
	public void loadFrame() {//加载登录窗口
		this.setTitle(CommonUtil.LOGIN_TITLE);
//		Container c = this.getContentPane();
		
		setBounds(400, 300, 350, 350);//设置窗口大小，四个参数表示(x,y,width,height)，x,y表示窗口左上角点的x,y值，后两个参数表示窗口的宽度和高度
		
		contentPane = new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(new ImageIcon(
						"images\\bg_login.jpg").getImage(), 0,
						0, getWidth(), getHeight(), null);
			}
		};
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		this.setLayout(null);
		
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				e.getWindow().dispose();
				System.exit(0);
			}
		});
		UserName.setBounds(50, 40, 100, 20);
		this.add(UserName);
		Name.setBounds(150, 40, 120, 20);
		this.add(Name);

		UserPaw.setBounds(50, 90, 100, 20);
		this.add(UserPaw);
		Paw.setBounds(150, 90, 120, 20);
		this.add(Paw);

		ServerHost.setBounds(50, 140, 100, 20);
		this.add(ServerHost);
		Host.setBounds(150, 140, 120, 20);
		this.add(Host);

		ServerPort.setBounds(50, 190, 100, 20);
		this.add(ServerPort);
		Port.setBounds(150, 190, 120, 20);
		this.add(Port);

		Load.setBounds(50, 250, 80, 40);
		this.add(Load);
		Quit.setBounds(190, 250, 80, 40);
		this.add(Quit);

		Host.setText(CommonUtil.SERVER_IP);//设置默认的服务器IP
		Port.setText(CommonUtil.PORT + "");//设置默认的服务器端口
		
		this.setVisible(true);//设置窗口可见
		this.setResizable(false);//窗口大小不可调整

		Load.addActionListener(this);
		Quit.addActionListener(this);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == Load) {
			String name = Name.getText();
			name.trim();
			String passwd = new String(Paw.getPassword());
			passwd.trim();
			if (name.length() == 0 || passwd.length() == 0) {
				this.showReturnMessage(CommonUtil.NAMEPASSWORD_NULL);
			} else {
				message = CommonUtil.USERNAME_MARK + name
						+ CommonUtil.USERPASSWD_SPLIT
						+ CommonUtil.PASSWORD_MARK + passwd;
				try {
					host = Host.getText();
					port = Integer.valueOf(Port.getText());
					boolean connect = this.connect(host, port);// 与服务器进行连接
					if (connect) {
						this.setVisible(false);// 隐藏登陆窗口
						MainChat mc = new MainChat(dis, dos,name);
						mc.launchFrame();// 打开聊天主窗口
						new Thread(mc).start();// 启动线程，获取聊天内容和在线用户列表
					}
				} catch (NumberFormatException e1) {
					this.showReturnMessage(CommonUtil.PORT_ILLEGAL);
				}
			}
		} else if (e.getSource() == Quit) {
			System.exit(0);
		}
	}

	/**
	 * @Title:connect
	 * @Description:TODO 与服务器进行连接
	 * @param
	 * @return void
	 * @throws
	 */
	public boolean connect(String host, int port) {
		try {
			if (host == null || host.equals("")) {
				host = CommonUtil.SERVER_IP;
			}
			s = new Socket(host, port);
			dos = new DataOutputStream(s.getOutputStream());
			dis = new DataInputStream(s.getInputStream());
			System.out.println(CommonUtil.SUCCEED_CONNECTE);
			dos.writeUTF(message);// 发送帐号密码信息
			String conmsg = dis.readUTF();
			if (conmsg.equals(CommonUtil.COMFIRM_FAIL_MARK)) {
				this.showReturnMessage(CommonUtil.COMFIRM_FAIL);
				bconnected = false;
			} else {
				System.out.println(CommonUtil.COMFIRM_SUCCESS);
				bconnected = true;
			}
		} catch (IllegalArgumentException e) {
			this.showReturnMessage(CommonUtil.CONNECT_ERROR);
		} catch (UnknownHostException e) {
			this.showReturnMessage(CommonUtil.CONNECT_ERROR);
		} catch (SocketException e) {
			this.showReturnMessage(CommonUtil.CONNECT_ERROR);
		}
		/*
		 * catch(IllegalArgumentException e) {
		 * this.showReturnMessage(CommonUtil.PORT_ILLEGAL); } catch
		 * (UnknownHostException e) {
		 * this.showReturnMessage(CommonUtil.HOST_ERROR); }
		 * catch(SocketException e) {
		 * this.showReturnMessage(CommonUtil.PORT_ERROR); }
		 */
		catch (IOException e) {
			this.showReturnMessage(CommonUtil.IOEXCEPTION_MESSAGE);
			System.exit(0);
		}
		return bconnected;
	}

	/**
	 * @param message
	 *            显示反馈信息
	 */
	public void showReturnMessage(String message) {
		JOptionPane.showMessageDialog(null, message, CommonUtil.ERROR_TITLE,
				JOptionPane.ERROR_MESSAGE);
	}
}
