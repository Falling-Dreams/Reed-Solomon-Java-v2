package reedSolomonV2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.regex.Pattern;

public class ManipulacaoArquivos {

	public static void main(String[] args) throws IOException {

		String localAbsolutoArquivo = "Z:\\@desenvolvimento\\workspace\\Testes-com-RS-GF(2^16)\\GPEs em atividade.pdf";
		File f = new File(localAbsolutoArquivo);
		
		// Recupera nome e extensao do arquivo
		String arquivoComExtensao = "";
		int in = f.getAbsolutePath().lastIndexOf("\\");
		if (in > -1) {
			arquivoComExtensao = f.getAbsolutePath().substring(in + 1);			
		}
		//Recupera o diretorio onde o arquivo esta armazenado
		String diretorio = localAbsolutoArquivo.replace(arquivoComExtensao, "");		
		
		//Recupera nome e extensao do arquivo
		String[] separaNomeDaExtensao = arquivoComExtensao.split("[.]");
		String arquivo = separaNomeDaExtensao[0];
		String extensao = separaNomeDaExtensao[1];
		
		//Armazena diretorio, nome do arquivo e extensao do arquivo em um array de strings
		String[] diretorioArquivoExtensao = new String[3];
		diretorioArquivoExtensao[0] = diretorio;
		diretorioArquivoExtensao[1] = arquivo;
		diretorioArquivoExtensao[2] = extensao;
		
	}

}
