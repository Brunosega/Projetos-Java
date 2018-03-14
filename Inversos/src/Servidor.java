
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.Semaphore;


public class Servidor {

	/**
	 * @param args 
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		try {
			
			Scanner scanner = new Scanner(System.in);
			Semaphore mutex = new Semaphore(1);
			
			//Se nao houver arquivo dados.txt, cria um
			String fileName = "dados.txt";
			
			File f = new File(fileName);
			if(!f.exists() && !f.isDirectory()) {
				System.out.println("Senha do usuário admin:");
				String admin = scanner.nextLine();
				//admin = scanner.nextLine();

				FileWriter fileWriter = new FileWriter("dados.txt");
				BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
				bufferedWriter.write("admin" + '\t' + admin);
				
				bufferedWriter.close();
				
			}
			
			
			ServerSocket ss = new ServerSocket( 5000 );
			System.out.println( "Servidor aguardando um cliente ...");
			

			
			while (true)
			{
				Socket t = ss.accept(); // bloqueia até receber pedido de conexão do cliente
				//System.out.println( "Servidor: conexao feita: " +  t.getPort());
				
				Trabalhador trab = new Trabalhador( t , mutex);
				trab.start();

			}
			
			//ss.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}