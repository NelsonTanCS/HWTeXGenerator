/**
 * Represents the list and sublist characters.
 */

public class Numbering {
    private String question; /* question numbering e.g. 1. */
    private String part; /* question numbering e.g. (a) */
    private int qNum; /* question number */
    private int pNum; /* part number */
    private char currQuestion; /* the current question Integer or Char */
    private boolean qIsInt; /* the question is enumerated with ints */
    private char currPart; /* the current part; Integer or Char */
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
            currQuestion = (char) (currQuestion + 1); // TODO: '9' + 1 = ':'
            qNum++;
        } else {
            currQuestion += 1;
            qNum++;
        }
    }

    public void nextPart() {
        if (pIsInt) {
            currPart = (char) (currPart + 1);
            pNum++;
        } else {
            currPart += 1;
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
            currPart = '1';
            pNum = 1;
        } else {
            currPart = 'a';
            pNum = 1;
        }
    }
}
