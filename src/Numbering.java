/**
 * Represents the list and sublist characters.
 */

public class Numbering {
    String question; /* question numbering e.g. 1. */ // TODO: parse the question and part to look for the incrementable piece and separate it from parentheses etc.
    String part; /* question number e.g. (a) */
    char currQuestion; /* the current question Integer or Char */
    boolean qIsInt; /* the question is enumerated with ints */
    char currPart; /* the current part Integer or Char */
    boolean pIsInt; /* the part is enumerated with ints */

    public Numbering (String question, String part) {
        this.question = question;
        this.part = part;
        if (question.contains("#")) {
            currQuestion = '1';
            qIsInt = true;
        } else if (question.contains("*")) {
            currQuestion = 'a';
            qIsInt = false;
        } else {
            throw new IllegalArgumentException("question parameter requires a \"*\" or \"#\"");
        }

        if (part.contains("#")) {
            currPart = '1';
            pIsInt = true;
        } else if (question.contains("*")) {
            currPart = 'a';
            pIsInt = false;
        }
    }

    public String getQuestion() {
        if (qIsInt) {
            return question.replace("#", Character.toString(currQuestion));
        } else {
            return question.replace("*", Character.toString(currQuestion));
        }
    }

    public String getPart() {
        if (pIsInt) {
            return part.replace("#", Character.toString(currPart));
        } else {
            return part.replace("*", Character.toString(currPart));
        }
    }

    public void nextQuestion() {
        if (qIsInt) {
            currQuestion = (char) (Character.getNumericValue(currQuestion) + 1);
        } else {
            currQuestion += 1;
        }
    }

    public void nextPart() {
        if (pIsInt) {
            currPart = (char) (Character.getNumericValue(currPart) + 1);
        } else {
            currPart += 1;
        }
    }

    public void resetPart() {
        if (pIsInt) {
            currPart = '1';
        } else {
            currPart = 'a';
        }
    }
}
