# Meaningless Reversible Degradation
Public repository for the paper "Data Validation Scheme Using Meaningless Reversible Degradation and NFC". Meaningless Reversible Degradation is a data validation scheme with Reed-Solomon error correction code and NFC. This method explores the Reed-Solomon reversible degradation capacity and the NFC architecture. A SHA-256 hash function is computed from a secret file and stored to be the data validation key. Then, the RS redundancy data is generated and stored and the secret file is partially corrupted until the RS error correction capacity, on three different levels: Low (5%), Medium (15%), and High (25%). The output of the coding process is the valid
NFC card, the hash file, the degraded file, and the redundancyfile. A valid NFC card is superimposed on the NFC reader to retrieve the file, this mechanism allows to begin the decoding process for data retrieving. Once a valid NFC card is read, the degraded file and the redundancy file are taken as input for the RS decoder. A retrieved file is generated, this file is exactly the same than the original file. The retrieved file is available, if and only if, the NFC card is superimposed on the NFC reader. If the NFC card is removed, the secret file is encoded again and the file becomes meaningless. The advantage of this propose is that the secret file is decoded,
if and only if, an authorized NFC card is superimposed on the NFC reader. Thus, an authorized part must be physically close to the NFC reader to superimpose the card on the NFC reader. The proposed system was implemented on the Java language program and computes the encoding and decoding process in few seconds, which turns this method on a feasible commercial system.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites

What things you need to install the software and how to install them

```
Give examples
```

### Installing

For the environment to work properly, make sure you have installed the guava library and smartcardio built-in module in your packages.

```
Java smartcardio https://docs.oracle.com/javase/7/docs/jre/api/security/smartcardio/spec/javax/smartcardio/package-summary.html
Guava https://github.com/google/guava
```
To encode a file you must manually provide a absolute path of the file to be encode and the percentage of the file to be degradeted, up to a maximum of 50% and you must have a NFC card reader with a valid card, the encoded file will be associated with the hash from the card, in that way only the card whom perfomed the encode processing will be enable to access the file. Notice that the higher the percent the more time will be taken to complete the encode processing:

```
absolutePath: File to be encode
degradationPercent: Total percentage of the file to be degradeted
```
The degradeted file will be writen in the same folder as the original one.

To decode you must provide a degradeted file, the hash of the card whom degradeted the file, the card whom generate the hash. The decoder process is executed as long as a valid card is on the reader, once the card is remove from the reader all the files, except the degradeted one, is remove from the disk.

```
encodedFile: The file encoded and corrupted
redundancyFile: The file containing the redundacy symbols for correctio
hashEncoder: The generated hash from the card whom encoded the file
degradationPercent: Corruption percentage of the file (must match the one in the encoding process)
```

## Contributing

Please read [CONTRIBUTING.md](https://gist.github.com/PurpleBooth/b24679402957c63ec426) for details on our code of conduct, and the process for submitting pull requests to us.

## Authors

* **Kevin de Santana** - [Lattes](http://lattes.cnpq.br/7478186395525413)

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgments

* Sean Owen and William Rucklidge for the implementation of the Reed-Solomon
* Centro Universitário IESB for the support.

