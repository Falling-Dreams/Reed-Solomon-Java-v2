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
import reedSolomonV2.ManipulaNFC;
import com.google.common.base.Stopwatch;

public class Sandbox {

	public static void main(String[] args) throws CardException, ReedSolomonException {

		ManipulaNFC nfc = new ManipulaNFC();
		byte[] uid = nfc.UID();

	}

}
