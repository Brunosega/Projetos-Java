import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;


public class Cliente {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		// abre conexão com o servidor
		try {
			Scanner scanner = new Scanner(System.in);
			String texto;
			String usuario;
			boolean admin = false;
			Socket s = new Socket("127.0.0.1", 5000);
			DataOutputStream saida = new DataOutputStream( s.getOutputStream());
			DataInputStream entrada = new DataInputStream( s.getInputStream());
			
			System.out.println("Digite seu usuário:");
			usuario = scanner.nextLine();
			saida.writeUTF(usuario);
			
			if(usuario.equals("admin")) {
				System.out.println(entrada.readUTF());
				saida.writeUTF(scanner.nextLine());
				System.out.println(entrada.readUTF());
				//senha incorreta
				if(!(admin = entrada.readBoolean())) {
					System.out.println(entrada.readUTF());
				}else { // ADMIN
					while(true){
						System.out.println(entrada.readUTF());
						switch(scanner.nextInt()) {
							case 1:
								saida.writeInt(1);
								//case servidor
								System.out.println(entrada.readUTF());
								scanner.nextLine();
								saida.writeUTF(scanner.nextLine());
								System.out.println(entrada.readUTF());
								break;
							case 2:
								saida.writeInt(2);
								//case servidor
								System.out.println(entrada.readUTF());
								scanner.nextLine();
								saida.writeUTF(scanner.nextLine());
								System.out.println(entrada.readUTF());
								saida.writeInt(scanner.nextInt());
								System.out.println(entrada.readUTF());
								break;

							case 3:
								saida.writeInt(3);
								//case servidor
								System.out.println(entrada.readUTF());
								scanner.nextLine();
								saida.writeUTF(scanner.nextLine());
								System.out.println(entrada.readUTF());
								saida.writeInt(scanner.nextInt());
								System.out.println(entrada.readUTF());
								break;
							default:
								saida.writeInt(0);
								System.out.println(entrada.readUTF());
								s.close();
								break;
						}
					}
				}
			}
			//USUARIO COMUM
			else if(!admin && !entrada.readBoolean()) {
				System.out.println("Para sair, clique ENTER. Para ver seu saldo, escreva e envie -s.");
				System.out.println("Para inverter um texto, apenas digite-o e envie-o:");
				while(true){
					System.out.println( '\n' + usuario +  ":" );
					texto = scanner.nextLine();
					saida.writeUTF(texto);
					if(texto.equals("")){
						break;
					}
					
					String resultado = entrada.readUTF();
					System.out.println(resultado);
				}
				s.close(); // encerra a conexao com o servidor
			}else {
				System.out.println( "Usuário não cadastrado. Entre em contato com o admin para saber mais.\n");
				s.close(); // encerra a conexao com o servidor
			}
			System.out.println("Desconectado.");
			s.close(); // encerra a conexao com o servidor
			} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
