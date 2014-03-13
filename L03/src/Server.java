/**
 * Created by Jose on 07-03-2014.
 */
public class Server {
     public void main(String[] args) {
         if (args.length != 3) {
             System.out.println("Usage: client <srvc_addr> <srvc_port>");
             System.exit(0);
         }

         new ServerThread().start();
     }
}
