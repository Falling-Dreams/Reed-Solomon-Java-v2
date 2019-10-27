package reedSolomonV2;

/**
 * <p>
 * Default Reed-Solomon error correction code exception, if for any reason the
 * encoding process or deconding process fails
 * </p>
 * 
 * @author Kevin de Santana
 *
 */

public final class ReedSolomonException extends Exception {

	public ReedSolomonException(String message) {
		super(message);
	}

}