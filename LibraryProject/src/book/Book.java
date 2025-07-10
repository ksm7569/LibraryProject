package book;

public class Book {
    private int bookId;
    private String title;
    private String author;

    // 생성자
    public Book(String title, String author) {
        this.title = title;
        this.author = author;
    }

    // DB 조회용 생성자
    public Book(int bookId, String title, String author) {
        this(title, author);
        this.bookId = bookId;
    }

    // Getter
    public int getBookId() { return bookId; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }

    @Override
    public String toString() {
        return "[" + bookId + "] " + title + " / " + author;
    }
} 