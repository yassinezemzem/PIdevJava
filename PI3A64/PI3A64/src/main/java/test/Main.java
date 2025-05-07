package test;

import services.ProduitService;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {
        ProduitService ps = new ProduitService();

        try {
//            ps.modifier(new Personne(1,26, "Ben Foulen", "Foulen"));
            System.out.println(ps.recuperer());
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

    }
}
