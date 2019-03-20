package reedSolomonV2;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class Sandbox {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		//String localAbsoluto = "/storage/emulated/0/Download/images.jpeg";
		String localAbsoluto = "Z:\\@desenvolvimento\\workspace\\Testes-com-RS-GF(2^16)\\UVERworld_Colors_of_the_Heart.mp3";
		String[] str = recuperoDiretorioNomeExtensao(localAbsoluto);
		System.out.println(Arrays.toString(str));
		
        
	}
	
	private static String[] recuperoDiretorioNomeExtensao(String localAbsoluto) {
		File f = new File(localAbsoluto);

		// Recupera nome e extensao do arquivo
		String arquivoComExtensao = "";
		int in = f.getAbsolutePath().lastIndexOf("\\");
		if (in > -1) {
			arquivoComExtensao = f.getAbsolutePath().substring(in + 1);
		}
		// Recupera o diretorio onde o arquivo esta armazenado
		String diretorio = localAbsoluto.replace(arquivoComExtensao, "");

		// Separa nome e extensao
		int indiceExtensao = arquivoComExtensao.lastIndexOf('.');
		int tamanhoNomeExtensao = arquivoComExtensao.length();		
		String extensao = arquivoComExtensao.substring(indiceExtensao, tamanhoNomeExtensao);
		String arquivo = arquivoComExtensao.substring(0, indiceExtensao);	

		// Armazena diretorio, nome do arquivo e extensao do arquivo em um array de
		// strings
		String[] diretorioArquivoExtensao = new String[3];
		diretorioArquivoExtensao[0] = diretorio;
		diretorioArquivoExtensao[1] = arquivo;
		diretorioArquivoExtensao[2] = extensao;

		return diretorioArquivoExtensao;
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