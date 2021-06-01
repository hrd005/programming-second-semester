//package stepanoff.denis.lab5;
//
//public class Main {
//
//    public static void main(String[] args) {
//        if (args.length == 0) {
//            ConsoleWriter.println("The data file name has not been passed.\nStop execution", ConsoleWriter.Color.RED);
//            System.exit(-1);
//        }
//
//        FileReader fileReader = FileIO.getReader();
//        Collection collection;
//        try {
//            collection = fileReader.parseFile(args[0]);
//
//            //ExecutorService.start(System.in, CommandSet.CLIENT_DEFAULT, collection);
//        } catch (FileReadingException | MaxRecursionDepthException e) {
//            e.printStackTrace();
//        }
//    }
//}
