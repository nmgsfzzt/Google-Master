package com.google.server;

import java.io.File;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;


public class WebService extends Service {

	private Server server;
	/** 服务器启动成功 */
	private static final int START_SUCCESS = 0;
	/** 服务器启动失败 */
	public static final int START_FAIL = 1;
	/** 服务器关闭成功 */
	private static final int STOP_SUCCESS = 2;
	/** 服务器关闭失败 */
	private static final int STOP_FAIL = 3;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			String toast = "";
			switch (msg.what) {
			case START_SUCCESS:
				toast = "服务器启动成功";
				break;
			case START_FAIL:
				toast = "服务器启动失败";
				break;
			case STOP_SUCCESS:
				toast = "服务器关闭成功";
				break;
			case STOP_FAIL:
				toast = "服务器关闭失败";
				break;
			}
			Toast.makeText(WebService.this, toast, Toast.LENGTH_SHORT).show();
		};
	};

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (server != null) {
			Toast.makeText(this, "服务器已经开启了", Toast.LENGTH_SHORT).show();
			return super.onStartCommand(intent, flags, startId);
		}
		
		startForeground(9999, new Notification());
		startServer();
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		stopServer();
		super.onDestroy();
	}

	private void startServer() {
		new Thread(new StartRunnable()).start();
	}

	private void stopServer() {
		if (server != null) {
			new Thread(new StopRunnable()).start();
		}
	}

	class StartRunnable implements Runnable {

		@Override
		public void run() {
			try {
				File JETTY_DIR = new File(Environment.getExternalStorageDirectory(), "jetty");
				// Set jetty.home
				System.setProperty("jetty.home", JETTY_DIR.getAbsolutePath());

				// ipv6 workaround for froyo
				System.setProperty("java.net.preferIPv6Addresses", "false");

				server = new Server(8090);
				// server.setHandler(new DefaultHandler());
				ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
				contextHandler.setContextPath("/");
				server.setHandler(contextHandler);
				ServlertConfig.config(contextHandler);

				server.start();
				handler.sendEmptyMessage(START_SUCCESS);
				server.join();
			} catch (Exception e) {
				server = null;
				e.printStackTrace();
				handler.sendEmptyMessage(START_FAIL);
			}
		}
	}

	class StopRunnable implements Runnable {

		@Override
		public void run() {
			try {
				if (server != null) {
					server.stop();
					server = null;
				}
				handler.sendEmptyMessage(STOP_SUCCESS);
			} catch (Exception e) {
				e.printStackTrace();
				handler.sendEmptyMessage(STOP_SUCCESS);
			}
		}
	}
}
