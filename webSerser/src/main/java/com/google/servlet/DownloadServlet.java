package com.google.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.util.StringUtil;

import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;

public class DownloadServlet extends BaseServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setStatus(HttpServletResponse.SC_OK);
		resp.setHeader("Accept-Ranges", "bytes");	// 表示支持断点继传
		String name = req.getParameter("name");
		String range = req.getHeader("RANGE");		// 表示从哪里开始取数据
		String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "WebInfos/" + name;
		File file = new File(path);
		long length = file.length();
		
		OutputStream out = resp.getOutputStream();
		
		if(range == null ||"".equals(range.trim())){
			resp.setContentLength((int) length);
			FileInputStream stream = new FileInputStream(file);
			int count = -1;
			byte[] buffer = new byte[1024];
			while ((count = stream.read(buffer)) != -1) {
				SystemClock.sleep(5);
				out.write(buffer, 0, count);
				out.flush();
			}
			stream.close();
			out.close();
		}else{
			range = range.replace("bytes=", "").replace("-", "");
			long startPosition = Long.valueOf(range);
			resp.setContentLength((int) (length - startPosition));
			RandomAccessFile raf = new RandomAccessFile(file, "r");
			raf.seek(startPosition);
			int count = -1;
			byte[] buffer = new byte[1024];
			while ((count = raf.read(buffer)) != -1) {
				SystemClock.sleep(5);
				out.write(buffer, 0, count);
				out.flush();
			}
			raf.close();
			out.close();
		}
		
	}
}
