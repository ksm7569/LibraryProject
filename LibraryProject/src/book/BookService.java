package book;

import java.util.List;
import java.util.Map;

import member.LibraryMember;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;

public class BookService {

    private BookDAO dao = new BookDAO();
    private BorrowDAO borrowDAO = new BorrowDAO();

    public BookService() {
        // 기본 생성자
    }

    public List<Book> listBooks() {
        return dao.findAll();
    }

    public BorrowInfo getBorrowInfoByBookId(int bookId) {
        return borrowDAO.getBorrowInfoByBookId(bookId);
    }

    public List<Book> search(String keyword) {
        return dao.searchBooks(keyword);
    }

    public boolean extendBorrow(String memberId, int bookId) {
        return borrowDAO.extendBorrowIfPossible(memberId, bookId);
    }

    public List<BorrowInfo> getBorrowInfoByMemberId(String memberId) {
        return borrowDAO.getBorrowInfoByMemberId(memberId);
    }

    public boolean borrowBook(LibraryMember member, int bookId) {
        if (isSuspended(member)) {
            printSuspensionNotice(member);
            return false;
        }

        if (borrowDAO.isBookBorrowed(bookId)) {
            System.out.println("이미 대출 중인 도서입니다.");
            return false;
        }

        return borrowDAO.borrowBook(member.getId(), bookId);
    }

    // 대출 정지 상태 확인
    private boolean isSuspended(LibraryMember member) {
        List<BorrowInfo> infos = borrowDAO.getBorrowInfoByMemberId(member.getId());

        for (BorrowInfo info : infos) {
            if (info.getReturnDate() == null && info.getReturnDueDate() != null) {
                LocalDate dueDate = ((java.sql.Date) info.getReturnDueDate()).toLocalDate();
                if (LocalDate.now().isAfter(dueDate)) {
                    long overdueDays = ChronoUnit.DAYS.between(dueDate, LocalDate.now());
                    member.banForDays((int) overdueDays);
                    return true;
                }
            }
        }

        return member.isBanned();
    }

    // 대출 정지 안내 메시지
    private void printSuspensionNotice(LibraryMember member) {
        System.out.println("현재 대출정지회원입니다 " + member.getPenaltyEndDate() + "까지 책을 대출할 수 없습니다");
    }


    public List<String> getBorrowedTitles(String memberId) {
        return borrowDAO.getBorrowedBookTitlesByMember(memberId);
    }

    public String getBorrowerByBookId(int bookId) {
        return borrowDAO.getBorrowerByBookId(bookId);
    }

    public boolean deleteBook(int bookId) {
        if (dao.isBookBorrowed(bookId)) {
            System.out.println("대출 중인 도서는 삭제할 수 없습니다");
            return false;
        }
        return dao.deleteBook(bookId);
    }

    public boolean returnBook(LibraryMember member, int bookId) {
        BorrowInfo info = borrowDAO.getBorrowInfoByBookId(bookId);

        if (info == null || !info.getMemberId().equals(member.getId())) {
            System.out.println("해당 도서를 반납할 권한이 없습니다.");
            return false;
        }

        LocalDate dueDate = ((java.sql.Date) info.getReturnDueDate()).toLocalDate();
        LocalDate returnDate = LocalDate.now();
        long overdueDays = ChronoUnit.DAYS.between(dueDate, returnDate);

        if (overdueDays > 0) {
            member.banForDays((int) overdueDays);
            System.out.println("연체 " + overdueDays + "일 → 대출 정지 " + overdueDays + "일 적용");
        }

        return borrowDAO.returnBook(member.getId(), bookId);
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

    public void showBookList() {
        List<Book> books = dao.findAll();

        System.out.printf("%-5s %-20s %-20s %-15s\n", "ID", "제목", "저자", "상태");
        for (Book book : books) {
            int bookId = book.getBookId();
            String title = book.getTitle();
            String author = book.getAuthor();

            String borrower = borrowDAO.getBorrowerByBookId(bookId);
            String status = borrower != null ? "대출 중(" + borrower + ")" : "대출 가능";

            System.out.printf("%-5d %-20s %-20s %-15s\n", bookId, title, author, status);
        }
    }

    public void showBorrowedBooks(String memberId) {
        borrowDAO.findBorrowsByMember(memberId);
    }

    public boolean registerBook(String title, String author) {
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

    public List<MemberBorrowCount> getMemberBorrowRanking(LocalDate start, LocalDate end) {
        return borrowDAO.getMemberBorrowRanking(start, end);
    }

    public List<MemberBorrowCount> getMemberBorrowRanking() {
        return borrowDAO.getMemberBorrowRanking();
    }

    
    public List<BookBorrowCount> getBookBorrowRanking() {
        return borrowDAO.getBookBorrowRanking();
    }
}
