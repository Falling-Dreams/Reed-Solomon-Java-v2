package reedSolomonV2;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import javax.smartcardio.CardException;
import reedSolomonV2.ReversibleDegradation;
import reedSolomonV2.FileHandle;

public class Sandbox {

	public static void main(String[] args)
			throws NoSuchAlgorithmException, IOException, ReedSolomonException, CardException {
		
		String absolutePath = "Z:\\Engenharia\\10º Semestre(2.2019)\\4. TCC II\\Apresentação\\TCCII_v1.0.1_FINAL.pdf";
		String[] directoryFileExtension = FileHandle.nameExtension(absolutePath);
		String directory = directoryFileExtension[0];
		String file = directoryFileExtension[1];
		String extension = directoryFileExtension[2];
		String fullFile = directory + file + "_" + "Encoded_Decoded" + extension;
		
		System.out.println(fullFile);

	}
	
}
