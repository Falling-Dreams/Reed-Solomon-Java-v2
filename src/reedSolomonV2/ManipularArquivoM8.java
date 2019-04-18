package reedSolomonV2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import javax.smartcardio.CardException;
import java.nio.file.Path;

public class ManipularArquivoM8 {

	// public static void main(String[] args)
	// throws IOException, ReedSolomonException, CardException,
	// NoSuchAlgorithmException {
	// }

	// O que faz: converte um vetor de bytes signed em um vetor de inteiros unsigned
	// Entrada: arquivoLido vetor de bytes do arquivo
	// Retorno: inteirosUnsigned um vetor de inteiros unsigned
	protected static int[] byteParaIntUnsigned(byte[] arquivoLido) {
		int tamanhoVetorInt = arquivoLido.length;
		int valorEmIntDoByte = 0;
		int[] inteirosUnsigned = new int[tamanhoVetorInt];
		for (int i = 0; i < tamanhoVetorInt; i++) {
			valorEmIntDoByte = arquivoLido[i];
			if (valorEmIntDoByte < 0) {
				valorEmIntDoByte = valorEmIntDoByte + 256;
			}
			inteirosUnsigned[i] = valorEmIntDoByte;
		}
		return inteirosUnsigned;
	}

	// O que faz: le os bytes de um arquivo informado pelo usuario e
	// retorna a sua conversao em um vetor de inteiros unsigned
	// Entrada: arquivoOriginal o local absoluto do arquivo, com nome e extensao
	// Retorno: bytesEmIntUnsigned um vetor de inteiros unsigned
	// byte[] -> int[]
	protected static int[] conversaoSignedUnsigned(String arquivoOriginal) throws IOException {
		Path path = Paths.get(arquivoOriginal);
		byte[] bytesArquivoLido = Files.readAllBytes(path);
		int[] bytesEmIntUnsigned = byteParaIntUnsigned(bytesArquivoLido);
		return bytesEmIntUnsigned;
	}

	// O que faz: converte um vetor de inteiros unsigned em um vetor de bytes signed
	// Entrada: vetorIntUnsigned o vetor a ser convertido
	// Retorno: bytesSigned um vetor de bytes signed
	// int[] -> byte[]
	protected static byte[] conversaoUnsignedSigned(int[] vetorIntUnsigned) throws IOException {
		int valorEmIntDoByte = 0;
		int tamanho = vetorIntUnsigned.length;
		byte[] bytesSigned = new byte[tamanho];
		for (int i = 0; i < bytesSigned.length; ++i) {
			valorEmIntDoByte = vetorIntUnsigned[i];
			bytesSigned[i] = byteParaInt(valorEmIntDoByte);
		}
		return bytesSigned;
	}

	// Metodo auxiliar para conversaoUnsignedSigned, transforma um unico inteiro
	// unsigned em inteiro signed
	private static byte byteParaInt(int unsigned) {
		byte valorDoByteEmInt = 0;
		if (unsigned <= 256) {
			valorDoByteEmInt = (byte) unsigned;
			if (valorDoByteEmInt > 127) {
				valorDoByteEmInt = (byte) (valorDoByteEmInt - 256);
			}
		}
		return valorDoByteEmInt;
	}

	// O que faz: indentifica a partir de um arquivo informado pelo usuario
	// o diretorio, o nome e a extensao do arquivo. Usado para gravar em disco
	// na codificacao, decodificacao, correcao e hash preservando o nome original do
	// arquivo
	// Entrada: localAbsoluto contendo o nome e a extensao do arquivo
	// Retorno: diretorioArquivoExtensao um array de string onde o indice[0] indica
	// o diretorio
	// o indice[1] o nome do arquivo e o indice[2] a extensao do arquivo
	protected static String[] recuperarDiretorioNomeExtensao(String localAbsoluto) {
		File f = new File(localAbsoluto);

		// Recupera nome e extensao do arquivo
		String arquivoComExtensao = "";
		int in = f.getAbsolutePath().lastIndexOf("\\");
		if (in > -1) {
			arquivoComExtensao = f.getAbsolutePath().substring(in + 1);
		}
		// Recupera o diretorio onde o arquivo esta armazenado
		String diretorio = localAbsoluto.replace(arquivoComExtensao, "");

		// Separa nome e extensao
		int indiceExtensao = arquivoComExtensao.lastIndexOf('.');
		int tamanhoNomeExtensao = arquivoComExtensao.length();
		String extensao = arquivoComExtensao.substring(indiceExtensao, tamanhoNomeExtensao);
		String arquivo = arquivoComExtensao.substring(0, indiceExtensao);

		// Armazena diretorio, nome do arquivo e extensao do arquivo em um array de
		// strings
		String[] diretorioArquivoExtensao = new String[3];
		diretorioArquivoExtensao[0] = diretorio;
		diretorioArquivoExtensao[1] = arquivo;
		diretorioArquivoExtensao[2] = extensao;

		return diretorioArquivoExtensao;
	}

	// o que faz: grava arquivo em disco
	// Entrada: bytesArquivo vetor de bytes a serem gravados, localAbsoluto local
	// onde o arquivo sera gravado
	// sufixoArquivo o sufixo que o arquivo recebera apos o nome e antes da extensao
	// Retorno: newFile o arquivo gerado
	protected static File gravarArquivo(byte[] bytesArquivo, String localAbsoluto, String sufixoArquivo)
			throws IOException {

		String[] diretorioArquivoExtensao = recuperarDiretorioNomeExtensao(localAbsoluto);
		String diretorio = diretorioArquivoExtensao[0];
		String arquivo = diretorioArquivoExtensao[1];
		String extensao = diretorioArquivoExtensao[2];
		String arquivoCompleto = diretorio + arquivo + "_" + sufixoArquivo + extensao;

		// Grava o arquivo com o nome fornecido e a extensao lida
		File newFile = new File(arquivoCompleto);
		FileOutputStream stream = new FileOutputStream(newFile);
		stream.write(bytesArquivo);
		stream.close();
		return newFile;
	}

	// O que faz: Corrompe 15% do arquivo com bytes randomicos
	// Entrada: bytesArquivo o vetor do arquivo que sera corrompido, t a quantidade
	// de erros que serão gerados
	// Retorno: void
	// Obs cada vetor precisa de uma variavel de incremento separada
	protected static void corrompeDado(byte[] bytesArquivo, int t) {
		int k = 177;
		int qtdIteracoes = bytesArquivo.length / k;
		int resto = bytesArquivo.length % 177;
		int incrementoVetorDados = 0;
		int incrementoVetorCorrupcao = 0;
		int inicioCopiaResto = bytesArquivo.length - resto;
		int indiceAleatorioVetorK = 0;
		int indiceAleatorioVetorCorrupcao = 0;
		byte[] vetorTempCorrupcao = new byte[k];
		SecureRandom random = new SecureRandom();
		byte[] corrupcao = new byte[t];
		random.nextBytes(corrupcao);

		// Divido o vetor original em um vetor de 177 posicoes, conforme o numero de
		// iteracoes
		for (int x = 0; x < qtdIteracoes; x++) {

			System.arraycopy(bytesArquivo, incrementoVetorDados, vetorTempCorrupcao, 0, k);
			incrementoVetorDados += k;

			// A cada 177 simbolos do arquivo original, corrompo t posicoes aleatorias
			// dessas 177
			for (int n = 0; n < 39; n++) {
				indiceAleatorioVetorK = getIndiceAleatorioVetorK(random);
				indiceAleatorioVetorCorrupcao = random.nextInt(corrupcao.length);
				System.arraycopy(corrupcao, indiceAleatorioVetorCorrupcao, vetorTempCorrupcao, indiceAleatorioVetorK,
						1);
			}
			// Devolvo o vetor original, agora, corrompido t posicoes aleatorias a cada k
			// simbolos
			System.arraycopy(vetorTempCorrupcao, 0, bytesArquivo, incrementoVetorCorrupcao, k);
			incrementoVetorCorrupcao += k;
		}
		if (resto > 0) {
			byte[] vetorRestoTempCorrupcao = new byte[resto];
			System.arraycopy(bytesArquivo, inicioCopiaResto, vetorRestoTempCorrupcao, 0, resto);
			for (int m = 0; m < resto; m++) {
				indiceAleatorioVetorCorrupcao = random.nextInt(corrupcao.length);
				System.arraycopy(corrupcao, indiceAleatorioVetorCorrupcao, bytesArquivo, inicioCopiaResto, 1);
			}
		}
	}

	// Corrompe 15% do arquivo com bytes randomicos
	// O que faz:
	// Entrada:
	// Retorno:
	protected static int getIndiceAleatorioVetorK(SecureRandom random) {
		byte[] vetorK = new byte[177];
		int indiceAleatorio = random.nextInt(vetorK.length);
		return indiceAleatorio;
	}

	// O que faz: Deleta os arquivos codificado, decodificado, correcao e hash
	// Entrada: os locais absolutos com nome do arquivo e extensao
	// Retorno: void
	// Exceção: Caso não seja possivel excluir o arquivo
	protected static void deletarArquivos(String codificado, String decodificado, String hash, String correcao)
			throws ReedSolomonException {
		// FileLock lock = channel.lock();
		try {
			Path pathCodificado = Paths.get(codificado);
			Path pathDecodificado = Paths.get(decodificado);
			Path pathHash = Paths.get(hash);
			Path pathCorrecao = Paths.get(correcao);

			Files.delete(pathCodificado);
			Files.delete(pathDecodificado);
			Files.delete(pathHash);
			Files.delete(pathCorrecao);

		} catch (Exception e) {
			throw new ReedSolomonException("Erro durante a exclusão dos arquivos");
		}
	}
}