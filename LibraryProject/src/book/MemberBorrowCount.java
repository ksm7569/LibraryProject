// book/MemberBorrowCount.java
package book;

public class MemberBorrowCount {
    private String memberId;
    private int borrowCount;

    public MemberBorrowCount(String memberId, int borrowCount) {
        this.memberId = memberId;
        this.borrowCount = borrowCount;
    }

    public String getMemberId() {
        return memberId;
    }

    public int getBorrowCount() {
        return borrowCount;
    }
}
