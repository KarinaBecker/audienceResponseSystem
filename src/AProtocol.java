/**
 * class AProtocol
 */

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class AProtocol implements Runnable {
    private static final int START = 0;
    private static final int QUESTION = 1;
    private static final int ANSWER = 2;
    private static final int END = 3;
    private int state = START;

    private static String[] questions;
    private static String[][] answers;
    private static int numQuestions;
    private static int currentQuestion = 0;
    private static int userScore = 0;
    private Socket socket;
    private static String theOutput = null;

    public AProtocol(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        if (!initQnA()) {
            System.err.println("Error: couldn't initialize questions and answers");
            return;
        }
        try {
            // Gets socket's output stream (write data to destination) and input stream (read data from source)
            PrintWriter out = new PrintWriter(new BufferedOutputStream(socket.getOutputStream()), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Initiate conversation with client
            String inputLine;
            String outputLine;
            outputLine = processInput(null);
            out.println(outputLine);
            out.flush();

            while ((inputLine = in.readLine()) != null) {
                outputLine = processInput(inputLine);
                out.println(outputLine);
                out.flush();
                if (outputLine.equals("That's it. Bye.")) {
                    break;
                }
            }

            // Cleaning up
            System.out.println("Client is disconnected");
            out.close();
            in.close();
            socket.close();
        } catch (IOException e) {
            System.err.println("Exception: Problems with client and client I/O");
            e.printStackTrace();
        }
    }

    // Initialize questions and answers
    private boolean initQnA() {
        try {
            File inFile = new File("QnA.txt");
            FileInputStream inStream = new FileInputStream(inFile);
            byte[] data = new byte[(int) inFile.length()];

            // Read the questions and answers into a byte array
            if (inStream.read(data) <= 0) {
                System.err.println("Error: couldn't read questions and answers.");
                return false;
            }

            // See how many question/answer pairs there are
            for (byte singleData : data) {
                if (singleData == (byte) '\n') {
                    numQuestions++;
                }
            }
            numQuestions /= 2;
            questions = new String[numQuestions];
            answers = new String[numQuestions][4];

            // Parse the questions and answers into arrays of strings
            int start = 0, index = 0;
            boolean isQ = true;
            for (int i = 0; i < data.length; i++)
                if (data[i] == (byte) '\n') {
                    if (isQ) {
                        questions[index] = new String(data, start, i - start - 1);
                        isQ = false;
                    } else {
                        String unsplittedAnswers = new String(data, start, i - start - 1);
                        String[] temp = unsplittedAnswers.split(",");
                        for (int ansI = 0; ansI < 4; ansI++) {
                            answers[index][ansI] = temp[ansI];
                        }
                        isQ = true;
                        index++;
                    }
                    start = i + 1;
                }
        } catch (FileNotFoundException e) {
            System.err.println("Exception: couldn't find the question file");
            return false;
        } catch (IOException e) {
            System.err.println("Exception: I/O error trying to read questions");
            return false;
        }
        return true;
    }


    // Method to keep track, handle conversation
    private String processInput(String theInput) {
        if (state == START) {
            theOutput = "Welcome! There are " + numQuestions + " questions. Are you ready?";
            state = QUESTION;

        } else if (state == QUESTION) {
            printQuestion();
            state = ANSWER;

        } else if (state == ANSWER) {
            isCorrectAnswer(theInput);
            currentQuestion++;
            if (currentQuestion < numQuestions) {
                state = QUESTION;
            } else {
                state = END;
            }
            System.out.println("User's score is " + userScore + " after " + currentQuestion + "/" + numQuestions + " questions.");

        } else if (state == END) {
            theOutput = "That's it. Bye.";
            System.out.println("The game is finished");
            //TRY   socket.close();
        }
        return theOutput;
    }

    // Checks whether the user's answer is correct for a particular question
    private static void isCorrectAnswer(String theInput) {
        boolean rightAnswer = theInput.trim().equalsIgnoreCase(answers[currentQuestion][0]);
        if (rightAnswer) {
            theOutput = "Correct! Your score is " + (++userScore) + " after " + (currentQuestion + 1) + "/" + numQuestions + " questions";

        } else {
            theOutput = "Incorrect! Your score is " + userScore + " and answer is " + answers[currentQuestion][0] + " after " + (currentQuestion + 1) + "/" + numQuestions + " questions";
        }
    }

    // Prints a specific question and its choices
    private static void printQuestion() {
        String theOutputI = questions[currentQuestion];
        String theOutputII = "";
        int lastColumn = answers[currentQuestion].length;
        for (int i = 0; i < lastColumn; i++) {
            theOutputII = theOutputII + "    " + answers[currentQuestion][i];
        }
        theOutput = theOutputI + theOutputII;
    }
}