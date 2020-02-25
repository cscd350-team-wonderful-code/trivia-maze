package questionDatabaseManagement;

import java.sql.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class QuestionGetter {
	private Database db;
	private int questionCount;
	private Queue<Question> questions;
	private LinkedList<Question> usedQuestions;

	public QuestionGetter(String url) {
		db = new Database(url);
		questionCount = getQuestionCountInitital();
		questions = getQuestionsInternal();
	}

	private Queue<Question> getQuestionsInternal() {
		Question toBeShuffled[] = new Question[questionCount];
		for (int i = 1; i <= questionCount; i++) {
			Connection con = db.getConnection();
			Statement s = null;
			try {
				Class.forName("org.sqlite.JDBC");
				s = con.createStatement();
				String sql = "SELECT Question, Type, CorrectAnswer FROM Questions WHERE ID is " + i;
				ResultSet rs = s.executeQuery(sql);
				String type = rs.getString("Type");
				String question = rs.getString("Question");
				String correctAnswer = rs.getString("CorrectAnswer");
				sql = "SELECT CommentWrong, CommentRight FROM QuestionComments WHERE ID is " + i;
				rs = s.executeQuery(sql);
				String commentWrong = rs.getString("CommentWrong");
				String commentRight = rs.getString("CommentRight");
				switch (type) {
				case "MC":
					sql = "SELECT Option1,Option2,Option3 FROM MultipleChoice WHERE ID is " + i;
					rs = s.executeQuery(sql);
					String option1 = rs.getString("Option1");
					String option2 = rs.getString("Option2");
					String option3 = rs.getString("Option3");
					toBeShuffled[i - 1] = new MultipleChoiceQuestion(type, question, correctAnswer, commentWrong,
							commentRight, option1, option2, option3);
					break;
				default:
					toBeShuffled[i - 1] = new Question(type, question, correctAnswer, commentWrong, commentRight);
					break;
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		Random random = new Random();
		for (int i = 0; i < toBeShuffled.length; i++) {
			int randomPosition = random.nextInt(toBeShuffled.length);
			Question temp = toBeShuffled[i];
			toBeShuffled[i] = toBeShuffled[randomPosition];
			toBeShuffled[randomPosition] = temp;
		}
		Queue<Question> result = new LinkedList<Question>();
		for (int i = 0; i < toBeShuffled.length; i++) {
			result.add(toBeShuffled[i]);
		}
		return result;
	}

	private int getQuestionCountInitital() {
		Connection con = db.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		int size = -1;
		try {
			String sql = "select count(id) from questions";
			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();
			size = rs.getInt(1);
		} catch (SQLException e) {
			System.out.println(e.toString());
			size = -1;
		}
		return size;
	}

	public int getQuestionCount() {
		return questionCount;
	}

	public Database getDB() {
		return db;
	}

	public Question getQuestion() {
		Question temp = questions.poll();
		usedQuestions.add(temp);
		if (questions.isEmpty()) {
			Collections.shuffle(usedQuestions);
			questions.addAll(usedQuestions);
			usedQuestions.clear();
		}
		return temp;
	}

	public Queue<Question> getQuestions() {
		return questions;
	}
}