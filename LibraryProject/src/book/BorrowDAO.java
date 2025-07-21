package book;

import db.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BorrowDAO {

    public boolean borrowBook(String memberId, int bookId) {
        String checkSql = "SELECT * FROM borrow_table WHERE book_id = ? AND return_date IS NULL";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setInt(1, bookId);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                System.out.println("해당 도서는 대출 중입니다.");
                return false;
            }

            String insertSql = "INSERT INTO borrow_table (member_id, book_id) VALUES (?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(insertSql);
            pstmt.setString(1, memberId);
            pstmt.setInt(2, bookId);

            return pstmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean returnBook(String memberId, int bookId) {
        String sql = "UPDATE borrow_table SET return_date = SYSDATE WHERE member_id = ? AND book_id = ? AND return_date IS NULL";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, memberId);
            pstmt.setInt(2, bookId);
            return pstmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean hasBorrowedBooks(String memberId) {
        String sql = "SELECT COUNT(*) FROM borrow_table WHERE member_id = ? AND return_date IS NULL";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, memberId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void findBorrowsByMember(String memberId) {
        String sql = "SELECT b.book_id, b.title, b.author, br.borrow_date, br.return_date " +
                     "FROM borrow_table br JOIN book b ON br.book_id = b.book_id " +
                     "WHERE br.member_id = ? ORDER BY br.borrow_date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, memberId);
            ResultSet rs = pstmt.executeQuery();

            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                System.out.println("\n도서 ID: " + rs.getInt("book_id"));
                System.out.println("제목: " + rs.getString("title"));
                System.out.println("저자: " + rs.getString("author"));
                System.out.println("대출일: " + rs.getDate("borrow_date"));
                Date returnDate = rs.getDate("return_date");
                System.out.println("반납일: " + (returnDate != null ? returnDate : "미반납"));
                System.out.println("-----------------------------");
            }

            if (!hasData) {
                System.out.println("대출/반납 중인 도서가 없습니다.");
            }

        } catch (Exception e) {
            System.out.println("대출 내역 조회 중 오류 발생");
            e.printStackTrace();
        }
    }

    public void deleteBorrowHistory(String memberId) {
        String sql = "DELETE FROM borrow_table WHERE member_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, memberId);
            pstmt.executeUpdate();
        } catch (Exception e) {
            System.out.println("대출 이력 삭제 중 오류 발생");
            e.printStackTrace();
        }
    }

    public List<String> getBorrowedBookTitlesByMember(String memberId) {
        List<String> titles = new ArrayList<>();
        String sql = "SELECT b.title FROM borrow_table br " +
                     "JOIN book b ON br.book_id = b.book_id " +
                     "WHERE br.member_id = ? AND br.return_date IS NULL";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, memberId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                titles.add(rs.getString("title"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return titles;
    }

    public List<Integer> getBorrowedBookIds(String memberId) {
        List<Integer> list = new ArrayList<>();
        String sql = "SELECT book_id FROM borrow_table WHERE member_id = ? AND return_date IS NULL";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, memberId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(rs.getInt("book_id"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean isBookBorrowed(int bookId) {
        String sql = "SELECT COUNT(*) FROM borrow_table WHERE book_id = ? AND return_date IS NULL";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (Exception e) {
            System.out.println("도서 대출 여부 확인 중 오류 발생");
            e.printStackTrace();
            return false;
        }
    }

    // ✅ 완전 새로 구현: DB에서 대출 정보 + Book 정보 가져오기
    public List<BorrowInfo> getBorrowInfoByMemberId(String memberId) {
        List<BorrowInfo> result = new ArrayList<>();

        String sql = "SELECT b.book_id, b.title, b.author, br.borrow_date " +
                     "FROM borrow_table br JOIN book b ON br.book_id = b.book_id " +
                     "WHERE br.member_id = ? AND br.return_date IS NULL";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, memberId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int bookId = rs.getInt("book_id");
                String title = rs.getString("title");
                String author = rs.getString("author");
                Date borrowDate = rs.getDate("borrow_date");

                Book book = new Book(bookId, title, author);
                BorrowInfo info = new BorrowInfo(memberId, borrowDate, book);
                result.add(info);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public BorrowInfo getBorrowInfoByBookId(int bookId) {
        String sql = "SELECT br.member_id, br.borrow_date, b.book_id, b.title, b.author " +
                     "FROM borrow_table br JOIN book b ON br.book_id = b.book_id " +
                     "WHERE br.book_id = ? AND br.return_date IS NULL";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, bookId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String memberId = rs.getString("member_id");
                Date borrowDate = rs.getDate("borrow_date");
                String title = rs.getString("title");
                String author = rs.getString("author");
                Book book = new Book(bookId, title, author);

                return new BorrowInfo(memberId, borrowDate, book);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getBorrowerByBookId(int bookId) {
        String sql = "SELECT member_id FROM borrow_table WHERE book_id = ? AND return_date IS NULL";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("member_id");
            }
        } catch (Exception e) {
            System.out.println("대출자 조회 중 오류 발생");
            e.printStackTrace();
        }

        return null;
    }
}
