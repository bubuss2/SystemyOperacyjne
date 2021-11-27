import java.util.Random;
import java.util.concurrent.Semaphore;

//klasa realizująca operacje na buforze - należy uzupełnić kod/pola *tylko w tej klasie*
//korzystając z semaforów (klasa Semaphore) i metod/bloków synchronized

class Bufor {
  private char buf[];
  private int rozmiar; //pomocnicze
  private int in, out;

  // semafor producentow i konsumentow
  static Semaphore semConsumers = new Semaphore(0);
  static Semaphore semProducers;
  
  // tworze objekty pomocniczy do synchronizacji producentow i konsumentow
  // gdyz synchronizacja na klase nie zadziala (zablokuje jednoczesnie producentow i konsumentow a nie osobno)
  Object synchConsumers = new Object();
  Object synchProducers = new Object();

  public Bufor(int N)
  {
    buf = new char[N];
    in = out = 0;
    // ustaw semafor konsumentow na wielkosc buffora
    semProducers = new Semaphore(N);

    System.out.println("Utworzono bufor rozmiaru "+N+".");
  }

//dodaj element x do bufora
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
        out = (out+1)%rozmiar;
    }

    semConsumers.release();
  }

//pobierz element z bufora i zwróć go
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
        in = (in+1)%rozmiar;
    }
    
    semProducers.release();
    return x;
  }

}

//klasa producenta
class Producent extends Thread {
  String id;
  Bufor buf;

  public Producent(String nazwa, Bufor bufor) {
    id = nazwa;
    buf = bufor;
  }

  public void run() {
    Random r = new Random();
    char x = 'A'-1;

    while (true) {
      System.out.println("Producent "+id+" produkuje...");
      try {
        sleep(500+r.nextInt(500)); //losowe opóźnienie produkcji
      } catch (InterruptedException e) {}

      x++;
      System.out.println("Producent "+id+" umieszczam w buforze: "+x);
      buf.dodaj(x);
    }
  }
}

//klasa konsumenta
class Konsument extends Thread {
  Bufor buf;
  String id;

  public Konsument(String nazwa, Bufor bufor) {
    buf = bufor;
    id = nazwa;
  }

  public void run() {
    Random r = new Random();

    while (true) {
      char x = buf.pobierz();
      System.out.println("Konsument "+id+" pobral: "+x);

      System.out.println("Konsument "+id+" konsumuje...");
      try {
        sleep(500+r.nextInt(2000)); //losowe opóźnienie konsumpcji
      } catch (InterruptedException e) {}
    }
  }
}

//klasa główna
public class ProdKons {

  public static void main(String[] arg) {
    Bufor b = new Bufor(5);
    Producent p1 = new Producent("P1", b);
    Konsument k1 = new Konsument("K1", b);
    Konsument k2 = new Konsument("K2", b);

    p1.start();
    k1.start();
    k2.start();

//uwaga: w obecnym rozwiązaniu wątki nie kończą działania
    try {
      p1.join();
      k1.join();
      k2.join();
    } catch (InterruptedException e) {}
  }
}