import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

public class Cliente extends Thread{
	//iniciar o buffer de entrada
	private BufferedReader entrada;

	private static String tipo_menssagem;


	public Cliente (BufferedReader a) {

		entrada = a;

		tipo_menssagem = "Not Null";
	}

	public static void main(String[] args) throws UnknownHostException, IOException{

		String msg_recebida; // mensagem recebida
		String nome_cliente; // nome do cliente
		String assunto; //assunto que o cliente se interessa


		BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));

		System.out.println("Informe o nome do cliente: ");

		//registra o nome do cliente
		nome_cliente = teclado.readLine();

		//abre um socket tcp na porta 8657
		
		

		Socket cli = new Socket("localhost", 8657);

		DataOutputStream saida_servidor = new DataOutputStream(cli.getOutputStream());
		BufferedReader entrada_servidor = new BufferedReader(new InputStreamReader(cli.getInputStream()));

		saida_servidor.writeBytes(nome_cliente + '\n');

		msg_recebida = entrada_servidor.readLine();

		System.out.println(msg_recebida);

		//registra o assunto escolhido
		System.out.println("Informe o assunto desejado pelo cliente: \n1 - Economia\n2 - Entretenimento\n3 - Tecnologia");

		assunto = teclado.readLine();

		saida_servidor.writeBytes(assunto + '\n');

		msg_recebida = entrada_servidor.readLine();

		System.out.println(msg_recebida);

		//inicia a Thread
		Thread t = new Cliente(entrada_servidor);

		t.start();

		while (true) {

			tipo_menssagem = teclado.readLine();

			saida_servidor.writeBytes(tipo_menssagem + '\n');

			if(tipo_menssagem.startsWith("fim") == true)
				break;
		}

		msg_recebida = entrada_servidor.readLine();

		if(msg_recebida != null) {
			System.out.println(msg_recebida);
		}

		cli.close();

	}

	//metodo run para identificar o tipo de mensagem
	public void run() {
		try {

			while(tipo_menssagem != null && !(tipo_menssagem.trim().equals("")) && ! (tipo_menssagem.startsWith("fim"))) {
				System.out.println(entrada.readLine());
			}
			System.exit(0);

		} catch (IOException e) {
			System.exit(0);
		}
	}

}
