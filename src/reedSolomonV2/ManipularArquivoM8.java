package reedSolomonV2;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.nio.file.Path;

/*
		TODO: Percorrer o vetor de dados e guardar a redundancia
		
		TODO: Metodo para verificar se o resultado de uma divisao gerou um numero inteiro ou nao
		
		TODO: Rotina de codificacao/decodificacao conforme o tamanho do arquivo, cada iteracao sera de 255 simbolos
		
		TODO: Caso a QTD_ITERACOES nao seja um numero inteiro e preciso acrescentar uma iteracao adicional
		
		TODO: Tratar o caso em que uma iteracao seja suficiente
		
		TODO: E preciso guardar a redundancia de cada iteracao e ao final concatena-la ao vetor de dado
		
		TODO: Dividir o arquivo em vetores de 255 posicoes
 
        Para m=8:
        Parâmetro 1 --> Default Primitive Polynomial=D^8 + D^4 + D^3 + D^2 + 1; Integer Representation=285.
        Parâmetro 2 --> n=2^m = 256
        n=200; k=120; n-k=80; t=40;
        
        RS para m=8:
        Com 5% de erro:
        0.1*255 = 255-k
        k=229 Bytes
        n-k=26 Bytes de redundância
        
        Com 10% de erro:
        0.2*255 = 255-k
        k=203 Bytes
        n-k=52 Bytes de redundância
        
        Com 15% de erro:
        0.3*255 = 255-k
        k=177 Bytes
        n-k=78 Bytes de redundância

 */

public class ManipularArquivoM8 {

	public static void main(String[] args) throws IOException, ReedSolomonException {

		String localAbsoluto = "Z:\\@desenvolvimento\\workspace\\Testes-com-RS-GF(2^16)\\Novo Documento de Texto.txt";
		ManipularArquivoM8.degradacaoCorretiva(localAbsoluto);
	}

	// Cria os arquivos codificado e redundancia. Corrompe original
	private static void degradacaoCorretiva(String localAbsoluto) throws IOException, ReedSolomonException {
		
        // Com 15% de erro: k=177 Bytes n-k=78 Bytes de redundância t=39
		int n = 255, k = 177, i;
		Path path = Paths.get(localAbsoluto);
		byte[] dado = Files.readAllBytes(path);
		int qtdSimbolos = dado.length;
		final int QTD_ITERACOES = qtdSimbolos/k + 1;
		
		for(int h = 1; h <= QTD_ITERACOES; h++) {
		//Dividir e armazenar o arquivo em vetores de 255 posicoes		
     
		GenericGF gf = new GenericGF(285,256, 1);
		int qtdSimbolosParidade = 78;
		int t = 39;

		// Transforma vetor de bytes em vetor de inteiros unsigned
		int[] arquivo = ManipularArquivoM8.byteSignedParaUnsigned(localAbsoluto, qtdSimbolosParidade);

		// Codificacao e vetor da redundancia
		ReedSolomonEncoder encoder = new ReedSolomonEncoder(gf);
		encoder.encode(arquivo, qtdSimbolosParidade);
		int[] vetorParidade = ManipularArquivoM8.salvaVetorRedundancia(arquivo, qtdSimbolosParidade);

		// Apaga redundancia
		ManipularArquivoM8.zerarRedundancia(arquivo, qtdSimbolosParidade);

		// Transformar de int[] para byte[]
		byte[] codificado = ManipularArquivoM8.byteUnsignedParaSigned(arquivo);

		// Guardar a redundancia em um arquivo sem converter de int[] para byte[]
		ManipularArquivoM8.gravarRedundancia(vetorParidade, localAbsoluto);

		// Cria vetor de bytes ja codificados pelo encoder e corrompido t posicoes
		ManipularArquivoM8.corrompeDado(codificado, t);

		// Gravar arquivo codificado e corrompido
		ManipularArquivoM8.gravaArquivoCodificado(codificado, localAbsoluto);

		// Transformar de byte[] para int[] guardar arquivo corrompido
		int[] voltaDeByte = ManipularArquivoM8.byteSignedParaUnsignedSemParidade(localAbsoluto);

		// Decodificacao
		ReedSolomonDecoder decoder = new ReedSolomonDecoder(gf);

		// Concatenando dado com redudancia
		ManipularArquivoM16.concatenaDadoComRedundancia(voltaDeByte, vetorParidade, qtdSimbolosParidade);
		decoder.decode(voltaDeByte, qtdSimbolosParidade);

		// gravo o arquivo decodificado com o mesmo tamanho do arquivo original, sem os
		// indices de paridade
		byte[] decodificado = ManipularArquivoM8.byteUnsignedParaSigned(voltaDeByte);		
		ManipularArquivoM8.gravaArquivoDecodificado(decodificado, localAbsoluto);
		
		}
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
		// System.out.println("Antes da codificacao: " + "\n" +
		// Arrays.toString(vetorDevolvido));
		System.out.println(
				"Quantidade de simbolos, sem os indices de paridade: " + (vetorDevolvido.length - qtdSimbolosParidade));
		return vetorDevolvido;
	}

	// Transformar os bytes de um arquivo lido de SIGNED [-128 a 127] em UNSIGNED [0
	// a 255] e escrever em um vetor de inteiros
	// Usar esse metodos para as demais conversoes que nao a primeira
	private static int[] byteSignedParaUnsignedSemParidade(String localAbsoluto) throws IOException {

		String[] diretorioArquivoExtensao = recuperoDiretorioNomeExtensao(localAbsoluto);
		String diretorio = diretorioArquivoExtensao[0];
		String arquivo = diretorioArquivoExtensao[1];
		String extensao = diretorioArquivoExtensao[2];
		String arquivoCompleto = diretorio + arquivo + "_" + "Codificado" + "." + extensao;

		Path path = Paths.get(arquivoCompleto);
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
		// System.out
		// .println("\n" + "Volta do vetor de dados de byte para int: " + "\n" +
		// Arrays.toString(novoVetorBytes));
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

	// Recupera nome e local de um arquivo a partir do local absoluto
	// Entrada: string representando o local absoluto
	// Retorno: Um vetor de strings, cuja posicao [0] eh o local onde este arquivo
	// esta gravado no disco
	// [1] eh o nome do arquivo e a posicao [2] eh a extensao do arquivo sem ponto
	// Recuperar local independente de quantas pastas existam
	private static String[] recuperoDiretorioNomeExtensao(String localAbsoluto) {
		File f = new File(localAbsoluto);

		// Recupera nome e extensao do arquivo
		String arquivoComExtensao = "";
		int in = f.getAbsolutePath().lastIndexOf("\\");
		if (in > -1) {
			arquivoComExtensao = f.getAbsolutePath().substring(in + 1);
		}
		// Recupera o diretorio onde o arquivo esta armazenado
		String diretorio = localAbsoluto.replace(arquivoComExtensao, "");

		// Recupera nome e extensao do arquivo
		String[] separaNomeDaExtensao = arquivoComExtensao.split("[.]");
		String arquivo = separaNomeDaExtensao[0];
		String extensao = separaNomeDaExtensao[1];

		// Armazena diretorio, nome do arquivo e extensao do arquivo em um array de
		// strings
		String[] diretorioArquivoExtensao = new String[3];
		diretorioArquivoExtensao[0] = diretorio;
		diretorioArquivoExtensao[1] = arquivo;
		diretorioArquivoExtensao[2] = extensao;

		return diretorioArquivoExtensao;
	}

	// Gravar codificado
	private static File gravaArquivoCodificado(byte[] arquivoCodificado, String localAbsoluto)
			throws IOException {

		String[] diretorioArquivoExtensao = recuperoDiretorioNomeExtensao(localAbsoluto);
		String diretorio = diretorioArquivoExtensao[0];
		String arquivo = diretorioArquivoExtensao[1];
		String extensao = diretorioArquivoExtensao[2];
		String arquivoCompleto = diretorio + arquivo + "_" + "Codificado" + "." + extensao;

		// Grava o arquivo com o nome fornecido e a extensao lida
		File newFile = new File(arquivoCompleto);
		FileOutputStream stream = new FileOutputStream(newFile);
		stream.write(arquivoCodificado);
		stream.close();
		return newFile;
	}

	// Gravar decodificado
	private static File gravaArquivoDecodificado(byte[] arquivoDecodificado, String localAbsoluto)
			throws IOException {
		
		String[] diretorioArquivoExtensao = recuperoDiretorioNomeExtensao(localAbsoluto);
		String diretorio = diretorioArquivoExtensao[0];
		String arquivo = diretorioArquivoExtensao[1];
		String extensao = diretorioArquivoExtensao[2];
		String arquivoCompleto = diretorio + arquivo + "_" + "Decodificado" + "." + extensao;

		// Grava o arquivo com o nome fornecido e a extensao lida
		File newFile = new File(arquivoCompleto);
		FileOutputStream stream = new FileOutputStream(newFile);
		stream.write(arquivoDecodificado);
		stream.close();
		return newFile;
	}

	// Gravar a redundancia em um arquivo
	private static void gravarRedundancia(int[] vetorRedundancia, String localAbsoluto) throws IOException {

		String[] diretorioArquivoExtensao = recuperoDiretorioNomeExtensao(localAbsoluto);
		String diretorio = diretorioArquivoExtensao[0];
		String arquivo = diretorioArquivoExtensao[1];
		String extensao = diretorioArquivoExtensao[2];	
		String arquivoCompleto = diretorio + arquivo + "_" + "Redundancia" + "." + extensao;

		// Grava o arquivo
		OutputStream os = new FileOutputStream(arquivoCompleto);
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
		// System.out.println("\n" + "Redundancia: " + "\n" +
		// Arrays.toString(redundanciaInt));
		System.out.println("Quantidade de simbolos de redundancia: " + redundanciaInt.length);
		return redundanciaInt;
	}

	private static int[] zerarRedundancia(int[] dados, int tamanhoParidade) {
		for (int x = dados.length - tamanhoParidade; x < dados.length; x++) {
			dados[x] = 0;
		}
		// System.out.println("\n" + "Novo vetor de dados com a redundancia zerada: " +
		// "\n" + Arrays.toString(dados));
		System.out.println("Quantidade de simbolos: " + dados.length);
		return dados;
	}

	// Corromper vetor de dados t posicoes
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
		// System.out.println("\n" + "Corrupcao: " + "\n" + Arrays.toString(dados));
		System.out.println("Quantidade de simbolos: " + dados.length);
	}

	// Concatena dado com redundancia
	public static void concatenaDadoComRedundancia(int[] dados, int[] vetorRedundancia, int tamanhoParidade) {
		// Concatenando dado com redudancia
		int k = 0;
		for (int x = dados.length - tamanhoParidade; x < dados.length; x++) {
			dados[x] = vetorRedundancia[k];
			k++;
		}
		// System.out.println("\n" + "Vetor de dados concatenado com redundancia: " +
		// "\n" + Arrays.toString(dados));
		System.out.println("Quantidade de simbolos: " + dados.length);
	}
	
	public static int[][] byteParaBitUnsigned(String fileName)throws IOException{
		Path path = Paths.get(fileName);
		byte[] data = Files.readAllBytes(path);
		int size = data.length;
		System.out.println("Tamanho do arquivo: " + size + " bytes");
		
		int r[] = new int[8];
		int q[] = new int[8];
		int bin[] = new int[8];
		int j, l, num_input;
		
		int arrayBits[][] = new int[size][8];
		int m = 0;

		for (int i = 0; i < size; i++) {
			// Se o valor em inteiro do byte for maior ou igual a zero, nao precisa somar 256
			if (data[i] >= 0) {
				// Mostra em Unsigned Bytes
				// System.out.println("\nByte: " + data[i]); 
				num_input = data[i];
				// preenche o vetor com valores binarios
				for (j = 0; j <= 7; j++) { 
					q[j] = num_input / 2;
					r[j] = num_input % 2;
					num_input = q[j];
				}
				// organiza o vetor de tras para frente (MSB)
				int k = 7;
				for (j = 0; j <= 7; j++) {
					bin[k] = r[j];
					k--;
				}
				// imprime o valor binario do byte em MSB
				System.out.println("Bits de Dados:      ");
				for (l = 0; l <= 7; l++) {
					System.out.print(bin[l]);					
				}
				System.out.println("\n");
				for (l = 0; l <= 7; l++) {
					arrayBits[i][l] = bin[l];
					
				}
				
			} else {
				num_input = data[i];
				num_input = num_input + 256;
				// System.out.println("\nByte: " + num_input); // Mostra em Unsigned Bytes
				// preenche o vetor com valores binarios
				for (j = 0; j <= 7; j++) { 
					q[j] = num_input / 2;
					r[j] = num_input % 2;
					num_input = q[j];
				}
				// organiza o vetor de tras para frente
				int k = 7;
				for (j = 0; j <= 7; j++) {
					bin[k] = r[j];
					k--;
				}
				System.out.println("Bits de Dados:      ");
				for (l = 0; l <= 7; l++) {
					System.out.print(bin[l]);
				}
				System.out.println("\n");
				for (l = 0; l <= 7; l++) {
					arrayBits[i][l] = bin[l];
					
				}
			}
		}
		
		return arrayBits;
	}
	/*
	 * // Gravar os bytes de um arquivo em um vetor de bytes private static byte[]
	 * lerBytesArquivo(String fileName) throws IOException { Path path =
	 * Paths.get(fileName); byte[] data = Files.readAllBytes(path); return data; }
	 * 
	 * 
	 * // Gravar um arquivo private static File gravaArquivo(byte[]
	 * convertidoDeIntArray, String localAbsolutoArquivo, String nomeDoArquivo)
	 * throws IOException { File f = new File(localAbsolutoArquivo); // Recupera
	 * nome e extensao do arquivo String extensao = ""; int in =
	 * f.getAbsolutePath().lastIndexOf("\\"); if (in > -1) { extensao =
	 * f.getAbsolutePath().substring(in + 1); } // Grava o arquivo com o nome
	 * fornecido e a extensao lida File newFile = new File(nomeDoArquivo + "." +
	 * extensao); FileOutputStream stream = new FileOutputStream(newFile);
	 * stream.write(convertidoDeIntArray); stream.close(); return newFile; }
	 * 
	 * // Arrays sao imutaveis, uma vez criados o seu tamanho NAO pode ser alterado,
	 * // portanto, o metodo abaixo nao funciona
	 * 
	 * public static byte[] apagaParidade(byte[] dados, int qtdSimbolosParidade) {
	 * byte[] dadoSemParidade = new byte[dados.length - qtdSimbolosParidade]; byte
	 * temp = 0; int ndP = 0; for (int x = 0; x < dados.length; x++) { temp =
	 * dados[x]; dadoSemParidade[ndP] = temp; ndP++; } return dadoSemParidade; }
	 */

}
