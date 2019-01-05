package reedSolomonV2;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.*;

public class ManipularArquivo {

	private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
	int[] byteParaInt = new int[1800];

	public static void main(String[] args) throws IOException, ReedSolomonException {

		// k=1656; n=1856; n-k=200
		// k=1656; n=2056; n-k=400
		//Codificado ainda continua legivel pelo windows
		GenericGF gf = new GenericGF(69643, 65536, 1);
		ManipularArquivo manipulacao = new ManipularArquivo();

		// Strings com os locais dos arquivos a serem gravados ou manipulados
		String arquivoLocal = "Z:\\@desenvolvimento\\workspace\\Testes-com-RS-GF(2^16)\\Q6_v2.txt";

		String arquivoNome = "Z:\\@desenvolvimento\\workspace\\Testes-com-RS-GF(2^16)\\Codificado";

		String redundanciaNome = "Z:\\@desenvolvimento\\workspace\\Testes-com-RS-GF(2^16)\\redundancia.rdg";

		String arquivoDecodificadoLocal = "Z:\\@desenvolvimento\\workspace\\Testes-com-RS-GF(2^16)Codificado.txt";

		String arquivoDecodificadoNome = "Z:\\@desenvolvimento\\workspace\\Testes-com-RS-GF(2^16)\\Decodificado";

		// byte[] arquivoLidoEmBytes = manipulacao.bytesArquivo(arquivoLocal);
		int[] data = manipulacao.byteSignedParaUnsigned(arquivoLocal, 400);

		System.out.println("Quantidade de simbolos: " + "\n" + data.length);
		
		//Codificacao
		ReedSolomonEncoder encoder = new ReedSolomonEncoder(gf);
		System.out.println("Antes da codificacao: " + "\n" + Arrays.toString(data));
		encoder.encode(data, 400);
		System.out.println("Depois da codificacao: " + "\n" + Arrays.toString(data));

		// Guarda a redundancia no vetor redundanciaInt
		int temp = 0;
		int ndP = 0;
		int[] redundanciaInt = new int[400]; // tamanho do vetor = n-k
		for (int x = 1656; x < 2056; x++) {
			temp = data[x];
			redundanciaInt[ndP] = temp;
			ndP++;
		}
		// Apaga redundancia
		for (int x = 1656; x < 2056; x++) {
			data[x] = 0;
		}

		System.out.println("Novo vetor de dados sem a redundancia: " + "\n" + Arrays.toString(data));
		System.out.println("Redundancia: " + "\n" + Arrays.toString(redundanciaInt));

		// Transformar de int[] para byte[] e guardar o arquivo codificado
		// Guardar a redundancia em um arquivo sem converter de int[] para byte[]
		byte[] codificado = manipulacao.byteUnsignedParaSigned(data);
		manipulacao.gravarRedundancia(redundanciaInt, redundanciaNome);
		System.out.println("Volta do vetor de dados de byte para int: " + "\n" + Arrays.toString(codificado));
		manipulacao.gravaArquivo(codificado, arquivoLocal, arquivoNome);

		// Transformar de byte[] para int[] e corromper
		int[] voltaDeByte = manipulacao.byteSignedParaUnsignedSemParidade(
				"Z:\\@desenvolvimento\\workspace\\Testes-com-RS-GF(2^16)\\Codificado.txt");

		// Corrupcao
		int i, k = 0;
		for (i = 0; i < 200; i++) {
			voltaDeByte[i] = 1;
		}
		/*
		//Decodificacao
		ReedSolomonDecoder decoder = new ReedSolomonDecoder(gf);

		System.out.println("Corrupcao: " + "\n" + Arrays.toString(voltaDeByte));

		// Concatenando dado com redudancia
		for (int x = 1656; x < 2056; x++) {
			voltaDeByte[x] = redundanciaInt[k];
			k++;
		}

		System.out.println("Vetor de dados concatenado com redundancia: " + "\n" + Arrays.toString(voltaDeByte));

		decoder.decode(voltaDeByte, 400);

		System.out.println("Mensagem corrigida: " + "\n" + Arrays.toString(voltaDeByte));

		// gravo o arquivo decodificado
		byte[] decodificado = manipulacao.byteUnsignedParaSigned(voltaDeByte);
		manipulacao.gravaArquivo(decodificado, arquivoDecodificadoLocal, arquivoDecodificadoNome);
		*/
	}

	// Gravar os bytes de um arquivo em um vetor de bytes
	private byte[] lerBytesArquivo(String fileName) throws IOException {
		Path path = Paths.get(fileName);
		byte[] data = Files.readAllBytes(path);
		return data;
	}

	// Transformar os bytes de um arquivo lido de SIGNED [-128 a 127] em UNSIGNED [0
	// a 255] e escrever em um vetor de inteiros
	// e acrescentar n-k posicoes no vetor devolvido, pois, o bloco n do RS e
	// composto por k(informacao) e n-k(paridade)
	// Usar APENAS na primeira leitura do arquivo para vetor de bytes
	private static int[] byteSignedParaUnsigned(String fileName, int qtdSimbolosParidade) throws IOException {
		Path path = Paths.get(fileName);
		byte[] bytesArquivoLido = Files.readAllBytes(path);
		int tamanhoVetorInt = bytesArquivoLido.length;
		int valorEmIntDoByte = 0;
		int[] vetorDevolvido = new int[tamanhoVetorInt + qtdSimbolosParidade];

		// Bytes em java representam inteiros em complemento de dois, a soma e feita nos
		// bytes com valores negativos
		// Pois, o RS opera com numeros inteiros e positivos
		for (int i = 0; i < tamanhoVetorInt; i++) {
			valorEmIntDoByte = bytesArquivoLido[i];
			if (valorEmIntDoByte < 0) {
				valorEmIntDoByte = valorEmIntDoByte + 256;
			}
			vetorDevolvido[i] = valorEmIntDoByte;
		}
		return vetorDevolvido;
	}

	// Transformar os bytes de um arquivo lido de SIGNED [-128 a 127] em UNSIGNED [0
	// a 255] e escrever em um vetor de inteiros
	// Usar esse metodos para as demais conversoes que nao a primeira
	private static int[] byteSignedParaUnsignedSemParidade(String fileName) throws IOException {
		Path path = Paths.get(fileName);
		byte[] bytesArquivoLido = Files.readAllBytes(path);
		int tamanhoVetorInt = bytesArquivoLido.length;
		int valorEmIntDoByte = 0;
		int[] vetorDevolvido = new int[tamanhoVetorInt];

		// Bytes em java representam inteiros em complemento de dois, a soma e feita nos
		// bytes com valores negativos
		// Pois, o RS opera com numeros inteiros e positivos
		for (int i = 0; i < tamanhoVetorInt; i++) {
			valorEmIntDoByte = bytesArquivoLido[i];
			if (valorEmIntDoByte < 0) {
				valorEmIntDoByte = valorEmIntDoByte + 256;
			}
			vetorDevolvido[i] = valorEmIntDoByte;
		}
		return vetorDevolvido;
	}

	// Metodo para conversao de int[] para byte[] apenas em inteiros com sinal e
	// oito bits (complemento de 2 do Java)
	// Metodo para transformar um byte UNSIGNED em SIGNED
	private static byte[] byteUnsignedParaSigned(int[] vetorIntUnsigned) throws IOException {
		int valorEmIntDoByte = 0;
		int tamanho = vetorIntUnsigned.length;
		byte[] novoVetorBytes = new byte[tamanho];
		for (int i = 0; i < novoVetorBytes.length; ++i) {
			valorEmIntDoByte = vetorIntUnsigned[i];
			novoVetorBytes[i] = byteParaInt(valorEmIntDoByte);
		}
		return novoVetorBytes;
	}

	// Metodo auxiliar para transformar um unico inteiro unsigned em inteiro signed
	// apenas em inteiro
	// com sinal e oito bits (complemento de 2 do Java)
	private static byte byteParaInt(int intByte) {
		byte valorDoByteEmInt = 0;
		if (intByte <= 256) {
			valorDoByteEmInt = (byte) intByte;
			if (valorDoByteEmInt > 127) {
				valorDoByteEmInt = (byte) (valorDoByteEmInt - 256);
			}
		}
		return valorDoByteEmInt;
	}

	// Gravar um arquivo
	private File gravaArquivo(byte[] convertidoDeIntArray, String localDoArquivo, String nomeDoArquivo)
			throws IOException {
		File f = new File(localDoArquivo);
		// Recuperar extensao do arquivo
		String extensao = "";
		int in = f.getAbsolutePath().lastIndexOf(".");
		if (in > -1) {
			extensao = f.getAbsolutePath().substring(in + 1);
		}
		// Grava o arquivo com o nome fornecido e a extensao lida
		File newFile = new File(nomeDoArquivo + "." + extensao);
		FileOutputStream stream = new FileOutputStream(newFile);
		stream.write(convertidoDeIntArray);
		stream.close();
		return newFile;
	}

	// Gravar a redundancia em um arquivo
	private void gravarRedundancia(int[] vetorRedundancia, String localComNomeArquivo) throws IOException {
		OutputStream os = new FileOutputStream(localComNomeArquivo);
		for (int i = 0; i < vetorRedundancia.length; i++) {
			os.write(vetorRedundancia[i]);
		}
		os.close();
	}

	// Percorrer o vetor de dados e guardar a redundancia

	// Corromper n-k posicoes do vetor de dados

	// Concatenar o vetor de redundancia com o vetor de dados

	// Corromper x posicoes do vetor de dados e guardar o arquivo

	/*
	 * Nao tenho certeza sobre esse metodo, talvez uma tentativa sem sucesso de
	 * conversao //Converter de int[] para byte[] private static void
	 * converteDeIntParaByte(int[] values) throws IOException {
	 * ByteArrayOutputStream baos = new ByteArrayOutputStream(); DataOutputStream
	 * dos = new DataOutputStream(baos); // int num_input = 0; int size =
	 * values.length; int[] dados = new int[size];
	 * 
	 * for (int i = 0; i < dados.length; ++i) { dos.writeInt(dados[i]); }
	 * baos.toByteArray(); }
	 */

	/*
	 * private static String[] bytesParaHex(byte[] dados){ byte num_input = 0; int
	 * size = dados.length; String[] dadosConvertidos = new String[size]; for (int i
	 * = 0; i < dados.length; ++i) { num_input = dados[i]; dadosConvertidos[i] =
	 * bytesToHex4(num_input); } return dadosConvertidos; }
	 * 
	 * private static StringBuffer bytesToHex(byte bytes){ StringBuffer buffer = new
	 * StringBuffer(); buffer.append(Character.forDigit((bytes >> 4) & 0xF, 16));
	 * buffer.append(Character.forDigit((bytes & 0xF), 16)); return buffer; }
	 * 
	 * //Transformar byte[] para hexadecimal[] private static String
	 * bytesToHex2(byte hashInBytes) { StringBuilder sb = new StringBuilder();
	 * sb.append(String.format("%02x", hashInBytes)); return sb.toString(); }
	 * 
	 * @SuppressWarnings("resource") private static String bytesToHex3(byte
	 * byteUnitario){ Formatter formatter = new Formatter();
	 * formatter.format("%02x", byteUnitario); String hex = formatter.toString();
	 * return hex; }
	 * 
	 * private static String bytesToHex4(byte byteUnitario){ int j = 0; char[]
	 * hexChars = new char[2]; int v = byteUnitario & 0xFF; hexChars[j * 2] =
	 * hexArray[v >>> 4]; hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	 * 
	 * return new String(hexChars); }
	 */

}
