import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.*;
import java.util.*;

/**
 * <b>GenModel</b> represents the list and sublist extracted from a pdf
 *
 * @specfield model :
 *
 * Abstract Invariant:
 * the model represents 
 */
public class GenModel {
	private ArrayList<Integer> model;

	// RI:
	// model is not null

	// AF:
	// index+1 is the question number and the value is the size of the sublist.
	// size of model is the number of questions not including subquestions

	/**
	 * Constructs a model for the number of questions in the file.
	 *
	 * @param filename the file to be parsed
	 * @throws InvalidPasswordException
	 * @throws IOException
	 */
	public GenModel (String filename) throws InvalidPasswordException, IOException {
		java.util.logging.Logger
				.getLogger("org.apache.pdfbox").setLevel(java.util.logging.Level.OFF);

		Scanner console = new Scanner(System.in);

		BufferedReader reader = null;
		try {
			PDDocument document = PDDocument.load(new File(filename));
			if (!document.isEncrypted()) {
				PDFTextStripper stripper = new PDFTextStripper();
				reader = new BufferedReader(new StringReader(stripper.getText(document)));
			}
			parseHomework(reader);
		} finally {
			closeReader(reader);
		}
	}

	public void generate(String filename) {

	}

	// parses the input file
	private void parseHomework(BufferedReader reader) {
		String inputLine; // current line
		model = new ArrayList<Integer>();
		int num = 1; 	  // question number
		int part = 1;     // part number
		try {
			while ((inputLine = reader.readLine()) != null) {
				if (inputLine.startsWith(num + ". ")) {
					num++;
					model.add(1);
					part = 1;
					if (inputLine.contains("(a)")) {
						model.set(num - 2, part);
						part++;
					}
				}
				char letter = (char) ('a' + (part - 1));
				if (inputLine.startsWith("(" + letter + ")")) {
					model.set(num - 2, part);
					part++;
				}
			}
		} catch (IOException e) {
			System.err.println(e.toString());
			e.printStackTrace(System.err);
		} finally {
			closeReader(reader);
		}
	}

	public Iterator<Integer> questions() {
		return model.iterator();
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
