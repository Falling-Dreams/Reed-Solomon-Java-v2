package reedSolomonV2;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import javax.smartcardio.CardException;

public class Main {
	/**
	 * Implements the reversible degradation, with reed-solomon and nfc encoded: the
	 * absolute path of the file that has been encoded and degradeted redundancy:
	 * the error correction bytes generated by the encoder process hash: the
	 * absolute path of the file matching the hash generated by the card that
	 * encoded the file
	 * 
	 * @param args main to execute the code, has a self implementation for static
	 *             methods
	 * @throws NoSuchAlgorithmException default sha256 exception
	 * @throws IOException              if for any reason the data to be degradeted
	 *                                  it's unavailable in the disk
	 * @throws ReedSolomonException     exception to handle the error correction
	 *                                  code
	 * @throws CardException            pc card reader default exception, if for any
	 *                                  reason there is no card in the reader or if
	 *                                  the reader itself is unavailable
	 */

	public static void main(String[] args)
			throws NoSuchAlgorithmException, IOException, ReedSolomonException, CardException {

		ReversibleDegradation degradation = new ReversibleDegradation();
		int degradationPercent = 5, m = 16;
		String absolutePath = "Z:\\matrizes.pdf";
		String encoded = "Z:\\matrizes_Encoded.pdf";
		String redundancy = "Z:\\matrizes_Redundancy.pdf";		

		// Encoder
		//degradation.encoder(absolutePath, degradationPercent, m);

		// Decoder
		degradation.decoder(absolutePath, encoded, redundancy, degradationPercent, m);

	}

}
