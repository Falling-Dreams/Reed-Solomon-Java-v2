package reedSolomonV2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import javax.smartcardio.CardException;
import java.nio.file.Path;

public class FileHandle {

	/**
	 * <p>
	 * Transform a vector of signed bytes into an unsigned integer vector
	 * <p>
	 * 
	 * @param file File to be transform
	 * @return unsignedInt: Vector containing the unsigned bytes
	 */
	protected static int[] byteToIntUnsigned(byte[] file) {
		int fileLen = file.length;
		int intValue = 0;
		int[] unsignedInt = new int[fileLen];
		for (int i = 0; i < fileLen; i++) {
			intValue = file[i];
			if (intValue < 0) {
				intValue = intValue + 256;
			}
			unsignedInt[i] = intValue;
		}
		return unsignedInt;
	}

	/**
	 * <p>
	 * Reads the bytes of a user-entered file and returns its conversion in an
	 * unsigned integer vector
	 * <p>
	 * 
	 * @param file The file to be transform into unsigned integer
	 * @return unsignedInt: Vector containing the conversion
	 * @throws IOException: if for any reason the file it's unavailable in the disk
	 */
	protected static int[] signedToUnsigned(String file) throws IOException {
		Path path = Paths.get(file);
		byte[] bytesArquivoLido = Files.readAllBytes(path);
		int[] unsignedInt = byteToIntUnsigned(bytesArquivoLido);
		return unsignedInt;
	}

	/**
	 * <p>
	 * Unsigned int to signed byte
	 * <p>
	 * 
	 * @param unsignedInt Integer vector to be transform
	 * @return signedByte: The transformed signed byte
	 * @throws IOException: if for any reason the file it's unavailable in the disk
	 */
	protected static byte[] unsignedToSigned(int[] unsignedInt) throws IOException {
		int intByte = 0;
		int size = unsignedInt.length;
		byte[] signedByte = new byte[size];
		for (int i = 0; i < signedByte.length; ++i) {
			intByte = unsignedInt[i];
			signedByte[i] = intUnToSig(intByte);
		}
		return signedByte;
	}

	/**
	 * <p>
	 * Transforms a single unsigned integer into signed integer
	 * <p>
	 * 
	 * @param unsigned The unsigned byte
	 * @return intvalue: The signed int
	 */
	private static byte intUnToSig(int unsigned) {
		byte intvalue = 0;
		if (unsigned <= 256) {
			intvalue = (byte) unsigned;
			if (intvalue > 127) {
				intvalue = (byte) (intvalue - 256);
			}
		}
		return intvalue;
	}

	/**
	 * <p>
	 * Identifies the directory, file name, and file extension from a user-entered
	 * file. Used to write to disk in encoding, decoding, correction and hash
	 * preserving the original file name
	 * <p>
	 * 
	 * @param absolutePath String containing the absolute path of the file
	 * @return directoryFileExtension: an array of string where the index [0]
	 *         indicates the directory, the index [1] the file name and the index
	 *         [2] the file extension
	 */
	protected static String[] nameExtension(String absolutePath) {
		File f = new File(absolutePath);

		String fileExtension = "";
		int in = f.getAbsolutePath().lastIndexOf("\\");
		if (in > -1) {
			fileExtension = f.getAbsolutePath().substring(in + 1);
		}
		// Directory
		String directory = absolutePath.replace(fileExtension, "");

		// Get name and extension
		int extensionIndex = fileExtension.lastIndexOf('.');
		int sizeNameExtension = fileExtension.length();
		String extension = fileExtension.substring(extensionIndex, sizeNameExtension);
		String file = fileExtension.substring(0, extensionIndex);

		// Stores directory, file name, and file extension in an array of strings
		String[] directoryFileExtension = new String[3];
		directoryFileExtension[0] = directory;
		directoryFileExtension[1] = file;
		directoryFileExtension[2] = extension;

		return directoryFileExtension;
	}

	/**
	 * <p>
	 * Write file to disk
	 * <p>
	 * 
	 * @param fileBytes    vector of bytes to be written
	 * @param absolutePath location where the file will be saved
	 * @param fileSufix    the suffix that the file will receive after the name and
	 *                     before the extension
	 * @return newFile the generated file
	 * @throws IOException: if for any reason the file it's unavailable in the disk
	 */
	protected static File writeFile(byte[] fileBytes, String absolutePath, String fileSufix) throws IOException {

		String[] directoryFileExtension = nameExtension(absolutePath);
		String directory = directoryFileExtension[0];
		String file = directoryFileExtension[1];
		String extension = directoryFileExtension[2];
		String fullFile = directory + file + "_" + fileSufix + extension;

		// Writes the file with the given name and read extension
		File newFile = new File(fullFile);
		FileOutputStream stream = new FileOutputStream(newFile);
		stream.write(fileBytes);
		stream.close();
		return newFile;
	}

	/**
	 * <p>
	 * Corrupts t% of file with random bytes
	 * <p>
	 * 
	 * @param file the vector of the file that will be corrupted
	 * @param t    the number of errors that will be generated for every n bytes
	 * @param k    Information symbols each vector needs a separate increment
	 *             variable
	 */
	protected static void corruption(byte[] file, int t, int k) {

		SecureRandom random = new SecureRandom();
		int interactions = file.length / k;
		int remainder = file.length % k;
		int incrementData = 0;
		int incrementCorruption = 0;
		int startRmndCopy = file.length - remainder;
		int randomIndex = random.nextInt(k);
		int randomIndexCorru = 0;
		byte[] tempCorru = new byte[k];
		byte[] corruption = new byte[t];
		random.nextBytes(corruption);

		// The original vector is divided into a vector of k positions, according to the
		// number of iterations
		for (int x = 0; x < interactions; x++) {

			System.arraycopy(file, incrementData, tempCorru, 0, k);
			incrementData += k;

			// Every k symbol of the original file, corrupted t random positions of these k
			// positions
			for (int n = 0; n < t; n++) {
				randomIndexCorru = random.nextInt(corruption.length);
				System.arraycopy(corruption, randomIndexCorru, tempCorru, randomIndex, 1);
			}
			// Returns the original vector, now corrupted t random positions every k symbols
			System.arraycopy(tempCorru, 0, file, incrementCorruption, k);
			incrementCorruption += k;
		}
		if (remainder > 0) {
			byte[] remainderTempCorr = new byte[remainder];
			System.arraycopy(file, startRmndCopy, remainderTempCorr, 0, remainder);
			for (int m = 0; m < remainder; m++) {
				randomIndexCorru = random.nextInt(corruption.length);
				System.arraycopy(corruption, randomIndexCorru, file, startRmndCopy, 1);
			}
		}
	}

	/**
	 * <p>
	 * Erases the encoded, the decoded, redundancy and hash files on the disk
	 * <p>
	 * 
	 * @param encoded    The file encoded by the RS encoder
	 * @param decoded    The file decoded by the RS decoder
	 * @param hash       The hash generated in the encoder process
	 * @param redundancy The error correction symbols, stored in a file
	 * @throws ReedSolomonException: If the files are not available to be erase
	 */
	protected static void eraseFiles(String absolutePath, String decoded, String hash, String redundancy)
			throws ReedSolomonException {
		// FileLock lock = channel.lock();
		try {
			Path pathFile = Paths.get(absolutePath);
			Path pathDecoded = Paths.get(decoded);
			Path pathHash = Paths.get(hash);
			Path pathRedundancy = Paths.get(redundancy);

			Files.delete(pathFile);
			Files.delete(pathDecoded);
			Files.delete(pathHash);
			Files.delete(pathRedundancy);
			System.out.println("The files has been successful erased" + "\n");

		} catch (Exception e) {
			throw new ReedSolomonException("Error while erasing files");
		}
	}

	/**
	 * <p>
	 * Transform a 16-bit string into a 8-bit string 
	 * <p>
	 * 
	 * @param redundancyM16 The n-k redundancy symbols from the m = 16 RS
	 * @return binary8 The decimal transform of the MSB and LSB binary simbols, in
	 *         the first half the MSB integer representation, and on the half + 1
	 *         the LSB integer representation of the 16-bin
	 */

	protected static int[] bin16ToBin8(int[] redundancyM16) {
		int[] binary8 = new int[redundancyM16.length * 2];
		int temp = 0;
		String[] binary16 = new String[redundancyM16.length]; // int to bin
		for (int i = 0; i < redundancyM16.length; i++) {
			temp = redundancyM16[i];
			String bin = Integer.toBinaryString(0x10000 | temp).substring(1); // the binary will always be of length 16
			binary16[i] = bin;
		}
		String left = "";
		String right = "";
		int msb = 0;
		int lsb = 0;
		int incrementLSB = binary8.length / 2;
		for (int i = 0; i < binary16.length; i++) {
			// Each variable will hold a part of the 16-bit number
			// Left will receive the MSB part and right the LSB
			left = binary16[i].substring(0, 8);
			right = binary16[i].substring(8, 16);

			// Here the 8 digit binary is transform into a integer number
			msb = Integer.parseInt(left, 2);
			lsb = Integer.parseInt(right, 2);

			// The array contain the MSB part of the 16 digit binary number on the 0th
			// position to the half and the LSB part on the half + 1 to the the end of the
			// array
			binary8[i] = msb;
			binary8[incrementLSB] = lsb;
			incrementLSB += 1;
		}
		return binary8;

	}

	/**
	 * <p>
	 * Transform a 8-bit integer into a 16-bit integer. Concatenating the first half
	 * with the second half i.e, padding the MSB part with the LSB part of the
	 * 16-bit, and then transform the resulting binary number into a integer number
	 * <p>
	 * 
	 * @param redudancy The array containing the integer redundancy symbols
	 * @return intM16 The transformed integer redundancy symbols from 16-bit binary
	 *         number
	 */
	protected static int[] intM16(int[] redudancy) {
		String[] binary = FileHandle.intToBin(redudancy);
		String left = "";
		int rightControl = binary.length / 2;
		String right = "";
		String[] leftRight = new String[binary.length / 2];
		int[] intM16 = new int[binary.length / 2];
		for (int i = 0; i < binary.length / 2; i++) {
			left = binary[i];
			right = binary[rightControl];
			rightControl += 1;
			leftRight[i] = left + right; // pad the MSB with the LSB
			intM16[i] = Integer.parseInt(leftRight[i], 2); // string to int
		}
		return intM16;
	}

	/**
	 * <p>
	 * Transform a single integer into a 8-bit binary number
	 * <p>
	 * 
	 * @param redundancy The array containing the integer redundancy symbols
	 * @return binary String array containing the 8-bit binary number
	 */
	protected static String[] intToBin(int[] redundancy) {
		int temp = 0;
		String[] binary = new String[redundancy.length];
		for (int i = 0; i < redundancy.length; i++) {
			temp = redundancy[i];
			String bin = Integer.toBinaryString(0x100 | temp).substring(1); // 8-bit binary number
			binary[i] = bin;
		}
		return binary;
	}
}