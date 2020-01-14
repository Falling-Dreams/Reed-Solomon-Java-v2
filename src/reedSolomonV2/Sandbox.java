package reedSolomonV2;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import reedSolomonV2.ReversibleDegradation;
import reedSolomonV2.FileHandle;

public class Sandbox {

	public static void main(String[] args)
			throws NoSuchAlgorithmException, IOException, ReedSolomonException {
		
		GenericGF gf_16 = new GenericGF(19, 16, 1);
		int operacao = gf_16.multiply(10,13);
		System.out.println(operacao);
		int[] coeficientes = {1,2,3,4,5,6,7,8,9,10,11};
		int[] coeficientes1 = {1,0,0,0,0};
		GenericGFPoly gf_poli = new GenericGFPoly(gf_16, coeficientes);
		GenericGFPoly gf_poli2 = new GenericGFPoly(gf_16, coeficientes1);
		gf_poli.multiply(gf_poli2);
		System.out.println(gf_poli.multiply(gf_poli2));
		
		//ReedSolomonEncoder encoder = new ReedSolomonEncoder(gf_16);
		//GenericGFPoly polinomio = new GenericGFPoly(gf_16, coeficientes);
		//encoder.encode(coeficientes, 4);
		//System.out.println(Arrays.toString(coeficientes));

	}
	
}
