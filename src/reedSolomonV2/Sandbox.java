package reedSolomonV2;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.smartcardio.CardException;

import reedSolomonV2.ReversibleDegradation;

public class Sandbox {

	public static void main(String[] args)
			throws NoSuchAlgorithmException, IOException, ReedSolomonException, CardException {
		
		String bit = "0";
		bit = String.format("%016d", Integer.parseInt(bit));
		int[] n = new int[4];
		n[0] = 54544;
		n[1] = 12454;
		n[2] = 9897;
		n[3] = 39745;
		
		int m = 0;
		String[] convertido = new String[n.length];
		
		for (int i = 0; i < n.length; ++i) {
			m = n[i];
			convertido[i] = String.format("%16s", Integer.toBinaryString(m)).replaceAll(" ", "0");			
		}
		
		// converter um binario de 16 posicoes para dois bytes signed de 8 posicoes
		String[] temp = new String[1];
		String[] temp2 = new String[16];
		String temp3 = "";
		String esquerda = "";
		String direita = "";
		int left = 0;
		int right = 0;
		int[] fromBinaryLeft = new int[convertido.length];
		int[] fromBinaryRight = new int[convertido.length];
		int[] leftRight = new int[convertido.length * 2];
		int incremento = 0;
		
		for (int i = 0; i < convertido.length; ++i) {
			temp3 = convertido[i];
			esquerda = temp3.substring(0,8);
			direita = temp3.substring(8,16);
			left = Integer.parseInt(esquerda,2);
			right = Integer.parseInt(direita,2);
			System.out.println(left);
			//System.out.println(right);
			fromBinaryLeft[i] = left;
			fromBinaryRight[i] = right;
			
		}
		
		for (int l = 0; l < fromBinaryLeft.length; l++) {
			System.arraycopy(fromBinaryLeft, incremento, leftRight, incremento, 1);
			incremento += 1;
			//System.arraycopy(fromBinaryRight[l+1], incremento, leftRight, incremento, 1);
			//incremento += 1;
		
		}
		
		System.out.println(Arrays.toString(leftRight));
		
	}

	

}
