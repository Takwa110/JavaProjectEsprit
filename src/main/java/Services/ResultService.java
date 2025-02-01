package Services;

import Entite.Resultat;
import java.util.ArrayList;
import java.util.List;

public class ResultService {
    private List<Resultat> results = new ArrayList<>();

    public void saveResult(Resultat result) {
        results.add(result);
        System.out.println("Résultat enregistré : " + result.getUsername() + " - Score : "
                + result.getCorrectAnswers() + "/" + result.getTotalQuestions());
    }

    public List<Resultat> getResults() {
        return results;
    }

    public Resultat getLastResult() {
        return results.isEmpty() ? null : results.get(results.size() - 1);
    }
}
