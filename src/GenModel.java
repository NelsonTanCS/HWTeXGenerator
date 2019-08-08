import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.*;
import java.util.*;

public class GenModel {
	private static final String HEADER =
			"%%%%%%%%%%%%%%%%%%%%%%%%% NOTE %%%%%%%%%%%%%%%%%%%%%%%%%%%%\r\n" +
            "%% You can ignore everything from here until             %%\r\n" +
            "%% \"Question 1\"                                        %%\r\n" +
            "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\r\n" +
            "\\documentclass[11pt]{article}\r\n" +
            "\\usepackage{amsmath, amsfonts, amsthm, amssymb}  % Some math symbols\r\n" +
            "\\usepackage{fullpage}\r\n" +
            "\r\n" +
            "\\usepackage[x11names, rgb]{xcolor}\r\n" +
            "\\usepackage{graphicx}\r\n" +
            "\\usepackage{tikz}\r\n" +
            "\\usetikzlibrary{decorations,arrows,shapes}\r\n" +
            "\r\n" +
            "\\usepackage{etoolbox}\r\n" +
            "\\usepackage{enumerate}\r\n" +
            "\\usepackage{listings}\r\n" +
            "\r\n" +
            "\\setlength{\\parindent}{0pt}\r\n" +
            "\\setlength{\\parskip}{5pt plus 1pt}\r\n" +
            "\r\n" +
            "\\newcommand{\\N}{\\mathbb N}\r\n" +
            "\\newcommand{\\E}{\\mathbb E}\r\n" +
            "\\newcommand{\\V}{Var}\r\n" +
            "\\renewcommand{\\P}{\\mathbb P}\r\n" +
            "\\newcommand{\\f}{\\frac}\r\n" +
            "\r\n" +
            "\r\n" +
            "\\newcommand{\\nopagenumbers}{\r\n" +
            "    \\pagestyle{empty}\r\n" +
            "}\r\n" +
            "\r\n" +
            "\\def\\indented#1{\\list{}{}\\item[]}\r\n" +
            "\\let\\indented=\\endlist\r\n" +
            "\r\n" +
            "\\providetoggle{questionnumbers}\r\n" +
            "\\settoggle{questionnumbers}{true}\r\n" +
            "\\newcommand{\\noquestionnumbers}{\r\n" +
            "    \\settoggle{questionnumbers}{false}\r\n" +
            "}\r\n" +
            "\r\n" +
            "\\newcounter{questionCounter}\r\n" +
            "\\newenvironment{question}[2][\\arabic{questionCounter}]{%\r\n" +
            "    \\addtocounter{questionCounter}{1}%\r\n" +
            "    \\setcounter{partCounter}{0}%\r\n" +
            "    \\vspace{.25in} \\hrule \\vspace{0.4em}%\r\n" +
            "        \\noindent{\\bf \\iftoggle{questionnumbers}{#1: }{}#2}%\r\n" +
            "    \\vspace{0.8em} \\hrule \\vspace{.10in}%\r\n" +
            "}{$ $\\newpage}\r\n" +
            "\r\n" +
            "\\newcounter{partCounter}[questionCounter]\r\n" +
            "\\renewenvironment{part}[1][\\alph{partCounter}]{%\r\n" +
            "    \\addtocounter{partCounter}{1}%\r\n" +
            "    \\vspace{.10in}%\r\n" +
            "    \\begin{indented}%\r\n" +
            "       {\\bf (#1)} %\r\n" +
            "}{\\end{indented}}\r\n" +
            "\r\n" +
            "\\def\\show#1{\\ifdefempty{#1}{}{#1\\\\}}\r\n" +
            "\r\n" +
            "\\newcommand{\\header}{%\r\n" +
            "\\begin{center}\r\n" +
            "    {\\Large \\show\\myhwname}\r\n" +
            "    \\show\\myname\r\n" +
            "    \\show\\myemail\r\n" +
            "    \\show\\mysection\r\n" +
            "    \\today\r\n" +
            "\\end{center}}\r\n" +
            "\r\n" +
            "\\usepackage{hyperref} % for hyperlinks\r\n" +
            "\\hypersetup{\r\n" +
            "    colorlinks=true,\r\n" +
            "    linkcolor=blue,\r\n" +
            "    filecolor=magenta,      \r\n" +
            "    urlcolor=blue,\r\n" +
            "}\r\n" +
            "\r\n" +
            "%%%%%%%%%%%%%%%%%%% Document Options %%%%%%%%%%%%%%%%%%%%%%\r\n" +
            "\\noquestionnumbers\r\n" +
            "\\nopagenumbers\r\n" +
            "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\r\n" +
            "\r\n" +
            "\\begin{document}\r\n" +
            "%\\header\r\n" +
            "\\begin{flushleft}\r\n" +
            "% add header stuff here\r\n" +
            "\\end{flushleft}\r\n" +
            "\r\n";


	/**
	 * <h1>LEGACY METHOD</h1>
	 *
	 * Combines the parseFile and printTemplate methods. Will probably make the other two
	 * private and this one the sole public method.
	 *
	 * @param file File to be parsed
	 * @param destination Name of template to be saved as (will update to absolute path).
	 */
	public static void makeTemplate(File file, String destination) throws IOException {
		ArrayList<Integer> questions = parseFile(file);
		printTemplate(questions, destination);
	}

	/**
	 * Combines the parseFile and printTemplate methods. Will probably make the other two
	 * private and this one the sole public method.
	 *
	 * @param file File to be parsed
	 * @param destination Name of template to be saved as (will update to absolute path).
	 */
	public static void makeTemplateNew(File file, String destination, Numbering numb) throws IOException {
		ArrayList<Integer> questions = parseFileNew(file, numb);
		printTemplate(questions, destination);
	}

	/**
	 * Parses the given filename and outputs and ArrayList representing the number of questions
	 *
	 * @param filename File name in string form
     * @throws IOException
	 * @return an ArrayList representing the number of questions in the file
	 */
	public static ArrayList<Integer> parseFile(String filename) throws IOException {
		java.util.logging.Logger
				.getLogger("org.apache.pdfbox").setLevel(java.util.logging.Level.OFF);
		File file = new File(filename);
		return parseFile(file);
	}

	/**
	 * <h1>LEGACY METHOD</h1>
	 *
	 * Parses the given file and outputs the ArrayList representing the number of questions.
	 *
	 * @param file the File to be parsed
     * @throws IOException
	 * @return ArrayList of integers representing the number of questions
	 */
	public static ArrayList<Integer> parseFile(File file) throws IOException {
		java.util.logging.Logger
				.getLogger("org.apache.pdfbox").setLevel(java.util.logging.Level.OFF);
		ArrayList<Integer> result = new ArrayList<Integer>();
		BufferedReader reader = null;
		try {
			// Load and put the file into a BufferedReader
			PDDocument document = PDDocument.load(file);
			if (!document.isEncrypted()) {
				PDFTextStripper stripper = new PDFTextStripper();
				reader = new BufferedReader(new StringReader(stripper.getText(document)));
			}

			// parse the file
			String inputLine; // current line
			int num = 1; 	  // question number
			int part = 1;     // part number
			while ((inputLine = reader.readLine()) != null) {
				if (inputLine.startsWith(num + ". ")) {
					num++;
					result.add(1);
					part = 1;
					if (inputLine.contains("(a)")) {
						result.set(num - 2, part);
						part++;
					}
				}
				char letter = (char) ('a' + (part - 1));
				if (inputLine.startsWith("(" + letter + ")")) {
					result.set(num - 2, part);
					part++;
				}
			}
		} catch (InvalidPasswordException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
		    reader.close();
        }

		return result;
	}

	/**
	 * Parses the given file and outputs the ArrayList representing the number of questions
	 *
	 * @param file the File to be parsed
	 * @throws IOException
	 * @return ArrayList of integers representing the number of questions. Index+1 is the question number and value is number of parts.
	 */
	public static ArrayList<Integer> parseFileNew(File file, Numbering numb) throws IOException {
		java.util.logging.Logger
				.getLogger("org.apache.pdfbox").setLevel(java.util.logging.Level.OFF); // turns off stack trace for missing symbols

		ArrayList<Integer> result = new ArrayList<Integer>();
		BufferedReader reader = null;
		try {
			// Load and put the file into a BufferedReader
			PDDocument document = PDDocument.load(file);
			if (!document.isEncrypted()) {
				PDFTextStripper stripper = new PDFTextStripper();
				reader = new BufferedReader(new StringReader(stripper.getText(document)));
			}

			// parse the file
			String inputLine; // current line
			boolean flag = false; // temp
			while ((inputLine = reader.readLine()) != null) {
				if (flag) {
					System.out.println(numb.getQuestion());
				}
				System.out.println(numb.getQuestion());
				if (inputLine.startsWith(numb.getQuestion())) { // getQuestion
					flag = true;
					numb.nextQuestion(); // nextQuestion
					result.add(1);
					numb.resetPart(); // resetPart
					if (inputLine.contains(numb.getPart())) { // getPart. If part is on same line as question
						result.set(numb.question() - 2, numb.part());
						numb.nextPart(); // nextPart
					}
					System.out.println(result.toString());
				}
				if (inputLine.startsWith(numb.getPart())) { // getPart
					result.set(numb.question() - 2, numb.part());
					numb.nextPart(); // nextPart
					System.out.println(result.toString());
				}
			}
			System.out.println("done");
			System.out.println(result.toString());
		} catch (InvalidPasswordException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			reader.close();
		}

		return result;
	}

	/**
	 * Creates and prints a template given the ArrayList representation of questions and a destination.
     *
	 * @paraam questions ArrayList representation of the number of questions generated form parseFile method
	 * @param destination String representaiton of the filename to save to.
	 */
	public static void printTemplate(ArrayList<Integer> questions, String destination) {
		try (BufferedWriter writer =
                     new BufferedWriter(new OutputStreamWriter(new FileOutputStream(destination), "utf-8"));
             BufferedReader template =
                     new BufferedReader(new StringReader(HEADER))) {
			String inputLine;
			while ((inputLine = template.readLine()) != null) {
				writer.write(inputLine);
				writer.newLine();
			}

			for (int i = 0; i < questions.size(); i++) {
				if (questions.get(i) == 1) {
					writer.write("%%%%%%%%%%%% Problem " + (i + 1) + " %%%%%%%%%%%%\r\n" +
							"\\begin{question}{Problem "+ (i + 1) +"}\r\n" +
							"   \r\n" +
							"\\textbf{Answer:} \\fbox{$answer$}\r\n" +
							"\r\n" +
							"\\textbf{Explanation:} \r\n" +
							"\r\n" +
							"Explain here.\r\n" +
							"\\end{question}\n");
					writer.newLine();
				} else {
					writer.write("%%%%%%%%%%%% Problem " + (i + 1) + " %%%%%%%%%%%%\r\n" +
							"\\begin{question}{Problem " + (i + 1) + "}\n");
					for (int j = 1; j <= questions.get(i); j++) {
						char letter = (char) ('a' + j - 1);
						writer.write(
								"\\begin{part} % part " + letter + "\r\n" +
										"\r\n" +
										"\\textbf{Answer:} \\fbox{$answer$}\r\n" +
										"\r\n" +
										"\\textbf{Explanation:} \r\n" +
										"\r\n" +
										"Explain here.\r\n" +
										"\\end{part}\r\n" +
										"\r\n");
					}
					writer.write("\\end{question}\n");
					writer.newLine();
				}
			}
			writer.write("\\end{document}");
		} catch (IOException e) {
            e.printStackTrace();
        }
	}

	/**
	 * Asks console for number of questions and number of parts for each question.
	 * 
	 * @requires input from Scanner and integer ArrayList
	 * @param console input
	 * @param questions ArrayList that number of questions will be stored in
	 * @return ArrayList where index+1 is the number of parts for that question
	 */
	public static ArrayList<Integer> manualSetup(Scanner console, ArrayList<Integer> questions) {
		System.out.println("How many questions?");
		int numOfQuestions = Integer.parseInt(console.nextLine());
		questions = new ArrayList<Integer>(numOfQuestions);
		for (int i = 0; i < numOfQuestions; i++) {
			System.out.println("How many parts to question " + (i + 1) + "?");
			int parts = Integer.parseInt(console.nextLine());
			if (parts == 1 || parts == 0) {
				questions.add(parts); 
			} else {
				questions.add(parts);
			}
		}
		return questions;
	}
}
