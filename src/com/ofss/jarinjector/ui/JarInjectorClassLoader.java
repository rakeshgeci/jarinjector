package com.ofss.jarinjector.ui;

import java.awt.EventQueue;

import javax.swing.JFrame;

import com.jcraft.jsch.JSchException;
import com.ofss.jarinjector.process.ssh.SshConnector;

import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import javax.swing.JProgressBar;

import org.apache.bcel.classfile.ClassFormatException;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;

public class JarInjectorClassLoader {

	private JFrame frame;
	SshConnector sshLogin;
	private JTextField fileNamePath;
	private JButton inject;
	private String fileName;
	private JButton btnOpenPutty;
	private File selectedFile;
	private String outputFilePath;
	private static int flag = 0;

	/**
	 * Create the application.
	 * @param fileName 
	 */
     public JarInjectorClassLoader() {
    	 initialize();
 		frame.setVisible(true);
     }
      

	public JarInjectorClassLoader(SshConnector sshLogin2, String fileName2) {
		this.sshLogin = sshLogin2;
		this.fileName = fileName;
		initialize();
		frame.setVisible(true);
	}
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		fileNamePath = new JTextField();
		fileNamePath.setBounds(24, 38, 319, 20);
		frame.getContentPane().add(fileNamePath);
		fileNamePath.setColumns(10);
		
		JButton btnChoose = new JButton("Choose");
		btnChoose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				openChooseFile();
			}
		});
		btnChoose.setBounds(353, 37, 91, 23);
		frame.getContentPane().add(btnChoose);
		
		inject = new JButton("Inject");
		inject.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				inject.setEnabled(false);
				performInjection();
			}
		});
		inject.setEnabled(false);
		inject.setBounds(353, 88, 91, 23);
		frame.getContentPane().add(inject);
		
		JProgressBar progressBar = new JProgressBar();
		progressBar.setBounds(38, 235, 394, 16);
		frame.getContentPane().add(progressBar);
		
		JButton btnDone = new JButton("Done");
		btnDone.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnDone.setBounds(54, 185, 91, 23);
		frame.getContentPane().add(btnDone);
		
		btnOpenPutty = new JButton("Open Putty");
		btnOpenPutty.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openPutty();
			}
		});
		btnOpenPutty.setBounds(330, 185, 91, 23);
		frame.getContentPane().add(btnOpenPutty);
	}

	protected void openPutty() {
		try {
			Runtime.getRuntime().exec("C:\\Program Files (x86)\\PuTTY\\putty.exe");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


	protected void performInjection() {
		if(flag==0) {
			copyJarToTemp();
		}
		
	}


	private void copyJarToTemp() {
		try {
			sshLogin.executeCommand("cd /");
			System.out.println(sshLogin.executeCommand("ls"));
		} catch (IOException | JSchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}


	protected void openChooseFile() {
		JFileChooser fileChooser = new JFileChooser();
		FileNameExtensionFilter xmlfilter = new FileNameExtensionFilter(
			     "class files (*.class)", "class");
		fileChooser.setFileFilter(xmlfilter);
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		int result = fileChooser.showOpenDialog(frame);
		if (result == JFileChooser.APPROVE_OPTION) {
		    selectedFile = fileChooser.getSelectedFile();
		    fileNamePath.setText(selectedFile.getAbsolutePath());
		    inject.setEnabled(true);
			try {
				ClassParser cp = new ClassParser(selectedFile.getAbsolutePath());
				JavaClass outputClass;
				outputClass = cp.parse();
				String packageName = outputClass.getPackageName();
				outputFilePath = "/"+packageName.replaceAll("\\.", "/")+"/";
			} catch (ClassFormatException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}
