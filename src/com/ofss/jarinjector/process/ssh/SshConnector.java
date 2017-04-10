package com.ofss.jarinjector.process.ssh;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

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
	
	
	public void copyFile(File file, String destination) throws JSchException, SftpException, FileNotFoundException {
		channel = session.openChannel("sftp");
		channel.connect();
		ChannelSftp channelSftp = (ChannelSftp)channel;
		channelSftp.cd(destination);
		channelSftp.put(new FileInputStream(file), file.getName());
	}
	
	public void executeCommands(String... commands) throws JSchException, IOException, InterruptedException {
		Channel channel=session.openChannel("shell");
        OutputStream ops = channel.getOutputStream();
        PrintStream ps = new PrintStream(ops, true);

         channel.connect();
         for(String cmd : commands)
        	 ps.println(cmd); 
          ps.close();

         InputStream in=channel.getInputStream();
         byte[] bt=new byte[1024];


         while(true)
         {

         while(in.available()>0)
         {
         int i=in.read(bt, 0, 1024);
         if(i<0)
          break;
            String str=new String(bt, 0, i);
          //displays the output of the command executed.
            System.out.print(str);


         }
         if(channel.isClosed())
         {

             break;
        }
         Thread.sleep(1000);
         channel.disconnect(); 
         }
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
