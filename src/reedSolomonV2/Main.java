package reedSolomonV2;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import javax.smartcardio.CardException;

public class Main {
	/**
	 * Implements the reversible degradation, with reed-solomon and nfc
	 * 
	 * @param args main to execute the code, has a self implementation for static
	 *             methods
	 * @throws NoSuchAlgorithmException default sha256 exception
	 * @throws IOException              if for any reason the data to be degradeted
	 *                                  it's unavailable on the disk
	 * @throws ReedSolomonException     exception to handle the error correction
	 *                                  code
	 * @throws CardException            pc card reader default exception, if for any
	 *                                  reason there is no card in the reader or if
	 *                                  the reader itself is unavailable
	 */

	public static void main(String[] args)
			throws NoSuchAlgorithmException, IOException, ReedSolomonException, CardException {

		ReversibleDegradation degradation = new ReversibleDegradation();
		int degradationPercent = 15;
		String absolutePath = "Z:\\@Projeto-Degradacao-Corretiva\\Testes-com-RS-GF(2^16)\\TCC1_v1.0.2.pdf";
		String encoded = "Z:\\@Projeto-Degradacao-Corretiva\\Testes-com-RS-GF(2^16)\\TCC1_v1.0.2_Encoded.pdf";
		String redundancy = "Z:\\@Projeto-Degradacao-Corretiva\\Testes-com-RS-GF(2^16)\\TCC1_v1.0.2_Redundancy.pdf";
		String hash = "Z:\\@Projeto-Degradacao-Corretiva\\Testes-com-RS-GF(2^16)\\TCC1_v1.0.2_Hash.pdf";

		// Encoder
		degradation.encoder(absolutePath, degradationPercent);

		// Decoder
		// degradation.decoder(absolutePath, encoded, redundancy, hash,
		// degradationPercent);

	}

}
