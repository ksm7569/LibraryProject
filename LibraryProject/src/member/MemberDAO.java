package member;

import db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.ArrayList;

public class MemberDAO {

    public boolean insertMember(LibraryMember member) {
        String sql = "INSERT INTO library_member (id, password, name, is_admin) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, member.getId());
            pstmt.setString(2, member.checkPassword());
            pstmt.setString(3, member.getName());
            pstmt.setString(4, member.isAdmin() ? "Y" : "N");

            int result = pstmt.executeUpdate();
            return result > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateMember(LibraryMember member) {
        String sql = "UPDATE library_member SET password = ?, name = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, member.checkPassword());
            pstmt.setString(2, member.getName());
            pstmt.setString(3, member.getId());

            int result = pstmt.executeUpdate();
            return result > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<LibraryMember> selectAllMembers() {
        List<LibraryMember> list = new ArrayList<>();
        String sql = "SELECT * FROM library_member ORDER BY id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String id = rs.getString("id");
                String password = rs.getString("password");
                String name = rs.getString("name");
                boolean isAdmin = rs.getString("is_admin").equalsIgnoreCase("Y");

                list.add(new LibraryMember(id, password, name, isAdmin));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
    public boolean deleteMember(String id) {
        String sql = "DELETE FROM library_member WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            int result = pstmt.executeUpdate();
            return result > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean existsById(String id) {
        String sql = "SELECT 1 FROM library_member WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            return rs.next(); // 결과가 있으면 true → 이미 존재
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public LibraryMember findMemberById(String id) {
        String sql = "SELECT * FROM library_member WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String password = rs.getString("password");
                String name = rs.getString("name");
                boolean isAdmin = rs.getString("is_admin").equalsIgnoreCase("Y");

                return new LibraryMember(id, password, name, isAdmin);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}