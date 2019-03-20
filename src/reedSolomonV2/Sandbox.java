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
	

}