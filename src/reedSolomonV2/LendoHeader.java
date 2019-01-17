package reedSolomonV2;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Random;

import reedSolomonV2.*;

public class LendoHeader {
	private byte novoDados[] = new byte[7000000];
	private int ndP;//contador/posicao do novoDados
	private int vetor_quadro[]=new int[255];
	private byte[] dados;
	private int tamanho;
	private int posicao_inicial;
	//public BytesToBits bytb = new BytesToBits();
	private byte[] redundancia;
	private int posi_redund;

	public LendoHeader(byte[] dados) {
		this.dados=dados;
		ndP=0;
		posi_redund=0;
		medirDados();
		lerHeader();

	}
	
	public byte[] getDados(){
		return dados;
	}
	
	public int getPosicao_inicial() {
		return posicao_inicial;
	}

	public int getTamanho() {
		return tamanho;
	}

	public void medirDados(){
		this.tamanho=this.dados.length;
	}
	
	public int getTamanhoQuadro() {
		int byte_rate_fa=0;
		int tam_quadro = 0;
		int posicao=posicao_inicial;
		if(dados[posicao]==-1) {
			byte_rate_fa=dados[posicao+2];
			switch(byte_rate_fa) {
			case -110:
				tam_quadro=(int) ((144*128/44.1)+1);
				break;
			case -112:
				tam_quadro=(int) ((144*128/44.1));
				break;
			case -78:
				tam_quadro=(int) ((144*192/44.1)+1);
				break;
			case -80:
				tam_quadro=(int) ((144*192/44.1));
				break;
			case -30:
				tam_quadro=(int) ((144*320/44.1)+1);
				break;
			case -32:
				tam_quadro=(int) ((144*320/44.1));
				break;
			}
		}
		return tam_quadro;
	}
	
	public void lerHeader(){//metodo responsavel por encontrar o primeiro quadro do arquivo
		int referencia=0;
		for(int i=0;i<tamanho;i++){
			if(dados[i]==-1)
			{
				referencia=i+1;
				if(dados[referencia]<-4 && dados[referencia]>-7){
					referencia++;
					if(dados[referencia]<-29){
						referencia++;
						if(dados[referencia]>0){
							posicao_inicial=i;
							break;
						}
					}
				}	
			}
		}
	}

	public int pularIntervalo(int posicao, int tempo){//pula quadros informados pelo tempo a partir da posicao
		int byte_rate_fa=0;//byte respectivo para bitrate e freq amostragem
		int tam_quadro=0;// tamanho do quadro
		for(int i = 0;i<tempo;i++){//FOR PARA NUMERO DE QUADROS
			if(dados[posicao]==-1){
				byte_rate_fa=dados[posicao+2];
				switch(byte_rate_fa){//CALCULA TAMANHO DO QUADRO
				case -110:
					tam_quadro=(int) ((144*128/44.1)+1);
					break;
				case -112:
					tam_quadro=(int) ((144*128/44.1));
					break;
				case -78:
					tam_quadro=(int) ((144*192/44.1)+1);
					break;
				case -80:
					tam_quadro=(int) ((144*192/44.1));
					break;
				case -30:
					tam_quadro=(int) ((144*320/44.1)+1);
					break;
				case -32:
					tam_quadro=(int) ((144*320/44.1));
					break;
				}
				posicao=posicao+tam_quadro;//pula quadro
			}
		}
		return posicao;
	}
	
	public void encodeIntervalo(int posicao, int tempo){//faz o encode dos quadros pela posicao e tempo(quantidade de quadros)
		GenericGF gf = new GenericGF(285,256, 1);
		int casting=0;
		int vet_p=0;
		int byte_rate_fa=0;//byte respectivo para bitrate e freq amostragem
		int tam_quadro=0;// tamanho do quadro
		
		for(int i = 0;i<tempo;i++){
			if(dados[posicao]==-1){
				byte_rate_fa=dados[posicao+2];
				switch(byte_rate_fa){
				case -110:
					tam_quadro=(int) ((144*128/44.1)+1);
					break;
				case -112:
					tam_quadro=(int) ((144*128/44.1));
					break;
				case -78:
					tam_quadro=(int) ((144*192/44.1)+1);
					break;
				case -80:
					tam_quadro=(int) ((144*192/44.1));
					break;
				case -30:
					tam_quadro=(int) ((144*320/44.1)+1);
					break;
				case -32:
					tam_quadro=(int) ((144*320/44.1));
					break;
				}
				for(int cont=posicao+6;cont<posicao+91;cont++){//FOR PARA RECOLHER BYTES QUE SERAO ENCRIPTADOS
					casting=(int)dados[cont];
					if(casting>=0){
						vetor_quadro[vet_p]=casting;
					} else{
						vetor_quadro[vet_p]=casting+256;
					}
					vet_p++;
				} 
				vet_p=0;
				ReedSolomonEncoder encoder = new ReedSolomonEncoder(gf);
				encoder.encode(vetor_quadro, 170);//REALIZA ENCODE
				for(int x=85;x<255;x++){//FOR PARA SALVAR A REDUNDANCIA
					if(vetor_quadro[x]>127){
						casting=vetor_quadro[x]-256;
						novoDados[ndP]=(byte) casting;
					}else{
						casting=vetor_quadro[x];
						novoDados[ndP]=(byte) casting;
					}
					ndP++;
				}
				posicao=posicao+tam_quadro;
			}
		}
	}

	public void corromperIntervalo(int posicao, int tempo){//corrompe os quadros que passaram pelo encode pelo mesmo paramentro

		int byte_rate_fa=0;//byte respectivo para bitrate e freq amostragem
		int tam_quadro=0;// tamanho do quadro

		//Random aleatorio=new Random();
		
		
		for(int i = 0;i<tempo;i++){
			if(dados[posicao]==-1){
				byte_rate_fa=dados[posicao+2];
				switch(byte_rate_fa){
				case -110:
					tam_quadro=(int) ((144*128/44.1)+1);
					break;
				case -112:
					tam_quadro=(int) ((144*128/44.1));
					break;
				case -78:
					tam_quadro=(int) ((144*192/44.1)+1);
					break;
				case -80:
					tam_quadro=(int) ((144*192/44.1));
					break;
				case -30:
					tam_quadro=(int) ((144*320/44.1)+1);
					break;
				case -32:
					tam_quadro=(int) ((144*320/44.1));
					break;
				}
				for(int cont=posicao+6;cont<posicao+91;cont++){
					//dados[cont]= (byte) aleatorio.nextInt(126);//Com ruído*******
					dados[cont]=0;//Sem ruído(apenas um silencio)***********
				}
				posicao=posicao+tam_quadro;
			}
		}
	}

	public void salvaCorrompido() throws IOException{// salva novo arquivo corrompido
		OutputStream os = new FileOutputStream("ArqCorrompido.mp3");
    	for(int i = 0; i < dados.length; i++) {
    		os.write(dados[i]);
        }
    	os.close();
	}
	
	public void salvaRecuperado() throws IOException{// salva novo arquivo corrompido
		OutputStream os = new FileOutputStream("ArqRecuperado.mp3");
    	for(int i = 0; i < dados.length; i++) {
    		os.write(dados[i]);
        }
    	os.close();
	}
	
	public void salvaRedundancia() throws IOException {
		OutputStream os = new FileOutputStream("redundancia.rdg");
    	for(int i =0; i<ndP;i++){
    		os.write(novoDados[i]);
    	}
    	os.close();
	}
	
	public void lerRedundancia() throws IOException {
		String nomeRedundancia = "redundancia.rdg";
		Path path = Paths.get(nomeRedundancia);
		redundancia = Files.readAllBytes(path);
	}
	
	public void decodeIntervalo(int posicao, int tempo) throws ReedSolomonException{
		GenericGF gf = new GenericGF(285,256, 1);
		
		int casting=0;
		int vet_p=0;
		int byte_rate_fa=0;//byte respectivo para bitrate e freq amostragem
		int tam_quadro=0;// tamanho do quadro
		int a=0;
		
		for(int i = 0;i<tempo;i++){
			if(dados[posicao]==-1){
				byte_rate_fa=dados[posicao+2];
				switch(byte_rate_fa){
				case -110:
					tam_quadro=(int) ((144*128/44.1)+1);
					break;
				case -112:
					tam_quadro=(int) ((144*128/44.1));
					break;
				case -78:
					tam_quadro=(int) ((144*192/44.1)+1);
					break;
				case -80:
					tam_quadro=(int) ((144*192/44.1));
					break;
				case -30:
					tam_quadro=(int) ((144*320/44.1)+1);
					break;
				case -32:
					tam_quadro=(int) ((144*320/44.1));
					break;
				}
				for(int cont=posicao+6;cont<posicao+91;cont++){
					casting = (int)dados[cont];
					if(casting>=0){
						vetor_quadro[vet_p]=casting;
					} else{
						vetor_quadro[vet_p]=casting+256;
					}
					vet_p++;
				} 
				vet_p=0;
				for(int x=85;x<255;x++) {
					casting=(int)redundancia[posi_redund];
					if(casting>=0) {
						vetor_quadro[x]=casting;
					}else {
						vetor_quadro[x]=casting+256;
					}
					posi_redund++;
				}
				ReedSolomonDecoder decoder = new ReedSolomonDecoder(gf);
				decoder.decode(vetor_quadro, 170);
				a=0;
				for(int cont=posicao+6;cont<posicao+91;cont++) {
					if(vetor_quadro[a]>127) {
						dados[cont]=(byte)(vetor_quadro[a]-256);
					}else {
						dados[cont]=(byte)vetor_quadro[a];
					}
					a++;
				}
				
				posicao=posicao+tam_quadro;
			}
		}
	}

	
	
	
}