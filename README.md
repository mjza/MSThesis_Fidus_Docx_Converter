# MSThesis_Fidus_Docx_Converter
Fidus Docx Converter is a Java based project which was written as part of my master thesis implementation. 

This project was writen in Eclipse and we strongly recommend to import it in Eclipse for more developments.

As soon as you import it in Eclipse all the class pathes will be set, just you have to selcet JRE 1.7 or above for compiling. 

The structure of the code has been divided into seven packages as follows:

1.  auxiliary: This package contains the classes that have been used in different classes for some general staffs same as sorting, I/O and working with files (e.g. extracting or compressing the files).

2.  mathEquations: The classes inside of this package are responsible for converting the math equations from OMML (Office Math Markup Language) which is used inside of Microsoft Office, to the LaTeX equations. LaTeX equation is the default formula inside of Fidus Writer. For doing this conversion we used the “fmath”  library, and two XSLT files from Microsoft (i.e. MML2OMML.XSL and OMML2MML.XSL). These two files are published within Microsoft Office. Currently, these two files are used in many libraries and software through the internet, however, the license of them has not been cleared from Microsoft. We called some people at Microsoft via email, however, they couldn’t provide a clear answer about their licenses. We had a student version of Microsoft Office on our system, thus we used the files that had existed in our file system. However, we removed them from the public repository of the project. Therefore, if someone wants to use the converter must copy these two files from his or her file system into the ‘templates/mathml_libs’ folder of the project or download them via the internet at his own risk and placed them inside of that folder.

3.	fidusWriter.fileStructure: This package and its classes are responsible for reading or writing the FIDUS files. When we want to convert a FIDUS file to the DOCX file we must extract the FIDUS file at first. This package manages this uncompressing and keeps the actual paths to the uncompressed files. Furthermore, when we want to convert a DOCX file to the FIDUS file, it is this package that moderates this compression. 

4.	fidusWriter.model: This package has three sub-packages. This package with its sub-packages has been used to model FIDUS file in JAVA. Each FIDUS file has three main files. They are bibliography.json, document.json and images.json. These three files are modeled inside of three sub-packages with the same name.

5.	fidusWriter.converter: This package has two sub-packages: ‘todocx’ and ‘tofidus’. As it is clear from their names the core codes that do the conversion and parse the input file and create the equivalent elements in the output file has been placed inside of this package.  

6.	window: This package has the code for moderating the GUI.

7.	threads: As this converter has been designed with the GUI, we designed some threads to prevent freezing of the GUI when the tool is in the middle of converting.

