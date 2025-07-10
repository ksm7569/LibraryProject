package book;

import java.util.Date;

public class Borrow {
    private int borrowId;
    private String memberId;
    private int bookId;
    private Date borrowDate;
    private Date returnDate;

    public Borrow(int borrowId, String memberId, int bookId, Date borrowDate, Date returnDate) {
        this.borrowId = borrowId;
        this.memberId = memberId;
        this.bookId = bookId;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
    }

    public int getBorrowId() { return borrowId; }
    public String getMemberId() { return memberId; }
    public int getBookId() { return bookId; }
    public Date getBorrowDate() { return borrowDate; }
    public Date getReturnDate() { return returnDate; }
}