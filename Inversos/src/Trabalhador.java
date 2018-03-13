<<<<<<< HEAD
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class Trabalhador extends Thread {
	
	private Socket t;
	private int restantes;
	private int rO;
	private String usuario;
	
	public Trabalhador( Socket t, int restantes)
	{
		this.t = t;
		this.restantes = restantes;
	}
	
	public void run()
	{
		try
		{
			DataInputStream entrada = new DataInputStream( t.getInputStream());
			DataOutputStream saida = new DataOutputStream( t.getOutputStream());	
			//Le username
			usuario = entrada.readUTF();
			//Le arquivo
			String line = null;
			String [] array;
			boolean novo = true;
			boolean admin = false;
			FileReader fileReader = new FileReader("dados.txt");
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			
			if(usuario.equals("admin")) {
				//compara a senha do admin no arquivo com a enviada pelo cliente
				saida.writeUTF("Digite a senha de administrador:");
				String senha = entrada.readUTF();
				line = bufferedReader.readLine();
				array = line.split("\t");
				if(senha.equals(array[1])) {
					saida.writeUTF("Admin logado com sucesso! Para sair, clique ENTER ou uma opcao invalida.");
					saida.writeBoolean(true);
					bufferedReader.close();
					//ADMIN
					while(true){

						saida.writeUTF("Opcoes: \n1-Inverter String \n2-Novo usuario\n3-Adicionar creditos");
						switch(entrada.readInt()) {
						
						//inverter string
						case 1:
							saida.writeUTF("String a ser invertida:");
							StringBuffer resultado = new StringBuffer( entrada.readUTF() ).reverse();
							saida.writeUTF(resultado.toString());	
							break;
						//Novo usuario
						case 2:
							
							saida.writeUTF("Nome do usuário:");
							usuario = entrada.readUTF();
							saida.writeUTF("Saldo do usuário:");
							restantes = entrada.readInt();
							Path path = Paths.get("dados.txt");
							novo = true;

							fileReader = new FileReader("dados.txt");
							bufferedReader = new BufferedReader(fileReader);
							//Procura usuario no arquivo
							while((line = bufferedReader.readLine()) != null){
								array = line.split("\t");
								if(array[0].equals(usuario)) {
									restantes = Integer.parseInt(array[1]);
									novo = false;
									break;
								}
							}
							bufferedReader.close();
							if(!novo) {
								saida.writeUTF("Usuário já cadastrado!");
							}
							else {
								FileWriter fileWriter = new FileWriter("dados.txt",true);
								BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
								bufferedWriter.newLine();
								bufferedWriter.write(usuario + '\t' + restantes);
								saida.writeUTF("Usuario criado!");
								bufferedWriter.close();
							}
							break;
						//adicionar credito a usuario
						case 3:
							saida.writeUTF("Nome do usuário:");
							usuario = entrada.readUTF();
							saida.writeUTF("Saldo a ser adicionado ao usuário:");
							int saldoAdd = entrada.readInt();
							Path path1 = Paths.get("dados.txt");
							novo = true;
							fileReader = new FileReader("dados.txt");
							bufferedReader = new BufferedReader(fileReader);
							//Procura usuario no arquivo
							while((line = bufferedReader.readLine()) != null){
								array = line.split("\t");
								if(array[0].equals(usuario)) {
									restantes = Integer.parseInt(array[1]);
									novo = false;
									break;
								}
							}
							bufferedReader.close();
							if(novo) {
								saida.writeUTF("Usuário não encontrado!");
							}
							else {
								saldoAdd = restantes + saldoAdd;
								Charset charset = StandardCharsets.UTF_8;
								String content = new String(Files.readAllBytes(path1), charset);
								content = content.replaceAll(usuario + '\t' + restantes, usuario + '\t' + saldoAdd);
								Files.write(path1, content.getBytes(charset));
								saida.writeUTF("Creditos adicionados ao usuario!");
							}
							break;
						default:
							saida.writeUTF("Desconectado.");
							t.close();
							break;
						}
						
					}
					
				}else {
					saida.writeUTF("Senha incorreta!");
					saida.writeBoolean(false);
					saida.writeUTF("Usuário desconectado.");
				}
			}else {
				//USUARIO COMUM
				//Procura usuario no arquivo
				while((line = bufferedReader.readLine()) != null){
					array = line.split("\t");
					if(array[0].equals(usuario)) {
						restantes = Integer.parseInt(array[1]);
						novo = false;
						break;
					}
				}
				saida.writeBoolean(novo);
				bufferedReader.close();
				//}
				rO = restantes;
				//Entradas do usuario
				while(true){
					String texto = entrada.readUTF();
					if(texto.equals("")){ //Se deseja parar
						System.out.println( "Servidor: conexao encerrada");
						//Procura usuario no arquivo e substitui o valor restante
						Path path = Paths.get("dados.txt");
						Charset charset = StandardCharsets.UTF_8;
						String content = new String(Files.readAllBytes(path), charset);
						content = content.replaceAll(usuario + '\t' + rO, usuario + '\t' + restantes);
						Files.write(path, content.getBytes(charset));
						t.close();
						break;
					}else if(texto.equals("-s")){
						saida.writeUTF("Saldo atual: " + restantes);
					}else if(restantes >= texto.length()) { //Pede para inverter string (com saldo)
						//System.out.println( "Recebido: " + texto );
						restantes = restantes - texto.length();
						
						StringBuffer resultado = new StringBuffer( texto ).reverse();
						saida.writeUTF(resultado.toString());	
					}else { //Pede para inverter string (sem saldo)
						saida.writeUTF("ERRO! Creditos insuficientes! Creditos atuais:" + restantes);	
					}
				}
			}

			t.close();
			
		}
		catch( Exception e )
		{
			//Procura usuario no arquivo e substitui o valor restante
			//System.out.println("Servidor: usuario desconectado.");
			Path path = Paths.get("dados.txt");
			Charset charset = StandardCharsets.UTF_8;
			String content;
			try {
				content = new String(Files.readAllBytes(path), charset);
				content = content.replaceAll(usuario + '\t' + rO, usuario + '\t' + restantes);
				Files.write(path, content.getBytes(charset));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
	}

}
=======
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class Trabalhador extends Thread {
	
	private Socket t;
	private int restantes;
	private int rO;
	private String usuario;
	
	public Trabalhador( Socket t, int restantes)
	{
		this.t = t;
		this.restantes = restantes;
	}
	
	public void run()
	{
		try
		{
			DataInputStream entrada = new DataInputStream( t.getInputStream());
			DataOutputStream saida = new DataOutputStream( t.getOutputStream());	
			//Le username
			usuario = entrada.readUTF();
			//Le arquivo
			String line = null;
			String [] array;
			boolean novo = true;
			boolean admin = false;
			FileReader fileReader = new FileReader("dados.txt");
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			
			if(usuario.equals("admin")) {
				//compara a senha do admin no arquivo com a enviada pelo cliente
				saida.writeUTF("Digite a senha de administrador:");
				String senha = entrada.readUTF();
				line = bufferedReader.readLine();
				array = line.split("\t");
				if(senha.equals(array[1])) {
					saida.writeUTF("Admin logado com sucesso! Para sair, clique ENTER ou uma opcao invalida.");
					saida.writeBoolean(true);
					bufferedReader.close();
					//ADMIN
					while(true){

						saida.writeUTF("Opcoes: \n1-Inverter String \n2-Novo usuario\n3-Adicionar creditos");
						switch(entrada.readInt()) {
						
						//inverter string
						case 1:
							saida.writeUTF("String a ser invertida:");
							StringBuffer resultado = new StringBuffer( entrada.readUTF() ).reverse();
							saida.writeUTF(resultado.toString());	
							break;
						//Novo usuario
						case 2:
							
							saida.writeUTF("Nome do usuário:");
							usuario = entrada.readUTF();
							saida.writeUTF("Saldo do usuário:");
							restantes = entrada.readInt();
							Path path = Paths.get("dados.txt");
							novo = true;

							fileReader = new FileReader("dados.txt");
							bufferedReader = new BufferedReader(fileReader);
							//Procura usuario no arquivo
							while((line = bufferedReader.readLine()) != null){
								array = line.split("\t");
								if(array[0].equals(usuario)) {
									restantes = Integer.parseInt(array[1]);
									novo = false;
									break;
								}
							}
							bufferedReader.close();
							if(!novo) {
								saida.writeUTF("Usuário já cadastrado!");
							}
							else {
								FileWriter fileWriter = new FileWriter("dados.txt",true);
								BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
								bufferedWriter.newLine();
								bufferedWriter.write(usuario + '\t' + restantes);
								saida.writeUTF("Usuario criado!");
								bufferedWriter.close();
							}
							break;
						//adicionar credito a usuario
						case 3:
							saida.writeUTF("Nome do usuário:");
							usuario = entrada.readUTF();
							saida.writeUTF("Saldo a ser adicionado ao usuário:");
							int saldoAdd = entrada.readInt();
							Path path1 = Paths.get("dados.txt");
							novo = true;
							fileReader = new FileReader("dados.txt");
							bufferedReader = new BufferedReader(fileReader);
							//Procura usuario no arquivo
							while((line = bufferedReader.readLine()) != null){
								array = line.split("\t");
								if(array[0].equals(usuario)) {
									restantes = Integer.parseInt(array[1]);
									novo = false;
									break;
								}
							}
							bufferedReader.close();
							if(novo) {
								saida.writeUTF("Usuário não encontrado!");
							}
							else {
								saldoAdd = restantes + saldoAdd;
								Charset charset = StandardCharsets.UTF_8;
								String content = new String(Files.readAllBytes(path1), charset);
								content = content.replaceAll(usuario + '\t' + restantes, usuario + '\t' + saldoAdd);
								Files.write(path1, content.getBytes(charset));
								saida.writeUTF("Creditos adicionados ao usuario!");
							}
							break;
						default:
							saida.writeUTF("Desconectado.");
							t.close();
							break;
						}
						
					}
					
				}else {
					saida.writeUTF("Senha incorreta!");
					saida.writeBoolean(false);
					saida.writeUTF("Usuário desconectado.");
				}
			}else {
				//USUARIO COMUM
				//Procura usuario no arquivo
				while((line = bufferedReader.readLine()) != null){
					array = line.split("\t");
					if(array[0].equals(usuario)) {
						restantes = Integer.parseInt(array[1]);
						novo = false;
						break;
					}
				}
				saida.writeBoolean(novo);
				bufferedReader.close();
				//}
				rO = restantes;
				//Entradas do usuario
				while(true){
					String texto = entrada.readUTF();
					if(texto.equals("")){ //Se deseja parar
						System.out.println( "Servidor: conexao encerrada");
						//Procura usuario no arquivo e substitui o valor restante
						Path path = Paths.get("dados.txt");
						Charset charset = StandardCharsets.UTF_8;
						String content = new String(Files.readAllBytes(path), charset);
						content = content.replaceAll(usuario + '\t' + rO, usuario + '\t' + restantes);
						Files.write(path, content.getBytes(charset));
						t.close();
						break;
					}else if(texto.equals("-s")){
						saida.writeUTF("Saldo atual: " + restantes);
					}else if(restantes >= texto.length()) { //Pede para inverter string (com saldo)
						//System.out.println( "Recebido: " + texto );
						restantes = restantes - texto.length();
						
						StringBuffer resultado = new StringBuffer( texto ).reverse();
						saida.writeUTF(resultado.toString());	
					}else { //Pede para inverter string (sem saldo)
						saida.writeUTF("ERRO! Creditos insuficientes! Creditos atuais:" + restantes);	
					}
				}
			}

			t.close();
			
		}
		catch( Exception e )
		{
			//Procura usuario no arquivo e substitui o valor restante
			//System.out.println("Servidor: usuario desconectado.");
			Path path = Paths.get("dados.txt");
			Charset charset = StandardCharsets.UTF_8;
			String content;
			try {
				content = new String(Files.readAllBytes(path), charset);
				content = content.replaceAll(usuario + '\t' + rO, usuario + '\t' + restantes);
				Files.write(path, content.getBytes(charset));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
	}

}
>>>>>>> 52bfdba5fb5fa564aedf6847bce72c5a65500fc0
