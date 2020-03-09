package questionDatabaseManagement;

import java.util.Arrays;
import java.util.Scanner;

public class MultipleChoiceQuestion extends Question {
	private String[] options;

	public MultipleChoiceQuestion(String type, String question, String correctAnswer, String commentWrong,
			String commentRight, String options) {
		super(type, question, correctAnswer, commentWrong, commentRight);
		this.options = convertStringToArray(options);
	}

	public String[] getOptions() {
		return options;
	}

	private String[] convertStringToArray(String string) {
		Scanner scanner = new Scanner(string);
		scanner.useDelimiter(",");
		int count = 4;
		String[] array = new String[count];
		for (int i = 0; i < count; i++) {
			array[i] = scanner.next();
		}
		return array;
	}

	@Override
	public String toString() {
		return super.toString() + " MultipleChoiceQuestion [options=" + Arrays.toString(options) + "]";
	}
}
