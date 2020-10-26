package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import entity.Problem;

public class ProblemDAO extends DAO {
    public ProblemDAO(){
        super();
    }

    public Problem getProblemById(int id){
        Problem problem = null;
        String sql = "SELECT id, name, statement, solution, numSolved, testPath, " + 
                    "timeLimit, memLimit FROM problem WHERE id = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                problem = new Problem(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("statement"),
                    rs.getString("solution"),
                    rs.getInt("numSolved"),
                    rs.getString("testPath"),
                    rs.getInt("timeLimit"),
                    rs.getInt("memLimit")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return problem;
    }
}