package reedSolomonV2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class Sandbox {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		String localAbsolutoArquivo = "Z:\\@desenvolvimento\\workspace\\Testes-com-RS-GF(2^16)\\Q6_v2.txt";			
		Path path = Paths.get(localAbsolutoArquivo);
		byte[] bytesArquivo = Files.readAllBytes(path);
		int qtdSimbolosArquivo = bytesArquivo.length;
		int n = 255, k = 177;
		int qtdIteracoesRS8 = qtdSimbolosArquivo/k + 1;
		int num_input;
	    int NumRS[]=new int[qtdSimbolosArquivo]; //vetor que armazena os bytes convertidos em unsigned
	    int contador = 0;
	    
        int[] data = new int[] {54,65,102,187,175,78,214,125,175,2,47,39,85,67,17,97,178,136,194,201,217,7,96,81,78,228,
        250,21,247,108,54,65,102,187,175,78,214,125,175,2,47,39,85,67,17,97,178,136,194,201,217,7,96,81,78,228,250,21,247,
        108,54,65,102,187,175,78,214,125,175,2,47,39,85,67,17,97,178,136,194,201,217,7,96,81,78,228,250,21,247,108,54,65,102,
        187,175,78,214,125,175,2,47,39,85,67,17,97,178,136,194,201,217,7,96,81,78,228,250,21,247,108};
        
        int[][] resultado = Sandbox.splitArray(data, 4);
        		
        System.out.println(Arrays.toString(resultado));
	    
;
	 
		/*
        for(int i = 0; i < qtdSimbolosArquivo; i++){ // A saida tera numeros negativos pq em Java byte é signed!
            //System.out.println("\nValor i: "+i);
            if (bytesArquivo[i]>=0){
                //System.out.println("\nByte: " +ByteArquivo[i]); // Mostra em Unsigned Bytes
                num_input=bytesArquivo[i];
                NumRS[i]=num_input;
                System.out.println("\nNum " +i + ": " +NumRS[i]); // Mostra em Unsigned Bytes
            }else{
                //System.out.println("\nByte: " +ByteArquivo[i]); // Mostra em Unsigned Bytes
                num_input=bytesArquivo[i];
                num_input=num_input+256;
                NumRS[i]=num_input;
                System.out.println("\nNum " +i + ": " +NumRS[i]); // Mostra em Unsigned Bytes
            }
        }*/
        

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
