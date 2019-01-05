package reedSolomonV2;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

public class Main {
	
	public static void main(String[] args) throws FileNotFoundException, IOException, ReedSolomonException {
		
		GenericGF gf = new GenericGF(69643,65536, 1);
		
		ReedSolomonEncoder encoder = new ReedSolomonEncoder(gf);
		//encoder.encode("I:\\@Eng\\Novissímo-TCC (Agora Vai)\\@Programas\\Testes-com-RS\\SampleAudio_0.4mb.mp3", 60);
		//System.out.println(Arrays.toString(data)); 
		
		//ReedSolomonDecoder decoder = new ReedSolomonDecoder(gf);
		//decoder.decode("I:\\@Eng\\Novissímo-TCC (Agora Vai)\\@Programas\\Testes-com-RS\\corrompido.docx", 60); 
        //System.out.println(Arrays.toString(data));
		
	}

}
