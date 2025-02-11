package org.esprit.projets.entity;


public class Etudiant extends Utilisateur {
    private String niveau;

    public Etudiant(int id, String nom, String email, String motDePasse, String niveau) {
        super(id, nom, email, motDePasse);
        this.niveau = niveau;
    }

    public Etudiant(String nom, String email, String motDePasse, String niveau) {
        super(nom, email, motDePasse);
        this.niveau = niveau;
    }

    public String getNiveau() { return niveau; }
    public void setNiveau(String niveau) { this.niveau = niveau; }

    @Override
    public String toString() {
        return super.toString() + ", niveau='" + niveau + '\'';
    }
}
