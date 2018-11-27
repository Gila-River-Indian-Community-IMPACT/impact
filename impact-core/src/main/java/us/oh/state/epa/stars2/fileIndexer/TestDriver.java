package us.oh.state.epa.stars2.fileIndexer;

public class TestDriver {
   public static void main(String[] args) {
       if (args.length != 2) {
           System.out.println("Not enough arguments. Two required, index directory and backup index directory");
       }
       
       IndexFiles indexer = new IndexFiles(args[0], args[1]);
       
       indexer.run();
   }
}
