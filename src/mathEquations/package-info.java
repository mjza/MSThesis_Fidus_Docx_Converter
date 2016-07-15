/**
 * @version 3.0
 */
/**
 * @author Mahdi, Jaberzadeh Ansari
 * @email mahdijaberzadehansari@yahoo.co.uk
 */
/**
 * @role The classes inside of this package are responsible for converting the
 *       math equations from OMML (Office Math Markup Language) which is used
 *       inside of the Microsoft Office, to the LaTeX equations which is used in
 *       FidusWriter. For doing this conversion we used the “fmath” library, and
 *       two XSLT files from Microsoft (i.e. MML2OMML.XSL and OMML2MML.XSL).
 *       These two files are published within Microsoft Office. Currently these
 *       two files are used in many libraries and software through the internet,
 *       but the license of them has not been cleared from Microsoft. We called
 *       some people at Microsoft via email, but they couldn’t provide a clear
 *       answer about their licenses. We had a student version of Microsoft
 *       Office on our system, thus we used the files that had been existed in
 *       our file system. But we removed them from the public repository of the
 *       project. Therefore, if someone wants to use the converter must copy
 *       these two files from his or her file system into the
 *       ‘templates/mathml_libs’ folder of the project or download them via the
 *       internet with his own risk and placed them inside of that folder.
 */
package mathEquations;