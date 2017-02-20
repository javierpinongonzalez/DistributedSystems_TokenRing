import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;


public class Servidor extends Thread{

	public int id;
	public boolean rdonly;
	public int valor;
	public int totalServers;
	
	public Socket prevSock;
	public Socket nextSocket;
	
	public PrintWriter nextOut;
	public PrintWriter prevOut;
	
	public BufferedReader nextIn;
	public BufferedReader prevIn;
	
	public Servidor(){
		id = -1;
		rdonly = false;
		valor = -1;
		totalServers = 0;
	}
	
	public Servidor(int id, boolean rdonly, int valor, int totalServers){
		this.id = id;
		this.rdonly = rdonly;
		this.valor = valor;
		this.totalServers = totalServers;
	}
	
	public void configPrevSocket(){
		try {
			if (id == 1)
			{
				prevSock = new Socket("127.0.0.1", 8000+totalServers);
			}else {
				prevSock = new Socket("127.0.0.1", 8000+id-1);
			}
			
			prevOut = new PrintWriter(prevSock.getOutputStream(), true);
			prevIn = new BufferedReader(new InputStreamReader(prevSock.getInputStream())); 
											
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void configNextSock(){
		try {
			ServerSocket serverSock = new ServerSocket(8000+id);
			nextSocket = serverSock.accept();
			
			nextOut = new PrintWriter(nextSocket.getOutputStream(), true);
			nextIn = new BufferedReader(new InputStreamReader(nextSocket.getInputStream()));
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void run(){
		
		initConfig();
		
		doIterations();
		
		closeConfig();
		System.out.println("Bye from thread num: "+id);
	}
	
	private void initConfig (){
		System.out.println("Thread " + id + " starting");
		if (id==1){
			configNextSock();
			try {
				sleep(250*(totalServers+1));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			configPrevSocket();
		}else{
			configPrevSocket();
			configNextSock();
		}
	}
	
	private void sendToken(int valor){
		nextOut.println("token-"+valor);
	}
	
	private String waitForToken(){
		String inputStr;
		String[] parts;
		
		try {
			do{
				inputStr = prevIn.readLine();
				parts = inputStr.split("-");
			}while (!parts[0].equals("token"));
			
			return inputStr;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	private void doIterations(){
		String inputStr;
		String[] parts;
		
		if(id!=1){
			inputStr = waitForToken();
			parts = inputStr.split("-");
		}else {
			inputStr = "token-0";
			parts = inputStr.split("-");
		}
		for (int i=0; i<10; i++){
			
			valor = Integer.parseInt(parts[1]);
			
			if (rdonly){
				System.out.println("Thread "+id+ " reading valor: "+valor);
			}else{
				System.out.println("Thread "+id+ " updating valor "+valor+" to "+(valor+1));
				valor ++;
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			sendToken(valor);
			
			inputStr = waitForToken();
			
			parts = inputStr.split("-");
			
		}
		
		sendToken(0);
	}
	
	private void closeConfig(){
		try {
			nextIn.close();
			nextOut.close();
			prevOut.close();
			prevIn.close();
			
			prevSock.close();
			nextSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void testPassingToken(){
		String inputStr;
		try {
			if(id==1){
				System.out.println("Thread" + id + " sends 'Token'");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				nextOut.println("Token");
				inputStr=prevIn.readLine();
			}else{
				inputStr=prevIn.readLine();
				System.out.println("Thread" + id + " sends 'Token'");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				nextOut.println("Token");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
