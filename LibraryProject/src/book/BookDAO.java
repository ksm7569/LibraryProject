package book;

import db.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {

    // 전체 도서 목록 조회
    public List<Book> findAll() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM book ORDER BY book_id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("book_id");
                String title = rs.getString("title");
                String author = rs.getString("author");
                books.add(new Book(id, title, author));
            }

        } catch (Exception e) {
            System.out.println("도서 목록 조회 중 오류발생!");
            e.printStackTrace();
        }

        return books;
    }

    // 도서 등록
    public boolean insertBook(Book book) {
        String sql = "INSERT INTO book (title, author) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            return pstmt.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("도서 등록 중 오류 발생!");
            e.printStackTrace();
            return false;
        }
    }

    // 도서 삭제 (대출 기록도 함께 삭제)
    public boolean deleteBook(int bookId) {
        String checkSql = "SELECT COUNT(*) FROM borrow_table WHERE book_id = ? AND return_date IS NULL";
        String deleteSql = "DELETE FROM book WHERE book_id = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            // 대출 중인지 확인
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, bookId);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    return false; // 삭제 불가
                }
            }

            // 삭제 수행
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                deleteStmt.setInt(1, bookId);
                int result = deleteStmt.executeUpdate();
                if (result > 0) {
                    conn.commit();
                    return true;
                } else {
                    conn.rollback();
                    return false;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 도서 ID로 검색
    public Book findById(int bookId) {
        String sql = "SELECT * FROM book WHERE book_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, bookId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String title = rs.getString("title");
                String author = rs.getString("author");
                return new Book(bookId, title, author);
            }

        } catch (Exception e) {
            System.out.println("❌ 도서 ID 검색 중 오류");
            e.printStackTrace();
        }

        return null;
    }

    // 제목으로 검색 (중복 가능)
    public List<Book> findAllByTitle(String title) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM book WHERE title = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, title);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("book_id");
                String author = rs.getString("author");
                books.add(new Book(id, title, author));
            }

        } catch (Exception e) {
            System.out.println("❌ 도서 제목 검색 중 오류");
            e.printStackTrace();
        }

        return books;
    }

    
    // 도서 검색 (제목 or 저자)
    public List<Book> searchBooks(String keyword) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM book WHERE title LIKE ? OR author LIKE ? ORDER BY book_id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String wildcard = "%" + keyword + "%";
            pstmt.setString(1, wildcard);
            pstmt.setString(2, wildcard);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("book_id");
                String title = rs.getString("title");
                String author = rs.getString("author");
                books.add(new Book(id, title, author));
            }

        } catch (Exception e) {
            System.out.println("❌ 도서 검색 중 오류");
            e.printStackTrace();
        }

        return books;
    }

    // 도서가 대출 중인지 확인
    public boolean isBookBorrowed(int bookId) {
        String sql = "SELECT COUNT(*) FROM borrow_table WHERE book_id = ? AND return_date IS NULL";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, bookId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (Exception e) {
            System.out.println("대출 상태 확인 중 오류발생!");
            e.printStackTrace();
        }

        return false;
    }
}