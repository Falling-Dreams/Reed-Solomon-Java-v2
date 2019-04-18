package reedSolomonV2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import javax.smartcardio.CardException;
import reedSolomonV2.ManipularArquivoM8;

/*
	Para m=8:
	Parametro 1 --> Default Primitive Polynomial=D^8 + D^4 + D^3 + D^2 + 1; Integer Representation=285.
	Parametro 2 --> n=2^m = 256
	n=200; k=120; n-k=80; t=40;
	RS para m=8:
	Com 5% de erro:
	0.1*255 = 255-k
	k=229 Bytes
	n-k=26 Bytes de redundancia
	Com 10% de erro:
	0.2*255 = 255-k
	k=203 Bytes
	n-k=52 Bytes de redundancia
	Com 15% de erro:
	0.3*255 = 255-k
	k=177 Bytes
	n-k=78 Bytes de redundancia
	
	TODO: Corromper t simbolos aleatorios - OK
	
	TODO: Padronizar nomenclatura de metodos e classes
	
	TODO: Documentar classes
	
	TODO: Alterar metodo de exclusão, para excluir um arquivo mesmo em uso
	
	TODO: Disponibilizar diferentes porcentagens de degradacao (5%, 10%, 20%)
*/

public class DegradacaoCorretiva {

	public static void main(String[] args)
			throws NoSuchAlgorithmException, IOException, ReedSolomonException, CardException {
		// Com 15% de erro: k=177 Bytes; n-k=78 Bytes de redundância; capacidade de
		// correcao t=39
		DegradacaoCorretiva degrada = new DegradacaoCorretiva();

		String localAbsoluto = "Z:\\@Projeto-Degradacao-Corretiva\\Testes-com-RS-GF(2^16)\\PMBOK 5ª Edição [Português][2013].pdf";
		//String localAbsolutoCodi = "Z:\\@Projeto-Degradacao-Corretiva\\Testes-com-RS-GF(2^16)\\PMBOK 5ª Edição [Português][2013]_Codificado.pdf";
		//String localAbsolutoCorrecao = "Z:\\@Projeto-Degradacao-Corretiva\\Testes-com-RS-GF(2^16)\\PMBOK 5ª Edição [Português][2013]_Correcao.pdf";
		//String localAbsolutoHash = "Z:\\@Projeto-Degradacao-Corretiva\\Testes-com-RS-GF(2^16)\\PMBOK 5ª Edição [Português][2013]_Hash.pdf";

		// Codificacao
		degrada.codificacao(localAbsoluto);

		// Guarda a hora inicial da execucacao
		long startTime = System.currentTimeMillis();

		// Decodificacao
		//degrada.decodificacao(localAbsolutoCodi, localAbsolutoCorrecao,
		//localAbsolutoHash);

		// Guarda a hora final da execucao
		long endTime = System.currentTimeMillis();

		// Calcula o tempo total de execucao em segundos
		long tempoTotal = ((endTime - startTime) / 1000);
		System.out.println("\n" + "Tempo total de execucao: " + tempoTotal + " segundos");

	}

	// Metodo responsavel pela codificacao do RS m = 8, com 15% de correcao
	protected void codificacao(String localAbsoluto)
			throws IOException, ReedSolomonException, CardException, NoSuchAlgorithmException {

		// Com 15% de erro: k=177 Bytes; n-k=78 Bytes de redundância; quantidade maxima
		// de correcao de t=39 simbolos

		// Instancia a classe que manipula o arquivo para degradacao
		// ManipularArquivoM8 manipula = new ManipularArquivoM8();

		// Instancia a classe que manipula o NFC
		ManipulaNFC nfc = new ManipulaNFC();

		// Instancia classe que gera o hash(sha256)
		SHA256 sha = new SHA256();

		// A codificacao acontece enquanto existir um cartao presente
		if (nfc.cartaoOuTerminalAusentes() == true) {
			System.out.println("Cartão NFC e terminais estão indisponíveis, a aplicaçãos será encerrada!" + "\n");
			System.exit(0);
		} else {
			System.out.println("Cartão NFC e terminais estão disponíveis!" + "\n");

			int k = 177, t = 39, qtdSimbolosCorrecao = 78;
			int incrementoVetorRS8C = 0, incrementoVetorUnico = 0, incrementoVetorCorrecao = 0;
			Path path = Paths.get(localAbsoluto);
			byte[] bytesLidosArquivo = Files.readAllBytes(path);
			int qtdSimbolos = bytesLidosArquivo.length;
			int qtdIteracoesRS = qtdSimbolos / k;
			int restoVetorRS = qtdSimbolos % k;
			int[] intUnsigned = ManipularArquivoM8.conversaoSignedUnsigned(localAbsoluto);
			int[] vetorRS8Codificado = new int[qtdSimbolos];
			int[] vetorSimbolosCorrecao = new int[((qtdIteracoesRS + 1) * qtdSimbolosCorrecao)];
			int[] vetorRS8C = new int[255];
			int sourcePosResto = qtdSimbolos - restoVetorRS;
			int destPosVetCorrecao = qtdIteracoesRS * qtdSimbolosCorrecao;

			GenericGF gf = new GenericGF(285, 256, 1);
			ReedSolomonEncoder encoder = new ReedSolomonEncoder(gf);

			System.out.println("Codificacao em andamento..." + "\n");

			/*
			 * Rotina de codificacao A ultima iteracao e tratada separadamente, quando ha
			 * resto na divisao qtdSimbolos/k A codificacao e efetuada a cada 255 simbolos
			 * do total de simbolos do arquivo O vetor original e divido em vetores de 255,
			 * ao final da codificacao sao gravados dois arquivos:
			 * Nome_do_arquivo_Codificado e Nome_do_arquivo_Redundancia O arquivo codificado
			 * e o arquivo original corrompido t posicoes, a redundancia sao os simbolos de
			 * correcao gerados a cada iteracao do for, de cada vetor do arquivo original
			 */

			for (int h = 0; h < qtdIteracoesRS; h++) {
				// A cada iteracao o vetorRS8C recebe 177 simbolos do arquivo original
				// As 78 posicoes restantes sao reservadas para geracao dos simbolos de correcao
				// desses 177 simbolos
				System.arraycopy(intUnsigned, incrementoVetorRS8C, vetorRS8C, 0, k);
				incrementoVetorRS8C += 177;

				// Depois de preenchido, sao gerados os simbolos de correcao do vetorRS8C a
				// partir da codificacao
				encoder.encode(vetorRS8C, qtdSimbolosCorrecao);

				// os k simbolos codificados pelo RS8 a cada iteracao sao armazenados em um
				// univo vetor
				System.arraycopy(vetorRS8C, 0, vetorRS8Codificado, incrementoVetorUnico, k);
				incrementoVetorUnico += 177;

				// os n-k simbolos de correcao codificados pelo RS8 sao armazenados em um unico
				// vetor
				System.arraycopy(vetorRS8C, k, vetorSimbolosCorrecao, incrementoVetorCorrecao, qtdSimbolosCorrecao);
				incrementoVetorCorrecao += 78;

			}
			// Tratamento para o caso em que restoVetorRS > 0 - FUNCIONANDO
			// Cria-se o vetor adicional apenas quando a resto na divisao 1774/177
			if (restoVetorRS > 0) {
				// Novo vetor apenas para o resto do vetor arquivo
				int[] vetorRS8CResto = new int[255];
				// Copia dos k simbolos de resto do vetor arquivo para o vetorRS8CResto
				System.arraycopy(intUnsigned, sourcePosResto, vetorRS8CResto, 0, restoVetorRS);
				// Codificacao dos simbolos restantes, a codificacao e feita
				// independentemente do resto, serao sempre codificados 177 simbolos (mesmo os
				// zeros)
				encoder.encode(vetorRS8CResto, qtdSimbolosCorrecao);
				// Copia dos k simbolos para o vetor unico vetorRS8Codificado
				System.arraycopy(vetorRS8CResto, 0, vetorRS8Codificado, sourcePosResto, restoVetorRS);
				// Copia dos simbolos de correcao do resto para o vetorSimbolosCorrecao
				System.arraycopy(vetorRS8CResto, k, vetorSimbolosCorrecao, destPosVetCorrecao, qtdSimbolosCorrecao);
			}

			if (Arrays.equals(intUnsigned, vetorRS8Codificado) != true) {
				throw new ReedSolomonException(
						"Erro na codificacao, vetor codificado nao e igual ao vetor do arquivo (em inteiro unsigned)");
			} else {
				System.out.println("Arquivo codificado com sucesso! " + "\n");
			}

			// Cria vetor de bytes ja codificados pelo encoder e corrompido t posicoes
			byte[] vetorCodificadoSigned = ManipularArquivoM8.conversaoUnsignedSigned(vetorRS8Codificado);
			ManipularArquivoM8.corrompeDado(vetorCodificadoSigned, t);
			
			// Cria vetor de bytes dos simbolos de correcao
			byte[] vetorCorrecaoSigned = ManipularArquivoM8.conversaoUnsignedSigned(vetorSimbolosCorrecao);

			// Gravar arquivo codificado e corrompido
			ManipularArquivoM8.gravarArquivo(vetorCodificadoSigned, localAbsoluto, "Codificado");
			ManipularArquivoM8.gravarArquivo(vetorCorrecaoSigned, localAbsoluto, "Correcao");

			// Gerar hash da UID do cartao que efetuou a codificacao, gravando o hash em um
			// arquivo de texto no mesmo local onde o arquivo se encontra
			byte[] uid = nfc.UID();
			byte[] hashUID = sha.sha256(uid);
			ManipularArquivoM8.gravarArquivo(hashUID, localAbsoluto, "Hash");
			System.out.println("Hash do cartão foi gravado ");
		}

	}

	// Metodo responsavel pela decodificacao do RS m = 8, com 15% de correcao
	protected void decodificacao(String localAbsolutoArquivoCodificado, String localAbsolutoCorrecao,
			String localAbsolutoHash)
			throws IOException, ReedSolomonException, CardException, NoSuchAlgorithmException {

		// Instancia a classe que manipula o arquivo para degradacao
		// ManipularArquivoM8 manipula = new ManipularArquivoM8();

		// Instancia a classe que manipula o NFC
		ManipulaNFC nfc = new ManipulaNFC();

		// Instancia classe que gera o hash(sha256)
		SHA256 sha = new SHA256();

		byte[] uid = nfc.UID();
		byte[] hashUID = sha.sha256(uid);

		if (nfc.cartaoOuTerminalAusentes() == true) {
			System.out.println("Cartão NFC e terminais estão indisponíveis, a aplicaçãos será encerrada!" + "\n");
			System.exit(0);
		} else {
			System.out.println("Aproxime da leitora o cartão que efetuou a codificação do arquivo" + "\n");

			if (sha.verificaChecksum(localAbsolutoHash, uid) != true) {
				System.out.println(
						"O cartão presente no terminal não é o mesmo que efetuou a codificação, a aplicação será encerrada"
								+ "\n");
				System.exit(0);
			} else {
				System.out.println(
						"O cartão presente no terminal é o mesmo que efetuou a codificação, a decodificação será iniciada"
								+ "\n");

				while (nfc.cartaoOuTerminalAusentes() != true) {

					// Com 15% de erro: k=177 Bytes n-k=78 Bytes de redundância t=39
					int n = 255, k = 177, t = 39, qtdSimbolosCorrecao = 78, destPos = 0;
					int srcPosDecodi = 0, incrementoVetorDecodificado = 0;
					Path path = Paths.get(localAbsolutoArquivoCodificado);
					byte[] dado = Files.readAllBytes(path);
					int qtdSimbolos = dado.length;
					int qtdIteracoesRS = qtdSimbolos / k;
					int restoVetorRS = qtdSimbolos % k;
					int[] vetorRS8D = new int[255];
					int destPosRS8D = vetorRS8D.length - qtdSimbolosCorrecao;
					int incrementoVetorResto = qtdSimbolos - restoVetorRS;
					int sourcePosRestoD = qtdIteracoesRS * qtdSimbolosCorrecao;
					int[] arquivo = ManipularArquivoM8.conversaoSignedUnsigned(localAbsolutoArquivoCodificado);
					int[] vetorRS8Decodificado = new int[qtdSimbolos];

					GenericGF gf = new GenericGF(285, 256, 1);
					ReedSolomonDecoder decoder = new ReedSolomonDecoder(gf);

					System.out.println("Decodificacao em andamento..." + "\n");

					// Recupera arquivo codificado e redundancia
					int[] vetorSimbolosCorrecao = new int[((qtdIteracoesRS + 1) * qtdSimbolosCorrecao)];
					vetorSimbolosCorrecao = ManipularArquivoM8.conversaoSignedUnsigned(localAbsolutoCorrecao);
					int[] voltaCodificado = ManipularArquivoM8
							.conversaoSignedUnsigned(localAbsolutoArquivoCodificado);

					// Rotina de decodificacao
					for (int h = 0; h < qtdIteracoesRS; h++) {

						// Quebrar vetor unico de simbolos codificados em vetores de 255
						// Copia 177 simbolos de informacao para o vetorRS8D
						System.arraycopy(voltaCodificado, incrementoVetorDecodificado, vetorRS8D, 0, k);
						incrementoVetorDecodificado += 177;

						// Concatenando dado com redudancia e efetua Decodificacao
						System.arraycopy(vetorSimbolosCorrecao, destPos, vetorRS8D, destPosRS8D, qtdSimbolosCorrecao);
						destPos += 78;

						// Decodificacao
						decoder.decode(vetorRS8D, qtdSimbolosCorrecao);

						// Guardar o que foi decodificado em um unico vetor para gravar o arquivo
						// decodificado
						System.arraycopy(vetorRS8D, 0, vetorRS8Decodificado, srcPosDecodi, k);
						srcPosDecodi += 177;
					}
					// Tratamento quando ha resto na divisao qtdSimbolosArquivo / k
					if (restoVetorRS > 0) {
						// Vetor para tratamento do resto
						int[] vetorRS8DResto = new int[255];
						// Copia k simbolos de resto para vetorRS8DResto
						System.arraycopy(voltaCodificado, incrementoVetorResto, vetorRS8DResto, 0, restoVetorRS);
						// Copia n-k simbolos de correcao para vetorRS8DResto
						System.arraycopy(vetorSimbolosCorrecao, sourcePosRestoD, vetorRS8DResto, k,
								qtdSimbolosCorrecao);
						// Decodificacao do resto
						decoder.decode(vetorRS8DResto, qtdSimbolosCorrecao);
						// Copia dos k simbolos de resto para vetor unico
						System.arraycopy(vetorRS8DResto, 0, vetorRS8Decodificado, incrementoVetorResto, restoVetorRS);
						// Copia dos n-k simbolos de correcao do resto para vetor unico
						System.arraycopy(vetorRS8DResto, k, vetorSimbolosCorrecao, sourcePosRestoD,
								qtdSimbolosCorrecao);
					}

					// if (Arrays.equals(arquivo, vetorRS8Decodificado) != true) {
					// throw new ReedSolomonException(
					// "Erro na decodificacao, vetor decodificado nao e igual ao vetor do arquivo
					// (em inteiro unsigned)");
					// } else {
					// System.out.println("Arquivo decodificado com sucesso! ");
					// }

					// Gravacao em disco do arquivo decodificado e corrigido
					byte[] decodificado = ManipularArquivoM8.conversaoUnsignedSigned(vetorRS8Decodificado);
					ManipularArquivoM8.gravarArquivo(decodificado, localAbsolutoArquivoCodificado, "Decodificado");

				}
				ManipularArquivoM8.deletarArquivos(localAbsolutoArquivoCodificado,
						"Z:\\@Projeto-Degradacao-Corretiva\\Testes-com-RS-GF(2^16)\\PMBOK 5ª Edição [Português][2013]_Codificado_Decodificado.pdf",
						localAbsolutoHash, localAbsolutoCorrecao);
			}
		}
	}

}