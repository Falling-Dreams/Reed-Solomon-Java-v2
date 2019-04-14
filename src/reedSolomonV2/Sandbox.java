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
import java.util.Arrays;
import reedSolomonV2.ManipularArquivoM8;
import javax.smartcardio.CardException;

import reedSolomonV2.ManipulaNFC;;

public class Sandbox {

	public static void main(String[] args)
			throws CardException, ReedSolomonException, NoSuchAlgorithmException, IOException {

		String localAbsoluto = "Z:\\@Projeto-Degradacao-Corretiva\\Testes-com-RS-GF(2^16)\\UVERworld_Colors_of_the_Heart.mp3";
		String localAbsolutoHash = "Z:\\@Projeto-Degradacao-Corretiva\\Testes-com-RS-GF(2^16)\\UVERworld_Colors_of_the_Heart_Hash_Codificacao.txt";
		SHA256 sha = new SHA256();
		ManipulaNFC nfc = new ManipulaNFC();

		byte[] uidLido = nfc.UID();
		//System.out.println(Arrays.toString(uidLido));		
		//System.out.println(Arrays.toString(sha256Gerado));
		//gravarHash(sha256Gerado, localAbsoluto);
		
		//System.out.println(Arrays.toString(dado));
		Boolean ehOhMesmoHash = sha.verificaChecksum(localAbsolutoHash, uidLido);
		System.out.println("Os hashs sao os mesmo? " + ehOhMesmoHash);


	}

	private static File gravarHash(byte[] sha256Gerado, String localAbsoluto) throws IOException {

		String[] diretorioArquivoExtensao = ManipularArquivoM8.recuperoDiretorioNomeExtensao(localAbsoluto);
		String diretorio = diretorioArquivoExtensao[0];
		String arquivo = diretorioArquivoExtensao[1];
		String extensao = ".txt";
		String arquivoCompleto = diretorio + arquivo + "_" + "Hash_Codificacao" + extensao;

		// Grava o arquivo com o nome fornecido e a extensao lida
		File newFile = new File(arquivoCompleto);
		FileOutputStream stream = new FileOutputStream(newFile);
		stream.write(sha256Gerado);
		stream.close();
		return newFile;
	}

}
