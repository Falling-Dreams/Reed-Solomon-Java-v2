package reedSolomonV2;

import reedSolomonV2.GenericGF;
import reedSolomonV2.ReedSolomonEncoder;
import java.util.Arrays;
import java.io.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

public class ReadingWritingBynaryFilesRS {

	public static void main(String[] args) throws IOException, ReedSolomonException {
		
		/*
        Para m=8:
        Parâmetro 1 --> Default Primitive Polynomial=D^8 + D^4 + D^3 + D^2 + 1; Integer Representation=285.
        Parâmetro 2 --> n=2^m = 256
        n=200; k=120; n-k=80; t=40;
        
        RS para m=8:
        Com 5% de erro:
        0.1*255 = 255-k
        k=229 Bytes
        n-k=26 Bytes de redundância
        
        Com 10% de erro:
        0.2*255 = 255-k
        k=203 Bytes
        n-k=52 Bytes de redundância
        
        Com 15% de erro:
        0.3*255 = 255-k
        k=177 Bytes
        n-k=78 Bytes de redundância
        */
        
        String fileName = "Z:\\@desenvolvimento\\workspace\\Testes-com-RS-GF(2^16)\\aula11contabcespe.pdf";

        Path path = Paths.get(fileName);
        byte[] ByteArquivo = Files.readAllBytes(path);
        int size = ByteArquivo.length;
        System.out.println("Tamanho do arquivo: " +size + " bytes");
        /*System.out.println("Signed Bytes:");
        for(int i=0;i<size;i++){ // A saida tera numeros negativos pq em Java byte é signed!
            System.out.println(ByteArquivo[i]);
        }*/
        // Mostrando os 8 bits unsigned do arquivo:
        //System.out.println("\nUnsigned Bytes:");
        int r[]=new int[8];
        int q[]=new int[8];
        int bin[]=new int[8];
        int j,l,num_input;
        int NumRS[]=new int[size];
        
        for(int i=0;i<size;i++){ // A saida tera numeros negativos pq em Java byte é signed!
            //System.out.println("\nValor i: "+i);
            if (ByteArquivo[i]>=0){
                //System.out.println("\nByte: " +ByteArquivo[i]); // Mostra em Unsigned Bytes
                num_input=ByteArquivo[i];
                NumRS[i]=num_input;
                System.out.println("\nNum " +i + ": " +NumRS[i]); // Mostra em Unsigned Bytes
            }else{
                //System.out.println("\nByte: " +ByteArquivo[i]); // Mostra em Unsigned Bytes
                num_input=ByteArquivo[i];
                num_input=num_input+256;
                NumRS[i]=num_input;
                System.out.println("\nNum " +i + ": " +NumRS[i]); // Mostra em Unsigned Bytes
            }
        }
        
        int QdeRS, cont_rs=0, Porcentagem=15;
        
        if(Porcentagem==5){
            int n=255,k_desejado=229,i ;
            QdeRS=size/k_desejado;
            System.out.println("\nQdeRS: " +QdeRS);
            for(int h=1;h<=QdeRS;h++){
                GenericGF gf = new GenericGF(285,256, 1);
                
                //int[] data = new int[] {54,65,102,187,175,78,214,125,175,2,47,39,85,67,17,97,178,136,194,201,217,7,96,81,78,228,250,21,247,108,54,65,102,187,175,78,214,125,175,2,47,39,85,67,17,97,178,136,194,201,217,7,96,81,78,228,250,21,247,108,54,65,102,187,175,78,214,125,175,2,47,39,85,67,17,97,178,136,194,201,217,7,96,81,78,228,250,21,247,108,54,65,102,187,175,78,214,125,175,2,47,39,85,67,17,97,178,136,194,201,217,7,96,81,78,228,250,21,247,108,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,};
                int[] data = new int[n];
                for(i=0;i<n;i++){
                    data[i]=0;
                }
                for(i=0;i<k_desejado;i++){
                    data[i]=NumRS[cont_rs];
                    cont_rs++;
                }
                ReedSolomonEncoder encoder = new ReedSolomonEncoder(gf);
                System.out.println("\nRS num: " +h);
                System.out.println(Arrays.toString(data)); // imprimo o input incluindo os 0 que ser�o redundandia
                encoder.encode(data, n-k_desejado); // codifica e cria 80 simbolos de redundancia
                System.out.println(Arrays.toString(data)); // imprime todos os simbolos
                // Processo de Decodificação:
                ReedSolomonDecoder decoder = new ReedSolomonDecoder(gf);
        
                for (i=0;i<40;i++){
                    data[i]=1;  // Corrompo os primeiros i simbolos com o valor 1
                }
                System.out.println(Arrays.toString(data)); // imprimo os simbolos, incluindo os corrompidos
                decoder.decode(data, 80); //faco a correcao dos simbolos corrompidos 
                System.out.println(Arrays.toString(data));
                        
            }
			
		
        }else if(Porcentagem==15){
            int n=255,k_desejado=203,i ;
            QdeRS=size/k_desejado;
            System.out.println("\nQdeRS: " +QdeRS);
            for(int h=1;h<=QdeRS;h++){
                GenericGF gf = new GenericGF(285,256, 1);
                
                //int[] data = new int[] {54,65,102,187,175,78,214,125,175,2,47,39,85,67,17,97,178,136,194,201,217,7,96,81,78,228,
                //250,21,247,108,54,65,102,187,175,78,214,125,175,2,47,39,85,67,17,97,178,136,194,201,217,7,96,81,78,228,250,21,247,
                //108,54,65,102,187,175,78,214,125,175,2,47,39,85,67,17,97,178,136,194,201,217,7,96,81,78,228,250,21,247,108,54,65,102,
                //187,175,78,214,125,175,2,47,39,85,67,17,97,178,136,194,201,217,7,96,81,78,228,250,21,247,108,0,0,0,0,0,0,0,0,0,0,0,0,
                //0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
                //0,0,0,0,0,0,0,0,0,};
                
                //Cria vetor do tamanho de n, primeiro cria um vetor com n poiscoes, todas zeradas
                int[] data = new int[n];
                for(i=0;i<n;i++){
                    data[i]=0;
                }
                // NumRs e o vetor em unsigned, cont_rs e um contador
                // Depois esse vetor e populado com os valores em bytes unsigned, as posicoes n-k continuam zeradas
                for(i=0;i<k_desejado;i++){
                    data[i]=NumRS[cont_rs];
                    cont_rs++;
                }
                
                ReedSolomonEncoder encoder = new ReedSolomonEncoder(gf);
                System.out.println("\nRS num: " +h);
                System.out.println(Arrays.toString(data)); // imprimo o input incluindo os 0 que serao redundandia
                encoder.encode(data, n-k_desejado); // codifica e cria 80 simbolos de redundancia
                System.out.println(Arrays.toString(data)); // imprime todos os simbolos
                // Processo de Decodificação:
                ReedSolomonDecoder decoder = new ReedSolomonDecoder(gf);
        
                for (i=0;i<40;i++){
                    data[i]=1;  // Corrompo os primeiros i simbolos com o valor 1
                }
                
                System.out.println(Arrays.toString(data)); // imprimo os simbolos, incluindo os corrompidos
                decoder.decode(data, 80); //faco a correcao dos simbolos corrompidos 
                System.out.println(Arrays.toString(data));
                        
            }
	
		
        }else if(Porcentagem==10){
            int n=255,k_desejado=177,i ;
            QdeRS=size/k_desejado;
            System.out.println("\nQdeRS: " +QdeRS);
            for(int h=1;h<=QdeRS;h++){
                GenericGF gf = new GenericGF(285,256, 1);
                
                //int[] data = new int[] {54,65,102,187,175,78,214,125,175,2,47,39,85,67,17,97,178,136,194,201,217,7,96,81,78,228,250,21,247,108,54,65,102,187,175,78,214,125,175,2,47,39,85,67,17,97,178,136,194,201,217,7,96,81,78,228,250,21,247,108,54,65,102,187,175,78,214,125,175,2,47,39,85,67,17,97,178,136,194,201,217,7,96,81,78,228,250,21,247,108,54,65,102,187,175,78,214,125,175,2,47,39,85,67,17,97,178,136,194,201,217,7,96,81,78,228,250,21,247,108,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,};
                int[] data = new int[n];
                for(i=0;i<n;i++){
                    data[i]=0;
                }
                for(i=0;i<k_desejado;i++){
                    data[i]=NumRS[cont_rs];
                    cont_rs++;
                }
                ReedSolomonEncoder encoder = new ReedSolomonEncoder(gf);
                System.out.println("\nRS num: " +h);
                System.out.println(Arrays.toString(data)); // imprimo o input incluindo os 0 que ser�o redundandia
                encoder.encode(data, n-k_desejado); // codifica e cria 80 simbolos de redundancia
                System.out.println(Arrays.toString(data)); // imprime todos os simbolos
                // Processo de Decodificação:
                ReedSolomonDecoder decoder = new ReedSolomonDecoder(gf);
        
                for (i=0;i<40;i++){
                    data[i]=1;  // Corrompo os primeiros i simbolos com o valor 1
                }
                System.out.println(Arrays.toString(data)); // imprimo os simbolos, incluindo os corrompidos
                decoder.decode(data, 80); //faco a correcao dos simbolos corrompidos 
                System.out.println(Arrays.toString(data));
                        
            }
        }	

	}

}
