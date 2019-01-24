package reedSolomonV2;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Arrays;
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
		int n = 255, k = 177, t = 39, qtdSimbolosCorrecao = 78, i, destPos = 0;
		int srcPos = 0, srcPosCodi = 0, srcPosDecodi = 0, destPosCorr = 0, srcPosDec = 0;
		Path path = Paths.get(localAbsoluto);
		byte[] dado = Files.readAllBytes(path);
		int qtdSimbolos = dado.length;
		int qtdIteracoesRS = qtdSimbolos / k;
		int restoVetorRS = qtdSimbolos % k;

		int[] arquivo = ManipularArquivoM8.byteSignedParaUnsigned(localAbsoluto); 																									
		int[] vetorRS8Codificado = new int[qtdSimbolos];
		int[] vetorRS8Decodificado = new int[qtdSimbolos];
		int[] vetorSimbolosCorrecao = new int[((qtdIteracoesRS + 1) * qtdSimbolosCorrecao)];
		// Descomentar apenas depois da codificacao e gravacao do documento codificado e redundancia
		vetorSimbolosCorrecao = recuperaRedundanciaGravada(localAbsoluto);		
		int[] voltaCodificado = ManipularArquivoM8.byteSignedParaUnsignedSemParidade(localAbsoluto);
		
		int[] vetorRS8C = new int[255];
		int[] vetorCorrecaoRS8 = new int[78];
		int[] vetorRS8D = new int[255];
		GenericGF gf = new GenericGF(285, 256, 1);
		ReedSolomonEncoder encoder = new ReedSolomonEncoder(gf);
		ReedSolomonDecoder decoder = new ReedSolomonDecoder(gf);
		System.out.println("Inteiros unsigned ORIGINAIS do arquivo: " + "\n" + Arrays.toString(arquivo) + "\n");
		
		/*
		
		//CODIFICACAO CORRIGIDA 24.01 - FUNCIONANDO
		// Rotina de codificacao
		// A ultima iteracao e tratada separadamente, para os casos em que ha resto da divisao
		for (int h = 0; h < qtdIteracoesRS; h++) {

			// Dividir e armazenar o vetor do arquivo em vetores de 255 posicoes
			// vetor de n simbolos de cada iteracao - OK FUNCIONANDO OK			
			System.arraycopy(arquivo, srcPos, vetorRS8C, 0, k);
			srcPos += 177;
			//System.out.println(Arrays.toString(vetorRS8C));
			
			// Codificacao ocorre a cada 255 simbolos, 177 simbolos de informacao do arquivo
			// original
			// e 78 simbolos de correcao			
			encoder.encode(vetorRS8C, qtdSimbolosCorrecao);

			// os k simbolos codificados pelo RS8 a cada iteracao sao armazenados em um
			// unico vetor - FUNCIONANDO
			System.arraycopy(vetorRS8C, 0, vetorRS8Codificado, srcPosCodi, k);
			srcPosCodi += 177;
			//System.out.println(Arrays.toString(vetorRS8Codificado));
			//System.out.println(vetorRS8Codificado.length);

			// os n-k simbolos de correcao codificados pelo RS8 sao armazenados em um unico
			// vetor - FUNCIONANDO OK OK
			System.arraycopy(vetorRS8C, k, vetorSimbolosCorrecao, destPosCorr, qtdSimbolosCorrecao);
			destPosCorr += 78;
			//System.out.println(Arrays.toString(vetorSimbolosCorrecao));
			//System.out.println(vetorSimbolosCorrecao.length);
			
		}		
		// Tratamento para o caso em que restoVetorRS > 0 - FUNCIONANDO
		// Crio um vetor adicional apenas quando a resto na divisao 1774/177
		if (restoVetorRS > 0) {
			//int[] vetorRS8CResto = new int[restoVetorRS + ((restoVetorRS * n / k))];
			int[] vetorRS8CResto = new int[255];
			System.arraycopy(arquivo, (qtdSimbolos - restoVetorRS), vetorRS8CResto, 0, restoVetorRS); //copia dos k simbolos do resto OK OK
			//System.out.println(((n - (restoVetorRS * n / k))));
			//System.out.println("Vetor resto ANTES do acescimo dos simbolos de correcao: " + Arrays.toString(vetorRS8CResto));
			encoder.encode(vetorRS8CResto, qtdSimbolosCorrecao);
			//System.out.println("Vetor resto DEPOIS do acescimo dos simbolos de correcao: " + Arrays.toString(vetorRS8CResto));
			System.arraycopy(vetorRS8CResto, 0, vetorRS8Codificado, (qtdSimbolos - restoVetorRS), restoVetorRS); //vetor unico - copiando restoVetorRS OK OK
			System.arraycopy(vetorRS8CResto, k, vetorSimbolosCorrecao, ((qtdIteracoesRS) * qtdSimbolosCorrecao), qtdSimbolosCorrecao); // copia do resto dos simbolos correcao OK OK			
			//System.out.println(Arrays.toString(vetorRS8Codificado));
			//System.out.println(Arrays.toString(vetorSimbolosCorrecao));
		}
		
		if (Arrays.equals(arquivo, vetorRS8Codificado) != true) {
	        throw new ReedSolomonException("Erro na codificacao, vetor codificado nao eh igual ao vetor do arquivo (em inteiro unsigned)");
	      }

		// Cria vetor de bytes ja codificados pelo encoder e corrompido t posicoes
		byte[] vetorCodificadoSigned = ManipularArquivoM8.byteUnsignedParaSigned(vetorRS8Codificado);
		ManipularArquivoM8.corrompeDado(vetorCodificadoSigned, t);
		
		// Gravar arquivo codificado e corrompido
		ManipularArquivoM8.gravaArquivoCodificado(vetorCodificadoSigned, localAbsoluto);
		ManipularArquivoM8.gravarRedundancia(vetorSimbolosCorrecao, localAbsoluto); */
		
		//System.out.println(Arrays.toString(voltaCodificado));
		//System.out.println(Arrays.toString(vetorSimbolosCorrecao));
				
		 
		
		 // Rotina de decodificacao 
		for (int h = 0; h < qtdIteracoesRS; h++) {		 
		  
		 // Quebrar vetor unico de simbolos codificados em vetores de 255
		 // Copia 177 simbolos de informacao para o vetorRS8D
		 System.arraycopy(voltaCodificado, srcPosDec, vetorRS8D, 0, k); 
		 srcPosDec += 177;
		 //System.out.println(Arrays.toString(vetorRS8D));		
		 		 
		 // Concatenando dado com redudancia e efetua Decodificacao - ERRO NA CONCATENACAO
		 // Rotina de codificacao e decodificacao (ate antes da concatenacao) OK
		System.arraycopy(vetorSimbolosCorrecao, destPos, vetorRS8D, (vetorRS8D.length - qtdSimbolosCorrecao), qtdSimbolosCorrecao);
		destPos += 78; 
		//System.out.println(Arrays.toString(vetorRS8D));		 
				
		 //Decodificacao
		 decoder.decode(vetorRS8D, qtdSimbolosCorrecao); 
		 //System.out.println("Vetor iteracao decodificado: " + Arrays.toString(vetorRS8D));
		 
		// Gravar o que foi decodificado em um unico vetor para gravar o arquivo decodificado - OK 
		System.arraycopy(vetorRS8D, 0, vetorRS8Decodificado, srcPosDecodi, k);
		srcPosDecodi += 177;
		//System.out.println(Arrays.toString(vetorRS8Decodificado));
		 
		 }
		
		if(restoVetorRS > 0) {
			//int[] vetorRS8DResto = new int[restoVetorRS + (restoVetorRS * n / k)];
			int[] vetorRS8DResto = new int[255];
			System.arraycopy(voltaCodificado, (qtdSimbolos - restoVetorRS), vetorRS8DResto, 0, restoVetorRS); //copia dos k simbolos do resto OK OK
			System.arraycopy(vetorSimbolosCorrecao, ((qtdIteracoesRS) * qtdSimbolosCorrecao), vetorRS8DResto, k, qtdSimbolosCorrecao); //concatena OK OK
			//System.out.println("Vetor resto DEPOIS da concatenacao dos simbolos de correcao: " + Arrays.toString(vetorRS8DResto));
			decoder.decode(vetorRS8DResto, qtdSimbolosCorrecao);
			//System.out.println("Vetor resto DEPOIS da correcao: " + Arrays.toString(vetorRS8DResto));
			System.arraycopy(vetorRS8DResto, 0, vetorRS8Decodificado, (qtdSimbolos - restoVetorRS), restoVetorRS); //vetor unico - copiando restoVetorRS OK OK
			System.arraycopy(vetorRS8DResto, k, vetorSimbolosCorrecao, ((qtdIteracoesRS) * qtdSimbolosCorrecao), qtdSimbolosCorrecao); // copia do resto dos simbolos correcao OK OK			
			//System.out.println("\n" + Arrays.toString(vetorRS8Decodificado));
			//System.out.println(Arrays.toString(vetorSimbolosCorrecao));
		}
		//System.out.println("Vetor decodificado: " + Arrays.toString(vetorRS8Decodificado));
		//System.out.println(Arrays.equals(arquivo, vetorRS8Decodificado));
		
		if (Arrays.equals(arquivo, vetorRS8Decodificado) != true) {
	        throw new ReedSolomonException("Erro na decodificacao, vetor decodificado nao eh igual ao vetor do arquivo (em inteiro unsigned)");
	      }
				
		 
		// Gravacao em disco do arquivo decodificado e corrigido
		byte[] decodificado = ManipularArquivoM8.byteUnsignedParaSigned(vetorRS8Decodificado);
		//System.out.println(Arrays.toString(decodificado));
		ManipularArquivoM8.gravaArquivoDecodificado(decodificado, localAbsoluto); 
		
		
		
	}

	// Transformar os bytes de um arquivo lido de SIGNED [-128 a 127] em UNSIGNED [0
	// a 255] e escrever em um vetor de inteiros
	// e acrescentar n-k posicoes no vetor devolvido, pois, o bloco n do RS e
	// composto por k(informacao) e n-k(paridade)
	// Usar APENAS na primeira leitura do arquivo para vetor de bytes
	private static int[] byteSignedParaUnsigned(String fileName) throws IOException {
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

	// Transformar os bytes de um arquivo lido de SIGNED [-128 a 127] em UNSIGNED [0
	// a 255] e escrever em um vetor de inteiros
	// Usar esse metodos para as demais conversoes que nao a primeira
	static int[] byteSignedParaUnsignedSemParidade(String localAbsoluto) throws IOException {

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
	private static File gravaArquivoCodificado(byte[] arquivoCodificado, String localAbsoluto) throws IOException {

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
	private static File gravaArquivoDecodificado(byte[] arquivoDecodificado, String localAbsoluto) throws IOException {

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

	// Corromper vetor de dados t posicoes, trocar o for por system.arraycopy
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
	}
	
	private static int[] recuperaRedundanciaGravada(String localAbsoluto) throws IOException {

		String[] diretorioArquivoExtensao = recuperoDiretorioNomeExtensao(localAbsoluto);
		String diretorio = diretorioArquivoExtensao[0];
		String arquivo = diretorioArquivoExtensao[1];
		String extensao = diretorioArquivoExtensao[2];
		String arquivoCompleto = diretorio + arquivo + "_" + "Redundancia" + "." + extensao;

		Path path = Paths.get(arquivoCompleto);
		byte[] bytesRedundancia = Files.readAllBytes(path);
		int tamanhoVetorInt = bytesRedundancia.length;
		int valorEmIntDoByte = 0;
		int[] vetorDevolvido = new int[tamanhoVetorInt];

		// Bytes em java representam inteiros em complemento de dois, a soma e feita nos
		// bytes com valores negativos
		// Pois, o RS opera com numeros inteiros e positivos
		for (int i = 0; i < tamanhoVetorInt; i++) {
			valorEmIntDoByte = bytesRedundancia[i];
			if (valorEmIntDoByte < 0) {
				valorEmIntDoByte = valorEmIntDoByte + 256;
			}
			vetorDevolvido[i] = valorEmIntDoByte;
		}
		return vetorDevolvido;
	}
}