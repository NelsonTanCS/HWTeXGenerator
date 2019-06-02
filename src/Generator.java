import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.text.PDFTextStripper;

public class Generator {
	private static final String HEADER = 
			"%%%%%%%%%%%%%%%%%%%%%%%%% NOTE %%%%%%%%%%%%%%%%%%%%%%%%%%%%\r\n" + 
			"%% You can ignore everything from here until             %%\r\n" + 
			"%% \"Question 1\"                                          %%\r\n" + 
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
	
	public static void main(String[] args) throws InvalidPasswordException, IOException {
		java.util.logging.Logger
	    .getLogger("org.apache.pdfbox").setLevel(java.util.logging.Level.OFF);
		
		Scanner console = new Scanner(System.in);

		ArrayList<Integer> questions = null; // stores number of questions
		System.out.println("Filename of homework (hw#.pdf) or return for manual setup\n"
				+ "PDF must be in same location as this jar\n"
				+ "Warning: will overwrite hw#boi.tex");
		String filename = console.nextLine();
		
		if (filename.equals("")) { // manual setup
			questions = manualSetup(console, questions);
		} else {
			BufferedReader reader = null;
			try {
				PDDocument document = PDDocument.load(new File(filename));
				if (!document.isEncrypted()) {
				    PDFTextStripper stripper = new PDFTextStripper();
				    reader = new BufferedReader(new StringReader(stripper.getText(document)));
				}
				questions = parseHomework(reader, questions);
			} finally {
				closeReader(reader);
			}
		}
		
		BufferedWriter writer = null;
		BufferedReader template = null;
		try {
			filename = filename.substring(0, filename.indexOf("."));
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename + "boi.tex"), "utf-8"));
			template = new BufferedReader(new StringReader(HEADER));
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
			System.out.println("Template created: " + filename + "boi.tex");
		} finally {
			writer.close();
			template.close();
		}
	}
	
	public static ArrayList<Integer> parseHomework(BufferedReader reader, ArrayList<Integer> questions) {
		String inputLine; // current line
		questions = new ArrayList<Integer>();
		int num = 1; 	  // question number
		int part = 1;     // part number
		try {
			while ((inputLine = reader.readLine()) != null) {
				if (inputLine.startsWith(num + ". ")) {
					num++;
					questions.add(1);
					part = 1;
					if (inputLine.contains("(a)")) {
						questions.set(num - 2, part);
						part++;
					}
				}
				char letter = (char) ('a' + (part - 1));
				if (inputLine.startsWith("(" + letter + ")")) {
					questions.set(num - 2, part);
					part++;
				}
			}
		} catch (IOException e) {
			System.err.println(e.toString());
			e.printStackTrace(System.err);
		} finally {
			closeReader(reader);
		}
		return questions;
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
	
	/**
	 * Attempts to close the reader after parsing.
	 * 
	 * @param reader BufferedReader to be closed
	 */
	private static void closeReader(BufferedReader reader) {
		if (reader != null) {
			try {
				reader.close();
			} catch (IOException e) {
				System.err.println(e.toString());
				e.printStackTrace(System.err);
			}
		}
	}
}
