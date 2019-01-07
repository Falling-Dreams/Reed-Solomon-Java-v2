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
		//Separa nome e extensao do arquivo
		String[] separaNomeDaExtensao = arquivoComExtensao.split("[.]");
		String arquivo = separaNomeDaExtensao[0];
		String extensao = separaNomeDaExtensao[1];
		
		// Coleta o local onde o arquivo esta localizado
		String[] separaDiretoriosArquivo = localAbsolutoArquivo.split(Pattern.quote("\\"));
		String parte1 = separaDiretoriosArquivo[0];
		String parte2 = separaDiretoriosArquivo[1];
		String parte3 = separaDiretoriosArquivo[2];
		String parte4 = separaDiretoriosArquivo[3];
		String parte5 = separaDiretoriosArquivo[4];
		String diretorioRaiz = parte1 + "\\" + parte2 + "\\" + parte3 + "\\" + parte4 + "\\";
		String arquivoCodificado = diretorioRaiz + arquivo + "_" + "Codificado" +  "." + extensao;

		System.out.println(diretorioRaiz + arquivo + "_" + "Codificado" +  "." + extensao);
		//System.out.println(arquivo);
		//System.out.println(extensao);

	}

}
