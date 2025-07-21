package book;

import java.util.Date;

public class BorrowInfo {
    private String memberId;
    private Date borrowDate;
    private Book book;  // ✅ 추가된 필드

    // 기존 생성자 유지
    public BorrowInfo(String memberId, Date borrowDate) {
        this.memberId = memberId;
        this.borrowDate = borrowDate;
    }

    // ✅ Book 포함된 생성자 추가
    public BorrowInfo(String memberId, Date borrowDate, Book book) {
        this.memberId = memberId;
        this.borrowDate = borrowDate;
        this.book = book;
    }

    public String getMemberId() {
        return memberId;
    }

    public Date getBorrowDate() {
        return borrowDate;
    }

    public Book getBook() {
        return book;
    }
}
