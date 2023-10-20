import java.time.LocalDateTime;

public class ThreadTest {
    public static void main(String[] args) {
        Runnable runnable = () -> {
            System.out.println(Thread.currentThread().getName());
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };
        Thread threadA = new Thread(runnable, "wj1"); // 스레드 생성
        threadA.start(); // runnable-> running(현재 실행중인 thread가 main밖에 없기때문)

        Thread threadB = new Thread(runnable, "wj2"); // 스레드 생성
        threadB.start(); // runnable-> running(현재 실행중인 thread가 main밖에 없기때문)
    }


    // 람다 함수는 지역변수를 자신의 스택(람다식이 동작하는 스레드)에 복사하여 사용한다.
    // 지역변수는 스택에 저장되고, thread마다 고유의 스택을 갖고 있다.
    // 지역변수가 존재하는 Thread가 사라져도 복사된 값을 참조하면서 에러가 발생하지 않는다.
    // multi thread환경에서는 동기화 문제가 발생한다.

    /**
     * 인스턴스 변수(람다 내부 변수)나 정적 변수를 사용하면, 힙영역에 위치하게 되고, 람다식을 사용하는 thead에서 항상 참조할 수 있다.
     * 힙은 모든 thread가 공유하는 영역이다.
     */
    public static class BusyWaiting {
        static boolean flag = true;

        public static void main(String[] args) {
            Thread threadA = new Thread(() -> {
                while (flag) {
                    if (LocalDateTime.now().getSecond() % 5 == 0) {
                        System.out.println("저장!" + LocalDateTime.now());
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }); // 스레드 생성
            threadA.start(); // runnable-> running(현재 실행중인 thread가 main밖에 없기때문)

            try {
                Thread.sleep(1000 * 60);
                flag = false;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class BusyWaiting_Sleep {
        static boolean flag = true;

        public static void main(String[] args) {
            Thread threadSleep = new Thread(() -> {
                while (flag) {
                    System.out.println("저장!" + LocalDateTime.now());
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }); // 스레드 생성
            threadSleep.start(); // runnable-> running(현재 실행중인 thread가 main밖에 없기때문)

            try {
                Thread.sleep(1000 * 60);
                flag = false;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // waitAndNotify예시
    public static class WaitAndNotifySample {
        public static class WaitThread extends Thread {
            @Override
            public void run() {
                synchronized (this) {
                    try {
                        System.out.println("sleep 10초");
                        Thread.sleep(10000);
                        System.out.println("notify!");
                        this.notify();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        public static void main(String[] args) {
            WaitThread waitThread = new WaitThread();
            waitThread.start(); //스레드 실행

            // 동기화 블록
            synchronized (waitThread) {
                System.out.println("동기화블록 진입");
                try {
                    // 동기화 시켜놓고 waitThread wait호출 -> mainThread가 중지됨
                    //-> wait메서드를 호출한 객체가 실행되는 thread를 중지시키는 것(=mainThread)
                    System.out.println("wait~!!");
                    waitThread.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                System.out.println("종료");
            }
        }
    }

    // waitAndNotify예시
    public static class JoinSample {
        public static class JoinThread extends Thread {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName()+" is Processing : " + LocalDateTime.now());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(Thread.currentThread().getName()+" is Terminated : " + LocalDateTime.now());
            }
        }

        public static void main(String[] args) {
//            for(int i=0; i<5; i++){
//                Thread thread = new JoinThread();
//                thread.setName("join_"+i);
//                thread.start();
//            }
//            System.out.println("MainThread End");

            for(int i=0; i<5; i++){
                Thread thread = new JoinThread();
                thread.setName("join_"+i);
                thread.start();
                try {
                    thread.join();
                }catch (InterruptedException interruptedException){
                    interruptedException.printStackTrace();
                }
            }
            System.out.println("MainThread End");
        }
    }
}
