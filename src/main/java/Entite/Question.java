package Entite;

public class Question {
    private int id;
    private String question;
    private String answer;

    // Constructor
    public Question(int id, String question, String answer) {
        this.id = id;
        this.question = question;
        this.answer = answer;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    @Override
    public String toString() {
        return "Question{id=" + id + ", question='" + question + "', answer='" + answer + "'}";
    }
}
