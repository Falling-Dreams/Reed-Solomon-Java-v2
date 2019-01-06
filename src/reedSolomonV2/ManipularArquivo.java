package reedSolomonV2;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.nio.file.Path;
import java.util.*;

/*
 * 	TODO: Percorrer o vetor de dados e guardar a redundancia

	TODO: Corromper n-k posicoes do vetor de dados

	TODO: Concatenar o vetor de redundancia com o vetor de dados

	TODO: Corromper x posicoes do vetor de dados e guardar o arquivo
 */

public class ManipularArquivo {

	public static void main(String[] args) throws IOException, ReedSolomonException {

		// k=2301; n=2501; n-k=200; t=100
		// Codificado ainda continua legivel pelo windows
		String arquivoLocal = "Z:\\@desenvolvimento\\workspace\\Testes-com-RS-GF(2^16)\\GPEs em atividade.pdf";
		ManipularArquivo.degradacaoCorretiva(arquivoLocal);

	}

	// Cria os arquivos codificado e redundancia. Corrompe original
	private static void degradacaoCorretiva(String localDoArquivo) throws IOException, ReedSolomonException {
		GenericGF gf = new GenericGF(69643, 65536, 1);
		int qtdSimbolosParidade = 200;
		int t = 100;
		
		String arquivoNome = "Z:\\@desenvolvimento\\workspace\\Testes-com-RS-GF(2^16)\\Codificado";
		String redundanciaNome = "Z:\\@desenvolvimento\\workspace\\Testes-com-RS-GF(2^16)\\redundancia.rdg";
		String arquivoDecodificadoLocal = "Z:\\@desenvolvimento\\workspace\\Testes-com-RS-GF(2^16)\\Codificado.pdf";
		String arquivoDecodificadoNome = "Z:\\@desenvolvimento\\workspace\\Testes-com-RS-GF(2^16)\\Decodificado";
		// Transforma vetor de bytes em vetor de inteiros unsigned
		int[] arquivo = ManipularArquivo.byteSignedParaUnsigned(localDoArquivo, qtdSimbolosParidade);

		// Codificacao e vetor da redundancia
		ReedSolomonEncoder encoder = new ReedSolomonEncoder(gf);
		encoder.encode(arquivo, qtdSimbolosParidade);
		int[] vetorParidade = ManipularArquivo.salvaVetorRedundancia(arquivo, qtdSimbolosParidade);

		// Apaga redundancia
		ManipularArquivo.zerarRedundancia(arquivo, qtdSimbolosParidade);

		// Transformar de int[] para byte[]
		byte[] codificado = ManipularArquivo.byteUnsignedParaSigned(arquivo);

		// Guardar a redundancia em um arquivo sem converter de int[] para byte[]
		ManipularArquivo.gravarRedundancia(vetorParidade, redundanciaNome);

		// Cria vetor de bytes ja codificados pelo encoder e corrompido t posicoes
		ManipularArquivo.corrompeDado(codificado, t);

		// Gravar arquivo codificado e corrompido
		ManipularArquivo.gravaArquivo(codificado, localDoArquivo, arquivoNome);

		// Transformar de byte[] para int[] guardar arquivo corrompido
		int[] voltaDeByte = ManipularArquivo.byteSignedParaUnsignedSemParidade(arquivoDecodificadoLocal);

		// Decodificacao
		ReedSolomonDecoder decoder = new ReedSolomonDecoder(gf);

		// Concatenando dado com redudancia
		ManipularArquivo.concatenaDadoComRedundancia(voltaDeByte, vetorParidade, qtdSimbolosParidade);

		decoder.decode(voltaDeByte, qtdSimbolosParidade);

		// gravo o arquivo decodificado com o mesmo tamanho do arquivo original, sem os
		// indices de paridade
		byte[] decodificado = ManipularArquivo.byteUnsignedParaSigned(voltaDeByte);
		ManipularArquivo.gravaArquivo(decodificado, arquivoDecodificadoLocal, arquivoDecodificadoNome);
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
		//System.out.println("Antes da codificacao: " + "\n" + Arrays.toString(vetorDevolvido));
		System.out.println("Quantidade de simbolos, sem os indices de paridade: " + (vetorDevolvido.length - qtdSimbolosParidade));
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
		//System.out
				//.println("\n" + "Volta do vetor de dados de byte para int: " + "\n" + Arrays.toString(novoVetorBytes));
		System.out.println("Quantidade de simbolos: " + novoVetorBytes.length);
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

	// Guardar vetor de inteiros da redundancia
	private static int[] salvaVetorRedundancia(int[] vetorDados, int tamanhoParidade) {
		int temp = 0;
		int ndP = 0;
		int[] redundanciaInt = new int[tamanhoParidade]; // tamanho do vetor = n-k
		for (int x = vetorDados.length - tamanhoParidade; x < vetorDados.length; x++) {
			temp = vetorDados[x];
			redundanciaInt[ndP] = temp;
			ndP++;
		}
		//System.out.println("\n" + "Redundancia: " + "\n" + Arrays.toString(redundanciaInt));
		System.out.println("Quantidade de simbolos de redundancia: " + redundanciaInt.length);
		return redundanciaInt;
	}

	private static int[] zerarRedundancia(int[] dados, int tamanhoParidade) {
		for (int x = dados.length - tamanhoParidade; x < dados.length; x++) {
			dados[x] = 0;
		}
		//System.out.println("\n" + "Novo vetor de dados com a redundancia zerada: " + "\n" + Arrays.toString(dados));
		System.out.println("Quantidade de simbolos: " + dados.length);
		return dados;
	}

	private static void corrompeDado(byte[] dados, int t) {
		// Vetor de bytes com SecureRandom de tamanho t
		SecureRandom random = new SecureRandom();
		byte[] corrupcao = new byte[t];
		random.nextBytes(corrupcao);
		// Concatenando dado com corrupcao randomica, de acordo com t
		int l = 0;
		for (int x = 0; x < t; x++) {
			dados[x] = corrupcao[l];
			l++;
		}
		//System.out.println("\n" + "Corrupcao: " + "\n" + Arrays.toString(dados));
		System.out.println("Quantidade de simbolos: " + dados.length);
	}

	public static void concatenaDadoComRedundancia(int[] dados, int[] vetorRedundancia, int tamanhoParidade) {
		// Concatenando dado com redudancia
		int k = 0;
		for (int x = dados.length - tamanhoParidade; x < dados.length; x++) {
			dados[x] = vetorRedundancia[k];
			k++;
		}
		//System.out.println("\n" + "Vetor de dados concatenado com redundancia: " + "\n" + Arrays.toString(dados));
		System.out.println("Quantidade de simbolos: " + dados.length);
	}
	
	//Arrays sao imutaveis, uma vez criados o seu tamanho NAO pode ser alterado, portanto, o metodo abaixo nao funciona
	/*
	public static byte[] apagaParidade(byte[] dados, int qtdSimbolosParidade) {
		byte[] dadoSemParidade = new byte[dados.length - qtdSimbolosParidade];
		byte temp = 0;
		int ndP = 0;
		for (int x = 0; x < dados.length; x++) {
			temp = dados[x];
			dadoSemParidade[ndP] = temp;
			ndP++;
		}		
		return dadoSemParidade;
	}*/

}
