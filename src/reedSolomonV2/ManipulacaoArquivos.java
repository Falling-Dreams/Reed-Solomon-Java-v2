package reedSolomonV2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import org.apache.commons.lang3.ArrayUtils;

public class ManipulacaoArquivos {
	

	public static void main(String[] args) throws IOException {

		String localAbsolutoArquivo = "Z:\\@desenvolvimento\\workspace\\Testes-com-RS-GF(2^16)\\IEEE-Conference-A4-format.pdf";
		final int K_MAX = 61440;		
		Path path = Paths.get(localAbsolutoArquivo);
		byte[] dado = Files.readAllBytes(path);
		int qtdSimbolos = dado.length;
		final int QTD_ITERACOES = qtdSimbolos/K_MAX;
		
		if(QTD_ITERACOES <= 1) {
			//Caso em que a quantidade de simbolos é igual ou menor a 1, apenas uma iteracao
			//Metodo ou classe para uma iteracao
			System.out.println(QTD_ITERACOES);
		}
		else if(QTD_ITERACOES >= 1) {
			//Quantidade de simbolos e superior a 61440, a rotina sera feita para cada 61440
			//Metodo ou classe para mais de uma iteracao
			System.out.println(QTD_ITERACOES);
		}
		
		
			
		
		/*
		// Transforma o vetor de duas dimensoes em uma arraylist de uma dimensao
		ArrayList<Integer[]> lista = new ArrayList<Integer[]>();		
		Integer[] newArray = new Integer[vetorBinario.length];		
		//System.out.println("2D para 1D: " + resultado);		
		System.out.println("Depois da Concatenacao(A): " + Arrays.toString(newArray) + "\n"); */

	}

	// Concatenar dois bytes em um, a partir do seu valor em binario
	public static void concatenaBytes(int[][] vetorDeBinarios) {
		// Tamanho do vetor = numero de linhas do vetor
		int tamanhoDoVetor = vetorDeBinarios.length;

		if (tamanhoDoVetor % 2 == 0) {
			// int bytesConcatenados[][] = new int[tamanhoDoVetor/2][16];
			int meioDoVetorPar = (int) Math.floor(tamanhoDoVetor / 2);
			int[][] A = new int[meioDoVetorPar][16];
			int[][] B = new int[tamanhoDoVetor - meioDoVetorPar][16];
			System.arraycopy(vetorDeBinarios, 0, A, 0, meioDoVetorPar);
			System.arraycopy(vetorDeBinarios, meioDoVetorPar, B, 0, tamanhoDoVetor - meioDoVetorPar);

		} else {
			// int bytesConcatenados[][] = new int[(tamanhoDoVetor + 1)/2][16];
			int meioDoVetorImpar = (int) Math.floor((tamanhoDoVetor / 2) + 1);
			int[][] A = new int[meioDoVetorImpar][16];
			int[][] B = new int[(tamanhoDoVetor - meioDoVetorImpar) + 1][16];
			System.arraycopy(vetorDeBinarios, 0, A, 0, meioDoVetorImpar);
			System.arraycopy(vetorDeBinarios, meioDoVetorImpar, B, 0, ((tamanhoDoVetor - meioDoVetorImpar) + 1));
		}

		int k = 0;

		for (int i = 0; i < tamanhoDoVetor; i++) {
			if (tamanhoDoVetor % 2 == 0) {
				int bytesConcatenados[][] = new int[tamanhoDoVetor / 2][16];

			} else {
				// Concatenar a ultima iteracao a particao B com a particao A
				int bytesConcatenados[][] = new int[(tamanhoDoVetor + 1) / 2][16];

			}
		}

		// return bytesConcatenados;
	}

	public static int[][] bitsArquivo(String fileName) throws IOException {
		Path path = Paths.get(fileName);
		byte[] data = Files.readAllBytes(path);
		int size = data.length;
		System.out.println("Tamanho do arquivo: " + size + " bytes");

		int r[] = new int[8];
		int q[] = new int[8];
		int bin[] = new int[8];
		int j, l, num_input;

		int[][] arrayBits = new int[size][8];
		int m = 0;

		for (int i = 0; i < size; i++) {
			// Se o valor em inteiro do byte for maior ou igual a zero, nao precisa somar
			// 256
			if (data[i] >= 0) {
				// Mostra em Unsigned Bytes
				// System.out.println("\nByte: " + data[i]);
				num_input = data[i];
				// preenche o vetor com valores binarios
				for (j = 0; j <= 7; j++) {
					q[j] = num_input / 2;
					r[j] = num_input % 2;
					num_input = q[j];
				}
				// organiza o vetor de tras para frente (MSB)
				int k = 7;
				for (j = 0; j <= 7; j++) {
					bin[k] = r[j];
					k--;
				}
				// imprime o valor binario do byte em MSB
				System.out.println("Bits de Dados:      ");
				for (l = 0; l <= 7; l++) {
					System.out.print(bin[l]);
				}
				System.out.println("\n");
				for (l = 0; l <= 7; l++) {
					arrayBits[i][l] = bin[l];

				}

			} else {
				num_input = data[i];
				num_input = num_input + 256;
				// System.out.println("\nByte: " + num_input); // Mostra em Unsigned Bytes
				// preenche o vetor com valores binarios
				for (j = 0; j <= 7; j++) {
					q[j] = num_input / 2;
					r[j] = num_input % 2;
					num_input = q[j];
				}
				// organiza o vetor de tras para frente
				int k = 7;
				for (j = 0; j <= 7; j++) {
					bin[k] = r[j];
					k--;
				}
				System.out.println("Bits de Dados:      ");
				for (l = 0; l <= 7; l++) {
					System.out.print(bin[l]);
				}
				System.out.println("\n");
				for (l = 0; l <= 7; l++) {
					arrayBits[i][l] = bin[l];

				}
			}
		}

		return arrayBits;
	}

}
