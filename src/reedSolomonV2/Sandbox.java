package reedSolomonV2;

import reedSolomonV2.SHA256;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import reedSolomonV2.ManipularArquivoM8;
import javax.smartcardio.CardException;

import reedSolomonV2.ManipulaNFC;;

public class Sandbox {

	public static void main(String[] args)
			throws CardException, ReedSolomonException, NoSuchAlgorithmException, IOException {

		String localAbsoluto = "Z:\\@Projeto-Degradacao-Corretiva\\Testes-com-RS-GF(2^16)\\Novo Documento de Texto2.txt";
		Path path = Paths.get(localAbsoluto);
		byte[] bytesArquivoLido = Files.readAllBytes(path);
		System.out.println("Antes da corrupcao: " + (Arrays.toString(bytesArquivoLido)));
		Sandbox.corrompeDado(bytesArquivoLido, 39);
		System.out.println("Depois da corrupcao: " + (Arrays.toString(bytesArquivoLido)));
		// System.out.println(Sandbox.getIndiceAleatorioVetorK());

	}

	protected static void corrompeDado(byte[] bytesArquivo, int t) {
		int k = 177;
		int qtdIteracoes = bytesArquivo.length / k;
		//int resto = bytesArquivo.length % 177;
		int incrementoVetorDados = 0;
		byte[] vetorTempCorrupcao = new byte[k];
		//byte[] vetorArquivoCorrompido = new byte[bytesArquivo.length];
		SecureRandom random = new SecureRandom();
		int indiceAleatorio = getIndiceAleatorioVetorK(random);
		byte[] corrupcao = new byte[t];
		random.nextBytes(corrupcao);

		for (int x = 0; x < qtdIteracoes; x++) {
			System.arraycopy(bytesArquivo, incrementoVetorDados, vetorTempCorrupcao, 0, k);
			incrementoVetorDados += k;

			//for (int n = 0; n < 39; n++) {
				//System.arraycopy(corrupcao, 0, vetorTempCorrupcao, indiceAleatorio, 1);
			//}

			System.arraycopy(vetorTempCorrupcao, 0, bytesArquivo, incrementoVetorDados, k);
		}

		//return vetorArquivoCorrompido;
	}

	protected static int getIndiceAleatorioVetorK(SecureRandom random) {
		byte[] vetorK = new byte[177];
		// SecureRandom random = new SecureRandom();
		int indiceAleatorio = random.nextInt(vetorK.length);
		return indiceAleatorio;
	}
}
