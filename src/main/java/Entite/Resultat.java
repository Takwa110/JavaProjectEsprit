package Entite;

public class Resultat {

        private String username;
        private int correctAnswers;
        private int wrongAnswers;
        private int totalQuestions;

    public Resultat(String username, int correctAnswers, int wrongAnswers, int totalQuestions){
        this.username = username;
        this.correctAnswers = correctAnswers;
        this.wrongAnswers = wrongAnswers;
        this.totalQuestions = totalQuestions;
    }

        public String getUsername () {
        return username;
    }

        public int getCorrectAnswers () {
        return correctAnswers;
    }

        public int getWrongAnswers () {
        return wrongAnswers;
    }

        public int getTotalQuestions () {
        return totalQuestions;
    }

        public String getScorePercentage () {
        return String.format("%.2f%%", (correctAnswers / (double) totalQuestions) * 100);
    }

        public String getRemark () {
        if (correctAnswers < 2) {
            return "Oh no..! You have failed the quiz. It seems that you need to improve your general knowledge. Practice daily!";
        } else if (correctAnswers >= 2 && correctAnswers < 5) {
            return "Oops..! You have scored less marks. It seems like you need to improve your general knowledge.";
        } else if (correctAnswers >= 5 && correctAnswers <= 7) {
            return "Good. A bit more improvement might help you to get better results. Practice is the key to success.";
        } else if (correctAnswers == 8 || correctAnswers == 9) {
            return "Congratulations! Your hard work and determination helped you to score good marks!";
        } else {
            return "Congratulations! You passed the quiz with full marks because of your dedication! Keep it up!";
        }
    }
    }
