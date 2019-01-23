package reedSolomonV2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class Sandbox {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		String localAbsoluto = "Z:\\@desenvolvimento\\workspace\\Testes-com-RS-GF(2^16)\\Novo Documento de Texto.txt";
		Path path = Paths.get(localAbsoluto);
		byte[] dado = Files.readAllBytes(path);
		byte[] totalVetoresRS = new byte[dado.length];
		int qtdSimbolos = dado.length;
		int n = 255, k = 177, t = 39, qtdSimbolosCorrecao = 78, srcPos = 0, srcPos2 = 0;
		int qtdIteracoesRS = qtdSimbolos/k;
		int restoVetorRS = qtdSimbolos % k;
		
		
		for(int i = 0; i < qtdIteracoesRS; i++) {
			byte[] vetorRS8 = new byte[255];
			System.arraycopy(dado, srcPos, vetorRS8, 0, k);
			srcPos += 177;
						
			// os k simbolos codificados pelo RS8 a cada iteracao sao armazenados em um unico vetor - PROBLEMA			
			System.arraycopy(vetorRS8, 0, totalVetoresRS, srcPos2, k);
			srcPos2 += 177;
			
			
			if(restoVetorRS > 0) {
				System.arraycopy(dado, (qtdSimbolos - restoVetorRS), vetorRS8, 0, restoVetorRS);
				System.arraycopy(vetorRS8, 0, totalVetoresRS, srcPos, restoVetorRS);
			}
		}
		System.out.println(Arrays.toString(dado));
		System.out.println(Arrays.toString(totalVetoresRS));
		System.out.println(Arrays.equals(dado, totalVetoresRS));
		
        
	}
	
	
	public static byte[] subVetor(byte[] vetor) {
		int vetorComeco = 0;
	    int vetorFinal = 177;
	    
	    byte[] vetorCortado = new byte[255];
	    System.arraycopy(vetor, 0, vetorCortado, 0, 177);
		
		return vetorCortado;
	}
	
	public static int[][] splitArray(int[] arrayToSplit, int chunkSize){
	    if(chunkSize<=0){
	        return null;  // just in case :)
	    }
	    // first we have to check if the array can be split in multiple 
	    // arrays of equal 'chunk' size
	    int rest = arrayToSplit.length % chunkSize;  // if rest>0 then our last array will have less elements than the others 
	    // then we check in how many arrays we can split our input array
	    int chunks = arrayToSplit.length / chunkSize + (rest > 0 ? 1 : 0); // we may have to add an additional array for the 'rest'
	    // now we know how many arrays we need and create our result array
	    int[][] arrays = new int[chunks][];
	    // we create our resulting arrays by copying the corresponding 
	    // part from the input array. If we have a rest (rest>0), then
	    // the last array will have less elements than the others. This 
	    // needs to be handled separately, so we iterate 1 times less.
	    for(int i = 0; i < (rest > 0 ? chunks - 1 : chunks); i++){
	        // this copies 'chunk' times 'chunkSize' elements into a new array
	        arrays[i] = Arrays.copyOfRange(arrayToSplit, i * chunkSize, i * chunkSize + chunkSize);
	    }
	    if(rest > 0){ // only when we have a rest
	        // we copy the remaining elements into the last chunk
	        arrays[chunks - 1] = Arrays.copyOfRange(arrayToSplit, (chunks - 1) * chunkSize, (chunks - 1) * chunkSize + rest);
	    }
	    return arrays; // that's it
	}
}