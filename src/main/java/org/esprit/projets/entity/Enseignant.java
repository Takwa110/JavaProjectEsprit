package org.esprit.projets.entity;

public class Enseignant extends Utilisateur {
    private String specialite;

    public Enseignant(int id, String nom, String email, String motDePasse, String specialite) {
        super(id, nom, email, motDePasse);
        this.specialite = specialite;
    }

    public Enseignant(String nom, String email, String motDePasse, String specialite) {
        super(nom, email, motDePasse);
        this.specialite = specialite;
    }

    public String getSpecialite() { return specialite; }
    public void setSpecialite(String specialite) { this.specialite = specialite; }

    @Override
    public String toString() {
        return super.toString() + ", specialite='" + specialite + '\'';
    }}