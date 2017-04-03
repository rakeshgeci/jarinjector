package com.ofss.jarinjector.process.ssh;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class SshConnector {
	private JSch jsch;
	private Session session;
	private Channel channel;
	
	/**
	 * 
	 * @param user
	 * @param host
	 * @param port
	 * @param password
	 * @throws JSchException
	 */
	public SshConnector(String user, String host, int port,String password) throws JSchException {
		login(user,host, port, password);
	}
	/**
	 * 
	 * @param user
	 * @param host
	 * @param password
	 * @throws JSchException
	 */
	public SshConnector(String user, String host,String password) throws JSchException {
		int port = 22;
		login(user,host, port, password);
	}
	
	private void login(String user, String host, int port,String password) throws JSchException {
		jsch = new JSch();
		session = jsch.getSession(user, host, port);
		session.setPassword(password);
		session.setConfig("StrictHostKeyChecking", "no");
		session.connect();
		channel = session.openChannel("exec");
	}
	
	/**
	 * 
	 * @param command
	 * @return
	 * @throws IOException
	 * @throws JSchException
	 */
	public StringBuilder executeCommand(String command) throws IOException, JSchException {
		StringBuilder outputBuffer = new StringBuilder();
		channel = session.openChannel("exec");
		if(channel!=null) {
			((ChannelExec)channel).setCommand(command);
		}
		InputStream commandOutput = channel.getInputStream();
        channel.connect();
        int readByte = commandOutput.read();

        while(readByte != 0xffffffff)
        {
           outputBuffer.append((char)readByte);
           readByte = commandOutput.read();
        }
        channel.disconnect();
		return outputBuffer;
	}
}
