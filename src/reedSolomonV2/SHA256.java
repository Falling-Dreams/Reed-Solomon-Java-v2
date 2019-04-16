package reedSolomonV2;

import java.io.File;
import java.io.FileOutputStream;
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
	o que faz? Verifica se o hash gerado na codificaco eh o mesmo do cartao que deseja decodificar o arquivo
	Parametros: String hashGravadoCodificacao o local onde o hash gravado na codificacao se encontra, 
	byte[] uidCartao o uid do cartao candidato a decodificacao
	Retorna: boolean true se o cartao que deseja efetuar a decodificacao foi o mesmo que codificou
		
*/

public class SHA256 {

	protected byte[] sha256(byte[] uidCartao) throws NoSuchAlgorithmException {
		MessageDigest mDigest = MessageDigest.getInstance("SHA-256");
		byte[] result = mDigest.digest(uidCartao);

		return result;
	}

	public boolean verificaChecksum(String hashGravadoCodificacao, byte[] uidCartao)
			throws NoSuchAlgorithmException, IOException {
		Path path = Paths.get(hashGravadoCodificacao);
		byte[] bytesArquivo = Files.readAllBytes(path);
		byte[] hashGerado = sha256(uidCartao);

		return Arrays.equals(bytesArquivo, hashGerado);
	}
	/*
	protected File gravarHash(byte[] sha256Gerado, String localAbsoluto) throws IOException {

		String[] diretorioArquivoExtensao = ManipularArquivoM8.recuperoDiretorioNomeExtensao(localAbsoluto);
		String diretorio = diretorioArquivoExtensao[0];
		String arquivo = diretorioArquivoExtensao[1];
		String extensao = ".txt";
		String arquivoCompleto = diretorio + arquivo + "_" + "Hash_Codificacao" + extensao;

		// Grava o arquivo com o nome fornecido e a extensao lida
		File newFile = new File(arquivoCompleto);
		FileOutputStream stream = new FileOutputStream(newFile);
		stream.write(sha256Gerado);
		stream.close();
		return newFile;
	}*/

}
