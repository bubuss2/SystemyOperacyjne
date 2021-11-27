import java.util.Random;
import java.util.concurrent.Semaphore;

class Buffor {
  private char buf[];
  private int size;
  private int in, out;

  static Semaphore semConsumers = new Semaphore(0);
  static Semaphore semProducers;
  

  Object synchConsumers = new Object();
  Object synchProducers = new Object();

  public Buffor(int N)
  {
    buf = new char[N];
    in = out = 0;
    semProducers = new Semaphore(N);

    System.out.println("Created buffor of size "+N+".");
  }

  public void dodaj(char x)
  {
    try {
        semProducers.acquire();
    }
    catch (Exception e) {
        System.out.println("InterruptedException");
    }

    synchronized (synchProducers){
        buf[out] = x;
        out = (out+1)%size;
    }

    semConsumers.release();
  }
  public char pobierz()
  {
    char x;
    try {
        semConsumers.acquire();
    }
    catch (Exception e) {
        System.out.println("InterruptedException");
    }

    synchronized(synchConsumers){
        x = buf[in];
        in = (in+1)%size;
    }
    
    semProducers.release();
    return x;
  }

}

class Producer extends Thread {
  String id;
  Buffor buf;

  public Producer(String name, Buffor Buffor) {
    id = name;
    buf = Buffor;
  }

  public void run() {
    Random r = new Random();
    char x = 'A'-1;

    while (true) {
      System.out.println("Producer "+id+" produkuje...");
      try {
        sleep(500+r.nextInt(500));
      } catch (InterruptedException e) {}

      x++;
      System.out.println("Producer "+id+" umieszczam w Bufforze: "+x);
      buf.dodaj(x);
    }
  }
}

class Consument extends Thread {
  Buffor buf;
  String id;

  public Consument(String name, Buffor Buffor) {
    buf = Buffor;
    id = name;
  }

  public void run() {
    Random r = new Random();

    while (true) {
      char x = buf.pobierz();
      System.out.println("Consument "+id+" pobral: "+x);

      System.out.println("Consument "+id+" konsumuje...");
      try {
        sleep(500+r.nextInt(2000));
      } catch (InterruptedException e) {}
    }
  }
}

public class ProdKons {

  public static void main(String[] arg) {
    Buffor b = new Buffor(5);
    Producer p1 = new Producer("P1", b);
    Consument k1 = new Consument("K1", b);
    Consument k2 = new Consument("K2", b);

    p1.start();
    k1.start();
    k2.start();

    try {
      p1.join();
      k1.join();
      k2.join();
    } catch (InterruptedException e) {}
  }
}