package reedSolomonV2;

import reedSolomonV2.SHA256;
import reedSolomonV2.ManipulaNFC;;

public class Sandbox {
	
	//SHA256 sha = new SHA256();
	ManipulaNFC nfc = new ManipulaNFC();
	
	byte[] uidLido = nfc.UID();
	

}
