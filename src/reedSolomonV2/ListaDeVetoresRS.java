package reedSolomonV2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import reedSolomonV2.VetorRS;

public class ListaDeVetoresRS {
	
	private ArrayList<VetorRS> listaVetoresRS = new ArrayList<>();
	private int qtdIteracoes;
	
	public static void main(String[] args) throws IOException {
		
		ListaDeVetoresRS listaVetoresRS = new ListaDeVetoresRS(4);
		listaVetoresRS.imprimeVetoresDaLista();
		
	}	
	
	public ListaDeVetoresRS(int qtdIteracoes) {
		this.qtdIteracoes = qtdIteracoes;
		
		for(int i = 0; i < qtdIteracoes; i++) {
			VetorRS vetorRS = new VetorRS();
			this.listaVetoresRS.add(vetorRS);
		}		
	}
	
	public void imprimeVetoresDaLista() {
		for(VetorRS vetor : listaVetoresRS) {
			System.out.println(Arrays.deepToString(listaVetoresRS.toArray()));
		}
					
	}
	
	

}
