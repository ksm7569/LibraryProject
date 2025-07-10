package member;

import java.util.List;
import book.BorrowDAO;  // ✅ 필요한 DAO import
import book.BookDAO;   // (만약 다른 데서 쓴다면 남겨도 됨)

public class MemberService {
    private MemberDAO dao = new MemberDAO();
    private BorrowDAO borrowDAO = new BorrowDAO(); // ✅ 이거만 쓰자

    public boolean withdraw(String id) {
        if (borrowDAO.hasBorrowedBooks(id)) {
            return false;
        }

        // 🔥 대출 이력 삭제 먼저
        borrowDAO.deleteBorrowHistory(id);

        return dao.deleteMember(id);
    }

    public boolean join(String id, String pw, String name) {
        if (dao.existsById(id)) {
            return false; // 이미 존재하는 ID
        }
        LibraryMember member = new LibraryMember(id, pw, name);
        return dao.insertMember(member);
    }

    public boolean update(String id, String newPw, String newName) {
        LibraryMember member = new LibraryMember(id, newPw, newName);
        return dao.updateMember(member);
    }

    public List<LibraryMember> getAllMembers() {
        return dao.selectAllMembers();
    }

    public LibraryMember login(String id, String pw) {
        LibraryMember member = dao.findMemberById(id);
        if (member != null && member.checkPassword(pw)) {
            return member;
        }
        return null;
    }
}