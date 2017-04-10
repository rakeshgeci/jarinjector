package com.ofss.jarinjector.ui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JButton;

import org.apache.commons.vfs.FileName;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;

import com.jcraft.jsch.JSchException;
import com.ofss.jarinjector.process.ssh.SshConnector;

import net.sf.vfsjfilechooser.VFSJFileChooser;
import net.sf.vfsjfilechooser.acessories.DefaultAccessoriesPanel;
import net.sf.vfsjfilechooser.utils.VFSUtils;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JPasswordField;

public class JarChooser {
	private FileName fileNameObj;
	private JFrame frame;
	private JTextField fileNamePath;
	private String host;
	private String port;
	private String user;
	private String password;
	private JLabel lblHost;
	private JLabel lblUsername;
	private JLabel lblPort;
	private JTextField hostField;
	private JTextField userField;
	private JTextField portField;
	private JButton btnNext;
	private JLabel lblPassword;
	private JPasswordField passwordField;
	private JButton btnNewButton;
	private SshConnector sshLogin;
	private JarInjectorClassLoader classLoader;
	private String tempLocation = "/tmp/temp/jarInjector";

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JarChooser window = new JarChooser();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public JarChooser() {
		initialize();
		host = "10.180.84.194";
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		btnNewButton = new JButton("Select Jar");
		btnNewButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				
				final VFSJFileChooser fileChooser = new VFSJFileChooser();

				// configure the file dialog
				fileChooser.setAccessory(new DefaultAccessoriesPanel(fileChooser));
				fileChooser.setFileHidingEnabled(false);
				fileChooser.setMultiSelectionEnabled(false);
				File dir = new File("sftp://readonly:readonly@10.180.84.194:22/scratch");
				//fileChooser.setCurrentDirectory(null);
				//fileChooser.setFileSelectionMode(SELECTION_MODE.FILES_ONLY);

				// show the file dialog
				int answer = fileChooser.showOpenDialog(frame);
				if(answer== 0) {
					FileObject selectedFile = fileChooser.getSelectedFile();
					String fileNamePaths = VFSUtils.getFriendlyName(selectedFile.toString());
					URL credentials = null;
					URI uri = null;
					try {
						credentials = selectedFile.getURL();
						uri = new URI(credentials.toString());
						hostField.setText(uri.getHost());
						portField.setText(uri.getPort()==-1?"22":uri.getPort()+"");
						fileNamePath.setText(uri.getPath());
						String[] unameNpassword = uri.getUserInfo().split(":");
						userField.setText(unameNpassword[0]);
						passwordField.setText(unameNpassword[1]);
						fileNameObj = selectedFile.getName();
						
					} catch (URISyntaxException | FileSystemException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
										
				}
			}
		});
		btnNewButton.setBounds(326, 34, 91, 23);
		frame.getContentPane().add(btnNewButton);
		
		fileNamePath = new JTextField();
		fileNamePath.setBounds(10, 35, 306, 20);
		frame.getContentPane().add(fileNamePath);
		fileNamePath.setColumns(10);
		
		lblHost = new JLabel("Host");
		lblHost.setBounds(32, 84, 46, 14);
		frame.getContentPane().add(lblHost);
		
		lblUsername = new JLabel("Username");
		lblUsername.setBounds(32, 109, 70, 14);
		frame.getContentPane().add(lblUsername);
		
		lblPort = new JLabel("Port");
		lblPort.setBounds(207, 84, 46, 14);
		frame.getContentPane().add(lblPort);
		
		hostField = new JTextField();
		hostField.setBounds(106, 81, 86, 20);
		frame.getContentPane().add(hostField);
		hostField.setColumns(10);
		
		userField = new JTextField();
		userField.setBounds(106, 106, 86, 20);
		frame.getContentPane().add(userField);
		userField.setColumns(10);
		
		portField = new JTextField();
		portField.setBounds(263, 81, 86, 20);
		frame.getContentPane().add(portField);
		portField.setColumns(10);
		
		btnNext = new JButton("Next");
		btnNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				performNext();
			}
		});
		btnNext.setBounds(263, 149, 91, 23);
		frame.getContentPane().add(btnNext);
		
		lblPassword = new JLabel("Password");
		lblPassword.setBounds(207, 109, 46, 14);
		frame.getContentPane().add(lblPassword);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(263, 106, 86, 20);
		frame.getContentPane().add(passwordField);
	}

	@SuppressWarnings("deprecation")
	protected void performNext() {
		String user = userField.getText();
		String password = passwordField.getText();
		String host = hostField.getText();
		int port = Integer.parseInt(portField.getText());
		String path = fileNamePath.getText();
		try {
			sshLogin = new SshConnector(user, host, port, password);
			String commands = "cp "+fileNamePath.getText()+" "+tempLocation;
			sshLogin.executeCommand("rm -rf "+tempLocation+"/*");
			sshLogin.executeCommand("mkdir -p "+tempLocation);
			String changeDir = "cd "+tempLocation;
			System.out.println(changeDir);
			System.out.println(commands);
			System.out.println(sshLogin.executeCommand(commands));
			sshLogin.executeCommand(changeDir);
			sshLogin.executeCommands(changeDir,"jar xvf "+ fileNameObj.getBaseName(),"rm -rf "+fileNameObj.getBaseName());
			System.out.println(sshLogin.executeCommand("pwd"));
			frame.setVisible(false);
			classLoader = new JarInjectorClassLoader(sshLogin, fileNameObj.getBaseName());
		} catch (JSchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

}
