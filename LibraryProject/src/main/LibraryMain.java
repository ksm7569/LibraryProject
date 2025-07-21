package main;

import member.LibraryMember;
import member.MemberService;
import book.BookService;
import book.BorrowInfo;
import book.Book;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class LibraryMain {
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
            } else if (loggedInMember.isAdmin()) {
                System.out.println("1. 사용자 목록 보기");
                System.out.println("2. 도서 목록 보기");
                System.out.println("3. 도서 검색");
                System.out.println("4. 도서 등록");
                System.out.println("5. 도서 삭제");
                System.out.println("6. 로그아웃");
            } else {
                System.out.println("1. 도서 목록 보기");
                System.out.println("2. 대출/반납 이력 보기");
                System.out.println("3. 도서 검색");
                System.out.println("4. 도서 대출");
                System.out.println("5. 도서 반납");
                System.out.println("6. 회원정보 수정");
                System.out.println("7. 로그아웃");
                System.out.println("8. 회원 탈퇴");
            }
            System.out.println("0. 종료");
            System.out.print("선택 >> ");
            String choice = sc.nextLine();

            if (choice.equals("0")) {
                System.out.println("프로그램 종료");
                break;
            }

            if (loggedInMember == null) {
                if (choice.equals("1")) {
                    System.out.print("ID: ");
                    String id = sc.nextLine();
                    System.out.print("PW: ");
                    String pw = sc.nextLine();
                    System.out.print("이름: ");
                    String name = sc.nextLine();

                    boolean success = memberService.join(id, pw, name);
                    System.out.println(success ? "가입 성공!" : "이미 존재하는 ID입니다");
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

            boolean isAdmin = loggedInMember.isAdmin();
            int menuIndex;
            try {
                menuIndex = Integer.parseInt(choice);
            } catch (NumberFormatException e) {
                System.out.println("숫자만 입력하세요");
                continue;
            }

            if (isAdmin) {
                switch (menuIndex) {
                case 1 -> {
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
                                String title = book.getTitle();

                                String borrowDateStr = new SimpleDateFormat("yyyy-MM-dd").format(info.getBorrowDate());
                                Calendar cal = Calendar.getInstance();
                                cal.setTime(info.getBorrowDate());
                                cal.add(Calendar.DAY_OF_MONTH, 14);
                                String returnDateStr = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());

                                System.out.printf("    %s (대출일자: %s, 반납일자: %s)\n", title, borrowDateStr, returnDateStr);
                            }
                        }
                        System.out.println();
                    }
                }
                case 2 -> printBookList(bookService.listBooks(), bookService);
                case 3 -> {
                    System.out.print("검색어 입력: ");
                    printBookList(bookService.search(sc.nextLine()), bookService);
                }
                    case 4 -> {
                        System.out.print("도서 제목: ");
                        String title = sc.nextLine();
                        System.out.print("저자: ");
                        String author = sc.nextLine();
                        boolean success = bookService.registerBook(title, author);
                        System.out.println(success ? "도서 등록 완료!" : "도서 등록 실패!");
                    }
                    case 5 -> {
                    	System.out.print("삭제할 도서 제목 입력: ");
                    	String title = sc.nextLine();
                    	Book book = bookService.getBookByTitle(title);

                    	if (book == null) {
                    	    System.out.println("해당 제목의 도서를 찾을 수 없습니다.");
                    	} else {
                    	    boolean deleted = bookService.deleteBook(book.getBookId());
                    	    System.out.println(deleted ? "도서 삭제 완료!" : "도서 삭제 실패!");
                    	}
                    }
                    case 6 -> {
                        loggedInMember = null;
                        System.out.println("로그아웃 완료");
                    }
                    default -> System.out.println("잘못된 메뉴 선택입니다.");
                }
            } else {
                switch (menuIndex) {
                case 1 -> printBookList(bookService.listBooks(), bookService);
                    case 2 -> bookService.showBorrowedBooks(loggedInMember.getId());
                    case 3 -> {
                        System.out.print("검색어 입력: ");
                        printBookList(bookService.search(sc.nextLine()), bookService);
                    }
                    case 4 -> {
                        System.out.print("대출할 도서 제목 입력: ");
                        Book book = bookService.getBookByTitle(sc.nextLine());
                        if (book == null) {
                            System.out.println("도서를 찾을 수 없습니다");
                        } else {
                            boolean success = bookService.borrowBook(loggedInMember.getId(), book.getBookId());
                            System.out.println(success ? "도서 대출 완료" : "이미 대출 중입니다");
                        }
                    }
                    case 5 -> {
                        System.out.print("반납할 도서 제목 입력: ");
                        Book book = bookService.getBookByTitle(sc.nextLine());
                        if (book == null) {
                            System.out.println("도서를 찾을 수 없습니다.");
                        } else {
                            boolean success = bookService.returnBook(loggedInMember.getId(), book.getBookId());
                            System.out.println(success ? "도서 반납 완료" : "반납할 수 없습니다");
                        }
                    }
                    case 6 -> {
                        System.out.print("새 비밀번호: ");
                        String newPw = sc.nextLine();
                        System.out.print("새 이름: ");
                        String newName = sc.nextLine();
                        boolean success = memberService.update(loggedInMember.getId(), newPw, newName);
                        System.out.println(success ? "회원 정보 수정 완료" : "수정에 실패했습니다");
                    }
                    case 7 -> {
                        loggedInMember = null;
                        System.out.println("로그아웃 완료");
                    }
                    case 8 -> {
                        System.out.print("정말 탈퇴하시겠습니까? (Y/N): ");
                        String confirm = sc.nextLine();
                        if (confirm.equalsIgnoreCase("Y")) {
                            boolean deleted = memberService.withdraw(loggedInMember.getId());
                            if (deleted) {
                                System.out.println("회원 탈퇴 완료");
                                loggedInMember = null;
                            } else {
                                System.out.println("대출중인 도서가 있으면 탈퇴할 수 없습니다");
                            }
                        }
                    }
                    default -> System.out.println("잘못된 메뉴 선택입니다");
                }
            }
        }
        sc.close();
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
                    // 대출일 문자열
                    String borrowDateStr = new SimpleDateFormat("yyyy-MM-dd").format(info.getBorrowDate());

                    // 반납 예정일 계산: 대출일 + 14일
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(info.getBorrowDate());
                    cal.add(Calendar.DAY_OF_MONTH, 14);
                    String returnDateStr = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());

                    status = "대출 중(" + info.getMemberId() + ", 대출일: " + borrowDateStr + ", 반납일자: " + returnDateStr + ")";
                } else {
                    status = "대출 가능";
                }

                System.out.println(bookId + "\t" + b.getTitle() + "\t" + b.getAuthor() + "\t" + status);
            }
        }
    }
}