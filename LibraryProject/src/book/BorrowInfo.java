package book;

import java.util.Date;

public class BorrowInfo {
    private String memberId;
    private Date borrowDate;
    private Date returnDate;       // ✅ 반납일 필드
    private Book book;
    private Date returnDueDate;    // ✅ 반납 예정일 필드

    // 기본 생성자들
    public BorrowInfo(String memberId, Date borrowDate) {
        this.memberId = memberId;
        this.borrowDate = borrowDate;
    }

    public BorrowInfo(String memberId, Date borrowDate, Book book) {
        this.memberId = memberId;
        this.borrowDate = borrowDate;
        this.book = book;
    }

    public BorrowInfo(String memberId, Date borrowDate, Date returnDueDate, Book book) {
        this.memberId = memberId;
        this.borrowDate = borrowDate;
        this.returnDueDate = returnDueDate;
        this.book = book;
    }

    // ✅ 모든 필드를 포함하는 생성자
    public BorrowInfo(String memberId, Date borrowDate, Date returnDueDate, Date returnDate, Book book) {
        this.memberId = memberId;
        this.borrowDate = borrowDate;
        this.returnDueDate = returnDueDate;
        this.returnDate = returnDate;
        this.book = book;
    }

    // ✅ Getter/Setter
    public String getMemberId() {
        return memberId;
    }

    public Date getBorrowDate() {
        return borrowDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public Date getReturnDueDate() {
        return returnDueDate;
    }

    public void setReturnDueDate(Date returnDueDate) {
        this.returnDueDate = returnDueDate;
    }

    public Book getBook() {
        return book;
    }
}
