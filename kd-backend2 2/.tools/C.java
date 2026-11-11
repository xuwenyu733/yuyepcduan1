import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
public class C {
  public static void main(String[] a) {
    BCryptPasswordEncoder e = new BCryptPasswordEncoder(4);
    String h = "$2a$04$zOM85o8lWRwj9NDKjRhzbeZoUiEh5vqGAaX7y53PB7PCgakly88qC";
    String[] ps = {"admin123","123456","123456k","admin","admin123456","060330"};
    for (String p : ps) System.out.println(p + " => " + e.matches(p, h));
    System.out.println("ENCODE_admin123=" + e.encode("admin123"));
    System.out.println("ENCODE_123456k=" + e.encode("123456k"));
  }
}