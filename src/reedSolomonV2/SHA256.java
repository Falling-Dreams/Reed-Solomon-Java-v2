package reedSolomonV2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/*
 	Metodo sha256
	o que faz? Gera o SHA256 dos bytes informados
	Parametros: byte[] input, vetor de bytes atraves do qual deseja-se gerar o hash
	Retorna: byte[] result um vetor de bytes contendo o sha256 do vetor informado
	
	Metodo verificaChecksum
	o que faz? Verifica a integridade de um arquivo
	Parametros: String localAbsoluto uma string contendo o local absoluto do arquivo, 
	byte[] testChecksum o hash a ser verificado do arquivo
	Retorna: boolean true se o hash informado for o mesmo do arquivo; false caso contrario
		
*/

public class SHA256 {

	/*public static void main(String[] args) throws NoSuchAlgorithmException, IOException {

		String localAbsoluto = "Z:\\@Projeto-Degradacao-Corretiva\\Testes-com-RS-GF(2^16)\\UVERworld_Colors_of_the_Heart.mp3";
		Path path = Paths.get(localAbsoluto);
		byte[] dado = Files.readAllBytes(path);
		byte[] hashDado = sha256(dado);

		boolean result = verificaChecksum(localAbsoluto, hashDado);
		System.out.println("O hash e o mesmo? " + result);

	}*/

	protected byte[] sha256(byte[] input) throws NoSuchAlgorithmException {
		MessageDigest mDigest = MessageDigest.getInstance("SHA-256");
		byte[] result = mDigest.digest(input);

		return result;
	}

	public boolean verificaChecksum(String localAbsoluto, byte[] testChecksum)
			throws NoSuchAlgorithmException, IOException {
		Path path = Paths.get(localAbsoluto);
		byte[] bytesArquivo = Files.readAllBytes(path);
		byte[] hashGerado = sha256(bytesArquivo);

		return Arrays.equals(hashGerado, testChecksum);
	}

}
