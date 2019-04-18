package reedSolomonV2;

import reedSolomonV2.SHA256;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
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

		String localAbsoluto = "Z:\\@Projeto-Degradacao-Corretiva\\Testes-com-RS-GF(2^16)\\PMBOK 5ª Edição [Português][2013]_Codificado_Decodificado.pdf";
		File file = new File(localAbsoluto);
		//FileChannel channel = new RandomAccessFile(file, "rw").getChannel();
		//FileLock lock = channel.lock();
		FileOutputStream fos = new FileOutputStream(localAbsoluto);
		try {
			//lock = channel.tryLock();
			fos.close();			
			file.delete();
		} catch (Exception e) {
			System.out.println("Deu ruim");
		}

	}

}
