import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;


/*
 * Responsabilidade: Unidade centralizadora que recebera as conexoes via socket
 * e envio de mensagem para os clientes conectados neste servidor
 * Quando o cliente se conecta ao servidor, ele cria uma thread para aquele cliente
 * cada conexao tera sua thread e o servidor vai fazer a gestao disso
 * 
 */
public class Servidor extends Thread {
	private static ArrayList<BufferedWriter> clientes;
	private static ServerSocket server;
	private String nome;
	private Socket socket;
	private InputStream inputStream;
	private InputStreamReader inputStreamReader;
	private BufferedReader bufferReader;
	
	public Servidor(Socket socket) {
		this.socket = socket;
		
		try {
			inputStream = socket.getInputStream();
			inputStreamReader = new InputStreamReader(inputStream);
			bufferReader = new BufferedReader(inputStreamReader);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		try {
			String msg;
			
			OutputStream outputStream = this.socket.getOutputStream();
			
			Writer outputStreamWriter = new OutputStreamWriter(outputStream);
			
			BufferedWriter bufferWriter = new BufferedWriter(outputStreamWriter);
			
			clientes.add(bufferWriter);
			
			nome = msg = bufferReader.readLine();
			
			while (!"Sair".equalsIgnoreCase(msg) && msg != null) {
				msg = bufferReader.readLine();
				
				sendToAll(bufferWriter, msg);
				
				System.out.println("Mensagem: "+ msg);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void sendToAll(BufferedWriter bufferWriter, String msg) throws IOException {
		
		BufferedWriter bws;
		
		for (BufferedWriter bw : clientes) {
			bws = (BufferedWriter)bw;
			
			if (!(bufferWriter == bws)) {
				bw.write(nome + " -> " + msg + "\r\n");
				bw.flush();
			}
		}
		
	}
	
	public static void main(String[] args) {
		try {
			JLabel lblMessage = new JLabel("Porta do servidor");
			JTextField txtPorta = new JTextField("12345");
			
			Object[] texts = {lblMessage, txtPorta};
			
			JOptionPane.showMessageDialog(null, texts);
			
			server = new ServerSocket(Integer.parseInt(txtPorta.getText()));
			
			clientes = new ArrayList<BufferedWriter>();
			
			JOptionPane.showMessageDialog(null, "Servidor ativo na porta: " + txtPorta.getText());
			
			while (true) {
				System.out.println("Aguardando conexões...");
				
				Socket socket = server.accept();
				
				System.out.println("Cliente conectado ...");
				
				Thread t = new Servidor(socket);
				t.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
