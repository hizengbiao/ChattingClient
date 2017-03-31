package org.zhangge.newchat.client;

import java.awt.Image;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.SocketException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.zhangge.newchat.common.CommonUtil;

public class MainChat extends JFrame implements Runnable {

	private static final long serialVersionUID = 1720361181828968073L;
	private TextArea tfTxt =new TextArea();//编辑框
	private JLabel content = new JLabel(CommonUtil.TALKING_HISTORY);//聊天记录，标题
	private TextArea taContent=new TextArea();//聊天记录
	private JLabel list = new JLabel(CommonUtil.ONLINE_USER);//群聊成员列表，标题
	private TextArea talkList = new TextArea();//群聊成员列表
	private JButton closeWindow = new JButton("退出群聊");//发送按钮
	private JButton sendMsg = new JButton("发送");//发送按钮
	private JLabel pic=new JLabel();
	private DataOutputStream dos;
	private DataInputStream dis;
	private boolean beConnected;
	
	public MainChat(DataInputStream dis, DataOutputStream dos) {
		this.dis = dis;
		this.dos = dos;
		this.beConnected = true;
	}
	/**
	 * @Title:launchFrame
	 * @Description:TODO 启动聊天主窗口
	 * @param 
	 * @return void
	 * @throws
	 */
	public void launchFrame() {
		this.setTitle(CommonUtil.MAIN_TITLE);
		setBounds(100,100,650,590);
		this.setLayout(null);
		
		content.setBounds(180, 10, 200, 20);//聊天记录，标题
		this.add(content);
		taContent.setBounds(10, 40, 420, 350);//聊天记录
		this.add(taContent);
		tfTxt.setBounds(10, 400, 420, 100);//编辑框
		this.add(tfTxt);
		closeWindow.setBounds(230, 510, 100, 30);//退出群聊
		this.add(closeWindow);
		sendMsg.setBounds(350, 510, 70, 30);//发送消息
		this.add(sendMsg);
		list.setBounds(470, 190, 150, 20);//群聊成员列表，标题
		this.add(list);
		talkList.setBounds(460, 220, 150, 300);//群聊成员列表
		this.add(talkList);
		
		ImageIcon icon = new ImageIcon("images\\benbenla.jpg");
		  icon.setImage(icon.getImage().getScaledInstance(150,150, Image.SCALE_DEFAULT));
		pic.setBounds(460, 30, 150, 150);//头像
		 pic.setIcon(icon);
		 this.add(pic);
		/*list.setBounds(50, 30, 200, 20);//群聊成员列表，标题
		this.add(list);
		talkList.setBounds(10, 70, 170, 400);//群聊成员列表
		this.add(talkList);
		content.setBounds(250, 30, 200, 20);//聊天记录，标题
		this.add(content);
		taContent.setBounds(220, 70, 400, 400);//聊天记录
		this.add(taContent);
		tfTxt.setBounds(10, 480, 600, 100);//编辑框
		this.add(tfTxt);
		closeWindow.setBounds(arg0, arg1, arg2, arg3);
		this.add(closeWindow);
		sendMsg.setBounds(arg0, arg1, arg2, arg3);
		this.add(sendMsg);*/
		setVisible(true);
		closeWindow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					beConnected = false;
					dis.close();
					dos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			//	e.getWindow().dispose();
				System.exit(0);
		}
		});
		sendMsg.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String str = tfTxt.getText().trim();//获取输入框的内容
				tfTxt.setText("");
				try {
					dos.writeUTF(str);//往流里写，发到服务器
					dos.flush();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
		}
		});
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				try {
					beConnected = false;
					dis.close();
					dos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				e.getWindow().dispose();
				System.exit(0);
			}
		});
		tfTxt.addKeyListener(new MyListener(tfTxt, dos));
	}

	public void run() {
		try{
			while (beConnected){
				String str=dis.readUTF();//读取服务器的信息
				if (str.length() >= CommonUtil.USER_LIST.length() && str.substring(0, CommonUtil.USER_LIST.length()).equals(CommonUtil.USER_LIST)) {
					String userList = str.substring(CommonUtil.USER_LIST.length());//更新用户列表
					talkList.setText(userList);
				} else {
//					taContent.setText(taContent.getText()+str+'\n');//更新聊天内容
					taContent.appendText(str+'\n');//更新聊天内容
				}
			}
		} catch (SocketException e) {
			this.showReturnMessage(CommonUtil.SOCKET_ERROR);
		} catch (EOFException e) {
			this.showReturnMessage(CommonUtil.SOCKET_ERROR);
		} catch(IOException e){
			this.showReturnMessage(CommonUtil.IOEXCEPTION_MESSAGE);
		} 
	}
	
	/**
	 * @param message
	 *  显示反馈信息
	 */
	public void showReturnMessage(String message) {
		JOptionPane.showMessageDialog(null, message, CommonUtil.ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
	}
}
