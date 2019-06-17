package reedSolomonV2;

import java.util.List;
import javax.smartcardio.*;

import com.google.common.base.Stopwatch;

public class ManipulaNFC {

	/**
	 * <p>
	 * Get the UID from the card. The first 4 bytes are the UID and the last two a
	 * confirmation from the reader. Uses the java smartcardio buit-in library.
	 * <p>
	 * 
	 * @return responseBytes: Vector with the UID bytes.
	 * @throws CardException:        Only for the correct execution of the method.
	 * @throws ReedSolomonException: If card or reader are not available.
	 */

	protected byte[] UID() throws CardException, ReedSolomonException {
		Stopwatch timer = new Stopwatch();
		timer.start();
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
			throw new ReedSolomonException("Could not read card");
		}
		// System.out.println("Tempo total de execucao: " + timer.stop());
		return responseBytes;
	}

	/**
	 * <p>
	 * Checks if a card is whether or not present in the reader.
	 * <p>
	 * 
	 * @return isAbsent: Boolean to know if a card is present in the reader
	 * @throws CardException:        Only for the correct execution of the method.
	 * @throws ReedSolomonException: If card or reader are not available.
	 */
	protected boolean cardOrTerminalUnavailable() throws CardException, ReedSolomonException {
		Boolean isAbsent = false;
		try {
			TerminalFactory factory = TerminalFactory.getDefault();
			List<CardTerminal> terminals = factory.terminals().list();
			CardTerminal term = terminals.get(0);

			if (term.isCardPresent() == false || terminals.isEmpty() == true) {
				isAbsent = true;
			}

		} catch (Exception e) {
			throw new ReedSolomonException("Could not access terminal");
		}

		return isAbsent;
	}

}
