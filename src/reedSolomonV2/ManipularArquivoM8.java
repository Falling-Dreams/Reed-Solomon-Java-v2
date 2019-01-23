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
			int n = 255, k = 177, t = 39, qtdSimbolosCorrecao = 78, i;
			int srcPos = 0, srcPosCodi = 0, srcPosDecodi = 0, destPosCorr = 0, srcPosDec = 0;
			Path path = Paths.get(localAbsoluto);
			byte[] dado = Files.readAllBytes(path);
			int qtdSimbolos = dado.length;
			int qtdIteracoesRS = qtdSimbolos/k;
			int restoVetorRS = qtdSimbolos % k;
			
			int[] arquivo = ManipularArquivoM8.byteSignedParaUnsigned(localAbsoluto, qtdSimbolosCorrecao); // Transforma vetor de bytes em vetor de inteiros unsigned
			int[] vetorRS8Codificado = new int[qtdSimbolos];
			int[] vetorRS8Decodificado = new int[qtdSimbolos];
			int[] vetorSimbolosCorrecao = new int[(qtdIteracoesRS * qtdSimbolosCorrecao) + (restoVetorRS * n/k)];		
			int[] voltaDeCodificado = ManipularArquivoM8.byteSignedParaUnsignedSemParidade(localAbsoluto);
			
			GenericGF gf = new GenericGF(285, 256, 1);			
			
			/*
			// Rotina de codificacao
			// A ultima iteracao e tratada separadamente, para os casos em que ha resto da divisao
			for (int h = 0; h < qtdIteracoesRS; h++) {
				
				// Dividir e armazenar o vetor do arquivo em vetores de 255 posicoes
				// vetor de n simbolos de cada iteracao - OK FUNCIONANDO
				int[] vetorRS8 = new int[255];
				System.arraycopy(arquivo, srcPos, vetorRS8, 0, k + 1);
				srcPos += 177;					

				// Codificacao ocorre a cada 255 simbolos, 177 simbolos de informacao do arquivo original
				// e 78 simbolos de correcao
				ReedSolomonEncoder encoder = new ReedSolomonEncoder(gf);
				encoder.encode(vetorRS8, qtdSimbolosCorrecao);
				
				// Vetor de simbolos de correcao de cada iteracao - OK FUNCIONANDO
				int[] vetorCorrecaoRS8 = new int[78];
				System.arraycopy(vetorRS8, k, vetorCorrecaoRS8, 0, (n-k));				
				
				// os k simbolos codificados pelo RS8 a cada iteracao sao armazenados em um unico vetor - FUNCIONANDO
				System.arraycopy(vetorRS8, 0, vetorRS8Codificado, srcPosCodi, k + 1);
				srcPosCodi += 177;				
				
				// os n-k simbolos de correcao codificados pelo RS8 sao armazenados em um unico vetor - FUNCIONANDO			
				System.arraycopy(vetorCorrecaoRS8, 0, vetorSimbolosCorrecao, destPosCorr, qtdSimbolosCorrecao);
				destPosCorr += 78;			
				
				// Tratamento para o caso em que restoVetorRS > 0 - PROBLEMA no vetor de redundancia quando ha resto
				if(restoVetorRS > 0) {
					System.arraycopy(arquivo, (qtdSimbolos - restoVetorRS), vetorRS8, 0, restoVetorRS);
					System.arraycopy(vetorRS8, 0, vetorRS8Codificado, srcPos, restoVetorRS);
					System.arraycopy(vetorRS8, (restoVetorRS), vetorCorrecaoRS8, 0, (restoVetorRS * n/k)); //redundacia iteracao
					System.arraycopy(vetorCorrecaoRS8, 0, vetorSimbolosCorrecao, destPosCorr, (restoVetorRS * n/k)); //vetor unico da redundacia
					//System.out.println("Vetor quando ha resto: " + Arrays.toString(vetorCorrecaoRS8) + "\n");
					//System.out.println(vetorCorrecaoRS8.length);
															
				}
				//System.out.println(Arrays.toString(vetorCorrecaoRS8) + "\n");
				
			} 
			//System.out.println(vetorSimbolosCorrecao.length);
			//System.out.println(Arrays.toString(vetorRS8Codificado));
			//System.out.println(Arrays.toString(vetorSimbolosCorrecao) + "\n");
			//System.out.println(vetorSimbolosCorrecao.length);
			
											
				// Cria vetor de bytes ja codificados pelo encoder e corrompido t posicoes
				byte[] vetorCodificadoSigned = ManipularArquivoM8.byteUnsignedParaSigned(vetorRS8Codificado);
				ManipularArquivoM8.corrompeDado(vetorCodificadoSigned, t);
				// Gravar arquivo codificado e corrompido
				ManipularArquivoM8.gravaArquivoCodificado(vetorCodificadoSigned, localAbsoluto);
				ManipularArquivoM8.gravarRedundancia(vetorSimbolosCorrecao, localAbsoluto);
				//System.out.println(Arrays.toString(voltaDeCodificado)); */
				
				
				
				// Rotina de decodificacao
				for (int h = 0; h < qtdIteracoesRS; h++) {
					ReedSolomonDecoder decoder = new ReedSolomonDecoder(gf);
					
					// Quebrar vetor unico de simbolos codificados em vetores de 255
					int[] vetorRS8D = new int[255];
					System.arraycopy(voltaDeCodificado, srcPosDec, vetorRS8D, 0, k + 1);
					srcPosDec += 177;			
					
					// Gravar o que foi decodificado em um unico vetor
					System.arraycopy(vetorRS8D, 0, vetorRS8Decodificado, srcPosDecodi, k + 1);
					srcPosDecodi += 177;
					
					if(restoVetorRS > 0) {
						System.arraycopy(voltaDeCodificado, (qtdSimbolos - restoVetorRS), vetorRS8D, 0, restoVetorRS);
						System.arraycopy(vetorRS8D, 0, vetorRS8Decodificado, srcPosDecodi, restoVetorRS);										
					}
					// Concatenando dado com redudancia e efetua Decodificacao
					ManipularArquivoM8.concatenaDadoComRedundancia(vetorRS8D, vetorSimbolosCorrecao, qtdSimbolosCorrecao);
					//decoder.decode(vetorRS8D, qtdSimbolosCorrecao);	
				} 
				
				System.out.println(Arrays.toString(vetorRS8Decodificado));
				System.out.println(vetorRS8Decodificado.length); 
				
				
								
				//byte[] decodificado = ManipularArquivoM8.byteUnsignedParaSigned(voltaDeCodificado);
				//ManipularArquivoM8.gravaArquivoDecodificado(decodificado, localAbsoluto);
				
				/*
				// Transformar de byte[] para int[] guardar arquivo corrompido
				int[] voltaDeByte = ManipularArquivoM8.byteSignedParaUnsignedSemParidade(localAbsoluto);
				
				// Decodificacao
				ReedSolomonDecoder decoder = new ReedSolomonDecoder(gf);
				
				// Concatenando dado com redudancia
				ManipularArquivoM16.concatenaDadoComRedundancia(voltaDeByte, vetorParidade, qtdSimbolosCorrecao);
				decoder.decode(voltaDeByte, qtdSimbolosCorrecao);
				
				// gravo o arquivo decodificado com o mesmo tamanho do arquivo original, sem os
				// indices de paridade
				byte[] decodificado = ManipularArquivoM8.byteUnsignedParaSigned(voltaDeByte);
				ManipularArquivoM8.gravaArquivoDecodificado(decodificado, localAbsoluto);*/
				
			
			
			//Transformo de int unsigned para signed e gravo o arquivo codificado
			//byte[] vetorCodificadoSigned = ManipularArquivoM8.byteUnsignedParaSigned(vetorRS8Codificado);
						
			//System.out.println(Arrays.equals(dado, vetorCodificadoSigned));
			//ManipularArquivoM8.gravaArquivoCodificado(vetorCodificadoSigned, localAbsoluto);
			//ManipularArquivoM8.gravarRedundancia(vetorSimbolosCorrecao, localAbsoluto);
			//System.out.println("\n" + Arrays.toString(vetorSimbolosCorrecao));
			
			//System.out.println("\n" + Arrays.toString(vetorRS8Codificado));
			
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
			// System.out.println("Antes da codificacao: " + "\n" +
			// Arrays.toString(vetorDevolvido));
			System.out.println(
					"Quantidade de simbolos do arquivo lido: " + (vetorDevolvido.length));
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
			System.out.println("Quantidade de simbolos (unsigned p/ signed): " + novoVetorBytes.length);
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
			System.out.println("Quantidade de simbolos (zerar redundancia): " + dados.length);
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
			System.out.println("Quantidade de simbolos (corromper dado): " + dados.length);
		}

		// Concatena dado com redundancia
		public static void concatenaDadoComRedundancia(int[] dados, int[] vetorRedundancia, int tamanhoParidade) {
			// Concatenando dado com redudancia
			int k = 0, destPos = 0;
			for (int x = dados.length - tamanhoParidade; x < dados.length; x++) {
				System.arraycopy(vetorRedundancia, destPos, dados, (dados.length - tamanhoParidade), tamanhoParidade);
				destPos += 78;
				//dados[x] = vetorRedundancia[k];
				//k++;
			}
			System.out.println("\n" + "Vetor de dados concatenado com redundancia: " +
			"\n" + Arrays.toString(dados));
			System.out.println("Quantidade de simbolos: " + dados.length);
		}
}