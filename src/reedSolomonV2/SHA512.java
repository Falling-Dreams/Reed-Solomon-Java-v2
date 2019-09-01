package reedSolomonV2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class SHA512 {
	/**
	 * <p>
	 * Get the sha256 from the provided bytes. Uses the built-in java function.
	 * <p>
	 * 
	 * @param cardUID uid from the card
	 * @return sha256: The calculated sha256
	 * @throws NoSuchAlgorithmException: If the selected hash does not exist on java
	 *                                   built-in modules.
	 */

	protected byte[] sha512(byte[] cardUID) throws NoSuchAlgorithmException {
		MessageDigest mDigest = MessageDigest.getInstance("SHA-512");
		byte[] sha512 = mDigest.digest(cardUID);

		return sha512;
	}

	/**
	 * <p>
	 * Checks if the provided card is the same that encoded the file.
	 * <p>
	 * 
	 * @param encoderHash The hash generated in the encoder process.
	 * @param cardUID     UID from the supposed card, the one whom may encoded the
	 *                    file.
	 * @return True if the encoderHash matches the one provided with the card.
	 * @throws NoSuchAlgorithmException: If the selected hash does not exist on java
	 *                                   built-in modules.
	 * @throws IOException:              If the saved hash it's not avaliable in the
	 *                                   disk.
	 */
	public boolean verificaChecksum(String encoderHash, byte[] cardUID) throws NoSuchAlgorithmException, IOException {
		Path path = Paths.get(encoderHash);
		byte[] bytesArquivo = Files.readAllBytes(path);
		byte[] hashGerado = sha512(cardUID);

		return Arrays.equals(bytesArquivo, hashGerado);
	}

}
