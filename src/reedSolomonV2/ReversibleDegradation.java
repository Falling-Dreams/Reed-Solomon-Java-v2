package reedSolomonV2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import javax.smartcardio.Card;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.TerminalFactory;
import reedSolomonV2.FileHandle;
import com.google.common.base.Stopwatch;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * Implements the meaningless reversible degradation, with Reed-Solomon and Near
 * Field Communication
 * </p>
 * Default Primitive Polynomial=D^16 + D^12 + D^3 + D + 1; Integer
 * Representation=69643. A PC card reader is necessary for the correct
 * implementation of the meaningless reversible degradation.
 * 
 * @author Kevin de Santana
 *
 */

public class ReversibleDegradation {

	/**
	 * <p>
	 * Implements the reed-solomon encoder and errases degradationPercent of random
	 * bytes of the file, every n bytes in the file
	 * <p>
	 * Encoder routine, the last iteration is treated separately, when there is rest
	 * in the division totalSymbols / k The coding is performed every 255 symbols of
	 * the total of symbols of the file the original vector and divided into vectors
	 * of 255, at the end of the coding are recorded two files: Coded_file_name and
	 * Redundant_file_name The encrypted file and the original corrupted file t
	 * positions, redundancy are the symbols of Correction generated at each
	 * iteration of for, of each vector of the original file.
	 * 
	 * @param absolutePath       string representing the file to be degradeted
	 *                           (receives any kind of extension, i.e, .pdf, .word,
	 *                           .jpg and etc.)
	 * @param degradationPercent total percentage of the file to be degradeted (up
	 *                           to a maximum of 50%). Notice that the higher the
	 *                           percent, the more the algorithm takes to encoder
	 *                           the file.
	 * @throws IOException              if for any reason the data to be degradeted
	 *                                  it's unavailable in the disk
	 * @throws ReedSolomonException     exception to handle the error correction
	 *                                  code
	 * @throws CardException            pc card reader default exception, if for any
	 *                                  reason there is no card in the reader or if
	 *                                  the reader itself is unavailable
	 * @throws NoSuchAlgorithmException default sha256 exception
	 */
	protected void encoder(String absolutePath, int degradationPercent, int m)
			throws IOException, ReedSolomonException, NoSuchAlgorithmException, CardException {

		// measure the total runtime of enconde processing
		Stopwatch timer = new Stopwatch();
		timer.start();

		int primitive = 0, size = 0, n = 0, k = 0;

		if (m == 8) {

			primitive = 285;
			size = 256;
			n = 255;

			switch (degradationPercent) {
			case 5:
				k = 229;
				break;
			case 10:
				k = 203;
				break;
			case 15:
				k = 177;
				break;
			case 20:
				k = 151;
				break;
			case 25:
				k = 125;
				break;
			case 50:
				k = 5;
				break;
			}

		} else {

			if (m == 16) {
				primitive = 69643;
				size = 65536;
				n = 65535;

				switch (degradationPercent) {
				case 5:
					k = 58983;
					break;
				case 10:
					k = 0;
					break;
				case 15:
					k = 0;
					break;
				case 20:
					k = 0;
					break;
				case 25:
					k = 0;
					break;
				case 50:
					k = 0;
					break;
				}
			}

		}
		NFCHandle nfc = new NFCHandle();
		SHA512 sha = new SHA512();
		if (nfc.cardOrTerminalUnavailable() == true) {
			System.out.println("NFC card and terminals are unavailable, application will be terminated!" + "\n");
			System.exit(0);
		} else {
			System.out.println("System ready to encode" + "\n");

			int t = ((n - k) / 2), redundacy = n - k;
			int incrementRS8C = 0, incrementEncoded = 0, incrementRdd = 0;
			Path path = Paths.get(absolutePath);
			byte[] file = Files.readAllBytes(path);
			int totalSymbols = file.length;
			int iteractionsRS = totalSymbols / k;
			int remainderRS = totalSymbols % k;
			int[] kUnsigned = FileHandle.signedToUnsigned(absolutePath);
			int[] encodedRS8 = new int[totalSymbols];
			int[] redundancy = new int[((iteractionsRS + 1) * redundacy)];
			int[] rs8c = new int[n];
			int srcPosRemainder = totalSymbols - remainderRS;
			int destPosRdd = iteractionsRS * redundacy;

			GenericGF gf = new GenericGF(primitive, size, 1);
			ReedSolomonEncoder encoder = new ReedSolomonEncoder(gf);

			System.out.println("Encoding..." + "\n");

			for (int h = 0; h < iteractionsRS; h++) {
				System.arraycopy(kUnsigned, incrementRS8C, rs8c, 0, k);
				incrementRS8C += k;

				encoder.encode(rs8c, redundacy);

				System.arraycopy(rs8c, 0, encodedRS8, incrementEncoded, k);
				incrementEncoded += k;

				System.arraycopy(rs8c, k, redundancy, incrementRdd, redundacy);
				incrementRdd += redundacy;

			}
			// remainingRS > 0
			if (remainderRS > 0) {
				int[] remainderRS8C = new int[n]; // New vector for the remainder symbols
				System.arraycopy(kUnsigned, srcPosRemainder, remainderRS8C, 0, remainderRS);
				encoder.encode(remainderRS8C, redundacy);
				System.arraycopy(remainderRS8C, 0, encodedRS8, srcPosRemainder, remainderRS);
				System.arraycopy(remainderRS8C, k, redundancy, destPosRdd, redundacy);
			}

			if (Arrays.equals(kUnsigned, encodedRS8) != true) {
				throw new ReedSolomonException(
						"Error in coding, coded vector is not equal to file vector (in unsigned integer)");
			} else {
				System.out.println("File successfully encoded! " + "\n");
			}

			// vector of bytes already encoded by the encoder and corrupted t positions
			byte[] encoded = FileHandle.unsignedToSigned(encodedRS8);
			FileHandle.corruption(encoded, t, k);

			// redundancy symbols
			if (m == 16) {

				int[] redundancyM16 = FileHandle.bin16ToBin8(redundancy);
				byte[] signedRdd = FileHandle.unsignedToSigned(redundancyM16);
				FileHandle.writeFile(encoded, absolutePath, "Encoded");
				FileHandle.writeFile(signedRdd, absolutePath, "Redundancy");
			} else {

				byte[] signedRdd = FileHandle.unsignedToSigned(redundancy);
				FileHandle.writeFile(encoded, absolutePath, "Encoded");
				FileHandle.writeFile(signedRdd, absolutePath, "Redundancy");
			}
			// Create hash from NFC card
			byte[] uid = nfc.UID();
			byte[] hashUID = sha.sha512(uid);
			FileHandle.writeFile(hashUID, absolutePath, "Hash");
			System.out.println("Hash file stored" + "\n");

		}
		System.out.println("Encoding time: " + timer.stop());
	}

	/**
	 * <p>
	 * Implements the reed-solomon decoder and correction of degradationPercent of
	 * the random erased bytes of the file, every n bytes in the file
	 * <p>
	 * 
	 * @param encodedFile        The file encoded and corrupted
	 * @param redundancyFile     The file containing the redundacy symbols for
	 *                           correction
	 * @param hashEncoder        The generated hash from the card whom encoded the
	 *                           file
	 * @param degradationPercent Corruption percentage of the file (must match the
	 *                           one in the encoding process)
	 * @throws IOException              if for any reason the data to be degradeted
	 *                                  it's unavailable in the disk
	 * @throws ReedSolomonException     exception to handle the error correction
	 *                                  code
	 * @throws CardException            pc card reader default exception, if for any
	 *                                  reason there is no card in the reader or if
	 *                                  the reader itself is unavailable
	 * @throws NoSuchAlgorithmException default sha256 exception
	 * @throws InterruptedException 
	 */
	protected void decoder(String absolutePath, String encodedFile, String redundancyFile, String hashEncoder,
			int degradationPercent, int m)
			throws IOException, ReedSolomonException, NoSuchAlgorithmException, CardException, InterruptedException {

		// count the total runtime of deconder processing
		Stopwatch timer = new Stopwatch();
		timer.start();

		int primitive = 0, size = 0, n = 0, k = 0;

		if (m == 8) {

			primitive = 285;
			size = 256;
			n = 255;

			switch (degradationPercent) {
			case 5:
				k = 229;
				break;
			case 10:
				k = 203;
				break;
			case 15:
				k = 177;
				break;
			case 20:
				k = 151;
				break;
			case 25:
				k = 125;
				break;
			case 50:
				k = 5;
				break;
			}

		} else {

			if (m == 16) {
				primitive = 69643;
				size = 65536;
				n = 65535;

				switch (degradationPercent) {
				case 5:
					k = 58983;
					break;
				case 10:
					k = 0;
					break;
				case 15:
					k = 0;
					break;
				case 20:
					k = 0;
					break;
				case 25:
					k = 0;
					break;
				case 50:
					k = 0;
					break;
				}
			}

		}

		NFCHandle nfc = new NFCHandle();
		SHA512 sha = new SHA512();
		byte[] uid = nfc.UID();
		byte[] hashUID = sha.sha512(uid);
		boolean writeFile = true;

		if (nfc.cardOrTerminalUnavailable() == true) {
			System.out.println("NFC card and terminals are unavailable, the application will be terminated!" + "\n");
			System.exit(0);
		} else {
			System.out.println("Please, superimpose the authorized NFC card to retrive the file" + "\n");
			TimeUnit.SECONDS.sleep(2);
			if (sha.verificaChecksum(hashEncoder, uid) != true) {
				System.out.println(
						"The card present in the terminal is not the same as the encoding, the application will be closed"
								+ "\n");
				System.exit(0);
			} else {
				System.out.println("Authorized card" + "\n");
				TimeUnit.SECONDS.sleep(2);
				
				TerminalFactory factory = TerminalFactory.getDefault();
				List<CardTerminal> terminals = factory.terminals().list();
				CardTerminal term = terminals.get(0);
				Card card = term.connect("*");
				int contador = 0;

				System.out.println(
						"To initiate the decode process, superimpose the card two times in the reader under one minute"
								+ "\n");
				TimeUnit.SECONDS.sleep(2);
				
				while (term.waitForCardAbsent(100000) == true) {
					if (term.waitForCardPresent(100) == true) {
						contador += 1;
					}

					if (contador == 2) {
						
						System.out.println("Card superimpose two times, initializing decode processing..." + "\n");
						
						int totalRddSymb = (n - k), destPosRdd = 0;
						int srcPosDec = 0, incrementRdd = 0;
						Path path = Paths.get(encodedFile);
						byte[] file = Files.readAllBytes(path);
						int totalSymb = file.length;
						int iteractionsRS = totalSymb / k;
						int remainderRS = totalSymb % k;
						int[] rs8d = new int[n];
						int destPosRS8D = rs8d.length - totalRddSymb;
						int incrementRemainder = totalSymb - remainderRS;
						int srcPosRemainder = iteractionsRS * totalRddSymb;
						int[] decoderRS8D = new int[totalSymb];

						GenericGF gf = new GenericGF(primitive, size, 1);
						ReedSolomonDecoder decoder = new ReedSolomonDecoder(gf);

						// Recovers encoded file and redundancy
						int[] redundancySymb = new int[((iteractionsRS + 1) * totalRddSymb)];
						int[] redundancyM16 = new int[redundancySymb.length];
						if (m == 16) {
							redundancySymb = FileHandle.signedToUnsigned(redundancyFile);
							redundancyM16 = FileHandle.intM16(redundancySymb); // redundancy

						} else {
							redundancySymb = FileHandle.signedToUnsigned(redundancyFile);
						}

						int[] kSymbols = FileHandle.signedToUnsigned(encodedFile); // encoded

						// Initiate the decoding process
						for (int h = 0; h < iteractionsRS; h++) {

							// Break single vector of symbols encoded in 255 vectors
							// Copy 177 information symbols to the vectorRS8D
							System.arraycopy(kSymbols, incrementRdd, rs8d, 0, k);
							incrementRdd += k;

							// Concatenating Data with Redundancy and Decoding
							System.arraycopy(redundancyM16, destPosRdd, rs8d, destPosRS8D, totalRddSymb);
							destPosRdd += totalRddSymb;

							// Decoder
							decoder.decode(rs8d, totalRddSymb);

							// Save what was decoded into a single vector to write the decoded file
							System.arraycopy(rs8d, 0, decoderRS8D, srcPosDec, k);
							srcPosDec += k;
						}
						// Remainder
						if (remainderRS > 0) {
							int[] rs8dRemainder = new int[n]; // decoder remainder
							// Copy k rest symbols to rs8dRemainder
							System.arraycopy(kSymbols, incrementRemainder, rs8dRemainder, 0, remainderRS);
							// Copy n-k rest symbols to rs8dRemainder
							System.arraycopy(redundancyM16, srcPosRemainder, rs8dRemainder, k, totalRddSymb);
							// Decoder process of the remainder
							decoder.decode(rs8dRemainder, totalRddSymb);
							// Copy of k remainder symbols for single vector
							System.arraycopy(rs8dRemainder, 0, decoderRS8D, incrementRemainder, remainderRS);
							// Copy of n-k remainder correction symbols for single vector
							System.arraycopy(rs8dRemainder, k, redundancyM16, srcPosRemainder, totalRddSymb);
						}

						if (writeFile == true) {
							// Save the decoded file
							byte[] decoded = FileHandle.unsignedToSigned(decoderRS8D);
							FileHandle.writeFile(decoded, encodedFile, "Decoded");
							System.out.println("File decoded and avaliable in disk" + "\n");
						}
						writeFile = false;
						System.out.println("Decoding time: " + timer.stop());
						System.exit(0);
					}
				}
			}
		}
	}
}