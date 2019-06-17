# Meaningless Reversible Degradation
Public repository for the paper "Data Validation Scheme Using Meaningless Reversible Degradation and NFC".

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

