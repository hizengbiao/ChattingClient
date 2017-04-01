package org.HeXiaoMin.newchat.client;

import java.io.IOException;
import java.net.UnknownHostException;

public class ClientChat {

	public static void main(String[] args) throws UnknownHostException, IOException {
		new LoginChat().loadFrame();//加载一个新的登录窗口
	}
}
