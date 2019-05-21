import java.util.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;

public class Generator {
	private static final String filepath = "C:\\Users\\nelso\\git\\HWTeXGenerator";
	
	public static void main(String[] args) throws InvalidPasswordException, IOException {
		Scanner console = new Scanner(System.in);

		ArrayList<Integer> questions = null; // stores number of questions
		System.out.println("Filename of homework or return for manual setup");
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
		
		System.out.println(questions.toString());
	}
	
	
	public static ArrayList<Integer> parseHomework(BufferedReader reader, ArrayList<Integer> questions) {
		String inputLine; // current line
		questions = new ArrayList<Integer>();
		int num = 1; 	  // question number
		int part = 1;     // part number
		try {
			while ((inputLine = reader.readLine()) != null) {
				//System.out.println(inputLine);
				if (inputLine.startsWith(num + ". ")) {
					System.out.println(inputLine);
					num++;
					questions.add(1);
					part = 1;
				}
				char letter = (char) ('a' + (part - 1));
				if (inputLine.startsWith("(" + letter + ")")) {
					System.out.println(inputLine);
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
