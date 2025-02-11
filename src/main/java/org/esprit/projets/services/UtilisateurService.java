package org.esprit.projets.services;

import org.esprit.projets.entity.Admin;
import org.esprit.projets.entity.Enseignant;
import org.esprit.projets.entity.Etudiant;
import org.esprit.projets.entity.Utilisateur;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UtilisateurService implements IService<Utilisateur> {

    private Connection con = DBConnection.getConnection();

    @Override
    public void ajouter(Utilisateur u) throws SQLException {
        // 1. Ajouter l'utilisateur dans la table utilisateur
        String query = "INSERT INTO utilisateur (nom, email, mot_de_passe, role, specialite, niveau) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement pst = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        pst.setString(1, u.getNom());
        pst.setString(2, u.getEmail());
        pst.setString(3, u.getMotDePasse());

        if (u instanceof Admin) {
            pst.setString(4, "admin");
            pst.setNull(5, Types.VARCHAR);  // Pas de spécialité pour l'admin
            pst.setNull(6, Types.VARCHAR);  // Pas de niveau pour l'admin
        } else if (u instanceof Enseignant) {
            pst.setString(4, "enseignant");
            pst.setString(5, ((Enseignant) u).getSpecialite());
            pst.setNull(6, Types.VARCHAR);
        } else if (u instanceof Etudiant) {
            pst.setString(4, "etudiant");
            pst.setNull(5, Types.VARCHAR);
            pst.setString(6, ((Etudiant) u).getNiveau());
        }

        pst.executeUpdate();

        // 2. Récupérer l'ID généré pour l'utilisateur
        ResultSet rs = pst.getGeneratedKeys();
        if (rs.next()) {
            int userId = rs.getInt(1);

            // 3. Ajouter l'Admin dans la table admin
            if (u instanceof Admin) {
                String adminQuery = "INSERT INTO admin (id, nom, email, mot_de_passe) VALUES (?, ?, ?, ?)";
                PreparedStatement pstAdmin = con.prepareStatement(adminQuery);
                pstAdmin.setInt(1, userId);
                pstAdmin.setString(2, u.getNom());
                pstAdmin.setString(3, u.getEmail());
                pstAdmin.setString(4, u.getMotDePasse());
                pstAdmin.executeUpdate();
            } else if (u instanceof Enseignant) {
                String enseignantQuery = "INSERT INTO enseignant (id, specialite) VALUES (?, ?)";
                PreparedStatement pstEnseignant = con.prepareStatement(enseignantQuery);
                pstEnseignant.setInt(1, userId);
                pstEnseignant.setString(2, ((Enseignant) u).getSpecialite());
                pstEnseignant.executeUpdate();
            } else if (u instanceof Etudiant) {
                String etudiantQuery = "INSERT INTO etudiant (id, niveau) VALUES (?, ?)";
                PreparedStatement pstEtudiant = con.prepareStatement(etudiantQuery);
                pstEtudiant.setInt(1, userId);
                pstEtudiant.setString(2, ((Etudiant) u).getNiveau());
                pstEtudiant.executeUpdate();
            }
        }

        System.out.println("Utilisateur ajouté avec succès !");
    }

    @Override
    public void supprimer(Utilisateur u) throws SQLException {
        String query = "DELETE FROM utilisateur WHERE id = ?";
        PreparedStatement pst = con.prepareStatement(query);
        pst.setInt(1, u.getId());
        pst.executeUpdate();
        System.out.println("Utilisateur supprimé !");
    }

    @Override
    public void update(Utilisateur u) throws SQLException {
        String query = "UPDATE utilisateur SET nom = ?, email = ?, mot_de_passe = ? WHERE id = ?";
        PreparedStatement pst = con.prepareStatement(query);
        pst.setString(1, u.getNom());
        pst.setString(2, u.getEmail());
        pst.setString(3, u.getMotDePasse());
        pst.setInt(4, u.getId());
        pst.executeUpdate();
        System.out.println("Utilisateur mis à jour !");
    }

    @Override
    public List<Utilisateur> getAll() throws SQLException {
        List<Utilisateur> list = new ArrayList<>();
        String query = "SELECT * FROM utilisateur";
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        while (rs.next()) {
            String role = rs.getString("role");
            Utilisateur u;
            if ("admin".equals(role)) {
                u = new Admin(rs.getInt("id"), rs.getString("nom"), rs.getString("email"), rs.getString("mot_de_passe"));
            } else if ("enseignant".equals(role)) {
                u = new Enseignant(rs.getInt("id"), rs.getString("nom"), rs.getString("email"), rs.getString("mot_de_passe"), rs.getString("specialite"));
            } else {
                u = new Etudiant(rs.getInt("id"), rs.getString("nom"), rs.getString("email"), rs.getString("mot_de_passe"), rs.getString("niveau"));
            }
            list.add(u);
        }
        return list;
    }

    @Override
    public Utilisateur getById(int id) throws SQLException {
        String query = "SELECT * FROM utilisateur WHERE id = ?";
        PreparedStatement pst = con.prepareStatement(query);
        pst.setInt(1, id);
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            return new Utilisateur(rs.getInt("id"), rs.getString("nom"), rs.getString("email"), rs.getString("mot_de_passe"));
        }
        return null;
    }


    public Utilisateur checkCredentials(String email, String password) throws SQLException {
        String query = "SELECT * FROM utilisateur WHERE email = ? AND mot_de_passe = ?";
        PreparedStatement pst = con.prepareStatement(query);
        pst.setString(1, email);
        pst.setString(2, password);
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            String role = rs.getString("role");
            Utilisateur utilisateur;
            if ("admin".equals(role)) {
                utilisateur = new Admin(rs.getInt("id"), rs.getString("nom"), rs.getString("email"), rs.getString("mot_de_passe"));
            } else if ("enseignant".equals(role)) {
                utilisateur = new Enseignant(rs.getInt("id"), rs.getString("nom"), rs.getString("email"), rs.getString("mot_de_passe"), rs.getString("specialite"));
            } else {
                utilisateur = new Etudiant(rs.getInt("id"), rs.getString("nom"), rs.getString("email"), rs.getString("mot_de_passe"), rs.getString("niveau"));
            }
            return utilisateur;
        }
        return null; // Si aucun utilisateur n'est trouvé
    }
    public Utilisateur getByEmail(String email) throws SQLException {
        String query = "SELECT * FROM utilisateur WHERE email = ?";
        PreparedStatement pst = con.prepareStatement(query);
        pst.setString(1, email);
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            String role = rs.getString("role");
            if ("admin".equals(role)) {
                return new Admin(rs.getInt("id"), rs.getString("nom"), rs.getString("email"), rs.getString("mot_de_passe"));
            } else if ("enseignant".equals(role)) {
                return new Enseignant(rs.getInt("id"), rs.getString("nom"), rs.getString("email"), rs.getString("mot_de_passe"), rs.getString("specialite"));
            } else {
                return new Etudiant(rs.getInt("id"), rs.getString("nom"), rs.getString("email"), rs.getString("mot_de_passe"), rs.getString("niveau"));
            }
        }
        return null;
    }
    public void saveResetToken(String email, String resetToken) throws SQLException {
        // Vérifier si l'email existe
        String query = "SELECT * FROM utilisateur WHERE email = ?";
        PreparedStatement pst = con.prepareStatement(query);
        pst.setString(1, email);
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            // Si l'utilisateur existe, insérer le token de réinitialisation dans la table utilisateur
            String updateQuery = "UPDATE utilisateur SET reset_token = ? WHERE email = ?";
            PreparedStatement updatePst = con.prepareStatement(updateQuery);
            updatePst.setString(1, resetToken);
            updatePst.setString(2, email);
            updatePst.executeUpdate();
            System.out.println("Token de réinitialisation enregistré avec succès.");
        } else {
            System.out.println("Aucun utilisateur trouvé avec cet email.");
        }
    }

}
