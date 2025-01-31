package entity;

public class Admin extends Utilisateur {
    public Admin(int id, String nom, String email, String motDePasse) {
        super(id, nom, email, motDePasse);
    }

    public Admin(String nom, String email, String motDePasse) {
        super(nom, email, motDePasse);
    }
}
