package reedSolomonV2;

import java.util.List;
import javax.smartcardio.*;

public class ManipulaNFC {

	protected byte[] UID() throws CardException, ReedSolomonException {
		byte[] responseBytes = new byte[6];
		try {
			TerminalFactory factory = TerminalFactory.getDefault();
			List<CardTerminal> terminals = factory.terminals().list();
			CardTerminal term = terminals.get(0);
			Card card = term.connect("*");
			CardChannel channel = card.getBasicChannel();
			byte[] instruction = { (byte) 0xFF, (byte) 0xCA, (byte) 0x00, (byte) 0x00, (byte) 0x00 };
			CommandAPDU getUID = new CommandAPDU(instruction);
			ResponseAPDU response = channel.transmit(getUID);
			responseBytes = response.getBytes();

		} catch (Exception e) {
			throw new ReedSolomonException("Nao foi possivel ler o cartao");
		}

		return responseBytes;
	}
	
	protected boolean cartaoOuTerminalAusentes() throws CardException, ReedSolomonException {
		Boolean estaAusente = false;
		try {		
		TerminalFactory factory = TerminalFactory.getDefault();
		List<CardTerminal> terminals = factory.terminals().list();
		CardTerminal term = terminals.get(0);	
		
		if (term.isCardPresent() == false || terminals.isEmpty() == true) {
			estaAusente = true;
		}
		
		}catch(Exception e) {
			throw new ReedSolomonException("Nao foi possivel acessar o terminal");
		}
		
		return estaAusente;
	}

}
