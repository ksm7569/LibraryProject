package book;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class BookService {

    private BookDAO dao = new BookDAO();
    private BorrowDAO borrowDAO = new BorrowDAO();

    public List<Book> listBooks() {
        return dao.findAll();
    }

    public List<Book> search(String keyword) {
        return dao.searchBooks(keyword);
    }

    public boolean borrowBook(String memberId, int bookId) {
        return borrowDAO.borrowBook(memberId, bookId);
    }
    public List<String> getBorrowedTitles(String memberId) {
        BorrowDAO borrowDAO = new BorrowDAO();
        return borrowDAO.getBorrowedBookTitlesByMember(memberId);
    }
    public boolean deleteBook(int bookId) {
        if (dao.isBookBorrowed(bookId)) {
            System.out.println("대출 중인 도서는 삭제할 수 없습니다");
            return false;
        }
        return dao.deleteBook(bookId);
    }

    public boolean returnBook(String memberId, int bookId) {
        return borrowDAO.returnBook(memberId, bookId);
    }

    public Book getBookByTitle(String title) {
        List<Book> books = dao.findAllByTitle(title);
        if (books.size() == 1) {
            return books.get(0);
        } else {
            System.out.println("도서 제목이 중복되었거나 존재하지 않습니다");
            return null;
        }
    }

    public Book getBookById(int bookId) {
        return dao.findById(bookId);
    }

    public List<Integer> getMyBorrowedBooks(String memberId) {
        return borrowDAO.getBorrowedBookIds(memberId);
    }


    public void showBorrowedBooks(String memberId) {
        borrowDAO.findBorrowsByMember(memberId); // 출력은 DAO에서 직접 처리
    }

    public boolean registerBook(String title, String author) {
        // 제목 중복 여부 확인
        if (!dao.findAllByTitle(title).isEmpty()) {
            System.out.println("동일한 제목의 도서가 존재합니다");
            return false;
        }
        Book book = new Book(title, author);
        return dao.insertBook(book);
    }

    public Map<Integer, Boolean> getBookStatusMap() {
        List<Book> books = dao.findAll();
        Map<Integer, Boolean> statusMap = new HashMap<>();

        for (Book b : books) {
            boolean isBorrowed = borrowDAO.isBookBorrowed(b.getBookId());
            statusMap.put(b.getBookId(), isBorrowed);
        }

        return statusMap;
    }
} 