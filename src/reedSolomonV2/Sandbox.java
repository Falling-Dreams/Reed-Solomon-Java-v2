package reedSolomonV2;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.TerminalFactory;

import reedSolomonV2.ReversibleDegradation;

public class Sandbox {

	public static void main(String[] args)
			throws NoSuchAlgorithmException, IOException, ReedSolomonException, CardException {
		
		NFCHandle nfc = new NFCHandle();
		
		
		try {
			TerminalFactory factory = TerminalFactory.getDefault();
			List<CardTerminal> terminals = factory.terminals().list();
			CardTerminal term = terminals.get(0);
			Card card = term.connect("*");
			CardChannel channel = card.getBasicChannel();
			int contador = 0;
			
			// parar de decodificar se existir um cartao no terminal por mais de 10 segundos
			while (term.waitForCardAbsent(10000) == true) {
				if (term.waitForCardPresent(100) == true) {
					contador += 1;					
				}
				
			}
			

		} catch (Exception e) {
			//throw new ReedSolomonException("Could not read card");
		}	
	
	
	}

	

}
