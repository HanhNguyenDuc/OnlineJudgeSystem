package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import entity.Problem;
import entity.Submission;

public class SubmissionDAO extends DAO {
    public SubmissionDAO() {
        super();
    }

    public Submission createSubmission(Problem problem, String code){
        Submission submission = null;
        String sql = "INSERT INTO submission (submissionCode) VALUES (?)";
        try{
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, code);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();

            while (rs.next()){
                submission = new Submission(rs.getInt(1), problem, code);
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        // Create Problem Submission
        String sqlProblemSubmission = "INSERT INTO problem_submission (id_problem, id_submission) VALUES (?, ?)";
        try{
            PreparedStatement ps = con.prepareStatement(sqlProblemSubmission);
            ps.setInt(1, problem.getId());
            ps.setInt(2, submission.getId());
            ps.executeUpdate();
        } catch (Exception e){
            e.printStackTrace();
        }
        return submission;
    }

    public void updateSubmissionReport(Submission submission){
        String sql = "UPDATE submission SET judgeReport = ? WHERE id = ?";
        try{
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, submission.getJudgeReport());
            ps.setInt(2, submission.getId());
            ps.executeUpdate();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void updateSubmissionStatus(Submission submission){
        String sql = "UPDATE submission SET judgeStatus = ? WHERE id = ?";
        try{
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, submission.getJudgeStatus());
            ps.setInt(2, submission.getId());
            ps.executeUpdate();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}