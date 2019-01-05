package reedSolomonV2;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import reedSolomonV2.GenericGF;
import reedSolomonV2.ReedSolomonEncoder;

public class ReedSolomonJava {
    
    @SuppressWarnings("null")
	public static void main(String[] args)throws Exception {
        /*
        Para m=16:
        Parametro 1 --> Default Primitive Polynomial = D^16 + D^12 + D^3 + D + 1; Integer Representation=69643.
        Parametro 2 --> n_max=2^m = 65536
        Parametro 3 --> the factor b in the generator polynomial can be 0- or 1-based (g(x) = (x+a^b)(x+a^(b+1))...(x+a^(b+2t-1))). 
        In most cases it should be 1, but for QR code it is 0.
        n=192; k=132; n-k=60; t=30;
        */
    	
    	//Criacao do GF generico
        /*GenericGF gf = new GenericGF(69643,65536, 1);
        
        //Array de dados em Int com 192 posicoes
        int[] data = new int[] {975,2784,5468,9575,9648,1576,9705,9571,4853,8002,1418,4217,9157,7922,9594,6557,357,
        		8491,9339,6787,7577,7431,3922,6554,1711,7060,318,2769,461,971,8234,6948,3170,9502,344,4387,3815,7655,
        		7951,1868,4897,4455,6463,7093,7546,2760,6797,6550,1626,1189,4983,9597,3403,5852,2238,7512,2550,5059,
        		6990,8909,9592,5472,1386,1492,2575,8407,2542,8142,2435,9292,3499,1965,2510,6160,4732,3516,8308,5852,
        		5497,9171,2858,7572,7537,3804,5678,758,539,5307,7791,9340,1299,5688,4693,119,3371,1621,7942,3112,5285,
        		1656,6019,2629,6540,6892,7481,4505,838,2289,9133,1523,8258,5383,9961,781,4426,1066,9618,46,7749,8173,
        		8686,844,3997,2598,8000,4314,9106,1818,2638,1455,1360,8692,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
        		0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        
        //Codificacao
       ReedSolomonEncoder encoder = new ReedSolomonEncoder(gf);
        // imprimo o input incluindo os 0 que serao redundancia
       System.out.println(Arrays.toString(data));
        //codifica e cria 60 simbolos de redundancia
       encoder.encode(data, 60); 
        //imprime todos os simbolos
       System.out.println(Arrays.toString(data)); 
        
        //Decodificacao
        ReedSolomonDecoder decoder = new ReedSolomonDecoder(gf);
       int i;
        //Corrompo os primeiros i simbolos com o valor 1
        for (i=0;i<30;i++){
            data[i]=1;  
        }
        //imprimo os simbolos, incluindo os corrompidos
        System.out.println(Arrays.toString(data)); 
        //faco a correcao dos simbolos corrompidos 
        decoder.decode(data, 60);
        		//"I:\\@Eng\\Noviss�mo-TCC (Agora Vai)\\" + "@Programas\\Testes-com-RS\\redundancia.jpg"); 
        System.out.println(Arrays.toString(data)); */
    	
        /*
        Para m=3:
        Parametro 1 --> Default Primitive Polynomial = D^3 + D + 1; Integer Representation=11.
        Parametro 2 --> n_max=2^m = 8
        Parametro 3 --> the factor b in the generator polynomial can be 0- or 1-based (g(x) = (x+a^b)(x+a^(b+1))...(x+a^(b+2t-1))). 
        In most cases it should be 1, but for QR code it is 0.
        n=7; k=3; n-k=4; t=2;
        */   
    	/*
    	GenericGF gf = new GenericGF(11,8, 1);
    	
    	int[] data = new int[] {7,3,2,0,0,0,0};
    	//int[] data = new int[] {8,9,12,0,0,0,0};
    	
    	ReedSolomonEncoder encoder = new ReedSolomonEncoder(gf);
        
    	
    	System.out.println("Antes da codificacao: " + "\n" + Arrays.toString(data));
    	
    	
    	encoder.encode(data, 4); 
    	    	
    	System.out.println("Depois da codificacao: " + "\n" + Arrays.toString(data));
    	
    	//Redundancia
    	int temp =0;
    	int ndP = 0;
    	int[] redundancia = new int[4]; //tamanho do vetor = n-k
    	for(int x = 3; x < 7; x++){
    		temp = data[x];
    		redundancia[ndP] = temp;
    		ndP++;
    	}
    	for(int x = 3; x < 7; x++){
    		data[x] = 0;
    	}
    	
    	System.out.println("Novo vetor de dados sem a redundancia: " + "\n" + Arrays.toString(data));
    	
    	System.out.println("Redundancia: " + "\n" + Arrays.toString(redundancia));
    	
    	
    	//Corrupcao
    	int i, k = 0;
        for (i=0;i<2;i++){
            data[i]=1;  
        }
        
        ReedSolomonDecoder decoder = new ReedSolomonDecoder(gf);
        
        System.out.println("Corrupcao: " + "\n" + Arrays.toString(data));
        
        //Concatenando dado com redudancia
        for(int x = 3; x < 7; x++){
        	data[x] = redundancia[k];
        	k++;
        }
        
        System.out.println("Vetor de dados concatenado com redundancia: " + "\n" + Arrays.toString(data));
        
        decoder.decode(data, 4);
        
        
        System.out.println("Mensagem corrigida: " + "\n" + Arrays.toString(data));
        */
        ///////////////////////////////////////////////////////////////////////////////
        /*
        Para m=8:
        Parametro 1 --> Default Primitive Polynomial=D^8 + D^4 + D^3 + D^2 + 1; Integer Representation=285.
        Parametro 2 --> n=2^m = 256
        n=200; k=120; n-k=80; t=40;
        */
        /*GenericGF gf = new GenericGF(285,256, 1);
        int n=223,k_desejado=120,i ;
        //int[] data = new int[] {54,65,102,187,175,78,214,125,175,2,47,39,85,67,17,97,178,136,194,201,217,7,96,81,78,228,
          250,21,247,108,54,65,102,187,175,78,214,125,175,2,47,39,85,67,17,97,178,136,194,201,217,7,96,81,78,228,250,21,
          247,108,54,65,102,187,175,78,214,125,175,2,47,39,85,67,17,97,178,136,194,201,217,7,96,81,78,228,250,21,247,108,
          54,65,102,187,175,78,214,125,175,2,47,39,85,67,17,97,178,136,194,201,217,7,96,81,78,228,250,21,247,108,0,0,0,0,
          0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
          0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,};
        int[] data = new int[n];
        for(i=0;i<n;i++){
            data[i]=0;
        }
        for(i=0;i<k_desejado;i++){
            data[i]=i;
        }
        
        ReedSolomonEncoder encoder = new ReedSolomonEncoder(gf);
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
        System.out.println(Arrays.toString(data)); */
        
    }
     
}