package main;

import member.LibraryMember;
import member.MemberService;
import book.BookService;
import book.BorrowInfo;
import book.MemberBorrowCount;
import book.Book;
import book.BorrowDAO;
import book.BookBorrowCount;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

public class LibraryMain {

	public static void showUserBookMenu(Scanner sc, BookService bookService, LibraryMember loggedInMember) {
        while (true) {
            System.out.println("\n📚 도서 이용 메뉴");
            System.out.println("1. 도서 검색 및 대출");
            System.out.println("2. 도서 반납");
            System.out.println("3. 대출 연장");
            System.out.println("4. 대출 내역 조회");
            System.out.println("5. 도서 대출 순위");
            System.out.println("0. 이전으로");
            System.out.print("선택 >> ");
            String choice = sc.nextLine();

            switch (choice) {
                case "1" -> searchAndBorrowFlow(sc, bookService, loggedInMember);
                case "2" -> {
                    System.out.print("반납할 도서 제목 입력: ");
                    Book book = bookService.getBookByTitle(sc.nextLine());
                    if (book == null) System.out.println("도서를 찾을 수 없습니다.");
                    else System.out.println(bookService.returnBook(loggedInMember, book.getBookId()) ? "반납 완료" : "반납 실패");
                }
                case "3" -> {
                    System.out.print("연장할 도서 제목 입력: ");
                    Book book = bookService.getBookByTitle(sc.nextLine());
                    if (book == null) System.out.println("도서를 찾을 수 없습니다.");
                    else System.out.println(bookService.extendBorrow(loggedInMember.getId(), book.getBookId()) ? "7일 연장 완료" : "연장 실패 연장은 1회만 가능합니다");
                }
                case "4" -> bookService.showBorrowedBooks(loggedInMember.getId());
                case "5" -> showBookBorrowRanking(bookService);
                case "0" -> {
                    return;
                }
                default -> System.out.println("잘못된 입력입니다.");
            }
        }
    }

    public static void showMyInfoMenu(Scanner sc, MemberService memberService, LibraryMember loggedInMember) {
        while (true) {
            System.out.println("\n👤 내 정보 메뉴");
            System.out.println("1. 회원정보 수정");
            System.out.println("2. 회원 탈퇴");
            System.out.println("0. 이전으로");
            System.out.print("선택 >> ");
            String choice = sc.nextLine();

            switch (choice) {
                case "1" -> {
                    System.out.print("새 비밀번호: ");
                    String newPw = sc.nextLine();
                    System.out.print("새 이름: ");
                    String newName = sc.nextLine();
                    boolean success = memberService.update(loggedInMember.getId(), newPw, newName);
                    System.out.println(success ? "수정 완료" : "실패");
                }
                case "2" -> {
                    System.out.print("정말 탈퇴하시겠습니까? (Y/N): ");
                    if (sc.nextLine().equalsIgnoreCase("Y")) {
                        boolean deleted = memberService.withdraw(loggedInMember.getId());
                        if (deleted) {
                            System.out.println("회원 탈퇴 완료");
                            return;
                        } else {
                            System.out.println("대출중인 도서가 있으면 탈퇴할 수 없습니다");
                        }
                    }
                }
                case "0" -> {
                    return;
                }
                default -> System.out.println("잘못된 입력입니다.");
            }
        }
    }

    public static void showAdminUserMenu(Scanner sc, MemberService memberService, BookService bookService) {
        List<LibraryMember> members = memberService.getAllMembers();
        System.out.println(" 전체 사용자 및 대출 현황:\n");

        for (LibraryMember m : members) {
            String role = m.isAdmin() ? "관리자" : "일반회원";
            System.out.println(" ID: " + m.getId() + " / 이름: " + m.getName() + " / 권한: " + role);

            List<BorrowInfo> borrowList = bookService.getBorrowInfoByMemberId(m.getId());
            if (borrowList.isEmpty()) {
                System.out.println("     없음");
            } else {
                for (BorrowInfo info : borrowList) {
                    Book book = info.getBook();
                    String borrowDateStr = new SimpleDateFormat("yyyy-MM-dd").format(info.getBorrowDate());
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(info.getBorrowDate());
                    cal.add(Calendar.DAY_OF_MONTH, 14);
                    String returnDateStr = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());

                    System.out.printf("    %s (대출일자: %s, 반납일자: %s)\n", book.getTitle(), borrowDateStr, returnDateStr);
                }
            }
            System.out.println();
        }
    }

    public static void showAdminBookMenu(Scanner sc, BookService bookService) {
        while (true) {
            System.out.println("\n📘 도서 관리 메뉴");
            System.out.println("1. 도서 목록 및 검색");
            System.out.println("2. 도서 등록");
            System.out.println("3. 도서 삭제");
            System.out.println("4. 도서 대출 순위");
            System.out.println("0. 이전으로");
            System.out.print("선택 >> ");
            String choice = sc.nextLine();

            switch (choice) {
                case "1" -> {
                    System.out.print("검색어 입력 (전체: all): ");
                    String keyword = sc.nextLine();
                    List<Book> result = keyword.equalsIgnoreCase("all") ?
                            bookService.listBooks() : bookService.search(keyword);
                    printBookList(result, bookService);
                }
                case "2" -> {
                    System.out.print("도서 제목: ");
                    String title = sc.nextLine();
                    System.out.print("저자: ");
                    String author = sc.nextLine();
                    System.out.println(bookService.registerBook(title, author) ? "등록 완료" : "등록 실패");
                }
                case "3" -> {
                    System.out.print("삭제할 도서 제목 입력: ");
                    String title = sc.nextLine();
                    Book book = bookService.getBookByTitle(title);
                    if (book == null) System.out.println("해당 제목의 도서를 찾을 수 없습니다.");
                    else System.out.println(bookService.deleteBook(book.getBookId()) ? "삭제 완료" : "삭제 실패");
                }
                case "4" -> showBookBorrowRanking(bookService);
                case "0" -> {
                    return;
                }
                default -> System.out.println("잘못된 입력입니다.");
            }
        }
    }

    public static void searchAndBorrowFlow(Scanner sc, BookService bookService, LibraryMember member) {
        System.out.print("검색어 입력 (전체 목록: all): ");
        String keyword = sc.nextLine();

        List<Book> foundBooks = keyword.equalsIgnoreCase("all") ?
                bookService.listBooks() : bookService.search(keyword);

        if (foundBooks.isEmpty()) {
            System.out.println("도서를 찾을 수 없습니다.");
            return;
        }

        System.out.println("검색 결과:");
        printBookList(foundBooks, bookService);

        System.out.print("대출할 도서 ID 입력 (취소: 0): ");
        String input = sc.nextLine();

        int bookId;
        try {
            bookId = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("숫자만 입력하세요.");
            return;
        }

        if (bookId == 0) return;


        boolean success = bookService.borrowBook(member, bookId);
        if (success) {
            System.out.println("도서 대출 완료");
        }
    }

    public static void showBookBorrowRanking(BookService bookService) {
        List<BookBorrowCount> ranking = bookService.getBookBorrowRanking();

        System.out.println("\n도서별 대출 순위:");
        int rank = 1;
        for (BookBorrowCount entry : ranking) {
            System.out.printf("%d. [도서 ID: %d] %s - %d회\n",
                    rank++, entry.getBookId(), entry.getTitle(), entry.getBorrowCount());
        }
    }


    public static void showMemberBorrowRanking(Scanner sc, BookService bookService) {
        System.out.println("\n📊 회원별 대출 순위 보기");
        System.out.println("1. 전체 순위 보기");
        System.out.println("2. 기간별 순위 보기");
        System.out.print("선택 >> ");
        String choice = sc.nextLine();

        if (choice.equals("1")) {
            List<MemberBorrowCount> ranking = bookService.getMemberBorrowRanking();
            int rank = 1;
            for (MemberBorrowCount m : ranking) {
                System.out.printf("%d. %s - %d회\n", rank++, m.getMemberId(), m.getBorrowCount());
            }
        } else if (choice.equals("2")) {
            try {
                System.out.print("시작일 (예: 250701): ");
                String startStr = sc.nextLine();
                System.out.print("종료일 (예: 250722): ");
                String endStr = sc.nextLine();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd");
                LocalDate start = LocalDate.parse(startStr, formatter);
                LocalDate end = LocalDate.parse(endStr, formatter).plusDays(1);

                List<MemberBorrowCount> ranking = bookService.getMemberBorrowRanking(start, end);
                int rank = 1;
                for (MemberBorrowCount m : ranking) {
                    System.out.printf("%d. %s - %d회\n", rank++, m.getMemberId(), m.getBorrowCount());
                }
            } catch (Exception e) {
                System.out.println("날짜 형식이 잘못되었습니다 (예: 250701)");
            }
        } else {
            System.out.println("잘못된 선택입니다.");
        }
    }


    private static void printBookList(List<Book> books, BookService bookService) {
        if (books.isEmpty()) {
            System.out.println("등록된 도서가 없습니다");
        } else {
            System.out.println("도서 목록:");
            System.out.println("ID\t제목\t저자\t상태");

            for (Book b : books) {
                int bookId = b.getBookId();
                BorrowInfo info = bookService.getBorrowInfoByBookId(bookId);

                String status;
                if (info != null) {
                    String borrowDateStr = new SimpleDateFormat("yyyy-MM-dd").format(info.getBorrowDate());
                    String returnDateStr = (info.getReturnDueDate() != null)
                            ? new SimpleDateFormat("yyyy-MM-dd").format(info.getReturnDueDate())
                            : "정보 없음";

                    status = "대출 중(" + info.getMemberId() + ", 대출일: " + borrowDateStr + ", 반납예정일: " + returnDateStr + ")";
                } else {
                    status = "대출 가능";
                }

                System.out.println(bookId + "\t" + b.getTitle() + "\t" + b.getAuthor() + "\t" + status);
            }
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        MemberService memberService = new MemberService();
        BookService bookService = new BookService();

        LibraryMember loggedInMember = null;

        while (true) {
            System.out.println("\n====== 도서관 시스템 ======");

            if (loggedInMember == null) {
                System.out.println("1. 회원가입");
                System.out.println("2. 로그인");
                System.out.println("0. 종료");
                System.out.print("선택 >> ");
                String choice = sc.nextLine();

                if (choice.equals("0")) {
                    System.out.println("프로그램 종료");
                    break;
                }

                if (choice.equals("1")) {
                    System.out.print("ID: ");
                    String id = sc.nextLine();
                    System.out.print("PW: ");
                    String pw = sc.nextLine();
                    System.out.print("이름: ");
                    String name = sc.nextLine();
                    System.out.println(memberService.join(id, pw, name) ? "가입 성공!" : "이미 존재하는 ID입니다");
                } else if (choice.equals("2")) {
                    System.out.print("ID: ");
                    String id = sc.nextLine();
                    System.out.print("PW: ");
                    String pw = sc.nextLine();
                    loggedInMember = memberService.login(id, pw);
                    System.out.println(loggedInMember != null ? loggedInMember.getName() + "님 로그인 성공!" : "로그인 실패!");
                }
                continue;
            }

            if (loggedInMember.isAdmin()) {
                System.out.println("1. 사용자 목록");
                System.out.println("2. 도서 관리");
                System.out.println("3. 회원별 대출 랭킹 보기"); // ✅ 신규 메뉴
                System.out.println("4. 로그아웃");
                System.out.print("선택 >> ");
                String choice = sc.nextLine();

                switch (choice) {
                    case "1" -> showAdminUserMenu(sc, memberService, bookService);
                    case "2" -> showAdminBookMenu(sc, bookService);
                    case "3" -> showMemberBorrowRanking(sc, bookService);
                    case "4" -> {
                        loggedInMember = null;
                        System.out.println("로그아웃 완료");
                    }
                    default -> System.out.println("잘못된 메뉴 선택입니다.");
                }
            } else {
                System.out.println("1. 도서 이용");
                System.out.println("2. 내 정보");
                System.out.println("3. 로그아웃");
                System.out.print("선택 >> ");
                String choice = sc.nextLine();

                int menuIndex;
                try {
                    menuIndex = Integer.parseInt(choice);
                } catch (NumberFormatException e) {
                    System.out.println("숫자만 입력하세요");
                    continue;
                }

                switch (menuIndex) {
                	case 1 -> showUserBookMenu(sc, bookService, loggedInMember);
                    case 2 -> showMyInfoMenu(sc, memberService, loggedInMember);
                    case 3 -> {
                        loggedInMember = null;
                        System.out.println("로그아웃 완료");
                    }
                    default -> System.out.println("잘못된 메뉴 선택입니다.");
                }
            }
        }
        sc.close();
    }
}
