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
		
		SHA256 sha = new SHA256();
		ManipulaNFC nfc = new ManipulaNFC();
		



	}

	

}
