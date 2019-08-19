/**
 * Represents the list and sublist characters.
 */

public class Numbering {
    private String question; /* question numbering e.g. 1. */
    private String part; /* question numbering e.g. (a) */
    private int qNum; /* question number */
    private int pNum; /* part number */
    private char charQuestion; /* the current question Integer or Char */
    private int intQuestion;
    private boolean qIsInt; /* the question is enumerated with ints */
    private char charPart; /* the current part; Integer or Char */
    private int intPart;
    private boolean pIsInt; /* the part is enumerated with ints */

    /**
     * Constructs a Numbering system.
     * e.g. Numbering("#.", "(*)") represents lists with 1., 2., 3. and sublists (a), (b), (c).
     *
     * @param question Format of the list
     * @param part Format of the sublist
     * @throws IllegalArgumentException if the parameters don't contain a "*" or "#"
     */
    public Numbering (String question, String part) {
        this.question = question;
        this.part = part;
        qNum = 1;
        pNum = 1;
        if (question.contains("#")) {
            intQuestion = 1;
            qIsInt = true;
        } else if (question.contains("*")) {
            charQuestion = 'a';
            qIsInt = false;
        } else {
            throw new IllegalArgumentException("question parameter requires a \"*\" or \"#\"");
        }

        if (part.contains("#")) {
            intPart = 1;
            pIsInt = true;
        } else if (question.contains("*")) {
            charPart = 'a';
            pIsInt = false;
        }
    }

    /**
     *
     *
     * @return
     */
    public String getQuestion() {
        if (qIsInt) {
            return question.replace("#",Integer.toString(intQuestion));
        } else {
            return question.replace("*", Character.toString(charQuestion));
        }
    }

    public String getPart() {
        if (pIsInt) {
            return part.replace("#", Integer.toString(intPart));
        } else {
            return part.replace("*", Character.toString(charPart));
        }
    }

    public void nextQuestion() {
        if (qIsInt) {
            intQuestion++;
            qNum++;
        } else {
            charQuestion += 1;
            qNum++;
        }
    }

    public void nextPart() {
        if (pIsInt) {
            intPart++;
            pNum++;
        } else {
            charPart += 1;
            pNum++;
        }
    }

    public int question() {
        return qNum;
    }

    public int part() {
        return pNum;
    }

    public void resetPart() {
        if (pIsInt) {
            intPart = 1;
            pNum = 1;
        } else {
            charPart = 'a';
            pNum = 1;
        }
    }
}
