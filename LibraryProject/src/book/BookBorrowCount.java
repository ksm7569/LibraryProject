package book;

public class BookBorrowCount {
    private int bookId;
    private String title;
    private int borrowCount;

    public BookBorrowCount(int bookId, String title, int borrowCount) {
        this.bookId = bookId;
        this.title = title;
        this.borrowCount = borrowCount;
    }

    public int getBookId() {
        return bookId;
    }

    public String getTitle() {
        return title;
    }

    public int getBorrowCount() {
        return borrowCount;
    }
}

