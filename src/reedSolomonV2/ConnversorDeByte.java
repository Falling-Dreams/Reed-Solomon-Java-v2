package reedSolomonV2;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class ConnversorDeByte  {

	// Metodo para conversao de byte[] para int[]
	public static int[] convertToIntArray(byte[] input) {
		int[] ret = new int[input.length];
		for (int i = 0; i < input.length; i++) {
			ret[i] = input[i] & 0xff; // range 0 a 255
			//((myArray[0] & 0xff) << 8) + (myArray[1] & 0xff)
		}
		return ret;		
	}

	public static int byteArrayToLeInt(byte[] b) {
		final ByteBuffer bb = ByteBuffer.wrap(b);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		return bb.get();
	}
	

	// Metodo para conversao de int[] para byte[]
	public static byte[] convertToByteArray(int[] values) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		for (int i = 0; i < values.length; ++i) {
			dos.writeInt(values[i]);
		}
		return baos.toByteArray();
	}

}
