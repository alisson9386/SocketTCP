import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Vector;

public class Servidor extends Thread{


	private Socket conexao;

	//Vetores dos assuntos
	private static Vector<DataOutputStream> vetorEconomia = new Vector<DataOutputStream>();
	private static Vector<DataOutputStream> vetorEntretenimento = new Vector<DataOutputStream>();
	private static Vector<DataOutputStream> vetorTecnologia = new Vector<DataOutputStream>();

	public Servidor (Socket s) {
		conexao = s;
	}

	public static void main(String[] args) throws IOException {




		@SuppressWarnings("resource") //usado para reduzir falso positivo
		ServerSocket servidor = new ServerSocket(8657); // cria socket de comunicação com os clientes na porta 8657

		while(true) {
			// espera conexão de algum cliente
			System.out.println("Esperando cliente se conectar...");
			Socket cx = servidor.accept();

			Thread t = new Servidor(cx);
			t.start();

			System.out.println("Cliente conectado!");
		}
	}

	//método Run

	public void run() {

		String msg_recebida; //lida do cliente
		String msg_enviada; //enviada ao cliente
		String nome_cliente = " ";
		String assunto_cliente;

		// cria streams de entrada e saida com o cliente que chegou
		BufferedReader entrada_cliente;


		try {

			entrada_cliente = new BufferedReader(new InputStreamReader(conexao.getInputStream()));
			DataOutputStream saida_cliente = new DataOutputStream(conexao.getOutputStream());



			nome_cliente = entrada_cliente.readLine();

			saida_cliente.writeBytes("Servidor> : (Oi " + nome_cliente + ") : <" + getDateTime()+ ">\n");

			assunto_cliente = entrada_cliente.readLine();

			//variavel de controle
			Integer i;
			Vector<DataOutputStream> v;


			//adiciona os clientes no vetor e separa por assuntos
			switch(assunto_cliente) {

			case "1":
				vetorEconomia.add(saida_cliente);
				v = vetorEconomia;
				assunto_cliente = "Economia";
				break;
			case "2":
				vetorEntretenimento.add(saida_cliente);
				v = vetorEntretenimento;
				assunto_cliente = "Entretenimento";
				break;
			case "3":
				vetorTecnologia.add(saida_cliente);
				v = vetorTecnologia;
				assunto_cliente = "Tecnologia";
				break;
			default:
				vetorEntretenimento.add(saida_cliente);
				v = vetorEntretenimento;
				assunto_cliente = "Entretenimento";

			}

			// envia as mensagens recebidas para todos os clientes com o mesmo assunto escolhido
			i = 0;
			while(i < v.size()) {
				v.get(i).writeBytes("Servidor> : (" + nome_cliente + " entrou no chat {" + assunto_cliente + "}) : <" + getDateTime()+"<\n");
				i = i + 1;

			}
			// envia o nome, o assunto e a mensagem do cliente, junto com a hora

			msg_recebida = entrada_cliente.readLine();

			while(msg_recebida != null && !(msg_recebida.trim().equals("")) && ! (msg_recebida.startsWith("fim"))) {

				System.out.println(nome_cliente + ": "+ msg_recebida);

				msg_enviada = " (" + nome_cliente + ") : (" + assunto_cliente + ") : <" + msg_recebida + "> : <" + getDateTime() + ">\n";

				i = 0;
				while(i < v.size()) {
					if(v.get(i) != saida_cliente) {
						v.get(i).writeBytes(msg_enviada);
					}
					i = i + 1;
				}

				msg_recebida = entrada_cliente.readLine();
			}

			//registra saida do cliente no servidor

			i = 0;
			while(i < v.size()) {
				v.get(i).writeBytes("Servidor> : (" + nome_cliente + " saiu da conversa {" + assunto_cliente + "}) : <" + getDateTime() + ">\n");
				i = i + 1;
			}
			i = 0;
			while(i < v.size()) {
				if(v.get(i) == saida_cliente) {
					v.remove(v.get(i));
					System.out.println("Cliente desconectado!");

				}
				i = i + 1;
			}

			conexao.close();


		}catch (IOException e) {
			e.printStackTrace();

		}
	}


	//necessário para a função de pegar a hora da mensagem
	private static String getDateTime() {
		DateFormat formatoData = new SimpleDateFormat("HH:mm");
		Date date = new Date(0);
		return formatoData.format(date);

	}
}
