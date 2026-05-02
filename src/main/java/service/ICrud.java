package service;

import java.sql.SQLException;
import java.util.List;

public interface ICrud <T>{

    //methodes

     void create(T t) throws SQLException;
     void update(T t)throws SQLException;
     void delete(int id)throws SQLException;
     List<T> read()throws SQLException;


}
