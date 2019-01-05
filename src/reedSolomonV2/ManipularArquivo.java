package reedSolomonV2;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.nio.file.Path;
import java.util.*;

public class ManipularArquivo {

	public static void main(String[] args) throws IOException, ReedSolomonException {

		// k=2301; n=2501; n-k=200		
		// Codificado ainda continua legivel pelo windows
		GenericGF gf = new GenericGF(69643, 65536, 1);
		ManipularArquivo manipulacao = new ManipularArquivo();
		SecureRandom random = new SecureRandom();
		int k = 0;
		int l = 0;

		// Strings com os locais dos arquivos a serem gravados ou manipulados
		String arquivoLocal = "Z:\\@desenvolvimento\\workspace\\Testes-com-RS-GF(2^16)\\Termo de Movimentação - HOMOL.pdf";

		String arquivoNome = "Z:\\@desenvolvimento\\workspace\\Testes-com-RS-GF(2^16)\\Codificado";

		String redundanciaNome = "Z:\\@desenvolvimento\\workspace\\Testes-com-RS-GF(2^16)\\redundancia.rdg";

		String arquivoDecodificadoLocal = "Z:\\@desenvolvimento\\workspace\\Testes-com-RS-GF(2^16)Codificado.pdf";

		String arquivoDecodificadoNome = "Z:\\@desenvolvimento\\workspace\\Testes-com-RS-GF(2^16)\\Decodificado";
		
		int[] data = ManipularArquivo.byteSignedParaUnsigned(arquivoLocal, 200);
		System.out.println("Quantidade de simbolos, com os indices para paridade: " + data.length);

		// Codificacao
		ReedSolomonEncoder encoder = new ReedSolomonEncoder(gf);
		System.out.println("\n" + "Antes da codificacao: " + "\n" + Arrays.toString(data));
		System.out.println("Quantidade de simbolos: " + data.length);
		encoder.encode(data, 200);
		System.out.println("\n" + "Depois da codificacao: " + "\n" + Arrays.toString(data));
		System.out.println("Quantidade de simbolos: " + data.length);
		
		// Guarda a redundancia no vetor redundanciaInt
		int temp = 0;
		int ndP = 0;
		int[] redundanciaInt = new int[200]; // tamanho do vetor = n-k
		for (int x = 2301; x < 2501; x++) {
			temp = data[x];
			redundanciaInt[ndP] = temp;
			ndP++;
		}
		// Apaga redundancia
		for (int x = 2301; x < 2501; x++) {
			data[x] = 0;
		}

		System.out.println("\n" + "Novo vetor de dados sem a redundancia: " + "\n" + Arrays.toString(data));
		System.out.println("Quantidade de simbolos: " + data.length);
		System.out.println("\n" + "Redundancia: " + "\n" + Arrays.toString(redundanciaInt));
		System.out.println("Quantidade de simbolos: " + redundanciaInt.length);

		// Transformar de int[] para byte[] e guardar o arquivo codificado
		// Guardar a redundancia em um arquivo sem converter de int[] para byte[]
		byte[] codificado = ManipularArquivo.byteUnsignedParaSigned(data);
		ManipularArquivo.gravarRedundancia(redundanciaInt, redundanciaNome);
		System.out.println("\n" + "Volta do vetor de dados de byte para int: " + "\n" + Arrays.toString(codificado));
		System.out.println("Quantidade de simbolos: " + codificado.length);
		
		//Vetor de bytes com SecureRandom
		byte[] corrupcao = new byte[100];
	    random.nextBytes(corrupcao);
		
		 // Concatenando dado com corrupcao randomica, de acordo com n-k/2
		  for (int x = 0; x < 100; x++) {
		  codificado[x] = corrupcao[l]; 
		  l++; 
		  }
		  

		// Gravar arquivo codificado e corrompido
		ManipularArquivo.gravaArquivo(codificado, arquivoLocal, arquivoNome);

		// Transformar de byte[] para int[] e corromper
		int[] voltaDeByte = ManipularArquivo.byteSignedParaUnsignedSemParidade(
				"Z:\\@desenvolvimento\\workspace\\Testes-com-RS-GF(2^16)\\Codificado.pdf");

		
		  //Decodificacao 
		  ReedSolomonDecoder decoder = new ReedSolomonDecoder(gf);
		  
		  System.out.println("\n" + "Corrupcao: " + "\n" + Arrays.toString(voltaDeByte));
		  System.out.println("Quantidade de simbolos: " + voltaDeByte.length);
		  
		  // Concatenando dado com redudancia 
		  for (int x = 2301; x < 2501; x++) {
		  voltaDeByte[x] = redundanciaInt[k]; 
		  k++; 
		  }
		  
		  System.out.println("\n" + "Vetor de dados concatenado com redundancia: " + "\n" +
		  Arrays.toString(voltaDeByte));
		  System.out.println("Quantidade de simbolos: " + voltaDeByte.length);
		  
		  decoder.decode(voltaDeByte, 200);
		  
		  System.out.println("\n" + "Mensagem corrigida: " + "\n" +
		  Arrays.toString(voltaDeByte));
		  System.out.println("Quantidade de simbolos: " + voltaDeByte.length);
		  
		  // gravo o arquivo decodificado 
		  byte[] decodificado =
		  ManipularArquivo.byteUnsignedParaSigned(voltaDeByte);
		  ManipularArquivo.gravaArquivo(decodificado, arquivoDecodificadoLocal,
		  arquivoDecodificadoNome);		 
	}

	// Gravar os bytes de um arquivo em um vetor de bytes
	private static byte[] lerBytesArquivo(String fileName) throws IOException {
		Path path = Paths.get(fileName);
		byte[] data = Files.readAllBytes(path);
		return data;
	}

	// Transformar os bytes de um arquivo lido de SIGNED [-128 a 127] em UNSIGNED [0
	// a 255] e escrever em um vetor de inteiros
	// e acrescentar n-k posicoes no vetor devolvido, pois, o bloco n do RS e
	// composto por k(informacao) e n-k(paridade)
	// Usar APENAS na primeira leitura do arquivo para vetor de bytes
	private static int[] byteSignedParaUnsigned(String fileName, int qtdSimbolosParidade) throws IOException {
		Path path = Paths.get(fileName);
		byte[] bytesArquivoLido = Files.readAllBytes(path);
		int tamanhoVetorInt = bytesArquivoLido.length;
		int valorEmIntDoByte = 0;
		int[] vetorDevolvido = new int[tamanhoVetorInt + qtdSimbolosParidade];

		// Bytes em java representam inteiros em complemento de dois, a soma e feita nos
		// bytes com valores negativos
		// Pois, o RS opera com numeros inteiros e positivos
		for (int i = 0; i < tamanhoVetorInt; i++) {
			valorEmIntDoByte = bytesArquivoLido[i];
			if (valorEmIntDoByte < 0) {
				valorEmIntDoByte = valorEmIntDoByte + 256;
			}
			vetorDevolvido[i] = valorEmIntDoByte;
		}
		return vetorDevolvido;
	}

	// Transformar os bytes de um arquivo lido de SIGNED [-128 a 127] em UNSIGNED [0
	// a 255] e escrever em um vetor de inteiros
	// Usar esse metodos para as demais conversoes que nao a primeira
	private static int[] byteSignedParaUnsignedSemParidade(String fileName) throws IOException {
		Path path = Paths.get(fileName);
		byte[] bytesArquivoLido = Files.readAllBytes(path);
		int tamanhoVetorInt = bytesArquivoLido.length;
		int valorEmIntDoByte = 0;
		int[] vetorDevolvido = new int[tamanhoVetorInt];

		// Bytes em java representam inteiros em complemento de dois, a soma e feita nos
		// bytes com valores negativos
		// Pois, o RS opera com numeros inteiros e positivos
		for (int i = 0; i < tamanhoVetorInt; i++) {
			valorEmIntDoByte = bytesArquivoLido[i];
			if (valorEmIntDoByte < 0) {
				valorEmIntDoByte = valorEmIntDoByte + 256;
			}
			vetorDevolvido[i] = valorEmIntDoByte;
		}
		return vetorDevolvido;
	}

	// Metodo para conversao de int[] para byte[] apenas em inteiros com sinal e
	// oito bits (complemento de 2 do Java)
	// Metodo para transformar um byte UNSIGNED em SIGNED
	private static byte[] byteUnsignedParaSigned(int[] vetorIntUnsigned) throws IOException {
		int valorEmIntDoByte = 0;
		int tamanho = vetorIntUnsigned.length;
		byte[] novoVetorBytes = new byte[tamanho];
		for (int i = 0; i < novoVetorBytes.length; ++i) {
			valorEmIntDoByte = vetorIntUnsigned[i];
			novoVetorBytes[i] = byteParaInt(valorEmIntDoByte);
		}
		return novoVetorBytes;
	}

	// Metodo auxiliar para transformar um unico inteiro unsigned em inteiro signed
	// apenas em inteiro
	// com sinal e oito bits (complemento de 2 do Java)
	private static byte byteParaInt(int intByte) {
		byte valorDoByteEmInt = 0;
		if (intByte <= 256) {
			valorDoByteEmInt = (byte) intByte;
			if (valorDoByteEmInt > 127) {
				valorDoByteEmInt = (byte) (valorDoByteEmInt - 256);
			}
		}
		return valorDoByteEmInt;
	}

	// Gravar um arquivo
	private static File gravaArquivo(byte[] convertidoDeIntArray, String localDoArquivo, String nomeDoArquivo)
			throws IOException {
		File f = new File(localDoArquivo);
		// Recuperar extensao do arquivo
		String extensao = "";
		int in = f.getAbsolutePath().lastIndexOf(".");
		if (in > -1) {
			extensao = f.getAbsolutePath().substring(in + 1);
		}
		// Grava o arquivo com o nome fornecido e a extensao lida
		File newFile = new File(nomeDoArquivo + "." + extensao);
		FileOutputStream stream = new FileOutputStream(newFile);
		stream.write(convertidoDeIntArray);
		stream.close();
		return newFile;
	}

	// Gravar a redundancia em um arquivo
	private static void gravarRedundancia(int[] vetorRedundancia, String localComNomeArquivo) throws IOException {
		OutputStream os = new FileOutputStream(localComNomeArquivo);
		for (int i = 0; i < vetorRedundancia.length; i++) {
			os.write(vetorRedundancia[i]);
		}
		os.close();
	}

	// Percorrer o vetor de dados e guardar a redundancia

	// Corromper n-k posicoes do vetor de dados

	// Concatenar o vetor de redundancia com o vetor de dados

	// Corromper x posicoes do vetor de dados e guardar o arquivo


}
