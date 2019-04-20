package reedSolomonV2;

import reedSolomonV2.SHA256;
import reedSolomonV2.ManipularArquivoM8;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.smartcardio.CardException;

import reedSolomonV2.ManipulaNFC;;

public class Sandbox {

	public static void main(String[] args) throws NoSuchAlgorithmException, IOException, ReedSolomonException, CardException {
		Sandbox sand = new Sandbox();
		int correcao = 15;
		String localAbsoluto = "Z:\\@Projeto-Degradacao-Corretiva\\Testes-com-RS-GF(2^16)\\CertiProf-Scrum-Master.pdf";
		String localAbsolutoCodi = "Z:\\@Projeto-Degradacao-Corretiva\\Testes-com-RS-GF(2^16)\\CertiProf-Scrum-Master_Codificado.pdf";
		String localAbsolutoCorrecao = "Z:\\@Projeto-Degradacao-Corretiva\\Testes-com-RS-GF(2^16)\\CertiProf-Scrum-Master_Correcao.pdf";
		String localAbsolutoHash = "Z:\\@Projeto-Degradacao-Corretiva\\Testes-com-RS-GF(2^16)\\CertiProf-Scrum-Master_Hash.pdf";
		//sand.codificacao(localAbsoluto);
		sand.decodificacao(localAbsolutoCodi, localAbsolutoCorrecao, localAbsolutoHash);
	}

	protected void codificacao(String localAbsoluto)
			throws IOException, ReedSolomonException, CardException, NoSuchAlgorithmException {	
		
		ManipulaNFC nfc = new ManipulaNFC();
		SHA256 sha = new SHA256();

		// A codificacao acontece enquanto existir um cartao presente
		if (nfc.cartaoOuTerminalAusentes() == true) {
			System.out.println("Cartão NFC e terminais estão indisponíveis, a aplicaçãos será encerrada!" + "\n");
			System.exit(0);
		} else {
			System.out.println("Cartão NFC e terminais estão disponíveis!" + "\n");
			
			int k = 177, n = 255, t = 39, qtdSimbolosRedundancia = 78;
			int incrementoVetorRS8C = 0, incrementoVetorUnico = 0, incrementoVetorCorrecao = 0;
			Path path = Paths.get(localAbsoluto);
			byte[] bytesLidosArquivo = Files.readAllBytes(path);
			int qtdSimbolos = bytesLidosArquivo.length;
			int qtdIteracoesRS = qtdSimbolos / k;
			int restoVetorRS = qtdSimbolos % k;
			int[] intUnsigned = ManipularArquivoM8.conversaoSignedUnsigned(localAbsoluto);
			int[] vetorRS8Codificado = new int[qtdSimbolos];
			int[] vetorSimbolosCorrecao = new int[((qtdIteracoesRS + 1) * qtdSimbolosRedundancia)];
			int[] vetorRS8C = new int[255];
			int sourcePosResto = qtdSimbolos - restoVetorRS;
			int destPosVetCorrecao = qtdIteracoesRS * qtdSimbolosRedundancia;

			GenericGF gf = new GenericGF(285, 256, 1);
			ReedSolomonEncoder encoder = new ReedSolomonEncoder(gf);
			System.out.println("Codificacao em andamento..." + "\n");			

			for (int h = 0; h < qtdIteracoesRS; h++) {			
				System.arraycopy(intUnsigned, incrementoVetorRS8C, vetorRS8C, 0, k);
				incrementoVetorRS8C += k;
				
				encoder.encode(vetorRS8C, qtdSimbolosRedundancia);
				
				System.arraycopy(vetorRS8C, 0, vetorRS8Codificado, incrementoVetorUnico, k);
				incrementoVetorUnico += k;

				System.arraycopy(vetorRS8C, k, vetorSimbolosCorrecao, incrementoVetorCorrecao, qtdSimbolosRedundancia);
				incrementoVetorCorrecao += qtdSimbolosRedundancia;

			}
			// Tratamento para o caso em que restoVetorRS > 0 - FUNCIONANDO			
			if (restoVetorRS > 0) {				
				int[] vetorRS8CResto = new int[255];				
				System.arraycopy(intUnsigned, sourcePosResto, vetorRS8CResto, 0, restoVetorRS);			
				encoder.encode(vetorRS8CResto, qtdSimbolosRedundancia);	
				System.arraycopy(vetorRS8CResto, 0, vetorRS8Codificado, sourcePosResto, restoVetorRS);		
				System.arraycopy(vetorRS8CResto, k, vetorSimbolosCorrecao, destPosVetCorrecao, qtdSimbolosRedundancia);
			}

			if (Arrays.equals(intUnsigned, vetorRS8Codificado) != true) {
				throw new ReedSolomonException(
						"Erro na codificacao, vetor codificado nao e igual ao vetor do arquivo (em inteiro unsigned)");
			} else {
				System.out.println("Arquivo codificado com sucesso! " + "\n");
			}

			// Cria vetor de bytes ja codificados pelo encoder e corrompido t posicoes
			byte[] vetorCodificadoSigned = ManipularArquivoM8.conversaoUnsignedSigned(vetorRS8Codificado);
			ManipularArquivoM8.corrompeDado(vetorCodificadoSigned, t, k);

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
	
	protected void decodificacao(String localAbsolutoArquivoCodificado, String localAbsolutoCorrecao,
			String localAbsolutoHash)
			throws IOException, ReedSolomonException, CardException, NoSuchAlgorithmException {

		// Instancia a classe que manipula o arquivo para degradacao
		// ManipularArquivoM8 manipula = new ManipularArquivoM8();
		/*
		int k = 0;

		switch (porcentagemCorrecao) {
		case 5:
			k = 229;
			break;
		case 10:
			k = 203;
			break;
		case 15:
			k = 177;
			break;
		case 20:
			k = 151;
			break;
		case 25:
			k = 125;
			break;
		case 50:
			k = 5;
			break;	
		}*/

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
					int k = 177, n = 255, t = 39, qtdSimbolosRedundancia = 78, destPos = 0;
					int srcPosDecodi = 0, incrementoVetorDecodificado = 0;
					Path path = Paths.get(localAbsolutoArquivoCodificado);
					byte[] dado = Files.readAllBytes(path);
					int qtdSimbolos = dado.length;
					int qtdIteracoesRS = qtdSimbolos / k;
					int restoVetorRS = qtdSimbolos % k;
					int[] vetorRS8D = new int[255];
					int destPosRS8D = vetorRS8D.length - qtdSimbolosRedundancia;
					int incrementoVetorResto = qtdSimbolos - restoVetorRS;
					int sourcePosRestoD = qtdIteracoesRS * qtdSimbolosRedundancia;
					//int[] arquivo = ManipularArquivoM8.conversaoSignedUnsigned(localAbsolutoArquivoCodificado);
					int[] vetorRS8Decodificado = new int[qtdSimbolos];

					GenericGF gf = new GenericGF(285, 256, 1);
					ReedSolomonDecoder decoder = new ReedSolomonDecoder(gf);					
					System.out.println("Decodificacao em andamento..." + "\n");

					// Recupera arquivo codificado e redundancia
					int[] vetorSimbolosCorrecao = new int[((qtdIteracoesRS + 1) * qtdSimbolosRedundancia)];
					vetorSimbolosCorrecao = ManipularArquivoM8.conversaoSignedUnsigned(localAbsolutoCorrecao);
					int[] voltaCodificado = ManipularArquivoM8
							.conversaoSignedUnsigned(localAbsolutoArquivoCodificado);
					
					// Rotina de decodificacao
					for (int h = 0; h < qtdIteracoesRS; h++) {

						// Quebrar vetor unico de simbolos codificados em vetores de 255
						// Copia 177 simbolos de informacao para o vetorRS8D
						
						System.arraycopy(voltaCodificado, incrementoVetorDecodificado, vetorRS8D, 0, k);
						incrementoVetorDecodificado += k;

						// Concatenando dado com redudancia e efetua Decodificacao
						System.arraycopy(vetorSimbolosCorrecao, destPos, vetorRS8D, destPosRS8D, qtdSimbolosRedundancia);
						destPos += qtdSimbolosRedundancia;

						// Decodificacao						
						decoder.decode(vetorRS8D, 78);
						

						// Guardar o que foi decodificado em um unico vetor para gravar o arquivo
						// decodificado
						System.arraycopy(vetorRS8D, 0, vetorRS8Decodificado, srcPosDecodi, k);
						srcPosDecodi += k;
					}
					// Tratamento quando ha resto na divisao qtdSimbolosArquivo / k
					if (restoVetorRS > 0) {
						// Vetor para tratamento do resto
						int[] vetorRS8DResto = new int[255];
						// Copia k simbolos de resto para vetorRS8DResto
						System.arraycopy(voltaCodificado, incrementoVetorResto, vetorRS8DResto, 0, restoVetorRS);
						// Copia n-k simbolos de correcao para vetorRS8DResto
						System.arraycopy(vetorSimbolosCorrecao, sourcePosRestoD, vetorRS8DResto, k,
								qtdSimbolosRedundancia);
						// Decodificacao do resto
						decoder.decode(vetorRS8DResto, qtdSimbolosRedundancia);
						// Copia dos k simbolos de resto para vetor unico
						System.arraycopy(vetorRS8DResto, 0, vetorRS8Decodificado, incrementoVetorResto, restoVetorRS);
						// Copia dos n-k simbolos de correcao do resto para vetor unico
						System.arraycopy(vetorRS8DResto, k, vetorSimbolosCorrecao, sourcePosRestoD,
								qtdSimbolosRedundancia);
					}
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
