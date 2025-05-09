package Services;

import java.sql.SQLException;
import java.util.List;

public interface IService<T> {

    void create(T t) throws SQLException;

    void update(T t) throws SQLException;

    void delete(T t) throws SQLException;

    List<T> readAll() throws SQLException;
    void ajouter(T t);
    void modifier(T t);
    void supprimer(int id);
    List<T> afficher();


}
