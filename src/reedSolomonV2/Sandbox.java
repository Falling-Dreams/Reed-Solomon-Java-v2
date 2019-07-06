package reedSolomonV2;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.smartcardio.CardException;

import reedSolomonV2.ReversibleDegradation;

public class Sandbox {

	public static void main(String[] args)
			throws NoSuchAlgorithmException, IOException, ReedSolomonException, CardException {
		NFCHandle nfc = new NFCHandle();
		SHA256 sha = new SHA256();
		byte[] uid = nfc.UID();
		System.out.println(Arrays.toString(uid));
		byte[] hashUID = sha.sha256(uid);

	}

	public void estaAberto(String fileName) {
		File file = new File(fileName);

		// try to rename the file with the same name
		File sameFileName = new File(fileName);

		if (file.renameTo(sameFileName)) {
			// if the file is renamed
			System.out.println("file is closed");
		} else {
			// if the file didnt accept the renaming operation
			System.out.println("file is opened");
		}
	}

}
